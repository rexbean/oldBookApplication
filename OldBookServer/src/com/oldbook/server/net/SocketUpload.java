package com.oldbook.server.net;

import java.io.BufferedInputStream;  
import java.io.BufferedOutputStream;  
import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.File;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.net.Socket;  
import java.util.Date;  

import com.oldbook.entity.MessageType;
import com.oldbook.server.dao.UserDao;
import com.oldbook.server.dao.impl.UserDaoFactory;
  
public class SocketUpload extends Thread
{  
      
    // socket对象  
    private Socket client;  
    private UserDao dao;  
    // 保存路径  
    private String savepath="d:/oldBook/";  
    private int id;
    //返回路径(相对路径)  
    //private String filepath="/"+AppConst.WEBDIR+"/";  
    private String filepath=savepath;
    
    public SocketUpload(Socket client)
    {  
        this.client = client;  
        dao = UserDaoFactory.getInstance();
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
  
    public void run()
    {  
        if (client == null)  
            return;  
  
        DataInputStream in = null;  
        BufferedOutputStream fo = null;  
        DataOutputStream out = null;  
  
        try
        {  
            // 1、访问Socket对象的getInputStream方法取得客户端发送过来的数据流  
            in = new DataInputStream(new BufferedInputStream(client.getInputStream()));  
  
            String[] str = in.readUTF().split("_");  
            //文件名  
            String fileName =str[2]; // 取得附带的文件名  
            System.out.println(new Date().toString() + " \n 文件名为:"+ fileName);  
            String filetype=str[2].split("\\.")[1];  
            id=Integer.parseInt(str[1]);
            //String userid = str[2];// 取用户ID  
            //存储路径  
            savepath=savepath+filetype;  
            if (savepath.endsWith("/") == false  
                    && savepath.endsWith("\\") == false) {  
                savepath += "\\";  
            }  
            System.out.println("保存路径："+savepath);  
            //创建目录  
            CreateDir(savepath);    
            //返回文件名和路径  
            filepath = filepath+filetype+"/"+id+"."+filetype;  
            System.out.println("返回路径："+filepath);  
  
            // 2、将数据流写到文件中  
            fo = new BufferedOutputStream(new FileOutputStream(new File(savepath+"\\"+id+"."+filetype)));  
  
            int bytesRead = 0;  
            byte[] buffer = new byte[1024];  
            while ((bytesRead = in.read(buffer, 0, buffer.length)) != -1)
            {  
                fo.write(buffer, 0, bytesRead);  
            }  
            fo.flush();  
            fo.close();  
  
            System.out.println(new Date().toString() + " \n 数据接收完毕");  
  
            
            
            // 3.存入数据库并返回客户端
            MessageType result;
        	out = new DataOutputStream(new BufferedOutputStream(  
                    client.getOutputStream()));  
            switch(str[0])
            {
            case "avastar":
            	result=dao.setAvatar(filepath,id);
            	if(result==MessageType.SUCCESS)
            	{
            		out.writeInt(1);  
            	}
            	else
            	{
            		out.writeInt(0);
            	}
                out.flush();
            	break;
            case "surface":
            	result=dao.setSurface(filepath,id);
            	if(result==MessageType.SUCCESS)
            	{
            		out.writeInt(1);  
            	}
            	else
            	{
            		out.writeInt(0);
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
            try
            {  
                out.close();  
                fo.close();  
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