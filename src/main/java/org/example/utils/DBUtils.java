package org.example.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtils {

    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    static {
        try {
            Properties properties = new Properties();
            InputStream is = DBUtils.class.getClassLoader().getResourceAsStream("db.properties");
            properties.load(is);

            driver = properties.getProperty("driver");
            url = properties.getProperty("url");
            username = properties.getProperty("username");
            password = properties.getProperty("password");

            Class.forName(driver);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 获取连接
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ===================== 新增：关闭连接方法 =====================
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}