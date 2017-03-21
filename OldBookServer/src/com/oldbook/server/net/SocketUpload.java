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
      
    // socket����  
    private Socket client;  
    private UserDao dao;  
    // ����·��  
    private String savepath="d:/oldBook/";  
    private int id;
    //����·��(���·��)  
    //private String filepath="/"+AppConst.WEBDIR+"/";  
    private String filepath=savepath;
    
    public SocketUpload(Socket client)
    {  
        this.client = client;  
        dao = UserDaoFactory.getInstance();
    }  
  
    // ����Ŀ¼���������򴴽���  
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
            // 1������Socket�����getInputStream����ȡ�ÿͻ��˷��͹�����������  
            in = new DataInputStream(new BufferedInputStream(client.getInputStream()));  
  
            String[] str = in.readUTF().split("_");  
            //�ļ���  
            String fileName =str[2]; // ȡ�ø������ļ���  
            System.out.println(new Date().toString() + " \n �ļ���Ϊ:"+ fileName);  
            String filetype=str[2].split("\\.")[1];  
            id=Integer.parseInt(str[1]);
            //String userid = str[2];// ȡ�û�ID  
            //�洢·��  
            savepath=savepath+filetype;  
            if (savepath.endsWith("/") == false  
                    && savepath.endsWith("\\") == false) {  
                savepath += "\\";  
            }  
            System.out.println("����·����"+savepath);  
            //����Ŀ¼  
            CreateDir(savepath);    
            //�����ļ�����·��  
            filepath = filepath+filetype+"/"+id+"."+filetype;  
            System.out.println("����·����"+filepath);  
  
            // 2����������д���ļ���  
            fo = new BufferedOutputStream(new FileOutputStream(new File(savepath+"\\"+id+"."+filetype)));  
  
            int bytesRead = 0;  
            byte[] buffer = new byte[1024];  
            while ((bytesRead = in.read(buffer, 0, buffer.length)) != -1)
            {  
                fo.write(buffer, 0, bytesRead);  
            }  
            fo.flush();  
            fo.close();  
  
            System.out.println(new Date().toString() + " \n ���ݽ������");  
  
            
            
            // 3.�������ݿⲢ���ؿͻ���
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