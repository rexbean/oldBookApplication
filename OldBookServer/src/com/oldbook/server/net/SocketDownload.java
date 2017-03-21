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
import java.io.ObjectOutputStream;
import java.net.Socket;  
import java.util.Date;  

import com.oldbook.entity.MessageType;
import com.oldbook.server.dao.UserDao;
import com.oldbook.server.dao.impl.UserDaoFactory;
  
public class SocketDownload extends Thread
{  
      
    // socket对象  
    private Socket client;  
    private UserDao dao;  
    private int bookId;
    
    public SocketDownload(Socket client)
    {  
        this.client = client;  
        dao = UserDaoFactory.getInstance();
    }  
    
    public void run()
    {  
        if (client == null)  
            return;  
  
        DataInputStream in = null;  
        BufferedOutputStream fo = null;  
        DataOutputStream out = null;  
        FileInputStream reader=null;
        InputStream result;
  
        try
        {  
        	
        	in = new DataInputStream(new BufferedInputStream(client.getInputStream()));

            
        	String[] str = in.readUTF().split("_");  
            switch(str[0])
            {
            case "avastar":
            	//result=dao.getAvastar(filepath,id);
            	//if(result==MessageType.SUCCESS)
            	//{
            		//out.writeInt(1);  
            	//}
            	//else
            	//{
            		//out.writeInt(0);
            	//}
                //out.flush();
            	break;
            case "surface":
            	this.bookId=Integer.parseInt(str[1]);
            	result=dao.getSurface(bookId);
            	out = new DataOutputStream(client.getOutputStream());
            	//ObjectOutputStream oop=new ObjectOutputStream().writeObject(arg0);
                //out.writeInt(1);
                String sendinfo="surface_"+bookId;
                out.writeUTF(sendinfo);
                
                int bufferSize = 20480; // 20K  
                byte[] buf = new byte[bufferSize];
                int read = 0;
                // 将文件输入流 循环 读入 Socket的输出流中  
                //File file=new File("D:/inspiration/OldBookServer/"+bookId+".jpg");
                //reader=new FileInputStream(file);
                while ((read = result.read(buf, 0, buf.length)) != -1)
                {
                    out.write(buf, 0, read);
                }
                
            	out.flush();
            	client.shutdownOutput();
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
            try
            {  
                out.close();  
                in.close();  
                //client.close();  
            }
            catch (IOException e)
            {  
                System.out.println(new Date().toString() + ":" + e.toString());  
            }  
        }  
  
    }  
}  