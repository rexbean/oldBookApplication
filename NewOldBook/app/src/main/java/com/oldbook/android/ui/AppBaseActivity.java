package com.oldbook.android.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.oldbook.android.Application.OldBookApplication;
import com.oldbook.android.entity.MessageEntity;

/**
 * 功能描述：应用中界面(Activity)旳基类
 * 对原有的Activity类进行扩展
 *
 * @author Administrator
 */
public abstract class AppBaseActivity extends Activity
{


	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		((OldBookApplication) this.getApplication()).addActivity(this);
		setContentView(getLayoutId());



		//初始化组件
		setupView();
		//初始化数据
		initializedData();

	}

	/**
	 * 布局文件ID
	 *
	 * @return 布局文件id
	 */
	protected abstract int getLayoutId();


	/**
	 * 初始化组件
	 */
	protected abstract void setupView();

	/**
	 * 初始化数据
	 */
	protected abstract void initializedData();


	@Override
	public void setContentView(int layoutResID)
	{
		//Set a break point on the next line or log out a message.
		super.setContentView(layoutResID);
	}

	/**
	 * 显示Toast形式的提示信息
	 *
	 * @param message 显示的信息
	 */
	protected void show(String message)
	{
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();


	}



	/**
	 * 广播接收者，接收GetMsgService发送过来的消息
	 */
	/*
	private BroadcastReceiver MsgReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			MessageEntity msg =  (MessageEntity)intent.getSerializableExtra(OldBookApplication.MSGKEY);
			if (msg != null)//如果不是空，说明是消息广播
			{
				// System.out.println("MyActivity:" + msg);
				getMessage(msg);// 把收到的消息传递给子类
			} else//如果是空消息，说明是关闭应用的广播
			{
				close();
			}
		}
	};
*/

	/**
	 * 抽象方法，用于子类处理消息，
	 *
	 * @param msg 传递给子类的消息对象
	 */
	//public abstract void getMessage(MessageEntity msg);

	/**
	 * 子类直接调用这个方法关闭应用
	 */
	public void close()
    {
		Intent i = new Intent();
		i.setAction(OldBookApplication.ACTION);
		sendBroadcast(i);
		finish();

	}

	@Override
	public void onStart()// 在start方法中注册广播接收者
	{
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		//client.connect();
		//IntentFilter intentFilter = new IntentFilter();
		//intentFilter.addAction(OldBookApplication.ACTION);
		//registerReceiver(MsgReceiver, intentFilter); // 注册接受消息广播



	}

	@Override
	protected void onStop()// 在stop方法中注销广播接收者
	{
		super.onStop();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.

		//OldBookApplication.getClient().getSocket().disconnect();
	}


}

