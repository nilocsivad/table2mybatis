/**
 * 
 */
package com.iam_vip.table2mybatis.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Colin
 */
public class TableDefine {

	public interface ITableBeanName {
		String translateIt(String tableName);
	}

	/**
	 * 
	 */
	public TableDefine() {
	}

	public String name;
	public String primaryKey;
	public String comment;
	public List<ColumnDefine> columns = new ArrayList<ColumnDefine>(8);

	public String getColumnString() {
		return getColumnString(false);
	}

	public String getColumnString(boolean includePrimaryKey) {
		StringBuffer buf = new StringBuffer();
		for (ColumnDefine cd : columns) {
			if (primaryKey != null && !"".equals(primaryKey) && cd.name.equals(primaryKey) && !includePrimaryKey) {
				continue;
			}
			buf.append(", " + cd.name);
		}
		return buf.substring(2).toString();
	}

	public String getColumnValueString() {
		return getColumnValueString(false);
	}

	public String getColumnValueString(boolean includePrimaryKey) {
		StringBuffer buf = new StringBuffer();
		for (ColumnDefine cd : columns) {
			if (primaryKey != null && !"".equals(primaryKey) && cd.name.equals(primaryKey) && !includePrimaryKey) {
				continue;
			}
			buf.append(", #{" + cd.name + "}");
		}
		return buf.substring(2).toString();
	}
	
	public String getColumnValueString2() {
		return getColumnValueString2(false);
	}
	
	public String getColumnValueString2(boolean includePrimaryKey) {
		StringBuffer buf = new StringBuffer();
		for (ColumnDefine cd : columns) {
			if (primaryKey != null && !"".equals(primaryKey) && cd.name.equals(primaryKey) && !includePrimaryKey) {
				continue;
			}
			buf.append(", #" + cd.name + "#");
		}
		return buf.substring(2).toString();
	}
	
	public String getColumnItemValueString() {
		return getColumnItemValueString(false);
	}
	
	public String getColumnItemValueString(boolean includePrimaryKey) {
		StringBuffer buf = new StringBuffer();
		for (ColumnDefine cd : columns) {
			if (primaryKey != null && !"".equals(primaryKey) && cd.name.equals(primaryKey) && !includePrimaryKey) {
				continue;
			}
			buf.append(", #{item." + cd.name + "}");
		}
		return buf.substring(2).toString();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return the columns
	 */
	public List<ColumnDefine> getColumns() {
		return columns;
	}

}
