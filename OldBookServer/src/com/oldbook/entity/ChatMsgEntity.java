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
	private String time;//ʱ��
	private String content;//��Ϣ
	private boolean isComMsg = true;// �ж��Ƿ�Է�����

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
	//������
	public int getSender()
	{
		return sender;
	}
	public void setSender(int sender)
	{
		this.sender = sender;
	}
	//������
	public void setReceiver(int receiver)
	{
		this.receiver=receiver;
	}
	public int getReceiver()
	{
		return receiver;
	}
	//��Ϣʱ��
	public String getTime()
    {
		return time;
	}
	public void setTime(String time) 
	{
		this.time = time;
	}
	//��Ϣ����
	public String getContent()
	{
		return content;
	}
	public void setContent(String content)
	{
		this.content= content;
	}
	//��Ϣ����
	public boolean getMsgType()
	{
		return isComMsg;
	}
	public void setMsgType(boolean isComMsg)
	{
		this.isComMsg = isComMsg;
	}
	//ͷ��
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