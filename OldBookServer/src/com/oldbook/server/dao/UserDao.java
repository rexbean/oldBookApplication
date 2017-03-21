package com.oldbook.server.dao;
import com.oldbook.entity.BookEntity;
import com.oldbook.entity.BorrowEntity;
import com.oldbook.entity.MessageType;
import com.oldbook.entity.UserEntity;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;


public interface UserDao
{
	//注册成功返回用户id
	public int register(UserEntity u);

	public UserEntity login(UserEntity u);

	public String refresh();
	//public void logout(int id);
	public String getOneInfor(int id);
	public String getEvaluation(int id);
	public int addNewBook(BookEntity be);
	public MessageType setAvatar(String filePath,int id);
	public MessageType setSurface(String filePath,int id);
	public InputStream getSurface(int id);
	public InputStream getAvatar(int id);
	public String bookList();
	public String initial();
	public String Borrow(BorrowEntity boe);
	public String BorrowList(int id);
	public String LendList(int id);
	public String Return(String bookName);
	public String Evaluation(String bookName,int evaluation);
	
}
