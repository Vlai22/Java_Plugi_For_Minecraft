package Vlad_Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Sql_DB {
	private static String url = "jdbc:mysql://MySQL-8.2:3306/minecraft_server";
	private static String name = "root";
	private static String pass = "";
	public static void Insert_Update(String query) {
        try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}        
        try (Connection connection = DriverManager.getConnection(url, name, pass);
            Statement statement = connection.createStatement();) {
            statement.executeUpdate(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.print("Error");
        }
	}
	public static String Select_One(String query ) {
        ResultSet resultSet = null;
        try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}        
        try (Connection connection = DriverManager.getConnection(url, name, pass);
            Statement statement = connection.createStatement();) {
            resultSet = statement.executeQuery(query);
            String result = "";
            while(resultSet.next()) {
            	result = resultSet.getString(1);
            } 
            return result;
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.print("Error");
            return null;
        }
	}
	public static ArrayList<ArrayList<String>> Select_Array(String query) {
        ResultSet resultSet = null;
        try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
        try (Connection connection = DriverManager.getConnection(url, name, pass);
                Statement statement = connection.createStatement();) {
                resultSet = statement.executeQuery(query);
                ArrayList<ArrayList<String>> result = new ArrayList<>();
                while(resultSet.next()) {
                	ResultSetMetaData metaData = resultSet.getMetaData();
                	ArrayList<String> array = new ArrayList<>();
                	for(int i=1;i<=metaData.getColumnCount();i++) {
                		String type = metaData.getColumnTypeName(i);
                		switch(type) {
                			case "BIGINT":
                				array.add(String.valueOf(resultSet.getBigDecimal(i)));
                				break;
                			case "INT":
                				array.add(String.valueOf(resultSet.getInt(i)));
                				break;
                			case "TEXT":
                				array.add(resultSet.getString(i));
                				break;
                			default:
                				System.out.print("Error type Sql_Array_Select");
                				break;
                		}
                	}
                	result.add(array);
                }
                return result;
        }
        catch (SQLException e) {
                e.printStackTrace();
                System.out.print("Error");
                return null;
        }
	}
}
