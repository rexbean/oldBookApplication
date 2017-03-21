package com.oldbook.android.entity;

import java.io.*;

public class UserEntity implements Serializable
{
	private static final long serialVersionUID = 1L;

	private int Id;      			//ID	
	private String username;        //用户名
	private String password;        //密码
	private String petname;         //昵称
	//private int avatar;               //头像

	//String time;

	//ID
	public int getId()
	{
		return Id;
	}
	public void setId(int Id)
	{
		this.Id = Id;
	}
	//Username
	public String getUsername()
	{
		return username;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}
	//password
	public String getPassword()
	{
		return password;
	}
	public void setPassword(String password)
	{
		this.password = password;
	}
	//petname
	public String getPetname()
	{
		return petname;
	}
	public void setPetname(String petname)
	{
		this.petname = petname;
	}
	/*
	//avastar
	public int getAvatar()
	{
		return avatar;
	}
	public void setAvatar(int avatar)
	{
		this.avatar = avatar;
	}
	
	public String getTime()
	{
		return time;
	}
	public void setTime(String time)
	{
		this.time = time;
	}
	
	*/
}
