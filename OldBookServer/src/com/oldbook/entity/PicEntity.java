package com.oldbook.entity;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;

/**
 *
 *
 * @author Administrator
 *
 */
public class PicEntity implements Serializable
{
	private static final long serialVersionUID = 6L;

	private int id;
	private String type;
	private long size;
	private InputStream reader;
	private String fileName;

	public PicEntity(int id,String type,int size,InputStream reader)
	{
		this.id=id;
		this.type=type;
		this.size=size;
		this.reader=reader;
	}
	public PicEntity()
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

	//����
	public String getType()
	{
		return type;
	}
	public void setType(String type)
	{
		this.type=type;
	}

	//��С
	public void setSize(long size)
	{
		this.size=size;
	}
	public long getSize()
	{
		return size;
	}

	//�ļ�
	public void setFile(InputStream reader)
	{
		this.reader=reader;
	}
	public InputStream getFile()
	{
		return reader;
	}
	
	//�ļ���
		public void setFileName(String fileName)
		{
			this.fileName=fileName;
		}
		public String getFileName()
		{
			return fileName;
		}

}
