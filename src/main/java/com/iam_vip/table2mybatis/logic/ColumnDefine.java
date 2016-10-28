/**
 * 
 */
package com.iam_vip.table2mybatis.logic;

/**
 * @author Colin
 */
public class ColumnDefine {

	/**
	 * 
	 */
	public ColumnDefine() {
	}

	public String name;
	public String methodName;
	public int typeInt = 0;
	public String type;
	public boolean isPrimayKey = false;
	public boolean isAutoIncrement = false;
	public boolean canBeNull = true;
	public String javaType;
	public int len;
	public String comment;


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the name
	 */
	public String getConstName() {
		return name.toUpperCase();
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return (name.charAt(0) + "").toUpperCase() + name.substring(1);
	}

	/**
	 * @return the typeInt
	 */
	public int getTypeInt() {
		return typeInt;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the isPrimayKey
	 */
	public boolean isPrimayKey() {
		return isPrimayKey;
	}

	/**
	 * @return the isAutoIncrement
	 */
	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}

	/**
	 * @return the isCanBeNull
	 */
	public boolean isCanBeNull() {
		return canBeNull;
	}

	/**
	 * @return the javaType
	 */
	public String getJavaType() {
		return javaType;
	}

	/**
	 * @return the len
	 */
	public int getLen() {
		return len;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

}
