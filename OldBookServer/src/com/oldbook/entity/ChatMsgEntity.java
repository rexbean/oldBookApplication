package com.oldbook.entity;

import java.io.Serializable;

/**
 * 
 * 
 * @author Administrator
 * 
 */
public class ChatMsgEntity implements Serializable
{
	private static final long serialVersionUID = 3L;

	private int sender;   
	private int receiver;
	private int avastarSend;
	private int avastarReceive;
	private String time;//时间
	private String content;//信息
	private boolean isComMsg = true;// 判断是否对方来信

	public ChatMsgEntity(int sender,int receiver,int avastarSend, int avastarReceive,String time,String content,boolean isComMsg)
	{
		this.sender=sender;
		this.receiver=receiver;
		this.time=time;
		this.avastarSend=avastarSend;
		this.avastarReceive=avastarReceive;
		this.isComMsg=isComMsg;
		
	}
	public ChatMsgEntity()
	{
		
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
	public void setReceiver(int receiver)
	{
		this.receiver=receiver;
	}
	public int getReceiver()
	{
		return receiver;
	}
	//消息时间
	public String getTime()
    {
		return time;
	}
	public void setTime(String time) 
	{
		this.time = time;
	}
	//消息内容
	public String getContent()
	{
		return content;
	}
	public void setContent(String content)
	{
		this.content= content;
	}
	//消息方向
	public boolean getMsgType()
	{
		return isComMsg;
	}
	public void setMsgType(boolean isComMsg)
	{
		this.isComMsg = isComMsg;
	}
	//头像
	public void setAvastar(int avastarSend)
	{
		this.avastarSend=avastarSend;
	}
	public int getAvastarSend()
	{
		return avastarSend;
	}
	public void setAvastarReceive(int avastarReceive)
	{
		this.avastarReceive=avastarReceive;
	}
	public int getAvastarReceive()
	{
		return avastarReceive;
	}


}