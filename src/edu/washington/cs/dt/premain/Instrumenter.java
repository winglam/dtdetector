package edu.washington.cs.dt.premain;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import edu.washington.cs.dt.util.Log;

public class Instrumenter extends AbstractTransformer implements ClassFileTransformer, Opcodes {

	public static boolean verbose = false;

	@Override
	protected void transformClassNode(ClassNode cn) {
		List<MethodNode> methods = cn.methods;
		for (MethodNode method : methods) {
			if ((method.access & ACC_NATIVE) > 0 || (method.access & ACC_ABSTRACT) > 0) {
				continue;
			}
			// skip constructors
			//if (InstrumentUtils.isClinit(method)) {
				// || InstrumentUtils.isInit(method)) {
			//	continue;
			//}
			// skip those proxy classes created by JVM
			if (cn.name.startsWith("$Proxy")) {
				continue;
			}
			if (verbose) {
				System.out.println("transforming: " + cn.name + "#" + method.name);
			}
			this.instrumentMethod(cn, method);
		}

		List<ClassNode> inners = cn.innerClasses;
		for (ClassNode innerCn : inners) {
			transformClassNode(innerCn);
		}
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if (!InstrumentUtils.shouldInstrumentThisClass(InstrumentUtils.transClassNameDotToSlash(className))) {
			return null;
		}

		try {
			// return instrumented class
			return this.treeAPITransform(classfileBuffer);
		} catch (Exception e) {
			Log.logln("Errors in transforming: " + className);
			return null; // return null means no transformation
		}
	}

	int outstandingNews = 0;
	/**
	 * Instrument each method node in class
	 * */
	private static String tracerClass = "edu/washington/cs/dt/instrumentation/OurTracer";

	private void instrumentMethod(ClassNode cn, MethodNode method) {

		System.err.println("Instrumented: " + cn.name + "::" + method.name);

		outstandingNews = 0;
		InsnList mlist = method.instructions;
		if (mlist.size() == 0) {
			return;
		}
		int instructionIndex = 0;
		
		
		if (inJunit(cn)) {
			for (instructionIndex = 0; instructionIndex < mlist.size(); instructionIndex++) {
				AbstractInsnNode inst = mlist.get(instructionIndex);
				if (inst instanceof MethodInsnNode) {
					MethodInsnNode methodNode = (MethodInsnNode) inst;
					instructionIndex = handleObjectInitialization(mlist, instructionIndex, methodNode);
				} else if (inst.getOpcode() == NEW) {
					outstandingNews++;
				}
			}	
		} else if (InstrumentUtils.isClinit(method)) {
			for (instructionIndex = 0; instructionIndex < mlist.size(); instructionIndex++) {
				AbstractInsnNode inst = mlist.get(instructionIndex);
				if (inst instanceof MethodInsnNode) {
					MethodInsnNode methodNode = (MethodInsnNode) inst;
					fixSecurityMgr(methodNode);
				}
			}
		} else 	{			
			for (instructionIndex = 0; instructionIndex < mlist.size(); instructionIndex++) {
				AbstractInsnNode inst = mlist.get(instructionIndex);
				if (inst instanceof MethodInsnNode) {
					MethodInsnNode methodNode = (MethodInsnNode) inst;
					fixSecurityMgr(methodNode);
					instructionIndex = handleObjectInitialization(mlist, instructionIndex, methodNode);
				} else if (inst instanceof FieldInsnNode) {
					instructionIndex = handleField(mlist, instructionIndex, inst);
				} else if (inst.getOpcode() == NEW) {
					outstandingNews++;
				}
			}
		}
	}

	private boolean inJunit(ClassNode cn) {
		String name  = cn.name;
		return name.startsWith("org/junit/") || name.startsWith("junit/"); 
	}

	private int handleObjectInitialization(InsnList mlist, int instructionIndex, MethodInsnNode methodNode) {
		if ("<init>".equals(methodNode.name) && outstandingNews > 0) {
			outstandingNews--;
			// we are making an assumption that the java compiler has
			// already inserted
			// a DUP operation before this.
			mlist.insert(mlist.get(instructionIndex), new InsnNode(DUP));
			instructionIndex++;
			mlist.insert(mlist.get(instructionIndex), new MethodInsnNode(INVOKESTATIC, tracerClass, "traceNew",
					"(Ljava/lang/Object;)V"));
			instructionIndex++;
		}

		if ("newInstance".equals(methodNode.name)
				&& ("java/lang/Class".equals(methodNode.owner) || "java/lang/reflect/Constructor"
						.equals(methodNode.owner))) {
			mlist.insert(mlist.get(instructionIndex), new InsnNode(DUP));
			++instructionIndex;
			mlist.insert(mlist.get(instructionIndex), new MethodInsnNode(INVOKESTATIC, tracerClass, "traceNew",
					"(Ljava/lang/Object;)V"));
			++instructionIndex;
		}
		if ("clone".equals(methodNode.name) && "java/lang/Object".equals(methodNode.owner)) {
			mlist.insert(mlist.get(instructionIndex), new InsnNode(DUP));
			++instructionIndex;
			mlist.insert(mlist.get(instructionIndex), new MethodInsnNode(INVOKESTATIC, tracerClass, "traceNew",
					"(Ljava/lang/Object;)V"));
			++instructionIndex;
		}
		return instructionIndex;
	}

