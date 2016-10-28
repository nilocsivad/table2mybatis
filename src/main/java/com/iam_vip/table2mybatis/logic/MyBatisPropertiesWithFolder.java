/**
 * 
 */
package com.iam_vip.table2mybatis.logic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.iam_vip.table2mybatis.logic.TableDefine.ITableBeanName;

/**
 * @author Colin
 */
public class MyBatisPropertiesWithFolder extends PropertiesWithFolder {

	private static final String BASE_PACKAGE_CHILD_FMT = "%s.%s";

	/**
	 * 
	 */
	public MyBatisPropertiesWithFolder() {
	}

	private String[] KEY_VELOCITY = { "package_c", "package_request_base", "package_model", "package_api", "package_database", "package_iapi", "package_ref", "package_adapter", "package_util", "package_mobile", "package_request" };
	private String[] KEY_PROP = { "PATH_PACKAGE_C", "PATH_PACKAGE_REQUEST", "PATH_PACKAGE_MODEL", "PATH_PACKAGE_API", "PATH_PACKAGE_DATABASE", "PATH_PACKAGE_IAPI", "PATH_PACKAGE_REF", "PATH_PACKAGE_ADAPTER", "PATH_PACKAGE_UTIL", "PATH_PACKAGE_REQUEST_MOBILE", "PATH_PACKAGE_REQUEST" };
	private String[] PKG_FOLDER = new String[KEY_PROP.length];
	private String[] PKG_CLASS = new String[PKG_FOLDER.length];

	private String[] KEY_FOLDER = { "FOLDER_CONFIG_MYBATIS" };
	private String[] VAL_FOLDER = new String[KEY_FOLDER.length];



	private Properties prop;

	private String PROJ_ROOT, BASE_PACKAGE, CONFIG_FOLDER;
	private String XML_PREFIX;


	@Override
	public String get(String key) {
		return prop.getProperty(key);
	}

	private void validFolder(File... folders) {
		for (File folder : folders) {
			System.out.println(folder.getAbsolutePath());
			if (!folder.exists() || folder.isFile()) {
				folder.mkdirs();
			}
		}
	}

	private int findIt(String[] arr, String it) {
		int index = 0;
		for (int i = 0; i < arr.length; i++) {
			if (it.equals(arr[i])) {
				index = i;
				break;
			}
		}
		return index;
	}

	private Properties getProp(InputStream in) throws IOException {
		if (prop == null) {
			prop = new Properties();
			prop.load(in);
		}
		return prop;
	}

