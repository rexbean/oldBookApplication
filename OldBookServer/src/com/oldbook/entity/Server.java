package com.oldbook.entity;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




import com.oldbook.entity.UserEntity;
import com.oldbook.server.control.ManageServerConClient;
import com.oldbook.server.dao.*;
import com.oldbook.server.net.InputThread;
import com.oldbook.server.net.InputThread_pic;
import com.oldbook.server.net.OutputThread;
import com.oldbook.server.net.OutputThreadMap;
import com.oldbook.server.net.SocketDownload;
import com.oldbook.server.net.SocketUpload;
import com.oldbook.server.thread.ServerConClientThread;
import com.oldbook.server.util.MyDate;
import com.server.oldbook.Constants;


/**
 * �������������û���¼�����ߡ�ת����Ϣ
 * 
 * @author Administrator
 * 
 */
	public class Server
	{
		private ExecutorService executorService;// �̳߳�
		private ServerSocket serverSocket = null;
		//private ServerSocket serverSocket_pic=null;
		private Socket socket = null;
		//private Socket socket_pic=null;
		private boolean isStarted = true;

		public Server()
		{
			try {
				// �����̳߳أ����о���(cpu����*50)���߳�
				executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
						.availableProcessors() * 50);
				serverSocket = new ServerSocket(Constants.PORT);
				//serverSocket_pic = new ServerSocket(Constants.PORT_PIC);
			} catch (IOException e) {
				e.printStackTrace();
				quit();
			}
		}

		public void start()
		{
			System.out.println(MyDate.getDateCN() + " ������������...");
			
			try
			{
				// �жϿͻ�����������  
	            DataInputStream in;
				while (isStarted)
				{
					socket = serverSocket.accept();
					
					
					String ip = socket.getInetAddress().toString();
					System.out.println(MyDate.getDateCN() + " �û���" + ip + " �ѽ�������");
					// Ϊ֧�ֶ��û��������ʣ������̳߳ع���ÿһ���û�����������
					if (socket.isConnected())
						executorService.execute(new SocketTask(socket));// ��ӵ��̳߳�
					
					//socket_pic=serverSocket_pic.accept();
					System.out.println("������");
					//if(socket_pic == null || socket_pic.isClosed())
						//continue;  
					
					//if(socket_pic.isConnected())
						//executorService.execute(new SocketTask_pic(socket_pic));
					
					
				}
				if (socket != null)
					socket.close();
				if (serverSocket != null)
					serverSocket.close();
			} catch (IOException e) 
			{
				System.out.println(e.toString());
				e.printStackTrace();
				// isStarted = false;
			}
		}

		private final class SocketTask implements Runnable 
		{
			private Socket socket = null;
			private InputThread in;
			private OutputThread out;
			private OutputThreadMap map;

			public SocketTask(Socket socket) 
			{
				this.socket = socket;
				map = OutputThreadMap.getInstance();
			}

			@Override
			public void run() {
				out = new OutputThread(socket, map);//
				// ��ʵ����д��Ϣ�߳�,���Ѷ�Ӧ�û���д�̴߳���map�������У�
				in = new InputThread(socket, out, map);// ��ʵ��������Ϣ�߳�
				out.setStart(true);
				in.setStart(true);
				in.start();
				out.start();
			}
		}

		private final class SocketTask_pic implements Runnable {
			private Socket socket = null;
			private InputThread_pic in;
			private OutputThread out;
			private OutputThreadMap map;

			public SocketTask_pic(Socket socket) {
				this.socket = socket;
				map = OutputThreadMap.getInstance();
			}

			@Override
			public void run() {
				out = new OutputThread(socket, map);//
				// ��ʵ����д��Ϣ�߳�,���Ѷ�Ӧ�û���д�̴߳���map�������У�
				in = new InputThread_pic(socket, out, map);// ��ʵ��������Ϣ�߳�
				out.setStart(true);
				in.setStart(true);
				in.start();
				out.start();
			}
		}
		
		
		
		
		
		
		/**
		 * �˳�
		 */
		public void quit() {
			try {
				this.isStarted = false;
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public static void main(String[] args) {
			new Server().start();
		}
	}

