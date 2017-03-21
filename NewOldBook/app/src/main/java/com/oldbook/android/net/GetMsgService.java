package com.oldbook.android.net;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;


import com.oldbook.android.Application.OldBookApplication;
import com.oldbook.android.client.Client;
import com.oldbook.android.entity.BookEntity;
import com.oldbook.android.entity.BorrowEntity;
import com.oldbook.android.entity.MessageEntity;
import com.oldbook.android.entity.MessageType;
import com.oldbook.android.entity.UserEntity;
import com.oldbook.android.util.MessageDB;
import com.oldbook.android.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * 收取消息服务
 *
 * @author Administrator
 *
 */
public class GetMsgService extends Service
{
	private static final int MSG = 0x001;

	private boolean isStart = false;// 是否与服务器连接上
	private Context mContext = this;
	private SharePreferenceUtil util;
	private MessageDB messageDB;              //<-----
	private Client client;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()// 在onCreate方法里面注册广播接收者
	{
		super.onCreate();
		Log.i("oldBook", "服务onCreate" + new Date().toString());
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
		messageDB = new MessageDB(this);

	}

	@Override
	public int onStartCommand(Intent intent,int flag, int startId)
	{
		super.onStartCommand(intent, flag, startId);
		Log.i("oldBook", "服务onStartCommand" + new Date().toString());
		client=OldBookApplication.getClient();
		util = new SharePreferenceUtil(getApplicationContext(),OldBookApplication.SAVE_USER);
		if(OldBookApplication.getSocketStatue()!=1)
		{
			if (isNetworkAvailable())
			{
				Log.i("oldBook", "服务socket连接" + new Date().toString());
				OldBookApplication.getClient().Start();
				Log.i("oldBook", "服务图片socket连接" + new Date().toString());
				//心跳包
				//handler.postDelayed(heartBeatRunnable,HEART_BEAT_RATE);
				System.out.println("client start:" + isStart);
				if(OldBookApplication.getSocketStatue()!=1)
				{
					Toast.makeText(getApplicationContext(), "启动服务失败，请检查设置", Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				dialog();
			}
		}
		//handler.postDelayed(heartBeatRunnable,HEART_BEAT_RATE);
		isStart=true;
		//client.getSocket().on("SERVERMSG", BroadCastMsg);
		return START_REDELIVER_INTENT;
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
	protected void dialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("网络未连接，请检查网络");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		builder.create().show();
	}



	@Override
	// 在服务被摧毁时，做一些事情
	public void onDestroy()
	{
		super.onDestroy();
		if (messageDB != null)
			messageDB.close();
	}





}
