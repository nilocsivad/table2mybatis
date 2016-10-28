/**
 * 
 */
package com.iam_vip.table2mybatis.rule;

import com.iam_vip.table2mybatis.logic.TableDefine.ITableBeanName;

/**
 * @author Colin
 */
public class NameRuleNoUnderline implements ITableBeanName {

	/**
	 * 
	 */
	public NameRuleNoUnderline() {
	}

	private String zeroToUpper(String str) {
		return (str.charAt(0) + "").toUpperCase() + str.substring(1).toLowerCase();
	}

	@Override
	public String translateIt(String tableName) {
		String[] arr = tableName.split("_");
		StringBuffer buf = new StringBuffer(tableName.length() + 8);
		for (String str : arr) {
			buf.append(zeroToUpper(str));
		}
		return buf.toString();
	}

}
