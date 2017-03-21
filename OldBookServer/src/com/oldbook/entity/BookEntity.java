package com.oldbook.entity;

import java.io.Serializable;

/**
 * �?��消息的JavaBean
 * 
 * @author way
 * 
 */
public class BookEntity implements Serializable
{
	private static final long serialVersionUID = 4L;

	private int id;   
	private int owner;
	private String bookName;
	private int bookSurface;
	private String bookPublishing;//������
	private String bookNumber;//���
	private String bookAuthor;//����
	private int size;

	public BookEntity(int id,String bookName,String bookAuthor,String bookPublishing,String bookNumber,int owner,int size)
	{
		this.id=id;
		this.owner=owner;
		this.bookName=bookName;
		this.bookAuthor=bookAuthor;
		this.bookPublishing=bookPublishing;
		this.bookNumber=bookNumber;
		this.size=size;
	}
	public BookEntity()
	{
		
	}
	
	//���
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	
	//ӵ����
	public void setOwner(int owner)
	{
		this.owner=owner;
	}
	public int getOwner()
	{
		return owner;
	}
	
	//����
	public String getBookName()
    {
		return bookName;
	}
	public void setBookName(String bookName) 
	{
		this.bookName = bookName;
	}
	
	//�鼮����
	public String getBookAuthor() {
		return bookAuthor;
	}
	public void setBookAuthor(String bookAuthor)
	{
		this.bookAuthor= bookAuthor;
	}
	
	//������
	public String getBookPublishing()
	{
		return bookPublishing;
	}
	public void setBookPublishing(String bookPublishing)
	{
		this.bookPublishing = bookPublishing;
	}
	
	//���
	public void setBookNumber(String bookNumber)
	{
		this.bookNumber=bookNumber;
	}
	public String getbookNumber()
	{
		return bookNumber;
	}
	//����
	public void setBookSurface(int bookSurface)
	{
		this.bookSurface=bookSurface;
	}
	public int getBookSurface()
	{
		return bookSurface;
	}
	//��С
	public void setSize(int size)
	{
		this.size=size;
	}
	public int getSize()
	{
		return size;
	}

}

