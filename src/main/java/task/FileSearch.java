package task;

import app.FileMeta;
import util.DBUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FileSearch {

    public static List<FileMeta> search(String dir, String content) {
        List<FileMeta> metas = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = DBUtil.getConnection();
            String sql = "select name, path, is_directory, size, last_modified from file_meta where (path = ? " +
                    "or path like ?)";
            if (content != null && content.trim().length() != 0) {
                sql += " and (name like ? or pinyin like ? or pinyin_first like ?)";
            }
            ps = connection.prepareStatement(sql);
            // 对占位符设值
            // 匹配子文件和子文件夹
            ps.setString(1, dir);
            // 匹配孙子文件
            ps.setString(2, dir + File.separator + "%");
            if (content != null && content.trim().length() != 0) {
                ps.setString(3, "%" + content + "%");
                ps.setString(4, "%" + content + "%");
                ps.setString(5, "%" + content + "%");
            }
            // 执行 sql 语句
            rs = ps.executeQuery();
            // 处理结果集
            while (rs.next()) {
                String name = rs.getString("name");
                String path = rs.getString("path");
                Boolean isDirectory = rs.getBoolean("is_directory");
                Long size = rs.getLong("size");
                Timestamp lastModified = rs.getTimestamp("last_modified");
                FileMeta meta = new FileMeta(name, path, isDirectory, size, new java.util.Date(lastModified.getTime()));
                metas.add(meta);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("数据库文件查询失败，路径：" + dir + ",搜索内容：" + content, e);
        } finally {
            DBUtil.close(connection, ps, rs);
        }
        return metas;
    }

}
