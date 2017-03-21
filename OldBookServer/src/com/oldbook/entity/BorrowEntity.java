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
	private String borrowTime;//ʱ��
	private String bookName;//���
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
	//ӵ����
	public int getFromUser()
	{
		return fromUser;
	}
	public void setFromUser(int fromUser)
	{
		this.fromUser = fromUser;
	}
	//������
	public void setGetUser(int getUser)
	{
		this.getUser=getUser;
	}
	public int getGetUser()
	{
		return getUser;
	}
	//���
	public String getBookName()
	{
		return bookName;
	}
	public void setBookName(String bookName)
	{
		this.bookName=bookName;
	}
	//���ʱ��
	public String getBorrowTime()
	{
		return borrowTime;
	}
	public void setBorrowTime(String borrowTime)
	{
		this.borrowTime = borrowTime;
	}

	//Ӧ��ʱ��
	public String getSReturnTime()
	{
		return sreturnTime;
	}
	public void setSReturnTime(String sreturnTime)
	{
		this.sreturnTime= sreturnTime;
	}

	//�黹ʱ��
	public String getReturnTime()
	{
		return returnTime;
	}
	public void setReturnTime(String returnTime)
	{
		this.returnTime = returnTime;
	}

	//״̬
	public MessageType getStatue()
	{
		return statue;
	}
	public void setStatue(MessageType statue)
	{
		this.statue=statue;
	}
	//����
	public void setEvaluation(int evaluation)
	{
		this.evaluation=evaluation;
	}
	public int getEvaluation()
	{
		return evaluation;
	}

}
