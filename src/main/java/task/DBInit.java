package task;

import util.DBUtil;

import java.io.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;

/**
 * 1.初始化数据库，数据库约定好，放在targe/everything-like.db
 * 2.读取 sql 文件
 * 3.执行 sql 语句来初始化表格
 */

public class DBInit {

    private static String[] readSQL() {
            try {
                // 通过 ClassLoader 获取流，或者通过FileInputStream 获取
                InputStream is = DBInit.class.getClassLoader().getResourceAsStream("init.sql");
                // 字节流转换为字符流：需要通过字节字符转换流来操作
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains("--")) {
                        // 去掉两根 -- 注释的代码
                        line = line.substring(0, line.indexOf("--"));
                    }
                    sb.append(line);
                }
                String[] sqls = sb.toString().split(";");
                return sqls;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("读取sql文件错误", e);
            }
    }

    public static void init() {
        // 数据库JDBC操作： sql 语句的执行
        Connection connection = null;
        Statement statement = null;
        try {
            // 1.建立数据库连接 Connection
            connection = DBUtil.getConnection();
            // 2.创建 sql 语句执行对象 Statement
            statement = connection.createStatement();
            String[] sqls = readSQL();
            for (String sql : sqls) {
                // 3.执行 sql 语句
                statement.executeUpdate(sql);
            }
            // 4.如果是查询操作，获取结果集 ResultSet，处理结果集
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("初始化数据库表操作失败", e);
        } finally {
            // 5.释放资源
            DBUtil.close(connection, statement);
        }
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(readSQL()));
        init();
    }
}
