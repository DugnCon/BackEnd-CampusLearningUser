package com.javaweb.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application-uat.properties")
public class JDBC {
    private static String url;
    private static String username;
    private static String password;

    @Value("${spring.datasource.url}")
    public void setUrl(String url) {
        JDBC.url = url;
    }

    @Value("${spring.datasource.username}")
    public void setUsername(String username) {
        JDBC.username = username;
    }

    @Value("${spring.datasource.password}")
    public void setPassword(String password) {
        JDBC.password = password;
    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    public static void closeConnection(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
