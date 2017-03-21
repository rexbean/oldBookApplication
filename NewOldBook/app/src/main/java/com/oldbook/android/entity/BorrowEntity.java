package com.oldbook.android.entity;

import java.io.Serializable;

/**
  * @author Administrator
 *
 */
public class BorrowEntity implements Serializable
{
	private static final long serialVersionUID = 5L;
	private int borrowId;
	private int fromUser;
	private int getUser;
	private String borrowTime;//时间
	//private BookEntity book;//书号
	private String returnTime;
	private String sreturnTime;
	private String statue;
	private int evaluation;
	private String bookName;
	private int bookId;

	public BorrowEntity(int fromUser,int getUser,String borrowTime,String bookName ,String sreturnTime,String returnTime,String statue,int evaluation)
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
	public BorrowEntity(int borrowId,String bookName,int fromUser,int getUser,String sreturnTime,String statue,int evaluation,int bookId)
	{
		this.borrowId=borrowId;
		this.bookName=bookName;
		this.fromUser=fromUser;
		this.getUser=getUser;
		this.sreturnTime=sreturnTime;
		this.statue=statue;
		this.evaluation=evaluation;
		this.bookId=bookId;
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
	public void setBook(String bookName)
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
	public String getStatue()
	{
		return statue;
	}
	public void setStatue(String statue)
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

	//借阅号
	public void setBorrowId(int borrowId)
	{
		this.borrowId=borrowId;
	}
	public int getBorrowId()
	{
		return borrowId;
	}

	public void setBookId(int bookId)
	{
		this.bookId=bookId;
	}
	public int getBookId()
	{
		return bookId;
	}
}
