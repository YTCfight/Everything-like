package util;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {

    private static volatile DataSource DATA_SOURCE;

    /**
     * 提供获取数据库连接池的功能
     * 使用单例模式(多线程版本)
     * @return
     */
    private static DataSource getDataSource() {
        if (DATA_SOURCE == null) {
            synchronized (DBUtil.class) {
                if (DATA_SOURCE == null) {
                    // 初始化操作
                    SQLiteConfig config = new SQLiteConfig();
                    config.setDateStringFormat(Util.DATE_PATTERN);
                    DATA_SOURCE = new SQLiteDataSource(config);
                    ((SQLiteDataSource)DATA_SOURCE).setUrl(getUrl());
                }
            }
        }
        return DATA_SOURCE;
    }

    /**
     * 获取 sqlite 数据库文件 url 的方法
     * @return
     */
    private static String getUrl() {
        try {
            // 获取 target 编译文件夹的路径
            // 通过classLoader.getResource() / classLoader.getResourceAsStream()
            // 默认根路径为编译文件夹(target/classes)
            URL classesURL = DBUtil.class.getClassLoader().getResource("./");
            // 获取 target/classes 文件夹的父目录路径
            String dir = new File(classesURL.getPath()).getParent();
            String url = "jdbc:sqlite://" + dir + File.separator + "everything-like.db";
            // new sqliteDataSource(),把这个对象的url设置进去，才会创建这个文件
            // 如果已经存在，就会读取这个文件
            url = URLDecoder.decode(url, "UTF-8");
            System.out.println("获取数据库文件路径" + url);
            return url;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("h获取数据库文件路径失败", e);
        }
    }


    /**
     * 提供获取数据库连接的方法：
     * 从数据库连接池 DataSource.getConnection() 来获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public static void main(String[] args) throws SQLException {
        System.out.println(getConnection());
    }

    public static void close(Connection connection, Statement statement) {
        close(connection, statement, null);
    }

    /**
     * 释放数据库资源
     * @param connection 数据库连接
     * @param statement sql 执行对象
     * @param resultSet 结果集
     */
    public static void close(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("数据库资源释放失败", e);
        }
    }
}
