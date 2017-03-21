package com.oldbook.android.entity;

import java.io.Serializable;
import java.util.List;

public class MessageEntity<T> implements Serializable
{
	private static final long serialVersionUID = 2L;
	private MessageType type;
	private MessageType result;
	private T object;
	private List<T> objectList;
	private int sender;
	private int receiver;

	//消息类型
	public MessageType getType()
	{
		return type;
	}
	public void setType(MessageType type)
	{
		this.type = type;
	}
	//发送者
	public int getSender()
	{
		return sender;
	}
	public void setSender(int sender)
	{
		this.sender = sender;
	}
	//接收者
	public int getReceiver()
	{
		return receiver;
	}
	public void setReceiver(int receiver)
	{
		this.receiver = receiver;
	}
	//内容
	public void setObject(T object)
	{
		this.object=object;
	}
	public T getObject()
	{
		return object;
	}
	//结果
	public void setResult(MessageType result)
	{
		this.result=result;
	}
	public MessageType getResult()
	{
		return result;
	}
	//List
	public void setObjectList(List<T> objectList)
	{
		this.objectList=objectList;
	}
	public List<T> getObjectList()
	{
		return objectList;
	}

}