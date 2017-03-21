package com.oldbook.server.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.oldbook.api.OldbookApplication;
import com.oldbook.entity.BookEntity;
import com.oldbook.entity.ChatMsgEntity;
import com.oldbook.entity.MessageEntity;
import com.oldbook.entity.MessageType;
import com.oldbook.entity.UserEntity;
import com.oldbook.server.dao.UserDao;
import com.oldbook.server.dao.impl.UserDaoFactory;
import com.oldbook.server.util.MyDate;


/**
 * 读消息线程和处理方法
 * 
 * @author way
 * 
 */
public class InputThread_pic extends Thread
{
	private Socket socket;// socket对象
	private OutputThread out1;// 传递进来的写消息线程，因为我们要给用户回复消息啊
	private OutputThreadMap map;// 写消息线程缓存器
	private DataInputStream ois;// 对象输入流
	private DataOutputStream out;
	private boolean isStart = true;// 是否循环读消息
	private int bookId;
	
	private UserDao dao;  
    // 保存路径  
    private String savepath="d:/oldBook/";  
    private int id;
    //返回路径(相对路径)  
    //private String filepath="/"+AppConst.WEBDIR+"/";  
    private String filepath=savepath;
	
	

	public InputThread_pic(Socket socket, OutputThread out, OutputThreadMap map)
	{
		this.socket = socket;
		//this.out1 = out;
		this.map = map;
		dao = UserDaoFactory.getInstance();
		try
		{
			this.ois = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));  
		}
		catch(IOException e)
		{
			
		}

	}

	public void setStart(boolean isStart)// 提供接口给外部关闭读消息线程
	{
		this.isStart = isStart;
	}

	 // 创建目录（不存在则创建）  
    public File CreateDir(String dir)
    {  
        File file = new File(dir);  
        if (!file.exists())
        {  
            file.mkdirs();  
        }  
        return file;  
    }  
	
	
	@Override
	public void run()
	{
		try 
		{
			while (isStart)
			{
				// 读取消息
				readMessage();
			}
			if (ois != null)
				ois.close();
			if (socket != null)
				socket.close();
		} 
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * 读消息以及处理消息，抛出异常
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void readMessage() throws IOException, ClassNotFoundException 
	{
		ois = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		switch (ois.readInt())
        {  
        case 0:
        	System.out.println("图片socket连接成功");
        	break;
        // 上传文件  
        case 1:  
        	 System.out.println("开始上传图片");
            Upload();
           
            break;  
        // 下载文件   
        case 2:  
        	System.out.println("开始下载图片");
            Download();
            break;  
        default:  
            break; 
        }
	}
	
	private void Upload()
	{
		savepath="d:/oldBook/";
		filepath=savepath;
		if (socket == null)  
            return;  
  
        //DataInputStream in = null;  
        BufferedOutputStream fo = null;  
        //DataOutputStream out = null;  
  
        try
        {  
            // 1、访问Socket对象的getInputStream方法取得客户端发送过来的数据流  
            //in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));  
        	String str=ois.readUTF();
        	System.out.println(str);
            String[] strArray = str.split("_");  
            //文件名  
            String fileName =strArray[2]; // 取得附带的文件名  
            System.out.println(new Date().toString() + " \n 文件名为:"+ fileName);  
            String filetype=strArray[2].split("\\.")[1];  
            id=Integer.parseInt(strArray[1]);
            //String userid = str[2];// 取用户ID  
            //存储路径  
            savepath=savepath+strArray[0];  
            if (savepath.endsWith("/") == false&& savepath.endsWith("\\") == false)
            {  
                savepath += "\\";  
            }  
            System.out.println("保存路径："+savepath);  
            //创建目录  
            CreateDir(savepath);    
            //返回文件名和路径  
            filepath = filepath+strArray[0]+"/"+id+"."+filetype;  
            System.out.println("返回路径："+filepath);  
  
            // 2、将数据流写到文件中  
            fo = new BufferedOutputStream(new FileOutputStream(new File(savepath+"\\"+id+"."+filetype)));  
  
            int bytesRead = 0;
            int readSize=0;
            int fileSize=Integer.parseInt(strArray[3]);
            byte[] buffer = new byte[20480];  
            while (readSize!=fileSize)
            { 
            	bytesRead= ois.read(buffer, 0, buffer.length);
            	readSize+=bytesRead;
            	System.out.println(bytesRead);
            	fo.write(buffer, 0, bytesRead);  
            }  
            fo.flush();  
            fo.close();  
  
            System.out.println(new Date().toString() + " \n 数据接收完毕");  
  
            
            
            // 3.存入数据库并返回客户端
            MessageType result;
        	out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));  
            switch(strArray[0])
            {
            case "avatar":
            	result=dao.setAvatar(filepath,id);
            	System.out.println(result);
            	if(result==MessageType.SUCCESS)
            	{
            		System.out.println(1);
            		out.writeInt(1);  
            	}
            	else
            	{
            		System.out.println(-1);
            		out.writeInt(-1);
            	}
                out.flush();
                //isStart=false;
            	break;
            case "surface":
            	result=dao.setSurface(filepath,id);
            	System.out.println(result);
            	if(result==MessageType.SUCCESS)
            	{
            		out.writeInt(1);  
            	}
            	else
            	{
            		out.writeInt(-1);
            	} 
                out.flush();
            	break;
            	default:
            		break;
            }
        }
        catch (Exception ex)
        {  
            ex.printStackTrace();  
            try 
            {  
                out.writeInt(0);  
                out.flush();  
            }
            catch (IOException e)
            {  
                System.out.println(new Date().toString() + ":" + e.toString());  
            }  
        } 
        finally
        {  
        	/*
            try
            {  
                //out.close();  
                //fo.close();  
                //ois.close();  
                //client.close();  
            }
            catch (IOException e)
            {  
                System.out.println(new Date().toString() + ":" + e.toString());  
            }
              */
        }  
  
    }  
	
	private void Download()
	{
		if (socket == null)  
            return;  
		//DataInputStream in = null;  
        BufferedOutputStream fo = null;  
        //DataOutputStream out = null;  
        FileInputStream reader=null;
        InputStream result;
  
        try
        {  
        	out = new DataOutputStream(socket.getOutputStream());  
        	//in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
        	String[] str = ois.readUTF().split("_"); 
        	
            switch(str[0])
            {
            	case "surface":
            		this.bookId=Integer.parseInt(str[1]);
            		System.out.println("bookId="+bookId);
                	result=dao.getSurface(bookId);
                	if(result!=null)
                	{
                  		int bufferSize_s = 20480; // 20K  
                		byte[] buf_s = new byte[bufferSize_s];
                		int read_s = 0;
                		while ((read_s = result.read(buf_s, 0, buf_s.length)) != -1)
                		{
                			out.write(buf_s, 0, read_s);
                		}
                		System.out.println("下载完成surface"+bookId);
                		out.flush();
                	}
                	else
                	{
               		
                		System.out.println("下载失败surface"+bookId);
                		
                	}
                	break;
            case "avatar":
            	this.id=Integer.parseInt(str[1]);
            	result=dao.getAvatar(id);
            	if(result!=null)
            	{
              		int bufferSize_s = 1024; // 20K  
            		byte[] buf_s = new byte[bufferSize_s];
            		int read_s = 0;
            		while ((read_s = result.read(buf_s, 0, buf_s.length)) != -1)
            		{
            			out.write(buf_s, 0, read_s);
            		}
            		System.out.println("下载完成avatar"+id);
            		out.flush();
            	}
            	else
            	{
            		//byte[] buf=new byte[0];
            		//out.write(buf, 0, buf.length);
            		System.out.println("下载失败avatar"+id);
            		
            	}
            	//socket.shutdownOutput();
            	break;
            	default:
            		break;
            }
        }
        catch (Exception ex)
        {  
            ex.printStackTrace();  
            try 
            {  
                out.writeInt(0);  
                out.flush();  
            }
            catch (IOException e)
            {  
                System.out.println(new Date().toString() + ":" + e.toString());  
            }  
        } 
        finally
        {  /*
            try
            {  
                //out.close();  
                //in.close();  
                //client.close();  
            }
            catch (IOException e)
            {  
                System.out.println(new Date().toString() + ":" + e.toString());  
            }  
            */
        }  
		
  
        
  
    
	}
	
	
	
}
