package com.oldbook.android.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;


import com.oldbook.android.Application.OldBookApplication;
import com.oldbook.android.R;
import com.oldbook.android.client.Client;

import com.oldbook.android.entity.BookEntity;
import com.oldbook.android.entity.Download;
import com.oldbook.android.entity.MessageEntity;
import com.oldbook.android.entity.MessageType;
import com.oldbook.android.entity.UserEntity;
import com.oldbook.android.net.GetMsgService;
import com.oldbook.android.util.SharePreferenceUtil;
import com.oldbook.android.util.Utils;
import com.oldbook.android.widget.CircularImage;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class LoginActivity extends AppBaseActivity implements View.OnClickListener
{

	private EditText etUsername;   //用户名
	private EditText etPassword;   //密码
	private CheckBox cbRemPw;      //记住密码
	private CheckBox cbAutoLogin;  //自动登录
	private String usernameValue;  //用户名
	private String passwordValue;  //密码
	private int Id;

	private OldBookApplication application;
	private SharePreferenceUtil util;
	private ProgressDialog progressDialog;
	private Client client;

    private String[] avatarArray;  //头像列表
	private String[] surfaceArray; //封面列表


	private List<Integer> avatarList=new ArrayList<Integer>();
	private List<Integer> bookList=new ArrayList<Integer>();

	private boolean bookOver=false;
	private boolean userOver=false;
	//private PicUploadService  pus;

	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		application = (OldBookApplication) this.getApplicationContext();
		util=new SharePreferenceUtil(LoginActivity.this,OldBookApplication.SAVE_USER);
		if(!util.getRecentUsername().equals("-1"))
		{
			etUsername.setText(String.valueOf(util.getRecentUsername()));
		}
		client=OldBookApplication.getClient();
		client.getSocket().on("LOGIN",loginResult);
        client.getSocket().on("GET_BOOK_LIST",bookListResult);
		client.getSocket().on("GET_USER_LIST",userListResult);

		System.out.println("onCreate_Activity" + new Date().toString());

		//获取屏幕属性
		Utils.getScreenWidth(this);
		Utils.getScreenHeight(this);
		Utils.getScreenDensity(this);

		//网络连接需要
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

		//设置头像
		CircularImage cover_user_photo = (CircularImage)findViewById(R.id.cover_user_photo);
		String path = Environment.getExternalStorageDirectory().getPath();
		int recentId=util.getRecentId();
		if(recentId!=-1)
		{
			File file = new File(path + "/oldBookImage/Image/avatar_" + util.getRecentId()+".jpg");
			try
			{
				if(file.exists())
				{
					FileInputStream fis = new FileInputStream(file);
					Bitmap avatar = BitmapFactory.decodeStream(fis);
					cover_user_photo.setImageBitmap(avatar);
				}
			}
			catch (FileNotFoundException e)
			{

			}
		}
		//判断连接状态
		int statue=OldBookApplication.getSocketStatue();
        if(statue!=1)
		{
			Log.i("NewOldBOok","未连接");
			show("服务器连接错误，请设置服务器IP");

		}
		else
		{

			//判断是否第一次登录
			boolean isFirstIn = util.getisFirst();
			//初始化

			if(isFirstIn)
			{

				String dirPath=path + "/oldBookImage/";
				deleteDir(dirPath);

			}
			File file = new File(path+"/oldBookImage/Image/");
			// 检测文件夹是否存在，不存在则创建文件夹
			if (!file.exists() && !file.isDirectory())
				file.mkdirs();
			while(!file.exists())
			{

			}
			Initialize();
		}
	}

	/**
	 * 删除所有文件
	 */
    private void deleteDir(String path)
	{
		try
		{
			File dir = new File(path);
			if (dir == null || !dir.exists() || !dir.isDirectory())
				return;

			for (File file : dir.listFiles()) {
				if (file.isFile())
					file.delete(); // 删除所有文件
				else if (file.isDirectory())
					deleteDir(file.getPath()); // 递规的方式删除文件夹
			}
			dir.delete();// 删除目录本身
		}
		catch(Exception e)
		{
			Log.e("oldBook", "删除文件夹错误 " + new Date().toString());
		}
	}








	/**
	 * 在onResume方法里面先判断网络是否可用，再启动服务,这样在打开网络连接之后返回当前Activity时，会重新启动服务联网，
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		checkServer();
		System.out.println("autoLogin" + new Date().toString());
		autoLogin();

	}

	/**
	 * 检测服务器连接
	 */

	private void checkServer()
	{
		Client client=application.getClient();
		//PicUploadService client_pic=application.getClient_pic();

		if(OldBookApplication.getSocketStatue()!=1)
		{
			if (isNetworkAvailable())
			{
				try
				{
					//连接socket
					OldBookApplication.getClient().Start();
					//client_pic.connect();
					System.out.println("client start:" + OldBookApplication.getSocketStatue());
					//socket连接成功启动服务
					if (OldBookApplication.getSocketStatue()==1)
					{
						Intent service = new Intent(this, GetMsgService.class);
						startService(service);
					}

				}
				catch(Exception e)
				{

				}
			}
			else
			{
				dialog();
			}
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
	/**
	 * 加载控件
	 */
	protected void setupView()
	{
		Button btnLogin=(Button)findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(this);

		Button btnRegister=(Button)findViewById(R.id.btn_register_l);
		btnRegister.setOnClickListener(this);

		Button btnSetting=(Button)findViewById(R.id.btn_setting);
		btnSetting.setOnClickListener(this);

		etUsername=(EditText)findViewById(R.id.et_l_username);
		etPassword=(EditText)findViewById(R.id.et_l_password);

		cbRemPw=(CheckBox)findViewById(R.id.cb_l_password);
		cbRemPw.setOnCheckedChangeListener(remPasswordListener);

		cbAutoLogin=(CheckBox)findViewById(R.id.cb_l_auto);
		cbAutoLogin.setOnCheckedChangeListener(autoLoginListener);


	}

	/**
	 *设置自动登录
	 */
	private void autoLogin()
	{
        System.out.println("自动登录");
		//判断记住密码多选框的状态
		if(util.getSavePassword())
		{
			//设置默认是记录密码状态
			cbRemPw.setChecked(true);

			etUsername.setText(util.getUsername());
			etPassword.setText(util.getPassword());
			//判断自动登陆多选框状态

			if(util.getAutoLogin())
			{
				//设置默认是自动登录状态
				cbAutoLogin.setChecked(true);
				//跳转界面
				Login();
				//GetFriendsList();
			}
		}
	}

	/**
	 * 数据初始化
	 */
	private void Initialize()
	{
		progressDialog = ProgressDialog.show(LoginActivity.this, "提示", "正在初始化", true, false);
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Log.e("order", "GET_BOOK_LIST " + new Date().toString());
					Log.e("order", "GET_USER_LIST " + new Date().toString());
					client.getSocket().emit("GET_BOOK_LIST");
					client.getSocket().emit("GET_USER_LIST");

					while(!userOver)
					{

					}
					for(int i=0;i<avatarList.size();i++)
					{
						int id=avatarList.get(i);
						Download download_avatar=new Download("avatar_"+id);
						boolean result=download_avatar.checkDirectory();
						if(!result)
						{
							download_avatar.download();
							while(!download_avatar.downloadOver)
							{

							}
						}

					}
					for(int i=0;i<bookList.size();i++)
					{
						int id=bookList.get(i);
						Download download=new Download("book_"+id);
						boolean result=download.checkDirectory();
						if(!result)
						{
							download.download();
							while(!download.downloadOver)
							{

							}
						}

					}
				}
				catch (Exception e)
				{
					//show("加载列表错误");
					Log.e("oldBook", "加载列表出错");
				}
				//向handler发消息
				handler.sendEmptyMessage(0);
			}
		}).start();

	}
	/**
	 * 用Handler来更新UI
	 */
	private Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{

			//关闭ProgressDialog
			progressDialog.dismiss();

			//更新UI
			show("初始化完成！");
		}
	};






	/**
	 * 记住密码复选框
	 **/
	private OnCheckedChangeListener remPasswordListener=new OnCheckedChangeListener()
	{
		public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
		{
			if (cbRemPw.isChecked())
			{
				System.out.println("记住密码已选中");
				util.setSavePassword(true);
			}
			else
			{
				System.out.println("自动登录没有选中");
				util.setSavePassword(false);
			}
		}
	};


	/**
	 * 自动登录复选框
	 **/
	private OnCheckedChangeListener autoLoginListener=new OnCheckedChangeListener()
	{
		public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
		{
			if (cbAutoLogin.isChecked())
			{
				System.out.println("自动登录已选中");
				util.setAutoLogin(true);
			}
			else
			{
				System.out.println("自动登录没有选中");
				util.setAutoLogin(false);
			}
		}
	};

	/**
	 * 设置点击按钮事件
	 */
	@Override
	public void onClick(View v)
	{

		switch (v.getId())
		{

			case R.id.btn_register_l:
				goRegisterActivity();
				break;
			case R.id.btn_login:
				//checkServer();
				Log.i("oldBook", "登录按钮已点击");
				Login();
				break;
			case R.id.btn_setting:
				Intent intent = new Intent();
				intent.setClass(this,Setting.class);
				startActivity(intent);
				this.finish();
			default:
				break;
		}
	}

	/**
	 * 登录事件
	 */
	public void Login()
	{
		usernameValue = etUsername.getText().toString();
		passwordValue = etPassword.getText().toString();
		if(usernameValue.length()==0||passwordValue.length()==0)
		{
			show("必选项不能为空");
		}
		else
		{
			try
			{
				try
				{

					Socket socket=client.getSocket();
					Log.e("order", "LOGIN " + new Date().toString());
					socket.emit("LOGIN",usernameValue,passwordValue);

				}
				catch(Exception e)
				{

					System.out.println(e.toString());
				}
			}
			catch(Exception e)
			{
				Log.e("oldBook", "发送登录信息失败 " + new Date().toString());
			}

		}
	}

	/**
	 * 进入注册界面
	 */
	public void goRegisterActivity()
	{
		Intent intent = new Intent();
		intent.setClass(this, RegisterActivity.class);
		startActivity(intent);
		this.finish();
	}


	private Emitter.Listener loginResult = new Emitter.Listener()
	{
		@Override
		public void call(final Object... args)
		{
			try
			{
				Log.i("Socket.io","获取登录结果");
				JSONObject messageData_Json =(JSONObject)args[0];
				int id=messageData_Json.getInt("id");
				String userName=messageData_Json.getString("username");
				String petName=messageData_Json.getString("petname");
				String password=messageData_Json.getString("password");

				Message msg=new Message();
				msg.what=1;
				handler_login.sendMessage(msg);
				OldBookApplication.ID = id;
				OldBookApplication.PET_NAME =petName;
				OldBookApplication.USER_NAME = userName;

				util.setId(OldBookApplication.ID);
				util.setRecentId(OldBookApplication.ID);
				util.setIsFirst(false);
				util.setRecentUsername(userName);

				util.setIp(OldBookApplication.SERVER_IP);
				util.setPort(OldBookApplication.PORT);

				//client.getSocket().emit("GET_BOOK_LIST");
				Log.i("NewOldBook", "登录成功！" + new Date().toString());
				Log.e("order", "ADD_USER_IN_LIST " + new Date().toString());
				client.getSocket().emit("ADD_USER_IN_LIST",id,petName);
				if (cbRemPw.
						isChecked())
				{
					//记住用户名、密码、
					util.setUsername(usernameValue);
					util.setPassword(passwordValue);
					util.setSavePassword(true);
				}
				if (cbAutoLogin.isChecked()) {
					cbRemPw.setChecked(true);
					util.setUsername(usernameValue);
					util.setPassword(passwordValue);
					util.setSavePassword(true);
					util.setAutoLogin(true);
				}


				try
				{
					Intent intent = new Intent(LoginActivity.this, HomepageActivity.class);
					LoginActivity.this.startActivity(intent);
					LoginActivity.this.finish();
				} catch (Exception e) {
					Log.e("oldBook", "messageArray有问题" + new Date().toString());
				}
			}
			catch (Exception e)
			{
				int messageData = (int)args[0];
				if(messageData==-1)
				{
					Message msg=new Message();
					msg.what=-1;
					handler_login.sendMessage(msg);
					Log.i("NewOldBook", "登录失败！" + new Date().toString());
				}
				else if(messageData==-2)
				{
					Message msg=new Message();
					msg.what=-2;
					handler_login.sendMessage(msg);
					Log.i("NewOldBook", "已经注册过了！" + new Date().toString());
				}
			}
		}
	};

	/**
	 * 用Handler来更新UI
	 */
	private Handler handler_login = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case 1:show("登陆成功");
					break;
				case -1:show("登录失败");
					break;
				case -2:show("未被注册");
					break;
				default:
					break;
			}

		}
	};


	@Override
	public void onBackPressed()
	{// 捕获返回按键
		exitDialog(LoginActivity.this, "提示", "您真的要退出吗？");

	}

	/**
	 * 退出时的提示框
	 *
	 * @param context
	 *            上下文对象
	 * @param title
	 *            标题
	 * @param msg
	 *            内容
	 */
	private void exitDialog(Context context, String title, String msg)
	{
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg)
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{


						try
						{
							Log.e("order", "CLOSE_USER_IN_LIST " + new Date().toString());
							client.getSocket().emit("CLOSE_USER_IN_LIST",OldBookApplication.ID,OldBookApplication.PET_NAME);
							Thread.sleep(1000);
							client.getSocket().disconnect();

						}
						catch(Exception e)
						{
							Log.e("oldBook", "关闭应用错误 " + new Date().toString());
						}

						if (OldBookApplication.getSocketStatue()==1)
						{// 如果连接还在，说明服务还在运行
							// 关闭服务
							Intent service = new Intent(LoginActivity.this,
									GetMsgService.class);
							stopService(service);
						}
						System.exit(0);
						close();// 调用父类自定义的循环关闭方法
					}
				}).setNegativeButton("取消", null).create().show();
	}
	protected void initializedData()
	{

	}

	protected int getLayoutId()
	{
		return R.layout.login;
	}


	private Emitter.Listener bookListResult = new Emitter.Listener()
	{
		@Override
		public void call(final Object... args)
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						Object json = new JSONTokener(args[0].toString()).nextValue();
						if(json instanceof JSONObject)
						{

							JSONObject jsonObject = (JSONObject)json;
							int id=jsonObject.getInt("bookId");

							bookList.add(id);
						}
						else if (json instanceof JSONArray)
						{

							JSONArray jsonArray = (JSONArray)json;
							int count=jsonArray.length();
							for(int i=0;i<count;i++)
							{
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								int id=jsonObject.getInt("bookId");
								bookList.add(id);
							}
						}
					}
					catch(Exception e)
					{
						Log.e("oldBook", "图书列表载入错误 " + new Date().toString());
						e.printStackTrace();
					}
					bookOver=true;
				}

			}).start();

		}

	};

	private Emitter.Listener userListResult = new Emitter.Listener()
	{
		@Override
		public void call(final Object... args)
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						Object json = new JSONTokener(args[0].toString()).nextValue();
						if(json instanceof JSONObject)
						{

							JSONObject jsonObject = (JSONObject)json;
							int id=jsonObject.getInt("id");

							avatarList.add(id);
						}
						else if (json instanceof JSONArray)
						{

							JSONArray jsonArray = (JSONArray)json;
							int count=jsonArray.length();
							for(int i=0;i<count;i++)
							{
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								int id=jsonObject.getInt("id");
								avatarList.add(id);
							}
						}
					}
					catch(Exception e)
					{
						Log.e("oldBook", "图书列表载入错误 " + new Date().toString());
						e.printStackTrace();
					}
					userOver=true;
				}

			}).start();

		}

	};




}