	private void fixSecurityMgr(MethodInsnNode methodNode) {
		if ("java/lang/System".equals(methodNode.owner)
				&& ("setSecurityManager".equals(methodNode.name) || "getSecurityManager".equals(methodNode.name))) {
			methodNode.owner = "edu/washington/cs/dt/instrumentation/OurSecurityManager";
		}
	}

	private int handleField(InsnList mlist, int instructionIndex, AbstractInsnNode inst) {
		FieldInsnNode fieldNode = (FieldInsnNode) inst;
		if ((fieldNode.getOpcode() == GETSTATIC || fieldNode.getOpcode() == PUTSTATIC
				|| fieldNode.getOpcode() == GETFIELD || fieldNode.getOpcode() == PUTFIELD)
				&& !InstrumentUtils.isSynthetic(fieldNode.name)) {
			String declaredClass = InstrumentUtils.transClassNameSlashToDot(fieldNode.owner);
			String fieldName = fieldNode.name;
			String fullFieldName = declaredClass + "." + fieldName;

			String methodToCall = null;
			switch (fieldNode.getOpcode()) {
			case GETSTATIC:
				methodToCall = "traceReadStatic";
				break;
			case PUTSTATIC:
				methodToCall = "traceWriteStatic";
				break;
			case GETFIELD:
				methodToCall = "traceReadField";
				break;
			case PUTFIELD:
				methodToCall = "traceWriteField";
				break;
			}

			if (fieldNode.getOpcode() == GETFIELD || fieldNode.getOpcode() == PUTFIELD) {
				if (fieldNode.getOpcode() == GETFIELD) {
					mlist.insertBefore(mlist.get(instructionIndex), new InsnNode(DUP));
					instructionIndex++;
				} else {
					if ("J".equals(fieldNode.desc) || "D".equals(fieldNode.desc)) {
						// gotta do : ref, {val, val} -> {val, val}, ref
						// stack sorcery in progress.
						mlist.insertBefore(mlist.get(instructionIndex), new InsnNode(DUP2_X1));
						instructionIndex++;
						mlist.insertBefore(mlist.get(instructionIndex), new InsnNode(POP2));
						instructionIndex++;
						mlist.insertBefore(mlist.get(instructionIndex), new InsnNode(DUP));
						instructionIndex++;
					} else {
						// ref, val -> val, ref
						mlist.insertBefore(mlist.get(instructionIndex), new InsnNode(SWAP));
						instructionIndex++;
						mlist.insertBefore(mlist.get(instructionIndex), new InsnNode(DUP));
						instructionIndex++;
					}
				}

				mlist.insertBefore(mlist.get(instructionIndex), new LdcInsnNode(fullFieldName)); // string
				instructionIndex++;
				mlist.insertBefore(mlist.get(instructionIndex), new MethodInsnNode(INVOKESTATIC, tracerClass,
						methodToCall, "(Ljava/lang/Object;Ljava/lang/String;)V"));
				instructionIndex++;
				if (fieldNode.getOpcode() == PUTFIELD) {
					if ("J".equals(fieldNode.desc) || "D".equals(fieldNode.desc)) {
						// continuation of stack sorcery
						// {val, val}, ref -> ref, {val, val} must be
						// done
						mlist.insertBefore(mlist.get(instructionIndex), new InsnNode(DUP_X2));
						instructionIndex++;
						mlist.insertBefore(mlist.get(instructionIndex), new InsnNode(POP));
						instructionIndex++;
					} else {
						// val, ref -> ref, val
						mlist.insertBefore(mlist.get(instructionIndex), new InsnNode(SWAP));
						instructionIndex++;
					}
				}

			} else { // static put / get
				mlist.insertBefore(mlist.get(instructionIndex), new LdcInsnNode(fullFieldName)); // string
				instructionIndex++;
				mlist.insertBefore(mlist.get(instructionIndex), new MethodInsnNode(INVOKESTATIC, tracerClass,
						methodToCall, "(Ljava/lang/String;)V"));
				instructionIndex++;
			}

		}
		return instructionIndex;
	}
}
