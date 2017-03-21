package com.oldbook.android.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import com.oldbook.android.Application.OldBookApplication;
import com.oldbook.android.R;
import com.oldbook.android.client.Client;
import com.oldbook.android.net.GetMsgService;
import com.oldbook.android.util.SharePreferenceUtil;

import java.util.Date;

//import com.oldbook.android.net.PicUploadService;


public class Setting extends Activity
{
	protected String ip;
	protected  int port;
	private EditText etIp;
	private EditText etPort;
	private Client client;
	private SharePreferenceUtil util;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting);
		client=OldBookApplication.getClient();
		util = new SharePreferenceUtil(this,OldBookApplication.SAVE_USER);

		Button btnSetting=(Button)findViewById(R.id.btn_Setting);
		etIp=(EditText)findViewById(R.id.et_l_IP);
		etPort=(EditText)findViewById(R.id.et_l_PORT);
		etIp.setText(OldBookApplication.SERVER_IP);
		etPort.setText(String.valueOf(OldBookApplication.PORT));

		System.out.println("端口设置");
		//Toast.makeText(getApplicationContext(), "服务器连接失败，请检测", Toast.LENGTH_LONG).show();
		btnSetting.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				ip=etIp.getText().toString();
				port= Integer.parseInt(etPort.getText().toString());
				//更新ip地址 端口号
				OldBookApplication.SERVER_IP=ip;
				OldBookApplication.PORT=port;

				client.setIp(ip);
				client.setPort(port);

				util.setIp(ip);
				util.setPort(port);

				//网络连通就启动服务
				if (isNetworkAvailable())
				{
					try
					{
						//连接socket
						OldBookApplication.setSocketStatue(0);
						Log.i("oldBook", "Setting 建立socket 连接 " + new Date().toString());
						client.Start();
						Log.i("oldBook", "socket连接状态" + OldBookApplication.getSocketStatue() + " " + new Date().toString());

						while(OldBookApplication.getSocketStatue()==0)
						{

						}
						//socket连接成功启动服务
						if (OldBookApplication.getSocketStatue()==1)
						{
							Log.i("oldBook", "Setting 开启服务" + new Date().toString());
							Intent service = new Intent(Setting.this, GetMsgService.class);
							startService(service);

							Log.i("oldBook", "开启登录activity" + new Date().toString());
							Intent intent=new Intent(Setting.this,LoginActivity.class);
							Setting.this.startActivity(intent);
							Setting.this.finish();
						}
						else
						{
							Toast.makeText(getApplicationContext(), "服务器连接失败，请检测", Toast.LENGTH_LONG).show();
						}

					}
					catch(Exception e)
					{
						Log.i("NewOldBook","服务器连接失败，请检测 "+new Date().toString());
						e.printStackTrace();
					}
				}
				else
				{
					dialog();
				}

			}
			
		});
		

		
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
}
