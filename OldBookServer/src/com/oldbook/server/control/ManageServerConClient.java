/**
 * 管理客户端连接的类
 */
package com.oldbook.server.control;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.oldbook.server.thread.ServerConClientThread;




public class ManageServerConClient
{
	public static HashMap hm=new HashMap<Integer,ServerConClientThread>();
	
	//添加一个客户端通信线程
	public static void addClientThread(int account, ServerConClientThread cc)
	{
		hm.put(account,cc);
	}
	//得到一个客户端通信线程
	public static ServerConClientThread getClientThread(int i)
	{
		return (ServerConClientThread)hm.get(i);
	}
	//返回当前在线人的情况
	public static List getAllOnLineUserid()
	{
		List list=new ArrayList();
		//使用迭代器完成
		Iterator it=hm.keySet().iterator();
		while(it.hasNext())
		{
			list.add((int) it.next());
		}
		return list;
	}
}
