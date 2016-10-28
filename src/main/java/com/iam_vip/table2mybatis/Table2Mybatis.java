/**
 * 
 */
package com.iam_vip.table2mybatis;

import java.io.InputStream;
import java.util.List;

import com.google.gson.Gson;
import com.iam_vip.table2mybatis.logic.DBAndTable;
import com.iam_vip.table2mybatis.logic.MyBatisPropertiesWithFolder;
import com.iam_vip.table2mybatis.logic.TableDefine;
import com.iam_vip.table2mybatis.logic.TableDefine.ITableBeanName;
import com.iam_vip.table2mybatis.rule.NameRulePrefixNoUnderline;

/**
 * @author Colin
 */
public class Table2Mybatis {

	/**
	 * 
	 */
	public Table2Mybatis() {
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Table2Mybatis run = new Table2Mybatis();


		MyBatisPropertiesWithFolder first = new MyBatisPropertiesWithFolder();

		// String tempDir = System.getProperty("java.io.tmpdir");
		// Properties properties = new Properties();
		// properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, tempDir);
		// Velocity.init(properties);

		/// 读取配置文件并初始化 ///
		InputStream in = run.getClass().getClassLoader().getResourceAsStream("table2-mybatis.properties");
		first.initProperties(in);
		in.close();

		// first.printProperties();
		/// 创建文件夹 ///
		first.initFolders();
		/// 复制模版文件 ///
		first.cpTemplate();



		DBAndTable dbt = new DBAndTable(first);
		// dbt.printTypeMap();

		ITableBeanName rule = new NameRulePrefixNoUnderline("");
		List<TableDefine> list = dbt.getTables();


		/// 生成 Bean ///
		first.toModel(list, rule);
		/// 生成 DB 接口 ///
		first.toIDB(list, rule);
		/// 生成业务逻辑接口 ///
		first.toInterface(list, rule);
		/// 生成逻辑实现 ///
		first.toImplement(list, rule);
		/// 生成 XML 文件 ///
		first.toXML(list, rule);


		Gson gson = new Gson();

		list.forEach(tb -> {
			System.out.println(gson.toJson(tb));
		});

	}

}