	public void initProperties(InputStream in) throws IOException {

		this.getProp(in);



		XML_PREFIX = prop.getProperty("XML_PREFIX", "mysql-");



		PROJ_ROOT = prop.getProperty("ROOT_PATH", "E:\\tmp");
		BASE_PACKAGE = prop.getProperty("PATH_PACKAGE", "com.iamVip.it.v1");



		// File packFile = new File(PROJ_ROOT, BASE_PACKAGE.replace(".", File.separator));
		// PACKAGE_FOLDER = packFile.getAbsolutePath();



		String config = prop.getProperty("FOLDER_CONFIG", "config");
		File configFile = new File(PROJ_ROOT, config);
		CONFIG_FOLDER = configFile.getAbsolutePath();



		validFolder(new File(PROJ_ROOT), /* packFile, */configFile);



		Properties properties = new Properties();
		properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, resource2temp("."));
		Velocity.init(properties);

	}

	public void printProperties() throws IOException {
		prop.forEach((key, val) -> {
			System.out.println(key + " --> " + val);
		});
	}

	public void initFolders() throws IOException {
		{ /// prefix with "PATH_PACKAGE_" ///
			File[] folders = new File[KEY_PROP.length];

			for (int i = 0; i < KEY_PROP.length; ++i) {
				String val = prop.getProperty(KEY_PROP[i]);
				String pk = String.format(BASE_PACKAGE_CHILD_FMT, BASE_PACKAGE, val);
				PKG_CLASS[i] = pk;
				PKG_FOLDER[i] = PROJ_ROOT + File.separator + pk.replace(".", File.separator);
				folders[i] = new File(PKG_FOLDER[i]);
			}

			validFolder(folders);
		}


		final String PATH_PREFIX = "PATH_PACKAGE_";

		prop.keySet().forEach(okey -> {
			String key = okey.toString();

			if (key.startsWith(PATH_PREFIX)) {
				String val = prop.getProperty(key, "");
				String pk = String.format(BASE_PACKAGE_CHILD_FMT, BASE_PACKAGE, val);
				validFolder(new File(PROJ_ROOT + File.separator + pk.replace(".", File.separator)));
			}
		});



		{ /// prefix with "FOLDER_CONFIG_" ///
			File[] folders = new File[KEY_FOLDER.length];

			for (int i = 0; i < KEY_FOLDER.length; ++i) {
				String val = prop.getProperty(KEY_FOLDER[i]);
				VAL_FOLDER[i] = CONFIG_FOLDER + File.separator + val;
				folders[i] = new File(VAL_FOLDER[i]);
			}

			validFolder(folders);
		}


		// final String FOLDER_PREFIX = "FOLDER_CONFIG_";
		//
		// prop.keySet().forEach(okey -> {
		// String key = okey.toString();
		//
		// if (key.startsWith(FOLDER_PREFIX)) {
		// String val = prop.getProperty(key, "");
		// validFolder(new File(CONFIG_FOLDER, val));
		// }
		// });
	}

	// private int iii = 0;

	private String resource2temp(String res) throws IOException {
		// InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(res);
		//
		// File tmp = File.createTempFile("Resource", ".vm");
		// FileOutputStream outputStream = new FileOutputStream(tmp);
		//
		// byte[] buf = new byte[1024 * 100];
		// int len = 0;
		// while ((len = inputStream.read(buf)) > 0) {
		// outputStream.write(buf, 0, len);
		// }
		// inputStream.close();
		// outputStream.close();
		// return tmp.getName();

		ClassLoader cl = this.getClass().getClassLoader();
		URL url = cl.getResource(res);
		// ++iii;
		// if (iii == 14) {
		// System.out.println(url.getPath());
		// }
		File f = new File(url.getPath());
		return f.getAbsolutePath();
	}

	public void cpTemplate() throws Exception {
		this.cpTemplate("mybatis");
	}

	public void cpTemplate(String folder) throws Exception {

		VelocityContext context = new VelocityContext();

		for (int i = 0; i < KEY_VELOCITY.length; i++) {
			context.put(KEY_VELOCITY[i], PKG_CLASS[i]);
		}

		// String pkg = this.getClass().getName();
		// pkg = pkg.substring(0, pkg.lastIndexOf("."));


		String PREFIX = "TEMPLATE_", SUFFIX = "_TO";

		for (Object okey : prop.keySet()) {
			String key = okey.toString();

			if (key.startsWith(PREFIX) && key.endsWith(SUFFIX)) {
				String toName = key.substring(PREFIX.length(), key.lastIndexOf(SUFFIX));

				String valKey = prop.getProperty(key);
				int index = findIt(KEY_PROP, valKey);
				String toFolder = PKG_FOLDER[index];

				// String templatePath = resource2temp(pkg.replace(".", "/") + "/" + toName + ".bin");
				String templatePath = "template/" + toName + ".vm"; // resource2temp("template/" + toName.replace("_", "") + ".vm"); // resource2temp("template/" + toName + ".bin");

				if (!Velocity.resourceExists(templatePath)) {
					templatePath = "template/" + folder + toName + ".vm";
				}

				if (Velocity.resourceExists(templatePath)) {

					Template template = Velocity.getTemplate(templatePath, "UTF-8");

					File toFile = new File(toFolder, toName);
					FileWriter writer = new FileWriter(toFile);

					template.merge(context, writer);

					writer.close();
				}
				else {
					System.err.format("Can't find resource '%s'. \r\n", templatePath);
				}



			}
		}
	}



	/**
	 * @param list
	 * @param rule
	 * @throws Exception
	 */
	public void toModel(List<TableDefine> list, ITableBeanName rule) throws Exception {

		// String pkg = this.getClass().getName();
		// pkg = pkg.substring(0, pkg.lastIndexOf("."));
		// String templatePath = resource2temp(pkg.replace(".", "/") + "/__Bean.java.bin");

		String templatePath = "template/" + "_Bean.java.vm"; // resource2temp("template/Bean.java.vm");

		if (Velocity.resourceExists(templatePath)) {

			int index = findIt(KEY_PROP, "PATH_PACKAGE_MODEL");
			String toFolder = PKG_FOLDER[index];
			String package_model = PKG_CLASS[index];

			Template template = Velocity.getTemplate(templatePath, "UTF-8");

			VelocityContext context = new VelocityContext();
			context.put("package_model", package_model);

			for (TableDefine td : list) {

				String bean = rule != null ? rule.translateIt(td.name) : td.name;
				File toFile = new File(toFolder, bean + "Model.java");

				context.put("comment", td.comment == null ? "" : td.comment);
				context.put("table", td.name);
				context.put("bean", bean);
				context.put("cols", td.columns);

				FileWriter writer = new FileWriter(toFile);

				template.merge(context, writer);

				writer.close();

			}

		}
		else {
			System.err.format("Can't find resource '%s'. \r\n", templatePath);
		}

	}

	/**
	 * @param list
	 * @param rule
	 * @throws Exception
	 */
	public void toIDB(List<TableDefine> list, ITableBeanName rule) throws Exception {

		String templatePath = "template/" + "_IDBAPI.java.vm";

		if (Velocity.resourceExists(templatePath)) {

			int index = findIt(KEY_PROP, "PATH_PACKAGE_DATABASE");
			String toFolder = PKG_FOLDER[index];
			String package_database = PKG_CLASS[index];

			index = findIt(KEY_PROP, "PATH_PACKAGE_MODEL");
			String package_model = PKG_CLASS[index];

			Template template = Velocity.getTemplate(templatePath, "UTF-8");

			VelocityContext context = new VelocityContext();
			context.put("package_database", package_database);
			context.put("package_model", package_model);

			for (TableDefine td : list) {

				String bean = rule != null ? rule.translateIt(td.name) : td.name;
				File toFile = new File(toFolder, "I" + bean + "DBApi.java");

				context.put("comment", td.comment == null ? "" : td.comment);
				context.put("table", td.name);
				context.put("bean", bean);

				FileWriter writer = new FileWriter(toFile);

				template.merge(context, writer);

				writer.close();

			}

		}
		else {
			System.err.format("Can't find resource '%s'. \r\n", templatePath);
		}

	}

	/**
	 * @param list
	 * @param rule
	 * @throws Exception
	 */
	public void toInterface(List<TableDefine> list, ITableBeanName rule) throws Exception {

		String templatePath = "template/" + "_IAPI.java.vm";

		if (Velocity.resourceExists(templatePath)) {

			int index = findIt(KEY_PROP, "PATH_PACKAGE_IAPI");
			String toFolder = PKG_FOLDER[index];
			String package_iapi = PKG_CLASS[index];

			index = findIt(KEY_PROP, "PATH_PACKAGE_MODEL");
			String package_model = PKG_CLASS[index];

			index = findIt(KEY_PROP, "PATH_PACKAGE_DATABASE");
			String package_database = PKG_CLASS[index];

			Template template = Velocity.getTemplate(templatePath, "UTF-8");

			VelocityContext context = new VelocityContext();
			context.put("package_iapi", package_iapi);
			context.put("package_model", package_model);
			context.put("package_database", package_database);

			for (TableDefine td : list) {

				String bean = rule != null ? rule.translateIt(td.name) : td.name;
				File toFile = new File(toFolder, "I" + bean + "Api.java");

				context.put("comment", td.comment == null ? "" : td.comment);
				context.put("table", td.name);
				context.put("bean", bean);

				FileWriter writer = new FileWriter(toFile);

				template.merge(context, writer);

				writer.close();

			}

		}
		else {
			System.err.format("Can't find resource '%s'. \r\n", templatePath);
		}

	}

	/**
	 * @param list
	 * @param rule
	 * @throws Exception
	 */
	public void toImplement(List<TableDefine> list, ITableBeanName rule) throws Exception {

		String templatePath = "template/" + "_APIDefaultImpl.java.vm";

		if (Velocity.resourceExists(templatePath)) {

			int index = findIt(KEY_PROP, "PATH_PACKAGE_API");
			String toFolder = PKG_FOLDER[index];
			String package_api = PKG_CLASS[index];

			index = findIt(KEY_PROP, "PATH_PACKAGE_IAPI");
			String package_iapi = PKG_CLASS[index];

			index = findIt(KEY_PROP, "PATH_PACKAGE_MODEL");
			String package_model = PKG_CLASS[index];

			index = findIt(KEY_PROP, "PATH_PACKAGE_DATABASE");
			String package_database = PKG_CLASS[index];

			Template template = Velocity.getTemplate(templatePath, "UTF-8");

			VelocityContext context = new VelocityContext();
			context.put("package_api", package_api);
			context.put("package_iapi", package_iapi);
			context.put("package_model", package_model);
			context.put("package_database", package_database);

			for (TableDefine td : list) {

				String bean = rule != null ? rule.translateIt(td.name) : td.name;
				File toFile = new File(toFolder, bean + "ApiImpl.java");

				context.put("comment", td.comment == null ? "" : td.comment);
				context.put("table", td.name);
				context.put("bean", bean);

				FileWriter writer = new FileWriter(toFile);

				template.merge(context, writer);

				writer.close();

			}

		}
		else {
			System.err.format("Can't find resource '%s'. \r\n", templatePath);
		}

	}

	/**
	 * @param list
	 * @param rule
	 * @throws Exception
	 */
	public void toXML(List<TableDefine> list, ITableBeanName rule) throws Exception {

		String templatePath = "template/" + "sql.xml.vm";

		if (Velocity.resourceExists(templatePath)) {

			int index = findIt(KEY_FOLDER, "FOLDER_CONFIG_MYBATIS");
			String toFolder = VAL_FOLDER[index];

			index = findIt(KEY_PROP, "PATH_PACKAGE_MODEL");
			String package_model = PKG_CLASS[index];

			index = findIt(KEY_PROP, "PATH_PACKAGE_DATABASE");
			String package_database = PKG_CLASS[index];

			Template template = Velocity.getTemplate(templatePath, "UTF-8");

			VelocityContext context = new VelocityContext();
			context.put("package_model", package_model);
			context.put("package_database", package_database);

			for (TableDefine td : list) {

				String bean = rule != null ? rule.translateIt(td.name) : td.name;
				File toFile = new File(toFolder, XML_PREFIX + td.name + ".xml");

				context.put("table", td);
				context.put("hasPrimaryKey", (td.primaryKey != null));
				context.put("primaryKey", td.primaryKey);
				context.put("bean", bean);

				FileWriter writer = new FileWriter(toFile);

				template.merge(context, writer);

				writer.close();

			}

		}
		else {
			System.err.format("Can't find resource '%s'. \r\n", templatePath);
		}

	}

	/**
	 * @param list
	 * @param rule
	 * @throws IOException
	 */
	public void toPhoneRequest(List<TableDefine> list, ITableBeanName rule) throws IOException {

		String templatePath = "template/" + "PhoneRequest.java.vm";

		if (Velocity.resourceExists(templatePath)) {

			int index = findIt(KEY_PROP, "PATH_PACKAGE_REQUEST_MOBILE");
			String toFolder = PKG_FOLDER[index];
			String package_mobile = PKG_CLASS[index];

			index = findIt(KEY_PROP, "PATH_PACKAGE_REQUEST");
			String package_request = PKG_CLASS[index];

			index = findIt(KEY_PROP, "PATH_PACKAGE_MODEL");
			String package_model = PKG_CLASS[index];

			index = findIt(KEY_PROP, "PATH_PACKAGE_DATABASE");
			String package_database = PKG_CLASS[index];

			index = findIt(KEY_PROP, "PATH_PACKAGE_C");
			String package_c = PKG_CLASS[index];

			Template template = Velocity.getTemplate(templatePath, "UTF-8");

			VelocityContext context = new VelocityContext();
			context.put("package_mobile", package_mobile);
			context.put("package_request", package_request);
			context.put("package_c", package_c);
			context.put("package_model", package_model);
			context.put("package_database", package_database);

			for (TableDefine td : list) {

				String bean = rule != null ? rule.translateIt(td.name) : td.name;
				File toFile = new File(toFolder, bean + "PhoneRequest.java");

				String url = td.name.replace("_", "/");

				context.put("table", td);
				context.put("hasPrimaryKey", (td.primaryKey != null));
				context.put("primaryKey", td.primaryKey);
				context.put("bean", bean);
				context.put("url", url);

				FileWriter writer = new FileWriter(toFile);

				template.merge(context, writer);

				writer.close();

			}

		}
		else {
			System.err.format("Can't find resource '%s'. \r\n", templatePath);
		}

	}

}
