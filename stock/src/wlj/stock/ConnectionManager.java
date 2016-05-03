package wlj.stock;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {

	public static Connection getConnection() throws SQLException, ClassNotFoundException, IOException {
		Class.forName("com.mysql.jdbc.Driver");
		Properties properties = new Properties();
		properties.load(ConnectionManager.class.getResourceAsStream("jdbc.properties"));
		return DriverManager.getConnection(properties.getProperty("jdbc.url"), properties.getProperty("jdbc.user"),
				properties.getProperty("jdbc.password"));
	}
}
