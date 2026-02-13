package org.wlpiaoyi.framework.lab.documet.config;

public class Constants {
	/**
	 * 系统分隔符
	 */
	public static String SYSTEM_SEPARATOR = "/";

	/**
	 * 获取项目根目录
	 */
	public static String PROJECT_ROOT_DIRECTORY = System.getProperty("user.dir").replaceAll("\\\\", SYSTEM_SEPARATOR);

	/**
	 * 临时文件相关
	 */
	public final static String DEFAULT_FOLDER_TMP = PROJECT_ROOT_DIRECTORY + "/tmp";
	public final static String DEFAULT_TARGET_TMP_GENERATE = PROJECT_ROOT_DIRECTORY + "/target/tmp-generate";
}
