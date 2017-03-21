package com.oldbook.android.entity;

import java.io.Serializable;

/**

 * @author Administrator
 *
 */
public class ChatMsgEntity implements Serializable
{
	private static final long serialVersionUID = 3L;

	private int sender;
	private int receiver;
	private String petNameSend;
	private String petNameReceive;
	private String time;//时间
	private String content;//信息
	private boolean isComMsg = true;// 判断是否对方来信

	public ChatMsgEntity(int sender,int receiver,String petNameSend,String petNameReceive,String time,String content,boolean isComMsg)
	{
		this.sender=sender;
		this.receiver=receiver;
		this.time=time;
		this.petNameSend=petNameSend;
		this.petNameReceive=petNameReceive;
		this.isComMsg=isComMsg;
		this.content=content;

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
	public String getContent() {
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
	public void setPetNameSend(String petNameSend)
	{
		this.petNameSend=petNameSend;
	}
	public String getPetNameSend()
	{
		return petNameSend;
	}
	public void setPetNameReceive(String petNameReceive)
	{
		this.petNameReceive=petNameReceive;
	}
	public String getPetNameReceive()
	{
		return petNameReceive;
	}


}
