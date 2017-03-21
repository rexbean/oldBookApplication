package com.oldbook.server.dao.impl;

import com.oldbook.server.dao.UserDao;


public class UserDaoFactory {
	private static UserDao dao;

	public static UserDao getInstance() {
		if (dao == null) {
			dao = new UserDaoImpl();
		}
		return dao;
	}
}
