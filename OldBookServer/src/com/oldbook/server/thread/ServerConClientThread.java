/**
 * ��������ĳ���ͻ��˵�ͨ���߳�
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
				//�Դӿͻ���ȡ�õ���Ϣ���������жϣ�����Ӧ�Ĵ���
				if(message.getType().equals(MessageType.COM_MES))//�������ͨ��Ϣ��
				{
					//ȡ�ý����˵�ͨ���߳�
					ServerConClientThread scc=ManageServerConClient.getClientThread(message.getReceiver());
					ObjectOutputStream oos=new ObjectOutputStream(scc.s.getOutputStream());
					message.setMsgType(true);
					//������˷�����Ϣ
					oos.writeObject(message);
					
				}
				else if(message.getType().equals(MessageType.GET_ONLINE_FRIENDS))//�������������б�
				{
					
					//��ʱ��������string����
					String res=new UserDao().getUser();            //�ĳ������û�
					//���ͺ����б��ͻ���
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
