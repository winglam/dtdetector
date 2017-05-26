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
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import edu.washington.cs.dt.util.Log;

public class StaticFieldAccessInstrumenter
    extends AbstractTransformer implements ClassFileTransformer, Opcodes {

	public static boolean verbose = false;
	
	@Override
	protected void transformClassNode(ClassNode cn) {
		List<MethodNode> methods = cn.methods;
        for (MethodNode method : methods) {
                if ((method.access & ACC_NATIVE) > 0
                                || (method.access & ACC_ABSTRACT) > 0) {
                        continue;
                }
                //skip constructors
                if(InstrumentUtils.isClinit(method) || InstrumentUtils.isInit(method)) {
                	continue;
                }
                //skip those proxy classes created by JVM
                if(cn.name.startsWith("$Proxy")) {
                	continue;
                }
                if(verbose) {
                    System.out.println("transforming: " + cn.name + "#" + method.name);
                }
                this.instrumentMethod(cn, method);
        }
	}

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		if (!InstrumentUtils.shouldInstrumentThisClass(
				InstrumentUtils.transClassNameDotToSlash(className))) {
        return null;
        }

		try {
			// return instrumented class
			return this.treeAPITransform(classfileBuffer);
		} catch (Exception e) {
			Log.logln("Errors in transforming: " + className);
			return null; //return null means no transformation
		}
	}
	
	/**
     * Instrument each method node in class
     * */
	private static String tracerClass = "edu/washington/cs/dt/premain/Tracer";
	private static String tracerMethod = "traceField";
    private void instrumentMethod(ClassNode cn, MethodNode method) {
            InsnList mlist = method.instructions;
            if (mlist.size() == 0) {
                    return;
            }
            // modify the instruction list below, to insert
            //  "Tracer.traceField(String)"
            // before every "getstatic", "putstatic" instruction
            int instructionIndex = 0; 
            for(instructionIndex = 0; instructionIndex < mlist.size(); instructionIndex++) {
            	AbstractInsnNode inst = mlist.get(instructionIndex);
            	if(!(inst instanceof FieldInsnNode)) {
            		continue;
            	}
            	FieldInsnNode fieldInst = (FieldInsnNode)inst;
            	//only care about two kinds of instructions
            	if(fieldInst.getOpcode() == Opcodes.GETSTATIC
            			||inst.getOpcode() == Opcodes.PUTSTATIC) {
            		//now modify the bytecode
            		String declaredClass = InstrumentUtils.transClassNameSlashToDot(fieldInst.owner);
            		String fieldName = fieldInst.name;
            		String fullFieldName = declaredClass + "." + fieldName;
            		//insert an instruction: Tracer.traceField(fullFieldName) below
            		//first insert a string creation instruction
            		mlist.insertBefore(mlist.get(instructionIndex), new LdcInsnNode(fullFieldName));
            		instructionIndex++;
            		mlist.insertBefore(mlist.get(instructionIndex),
            				new MethodInsnNode(
            			      INVOKESTATIC, tracerClass, tracerMethod,
            			      "(Ljava/lang/String;)V"));
            		instructionIndex++; //must increase it. the length of mlist is increasing!
            		if(verbose) {
            		    System.out.println("  " + fullFieldName);
            		}
            	} else {
            		continue;
            	}
            }
            
    }

}
