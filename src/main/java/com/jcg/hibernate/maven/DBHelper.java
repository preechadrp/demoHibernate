package com.jcg.hibernate.maven;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBHelper {

	public static final String driverName = "org.mariadb.jdbc.Driver";
	public static final String urlConnect = "jdbc:mariadb://localhost:8094/tutorialDb";
	public static final String userName = "root";
	public static final String passWord = "a1234";

	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName(DBHelper.driverName);
			conn = DriverManager.getConnection(DBHelper.urlConnect, DBHelper.userName, DBHelper.passWord);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	
}
