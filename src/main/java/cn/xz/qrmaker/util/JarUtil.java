package cn.xz.qrmaker.util;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * jar 文件工具类
 * @author gsx
 *
 */
public class JarUtil {
	
	private static Logger logger = LoggerFactory.getLogger(JarUtil.class);
	
	/**
	 * 获取 jar 文件所在路径
	 * @param clazz
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getJarpath(Class<?> clazz) throws UnsupportedEncodingException{
		String path = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
		path = java.net.URLDecoder.decode(path, "UTF-8");
		logger.info(path);
		int firstIndex = path.lastIndexOf(System.getProperty("path.separator")) + 1;
		int lastIndex = path.lastIndexOf(File.separator) + 1;
		logger.info(path.substring(firstIndex, lastIndex));
		return path;
	}

}
