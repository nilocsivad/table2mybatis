/**
 * 
 */
package com.iam_vip.table2mybatis.logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Colin
 */
public class DBAndTable {

	private Connection conn;

	public DBAndTable(PropertiesWithFolder prop) throws SQLException, ClassNotFoundException {
		super();

		if (conn == null) {
			String driver = prop.get("MYSQL_DRIVER");
			Class.forName(driver);
			conn = DriverManager.getConnection(prop.get("MYSQL_URL"), prop.get("MYSQL_USER"), prop.get("MYSQL_PWD"));
		}
	}

	public List<String> getTableNames() throws SQLException {

		List<String> list = new ArrayList<String>(8);

		PreparedStatement stmt = conn.prepareStatement("SHOW TABLES");
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			list.add(rs.getString(1));
		}

		if (stmt != null)
			stmt.close();
		if (rs != null)
			rs.close();

		return list;
	}

	public List<TableDefine> getTables() throws SQLException {

		List<TableDefine> list = new ArrayList<TableDefine>(8);

		PreparedStatement stmt = conn.prepareStatement("SHOW TABLES");
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			TableDefine tbl = new TableDefine();
			tbl.name = rs.getString(1);
			list.add(tbl);
		}

		if (stmt != null)
			stmt.close();
		if (rs != null)
			rs.close();

		for (TableDefine tbl : list) {
			this.getColumns(tbl);
		}

		return list;
	}

	public void printTypeMap() throws SQLException {
		conn.getTypeMap().forEach((key, val) -> {
			System.out.println(key + " --- " + val);
		});
	}

	public List<TableDefine> getTables(String... tables) throws SQLException {

		List<TableDefine> list = new ArrayList<TableDefine>(tables.length + 1);

		for (String table : tables) {
			TableDefine tbl = new TableDefine();
			tbl.name = table;
			list.add(this.getColumns(tbl));
		}

		return list;
	}

	public TableDefine getColumns(String table) throws SQLException {
		TableDefine tbl = new TableDefine();
		tbl.name = table;
		return this.getColumns(tbl);
	}

	public TableDefine getColumns(TableDefine tbl) throws SQLException {



		ResultSet rs = conn.getMetaData().getPrimaryKeys(null, null, tbl.name);

		String primaryKey = null;

		while (rs.next()) {
			primaryKey = rs.getString(4);
			tbl.primaryKey = primaryKey;
			break;
		}





		String sql = String.format("SHOW CREATE TABLE %s", tbl.name);
		PreparedStatement stmt = conn.prepareStatement(sql);
		rs = stmt.executeQuery();
		while (rs.next()) {
			String col = rs.getString(2);
			String last = col.substring(col.lastIndexOf(")"));
			int index = last.indexOf("COMMENT=");
			if (index > 0) {
				tbl.comment = last.substring(index + 8).replace("'", "");
			}
		}





		sql = String.format("SELECT * FROM %s LIMIT 0", tbl.name);
		stmt = conn.prepareStatement(sql);

		rs = stmt.executeQuery();
		ResultSetMetaData md = rs.getMetaData();

		int len = md.getColumnCount();
		List<ColumnDefine> cols = new ArrayList<ColumnDefine>(len + 1);

		for (int i = 1; len >= i; ++i) {

			ColumnDefine col = new ColumnDefine();
			col.name = md.getColumnName(i);
			col.typeInt = md.getColumnType(i);
			col.type = md.getColumnTypeName(i);
			col.javaType = md.getColumnClassName(i).replace("java.lang.", "");
			col.isAutoIncrement = md.isAutoIncrement(i);
			col.isPrimayKey = (primaryKey != null && primaryKey.equals(col.name));
			col.canBeNull = md.isNullable(i) == 1 ? true : false;
			col.len = md.getColumnDisplaySize(i);
			cols.add(col);

		}





		sql = String.format("SHOW FULL COLUMNS FROM %s", tbl.name);
		stmt = conn.prepareStatement(sql);
		rs = stmt.executeQuery();
		while (rs.next()) {
			String col = rs.getString(1);
			for (ColumnDefine cd : cols) {
				if (col.equals(cd.name)) {
					cd.comment = rs.getString(9);
					break;
				}
			}
		}



		tbl.primaryKey = primaryKey;
		tbl.columns = cols;

		return tbl;
	}

}
