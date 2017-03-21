package com.oldbook.android.Application;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.oldbook.android.client.Client;
import com.oldbook.android.net.GetMsgService;

import java.util.ArrayList;
import java.util.Date;

import io.socket.client.Socket;


/**
 * 功能描述：用于存放全局变量和公用的资源等
 * @author Administrator
 *
 */

public class OldBookApplication extends Application
{

	public static String SERVER_IP="112.74.97.22";
	public static int PORT=8090;
	public static final String ACTION = "com.oldbook.android.message";//消息广播action
	public static final String MSGKEY = "message";//消息的key
	public static final String SAVE_USER = "saveUser";//保存用户信息的xml文件名
	public static final String BACKKEY_ACTION="com.oldbook.backKey";//返回键发送广播的action
	public static final int NOTIFY_ID = 0x911;//通知ID
	public static final String DBNAME = "OldBook.db";//数据库名称

	public static int SCREEN_WIDTH;      //屏幕宽度
	public static int SCREEN_HEIGHT;     //屏幕高度
	public static float DENSITY;         //密度
	public static String Evaluation;     //评价
	public static String USER_NAME;      //用户名
	public static String PET_NAME;       //昵称
	public static int ID;                //ID


	private int newMsgNum = 0;// 后台运行的消息
	//private LinkedList<RecentChatEntity> mRecentList;     // <-----
	//private RecentChatAdapter mRecentAdapter;             // <-----
	private int recentNum = 0;
	private int activeId;
	private int statue;//连接状态
	private boolean isClose=false;




	/**
	 * Activity集合
	 */
	private ArrayList<Activity> activitys=new ArrayList<Activity>();
	private static  Client client=null;
	private static int socketStatue=0;

	public static Context context;

	public void onCreate()
	{
		super.onCreate();
		context=getApplicationContext();
		client=new Client(SERVER_IP,PORT);
		//网络连通就启动服务
		if (isNetworkAvailable())
		{
			try
			{
				client.Start();
				while(socketStatue==0)
				{

				}
				Log.i("oldBook", "socket连接状态 " + socketStatue + " " + new Date().toString());
				//socket连接成功启动服务
				if (socketStatue==1)
				{
					Log.i("oldBook","startService() 开启服务" +new Date().toString());
					Intent service = new Intent(this, GetMsgService.class);
					startService(service);
				}
			}
			catch(Exception e)
			{
				socketStatue=-2;
			}
		}
		else
		{
			dialog();
			socketStatue=-1;
		}


	}

	/**
	 * 判断手机网络是否可用
	 * @return 是否连接
	 */
	private boolean isNetworkAvailable()
	{
		ConnectivityManager mgr = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = mgr.getAllNetworkInfo();
		if (info != null)
		{
			for (int i = 0; i < info.length; i++)
			{
				if (info[i].getState() == NetworkInfo.State.CONNECTED)
				{
					return true;
				}
			}
		}
		return false;
	}



	/**
	 * 网络连接提示框
	 */
	protected void dialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("网络未连接，请检查网络");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	public static Context getContext()
	{
		return context;
	}

	public static Client getClient()
	{
		return client;
	}

	//socket状态
	public static int getSocketStatue()
	{
		return socketStatue;
	}
	public static void setSocketStatue(int socketStatueIn)
	{
		socketStatue=socketStatueIn;
	}

	public void addActivity(Activity activity)
	{
		String className= activity.getClass().getName();
		for(Activity at: activitys)
		{
			if(className.equals(at.getClass().getName()))
			{
				activitys.remove(at);
				break;
			}
		}
		activitys.add(activity);
	}

	public int getNewMsgNum() {
		return newMsgNum;
	}

	public void setNewMsgNum(int newMsgNum)
	{
		this.newMsgNum = newMsgNum;
	}
	public void setActiveId(int activeId)
	{
		this.activeId=activeId;
	}
	public int getActiveId()
	{
		return activeId;
	}

	public int  getStatue()
	{
		return statue;
	}
	public void setStatue(int statue)
	{
		this.statue=statue;
	}
	public void setIsClose(boolean isClose)
	{
		this.isClose=isClose;
	}
	public boolean getIsClose()
	{
		return isClose;
	}

	public int getRecentNum() {
		return recentNum;
	}

	public void setRecentNum(int recentNum) {
		this.recentNum = recentNum;
	}
}
