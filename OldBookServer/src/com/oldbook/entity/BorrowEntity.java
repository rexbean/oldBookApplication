package com.oldbook.entity;

import java.io.Serializable;

/**
  * @author Administrator
 *
 */
public class BorrowEntity implements Serializable
{
	private static final long serialVersionUID = 5L;

	private int fromUser;
	private int getUser;
	private String borrowTime;//时间
	private String bookName;//书号
	private String returnTime;
	private String sreturnTime;
	private MessageType statue;
	private int evaluation;

	public BorrowEntity(int fromUser,int getUser,String borrowTime,String bookName ,String sreturnTime,String returnTime,MessageType statue,int evaluation)
	{
		this.fromUser=fromUser;
		this.getUser=getUser;
		this.borrowTime=borrowTime;
		this.bookName=bookName;
		this.returnTime=returnTime;
		this.sreturnTime=sreturnTime;
		this.statue=statue;
		this.evaluation=evaluation;
	}
	public BorrowEntity()
	{

	}
	//拥有者
	public int getFromUser()
	{
		return fromUser;
	}
	public void setFromUser(int fromUser)
	{
		this.fromUser = fromUser;
	}
	//借阅者
	public void setGetUser(int getUser)
	{
		this.getUser=getUser;
	}
	public int getGetUser()
	{
		return getUser;
	}
	//书号
	public String getBookName()
	{
		return bookName;
	}
	public void setBookName(String bookName)
	{
		this.bookName=bookName;
	}
	//借出时间
	public String getBorrowTime()
	{
		return borrowTime;
	}
	public void setBorrowTime(String borrowTime)
	{
		this.borrowTime = borrowTime;
	}

	//应还时间
	public String getSReturnTime()
	{
		return sreturnTime;
	}
	public void setSReturnTime(String sreturnTime)
	{
		this.sreturnTime= sreturnTime;
	}

	//归还时间
	public String getReturnTime()
	{
		return returnTime;
	}
	public void setReturnTime(String returnTime)
	{
		this.returnTime = returnTime;
	}

	//状态
	public MessageType getStatue()
	{
		return statue;
	}
	public void setStatue(MessageType statue)
	{
		this.statue=statue;
	}
	//评价
	public void setEvaluation(int evaluation)
	{
		this.evaluation=evaluation;
	}
	public int getEvaluation()
	{
		return evaluation;
	}

}
