package com.oldbook.entity;

import java.io.Serializable;

/**
 * 涓?釜娑堟伅鐨凧avaBean
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
	private String bookPublishing;//出版社
	private String bookNumber;//版次
	private String bookAuthor;//作者
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
	
	//书号
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	
	//拥有者
	public void setOwner(int owner)
	{
		this.owner=owner;
	}
	public int getOwner()
	{
		return owner;
	}
	
	//书名
	public String getBookName()
    {
		return bookName;
	}
	public void setBookName(String bookName) 
	{
		this.bookName = bookName;
	}
	
	//书籍作者
	public String getBookAuthor() {
		return bookAuthor;
	}
	public void setBookAuthor(String bookAuthor)
	{
		this.bookAuthor= bookAuthor;
	}
	
	//出版社
	public String getBookPublishing()
	{
		return bookPublishing;
	}
	public void setBookPublishing(String bookPublishing)
	{
		this.bookPublishing = bookPublishing;
	}
	
	//版次
	public void setBookNumber(String bookNumber)
	{
		this.bookNumber=bookNumber;
	}
	public String getbookNumber()
	{
		return bookNumber;
	}
	//封面
	public void setBookSurface(int bookSurface)
	{
		this.bookSurface=bookSurface;
	}
	public int getBookSurface()
	{
		return bookSurface;
	}
	//大小
	public void setSize(int size)
	{
		this.size=size;
	}
	public int getSize()
	{
		return size;
	}

}

