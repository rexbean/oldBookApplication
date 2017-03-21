/**
 * 服务器和某个客户端的通信线程
 */
package com.oldbook.server.thread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import com.oldbook.entity.MessageEntity;
import com.oldbook.entity.MessageType;
import com.oldbook.server.control.ManageServerConClient;
import com.oldbook.server.dao.UserDao;


public class ServerConClientThread extends Thread
{/*
	Socket s;
	public ServerConClientThread(Socket s)
	{
		this.s=s;
	}

	public void run()
	{
		while(true)
		{
			ObjectInputStream ois = null;
			MessageEntity message = null;
			try
			{
				ois=new ObjectInputStream(s.getInputStream());
				message=(MessageEntity) ois.readObject();
				//对从客户端取得的消息进行类型判断，做相应的处理
				if(message.getType().equals(MessageType.COM_MES))//如果是普通消息包
				{
					//取得接收人的通信线程
					ServerConClientThread scc=ManageServerConClient.getClientThread(message.getReceiver());
					ObjectOutputStream oos=new ObjectOutputStream(scc.s.getOutputStream());
					message.setMsgType(true);
					//向接收人发送消息
					oos.writeObject(message);
					
				}
				else if(message.getType().equals(MessageType.GET_ONLINE_FRIENDS))//如果是请求好友列表
				{
					
					//暂时将结果揉成string类型
					String res=new UserDao().getUser();            //改成在线用户
					//发送好友列表到客户端
					ServerConClientThread scc=ManageServerConClient.getClientThread(message.getSender());
					ObjectOutputStream oos=new ObjectOutputStream(scc.s.getOutputStream());
					MessageEntity ms=new MessageEntity();
					ms.setType(MessageType.RET_ONLINE_FRIENDS);
					ms.setContent(res);
					System.out.println(res);
					oos.writeObject(ms);
				}
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				try
				{
					s.close();
					ois.close();
				}
				catch (IOException e1)
				{
					
				}
			}
		}
	}*/
}
