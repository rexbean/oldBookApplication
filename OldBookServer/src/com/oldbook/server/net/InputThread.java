package com.oldbook.server.net;

import java.io.BufferedOutputStream;
import java.io.File;
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
import com.oldbook.entity.BorrowEntity;
import com.oldbook.entity.ChatMsgEntity;
import com.oldbook.entity.MessageEntity;
import com.oldbook.entity.MessageType;
import com.oldbook.entity.PicEntity;
import com.oldbook.entity.UserEntity;
import com.oldbook.server.dao.UserDao;
import com.oldbook.server.dao.impl.UserDaoFactory;
import com.oldbook.server.util.MyDate;


/**
 * 读消息线程和处理方法
 * 
 * @author administrator
 * 
 */
public class InputThread extends Thread
{
	private Socket socket;// socket对象
	private OutputThread out;// 传递进来的写消息线程，因为我们要给用户回复消息啊
	private OutputThreadMap map;// 写消息线程缓存器
	private ObjectInputStream ois;// 对象输入流
	private boolean isStart = true;// 是否循环读消息

	public InputThread(Socket socket, OutputThread out, OutputThreadMap map)
	{
		this.socket = socket;
		this.out = out;
		this.map = map;
		try
		{
			ois = new ObjectInputStream(socket.getInputStream());// 实例化对象输入流
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setStart(boolean isStart)// 提供接口给外部关闭读消息线程
	{
		this.isStart = isStart;
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
		
		ois.defaultReadObject();// 从流中读取对象
		Object readObject = null;
		UserDao dao = UserDaoFactory.getInstance();// 通过dao模式管理后台
		if (readObject != null && readObject instanceof MessageEntity)
		{
			MessageEntity msg = (MessageEntity) readObject;// 转换成传输对象
			switch (msg.getType())
			{
			case INITIAl:
				String initialString=dao.initial();
				MessageEntity me=new MessageEntity();
				me.setType(MessageType.INITIAl);
				ChatMsgEntity ce=new ChatMsgEntity();
				ce.setContent(initialString);
				me.setObject(ce);
				out.setMessage(me);
				break;
				
			case REGISTER:// 如果用户是注册
				UserEntity registerUser = (UserEntity)msg.getObject();
				int registerResult = dao.register(registerUser);
				System.out.println(MyDate.getDateCN() + " 新用户注册:"
						+ registerResult);
				// 给用户回复消息

				
				String refreshList_r = dao.refresh();
				String bookList_r=dao.bookList();
				MessageEntity<ChatMsgEntity> mRegister = new MessageEntity<ChatMsgEntity>();
				ChatMsgEntity cme_r=new ChatMsgEntity();
				cme_r.setContent(registerResult+"@"+registerUser.getPetname()+"@"+100+"@"+refreshList_r+"@"+bookList_r);
				System.out.println(registerResult+"@"+registerUser.getPetname()+"@"+100+"@"+refreshList_r+"@"+bookList_r);
				mRegister.setObject(cme_r);
				mRegister.setType(MessageType.REGISTER);
				out.setMessage(mRegister);
				if(registerResult>0)
				{
					String res=dao.refresh();
					MessageEntity<ChatMsgEntity> mLoginOnline=new MessageEntity<ChatMsgEntity>();
					ChatMsgEntity resultMessage=new ChatMsgEntity();
					resultMessage.setContent(res);
					mLoginOnline.setObject(resultMessage);
					mLoginOnline.setType(MessageType.LOGIN_ONLINE);
					System.out.println(MyDate.getDateCN() + " 用户："
							+ registerResult + " 上线了");
					for (OutputThread login : map.getAll())// 广播用户下线消息
					{
						if(login!=null)
						{
							login.setMessage(mLoginOnline);
						}
					}
					map.add(registerResult, out);
				}
				
				
				break;
			case LOGIN:
				UserEntity loginUser = (UserEntity)msg.getObject();
				UserEntity loginResult = dao.login(loginUser);
				String evaluation=dao.getEvaluation(loginResult.getId());
				String refreshList = dao.refresh();
				String bookList=dao.bookList();
				
				String borrowList_login=dao.BorrowList(loginResult.getId());
				String lendList_login=dao.LendList(loginResult.getId());
				String resultList_login="";
				if(borrowList_login.equals("null"))
				{
					resultList_login=borrowList_login+" "+lendList_login;
				}
				else
				{
					resultList_login=borrowList_login+lendList_login;
				}
				
				
				
				MessageEntity<ChatMsgEntity> mLogin = new MessageEntity<ChatMsgEntity>();
				ChatMsgEntity cme=new ChatMsgEntity();
				cme.setContent(loginResult.getId()+"@"+loginResult.getPetname()+"@"+evaluation+"@"+refreshList+"@"+bookList+"@"+resultList_login);
				mLogin.setObject(cme);
				mLogin.setType(MessageType.LOGIN);
				out.setMessage(mLogin);// 同时把登录信息回复给用户
				if(loginResult.getId()>0)
				{
					String res=dao.refresh();
					MessageEntity<ChatMsgEntity> mLoginOnline=new MessageEntity<ChatMsgEntity>();
					ChatMsgEntity resultMessage=new ChatMsgEntity();
					resultMessage.setContent(res);
					mLoginOnline.setObject(resultMessage);
					mLoginOnline.setType(MessageType.LOGIN_ONLINE);
					System.out.println(MyDate.getDateCN() + " 用户："
							+ loginResult.getId() + " 上线了");
					for (OutputThread login : map.getAll())// 广播用户下线消息
					{
						if(login!=null)
						{
							login.setMessage(mLoginOnline);
						}
					}
					map.add(loginResult.getId(), out);
				}
				break;
			case LOGOUT:// 如果是退出，更新数据库在线状态，同时群发告诉所有在线用户
				UserEntity logoutUser = (UserEntity) msg.getObject();
				int offId = logoutUser.getId();
				System.out.println(MyDate.getDateCN() + " 用户：" + offId + " 下线了");
				//dao.logout(offId);
				isStart = false;// 结束自己的读循环
				OldbookApplication.online.remove(offId);
				Iterator iter = OldbookApplication.online.entrySet().iterator();
				while (iter.hasNext())
				{
					UserEntity u=new UserEntity();
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
					u=(UserEntity)val;
					System.out.println(key+"-"+u.getId());
				}
				
				map.remove(offId);// 从缓存的线程中移除
				String refreshListLogout = dao.refresh();
				refreshListLogout+="@"+offId;
				MessageEntity<ChatMsgEntity> mLogout=new MessageEntity<ChatMsgEntity>();
				ChatMsgEntity logoutMsg = new ChatMsgEntity();
				logoutMsg.setContent(refreshListLogout);
				mLogout.setObject(logoutMsg);
				mLogout.setType(MessageType.LOGOUT);
				for (OutputThread offOut : map.getAll())// 广播用户下线消息
				{
					if(offOut!=null)
					{
						offOut.setMessage(mLogout);
					}
				}
				out.setStart(false);// 再结束写线程循环
				break;
			case MESSAGE:// 如果是转发消息（可添加群发）
				// 获取消息中要转发的对象id，然后获取缓存的该对象的写线程
				int id2=msg.getReceiver();
				((ChatMsgEntity)(msg.getObject())).setMsgType(true);
				OutputThread toOut = map.getById(id2);
				if(toOut != null) 
				{// 如果用户在线
					toOut.setMessage(msg);
				} 
				break;
			
			case GET_ONLINE_FRIENDS:
				refreshList = dao.refresh();
				MessageEntity<ChatMsgEntity> mReturn=new MessageEntity<ChatMsgEntity>();
				ChatMsgEntity refreshO = new ChatMsgEntity();
				refreshO.setContent(refreshList);
				mReturn.setObject(refreshO);
				mReturn.setType(MessageType.RET_ONLINE_FRIENDS);
				out.setMessage(mReturn);
				break;
			case NEW_BOOK:
				MessageEntity me_nb=new MessageEntity();
				BookEntity be=(BookEntity)msg.getObject();
				BookEntity beNew=new BookEntity();
				int result=dao.addNewBook(be);
				me_nb.setType(MessageType.NEW_BOOK);
				beNew.setId(result);
				me_nb.setObject(beNew);
				out.setMessage(me_nb);
				break;
			case SURFACE_SUCCESS:
				refreshList = dao.bookList();
				
				MessageEntity<ChatMsgEntity> mBookList=new MessageEntity<ChatMsgEntity>();
				ChatMsgEntity cmeBookList = new ChatMsgEntity();
				cmeBookList.setContent(refreshList);
				mBookList.setObject(cmeBookList);
				mBookList.setType(MessageType.BOOK_LIST);
				out.setMessage(mBookList);
				break;
			case BOOK_LIST:
				refreshList = dao.bookList();
				MessageEntity<ChatMsgEntity> mBookList_l=new MessageEntity<ChatMsgEntity>();
				ChatMsgEntity cmeBookList_l = new ChatMsgEntity();
				cmeBookList_l.setContent(refreshList);
				mBookList_l.setObject(cmeBookList_l);
				mBookList_l.setType(MessageType.BOOK_LIST);
				out.setMessage(mBookList_l);
				break;
			case BORROW:
				BorrowEntity boe=(BorrowEntity)msg.getObject();
				int from=boe.getFromUser();
				int to=boe.getGetUser();
				
				String result_borrow=dao.Borrow(boe);
				String borrowList_from=dao.BorrowList(from);
				String lendList_from=dao.LendList(from);
				String resultList_from="";
				if(borrowList_from.equals("null"))
				{
					resultList_from=borrowList_from+" "+lendList_from;
				}
				else
				{
					resultList_from=borrowList_from+lendList_from;
				}
				
				
				MessageEntity me_borrow =new MessageEntity();
				me_borrow.setType(MessageType.BORROW);
				ChatMsgEntity cme_borrow=new ChatMsgEntity();
				cme_borrow.setContent(resultList_from);
				me_borrow.setObject(cme_borrow);
				out.setMessage(me_borrow);
				
				
				String borrowList_to=dao.BorrowList(to);
				String lendList_to=dao.LendList(to);
				String resultList_to="";
				if(borrowList_to.equals("null"))
				{
					resultList_to=borrowList_to+" "+lendList_to;
				}
				else
				{
					resultList_to=borrowList_to+lendList_to;
				}
				
				OutputThread ot=map.getById(to);
				if(ot!=null)
				{
					MessageEntity me_borrow2=new MessageEntity();
					me_borrow2.setType(MessageType.BORROW);
					ChatMsgEntity cme_lend=new ChatMsgEntity();
					cme_lend.setContent(resultList_to);
					me_borrow2.setObject(cme_lend);
					ot.setMessage(me_borrow2);
				}
				break;
			case RETURN:
				int owner=msg.getReceiver();
				int sender=msg.getSender();
				ChatMsgEntity cme_return=(ChatMsgEntity)msg.getObject();
				
				String result_return=dao.Return(cme_return.getContent());
				String borrowList_return=dao.BorrowList(owner);
				String lendList_return=dao.LendList(owner);
				String resultList_return="";
				if(borrowList_return.equals("null"))
				{
					resultList_return=borrowList_return+" "+lendList_return;
				}
				else
				{
					resultList_return=borrowList_return+lendList_return;
				}
				
				OutputThread ot_return=map.getById(owner);
				
				if(ot_return!=null)
				{
					MessageEntity me_return=new MessageEntity();
					me_return.setType(MessageType.BORROW);
					ChatMsgEntity cme_return_to=new ChatMsgEntity();
					cme_return_to.setContent(resultList_return);
					me_return.setObject(cme_return_to);
					ot_return.setMessage(me_return);
				}
				break;
			case EVALUATION:
				int fromer=msg.getReceiver();
				ChatMsgEntity cme_evaluation=(ChatMsgEntity)msg.getObject();
				String[] array=cme_evaluation.getContent().split("_");
				String bookName=array[0];
				int evaluation_value=Integer.parseInt(array[1]);
				String result_cme_evaluation=dao.Evaluation(bookName,evaluation_value);
				String borrowList_evaluation=dao.BorrowList(fromer);
				String lendList_evaluation=dao.LendList(fromer);
				String resultList_evaluation="";
				if(borrowList_evaluation.equals("null"))
				{
					resultList_evaluation=borrowList_evaluation+" "+lendList_evaluation;
				}
				else
				{
					resultList_evaluation=borrowList_evaluation+lendList_evaluation;
				}
				
				OutputThread ot_evaluation=map.getById(fromer);
				if(ot_evaluation!=null)
				{
					MessageEntity me_evaluation=new MessageEntity();
					me_evaluation.setType(MessageType.BORROW);
					ChatMsgEntity cme_evaluation_from=new ChatMsgEntity();
					cme_evaluation_from.setContent(resultList_evaluation);
					me_evaluation.setObject(cme_evaluation_from);
					ot_evaluation.setMessage(me_evaluation);
				}
				break;
			case BEAT:
				MessageEntity me_beat=new MessageEntity();
				me_beat.setType(MessageType.BEAT);
				out.setMessage(me_beat);
				break;
			case UPLOAD:
				PicEntity pe_upload=(PicEntity)msg.getObject();
				String uploadResult=Upload(pe_upload);
				MessageEntity me_upload=new MessageEntity();
				me_upload.setType(MessageType.UPLOAD);
				ChatMsgEntity cme_upload=new ChatMsgEntity();
				if(uploadResult.equals("-1"))
				{
					cme_upload.setContent("fail");
				}
				else
				{
					
					switch(pe_upload.getType())
					{
						case "avatar":
							MessageType result_upload_avatar=dao.setAvatar(uploadResult,pe_upload.getId());
							if(result_upload_avatar==MessageType.SUCCESS)
							{
								cme_upload.setContent("success");  
							}
							else
							{
								cme_upload.setContent("fail");  
							}
							break;
						case "surface":
							MessageType result_upload_surface=dao.setAvatar(uploadResult,pe_upload.getId());
							if(result_upload_surface==MessageType.SUCCESS)
							{
								cme_upload.setContent("success");  
							}
							else
							{
								cme_upload.setContent("fail");  
							}
							break;
							default:
								break;
								
					}
					me_upload.setObject(cme_upload);
					out.setMessage(me_upload);
				}
				break;
			case DOWNLOAD:
				InputStream downloadResult=null;
				PicEntity pe_download_infor=(PicEntity)msg.getObject();
				String type=pe_download_infor.getType();
				int id=pe_download_infor.getId();

				switch(type)
		        {
					case"avatar":
						System.out.println("id="+id);
						downloadResult=dao.getAvatar(id);
						break;
		        
		        	case "surface":
		        		System.out.println("bookId="+id);
		            	downloadResult=dao.getSurface(id);
		            	break;
		            default:
		            	break;
		        }
				
				MessageEntity me_download=new MessageEntity();
				PicEntity pe_download=new PicEntity();
				
				if(downloadResult!=null)
				{
					pe_download.setType(type);
					pe_download.setId(id);
					pe_download.setFile(downloadResult);
					pe_download.setSize(downloadResult.available());
				}
				else
				{
					pe_download.setId(-1);
				}
				me_download.setObject(pe_download);
				me_download.setType(MessageType.DOWNLOAD);
				out.setMessage(me_download);
				break;
			default:
				break;
			}
		}
		
		
		
	}
	
	public String Upload(PicEntity pe)
	{
		int id=-1;
		int i=0;
		int m=0;
		int k=0;
		String savepath="c:/oldBook/";  
	    BufferedOutputStream fo = null;  
	    
	    String filepath=savepath;
		String fileName =pe.getFileName(); // 取得附带的文件名  
		String type=pe.getType();
		String filetype=fileName.split("\\.")[1]; 
		int size=(int)(pe.getSize());
		
		
        System.out.println(new Date().toString() + " \n 文件名为:"+ fileName);  
         
        id=pe.getId();
        //存储路径  
        savepath=savepath+type;  
        if (savepath.endsWith("/") == false&& savepath.endsWith("\\") == false)
        {  
            savepath += "\\";  
        }  
        System.out.println("保存路径："+savepath);  
        //创建目录  
        CreateDir(savepath);    
        //返回文件名和路径  
        filepath = filepath+type+"/"+id+"."+filetype;  
        System.out.println("返回路径："+filepath); 
        
        try
        {
            System.out.println("下载目录：" + filepath);
            //接收文件
            fo = new BufferedOutputStream(new FileOutputStream(filepath));
            int bytesRead = 0;
            int readSize=0;
            byte[] buffer = new byte[1024];
            byte[] buffer1=new byte[(int)(pe.getSize())+1024];
            byte[] buffer2=new byte[(int)(pe.getSize())];
            while (readSize < pe.getSize())
            {
                System.out.println(readSize);
                bytesRead = pe.getFile().read(buffer,0,buffer.length);
                if(i==0)
                {
                    for(int j=5;j<15;j++)
                    {
                        if (buffer[j] == 0)
                        {
                            m++;
                        }
                    }
                    if(buffer[0]==-1&&buffer[1]==-40&&buffer[2]==-1&&buffer[3]==-32)
                    {
                        k=1;
                        System.arraycopy(buffer,0,buffer1,readSize,bytesRead);
                        readSize+=bytesRead;
                    }
                    else if(buffer[0]==-84&&buffer[1]==-19&&buffer[2]==0&&buffer[3]==5 &&
                            buffer[4]!=0&&buffer[5]!=0&&buffer[6]!=0&&buffer[7]!=0)
                    {
                        System.arraycopy(buffer,0,buffer1,readSize,bytesRead);
                        readSize+=bytesRead;
                    }
                    else
                        k=1;

                }
                else
                {
                    System.arraycopy(buffer,0,buffer1,readSize,bytesRead);
                    readSize+=bytesRead;
                }
                i++;
            }
            System.out.println("下载完成-1" + id);
            if(k==0)
            {
                for (int j = 0; j < (int)(pe.getSize()); j++)
                {
                    buffer2[j] = buffer1[j + 4];
                }
            }
            else
            {
                buffer2=buffer1;
            }
            System.out.println("下载完成-2" + id);
            fo.write(buffer2, 0, (int)(pe.getSize()));
            System.out.println("下载完成-3" + id);
            fo.flush();
            fo.close();

            System.out.println("数据接收完毕");
        }
        catch(IOException e)
        {
        	return "-1";
        }
        return filepath;
        
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
	
	
	
	
}
