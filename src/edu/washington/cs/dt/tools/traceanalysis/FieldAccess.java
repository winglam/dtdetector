package edu.washington.cs.dt.tools.traceanalysis;

import edu.washington.cs.dt.util.Utils;

public class FieldAccess {

	public final long classObjId;
	public final String fieldName;
	public final String fieldType;
	
	//READ#Lcrystal/model/DataSource;.$assertionsDisabled#Z#0
	public static FieldAccess parseFieldAccess(String line) {
		Utils.checkNull(line, "");
		String[] splits = line.split("#");
		if(splits.length != 4) {
			return null;
		}
		Utils.checkTrue(splits.length==4, "actual length: " + splits.length + ", in:" + line);
		String fieldName = splits[1];
		String fieldType = splits[2];
		long id = Long.parseLong(splits[3]);
		return new FieldAccess(id, fieldName, fieldType);
	}
	
	public FieldAccess(long id, String filedName, String fieldType) {
		this.classObjId = id;
		Utils.checkNull(filedName, "");
		Utils.checkNull(fieldType, "");
		this.fieldName = filedName;
		this.fieldType = fieldType;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof FieldAccess)) {
			return false;
		}
		FieldAccess fa = (FieldAccess)obj;
		return this.classObjId == fa.classObjId && this.fieldName.equals(fa.fieldName)
				&& this.fieldType.equals(fa.fieldType);
	}
	
	@Override
	public String toString() {
		return fieldType + "  " + fieldName + "@" + classObjId;
	}
	
	@Override
	public int hashCode() {
		return (this.classObjId + "").hashCode() + 99*this.fieldName.hashCode() + 197*this.fieldType.hashCode();
	}
}