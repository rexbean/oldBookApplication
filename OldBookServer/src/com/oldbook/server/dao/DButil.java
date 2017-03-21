package com.oldbook.server.dao;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.sql.Connection;
import java.util.Properties;

import com.mysql.jdbc.*;


public class DButil
{
	/**
	 * �������ݿ�
	 * 
	 * @return ���ݿ����Ӷ���
	 */
	public static Connection connect()
	{
		Properties pro = new Properties();
		String driver = null;
		String url = null;
		String username = null;
		String password = null;
		try {
			InputStream is = DButil.class.getClassLoader()
					.getResourceAsStream("DB.properties");
			// System.out.println(is.toString());
			pro.load(is);
			driver = pro.getProperty("driver");
			url = pro.getProperty("url");
			username = pro.getProperty("username");
			password = pro.getProperty("password");
//			 System.out.println(driver + ":" + url + ":" + username + ":"
//			 + password);
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, username,
					password);
			return conn;
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}



	/**
	 * �ر����ݿ�
	 * 
	 * @param conn
	 *            �������ݿ����Ӷ���
	 */
	public static void close(Connection con)
	{
		if (con != null)
		{
			try
			{
				con.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
}
