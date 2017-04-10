package cn.xz.qrmaker.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.xz.qrmaker.config.Resources;
import cn.xz.qrmaker.entity.UrlLog;

public class SQLIteUtil {
	private static Logger logger = LoggerFactory.getLogger(SQLIteUtil.class);
	
	private static Connection conn;
	private static String dbPath = SQLIteUtil.class.getResource("").getPath() + Resources.DB_NAME;

	public static Connection getConn() throws SQLException{
		if (null == conn || conn.isClosed()) {
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath, null, null);
		}
		return conn;
	}
	
	static {
		try {
			Class.forName("org.sqlite.JDBC");
			logger.info(dbPath);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "加载sqlite驱动失败，请检查！", "初始化错误！", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * 执行一个 sql 语句
	 * @param sql
	 * @throws SQLException
	 */
	private static void exc(String sql) throws SQLException{
		Connection connection = getConn();
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.executeUpdate();
		ps.close();
		connection.close();
	}
	
	/**
	 * 要求 sql 语句只查询一个字段，即总数
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	private static Integer count(String sql) throws SQLException{
		Connection connection = getConn();
		PreparedStatement ps = connection.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		int ret = -1;
		if (rs.next()) {
			ret = rs.getInt(1);
		}
		ps.close();
		connection.close();
		return ret;
	}

	/**
	 * 初始化 db 中的 log 表
	 * @param dbPath
	 * @throws SQLException
	 */
	public static void initDB(String dbPath) throws SQLException{
		String sql = "CREATE TABLE IF NOT EXISTS log(id integer PRIMARY KEY autoincrement, url varchar(2000))";
		exc(sql);
	}
	
	/**
	 * 清空 log 表
	 * @param dbPath
	 * @throws SQLException
	 */
	public static void clearDB() throws SQLException{
		String sql = "delete from log";
		exc(sql);
	}
	
	/**
	 * 插入一个 log 记录
	 * @param url
	 * @throws SQLException
	 */
	public static void insertLog(String url) throws SQLException{
		String sql = "INSERT INTO log(url) VALUES('" + url + "')";
		exc(sql);
	}
	
	public static Integer getCount() throws SQLException{
		String sql = "select count(1) from log";
		return count(sql);
	}
	
	/**
	 * 查找所有记录，按照 id 倒序排列
	 * @return
	 * @throws SQLException
	 */
	public static List<UrlLog> listAll() throws SQLException{
		String sql = "select * from log order by id desc";
		List<UrlLog> ret = new ArrayList<UrlLog>();
		Connection connection = getConn();
		PreparedStatement ps = connection.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Integer id = rs.getInt("id");
			String url = rs.getString("url");
			UrlLog log = new UrlLog();
			log.setId(id);
			log.setUrl(url);
			ret.add(log);
		}
		rs.close();
		ps.close();
		connection.close();
		return ret;
	}

	/**
	 * 检测表中是否已存在此记录
	 * @param url
	 * @return
	 * @throws SQLException
	 */
	public static boolean check(String url) throws SQLException {
		String sql = "select count(1) from log where url='" + url + "'";
		if (1 > count(sql)) {
			return false;
		}
		return true;
	}
}
