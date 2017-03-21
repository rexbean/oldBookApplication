package com.oldbook.android.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.oldbook.android.Application.OldBookApplication;
import com.oldbook.android.R;
import com.oldbook.android.client.Client;
import com.oldbook.android.entity.MessageEntity;
import com.oldbook.android.entity.MessageType;
import com.oldbook.android.entity.UserEntity;
import com.oldbook.android.net.GetMsgService;
import com.oldbook.android.util.SharePreferenceUtil;
import com.oldbook.android.widget.CircularImage;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.socket.emitter.Emitter;

//import com.oldbook.android.net.PicUploadService;

public class RegisterActivity extends AppBaseActivity implements OnClickListener
{


	private Button btnRegister;  //注册按钮
	private EditText etUsername;   //用户名
	private EditText etPassword;   //密码
	private EditText etConfirmPassword; //确认密码
	private EditText etPetName;		//昵称
	private OldBookApplication application;
	private ImageView ivAvatar		;//头像
	private String AvatarName;
    private SharePreferenceUtil util;
    private File tempFile;
	private String s_Username;
	private String s_Petname;
	private String s_Password;
	private Client client;
	private boolean registOver=false;

	SimpleDateFormat formatter;
	String name;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

        util=new SharePreferenceUtil(RegisterActivity.this, OldBookApplication.SAVE_USER);
		client=OldBookApplication.getClient();
		client.getSocket().on("REGISTER",registerResult);
		client.getSocket().on("UPLOAD_PIC_RESULT",uploadResult);

		/**
		 * 摄者圆形头像
		 */
		CircularImage cover_user_photo = (CircularImage) findViewById(R.id.cover_user_photo);
		//cover_user_photo.setImageResource(R.drawable.logo);

	}

	/**
	 * 加载控件
	 */
	@Override
	protected void setupView()
	{

		btnRegister=(Button)findViewById(R.id.btn_register);
		btnRegister.setOnClickListener(this);

		etUsername=(EditText)findViewById(R.id.et_r_username);
		etPassword=(EditText)findViewById(R.id.et_r_password);
		etConfirmPassword=(EditText)findViewById(R.id.et_r_confirm_password);
		etPetName=(EditText)findViewById(R.id.et_r_petname);

		ivAvatar=(ImageView)findViewById(R.id.cover_user_photo);
		ivAvatar.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				showCustomAlertDialog();
			}
		});

	}

	/**
	 * 设置点击按钮事件
	 */
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.btn_register:
				Register();
				break;
			default:
				break;

		}
	}

	public void Register()
	{
		if (etUsername.getText().toString().equals("")||etPassword.getText().toString().equals("")||etConfirmPassword.getText().toString().equals(""))
		{
			show("必填项不能为空");
		}
		else if(!etPassword.getText().toString().equals(etConfirmPassword.getText().toString()))
		{
			etPassword.setText("");
			etConfirmPassword.setText("");
			show("两次密码输入不一致");
		}
		else if(AvatarName==null)
		{
			show("请设置头像！");
		}
		else
		{
			//获取用户名 密码 昵称
			s_Username=etUsername.getText().toString();
			s_Password=etPassword.getText().toString();
			s_Petname=etPetName.getText().toString();

			try
			{
				Log.e("order", "REGISTER " + new Date().toString());
				client.getSocket().emit("REGISTER",s_Username,s_Password,s_Petname);

			}
			catch(Exception e)
			{
				Log.i("oldBook", "注册信息发送错误");
			}


		}

	}


	private Emitter.Listener registerResult = new Emitter.Listener()
	{
		@Override
		public void call(final Object... args)
		{
			try
			{
				JSONObject messageData_Json = (JSONObject)args[0];
				int id = messageData_Json.getInt("id");
				String userName = messageData_Json.getString("username");
				String petName = messageData_Json.getString("petname");
				String password = messageData_Json.getString("password");

				Message msg=new Message();
				msg.what=1;
				handler_register.sendMessage(msg);

				OldBookApplication.ID=id;
				OldBookApplication.PET_NAME=petName;
				OldBookApplication.USER_NAME=userName;
				OldBookApplication.Evaluation = String.valueOf(100);

				util.setId(OldBookApplication.ID);
				util.setRecentId(OldBookApplication.ID);
				util.setRecentUsername(userName);
				util.setIsFirst(false);
				Log.i("NewOldBook", "注册成功！" + new Date().toString());

			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						String path = Environment.getExternalStorageDirectory().getPath();
						File file = new File(path + "/oldBookImage/Image/" + AvatarName);
						int size=(int)file.length();
						byte[] buffer=new byte[size];
						InputStream in =new FileInputStream(file);
						in.read(buffer);
						Log.e("order", "PIC_CLIENT_TO_SERVER " + new Date().toString());
						client.getSocket().emit("PIC_CLIENT_TO_SERVER", "avatar_" + OldBookApplication.ID+".jpg", buffer);
						//重命名头像文件
						File newFile = new File(path + "/oldBookImage/Image/" + "avatar_"+OldBookApplication.ID + ".jpg");
						Boolean result = file.renameTo(newFile);
						if (result)
						{
							System.out.println("重命名成功！");
						}
						else
						{
							System.out.println("重命名失败！");
						}



					}
					catch (IOException e)
					{
						Log.e("Socket.io","找不到指定文件");
						e.printStackTrace();
					}


				}
			}).start();

			}
			catch (Exception e)
			{
				int messageData =(int)args[0];
				if (messageData == -1)
				{
					Message msg=new Message();
					msg.what=-1;
					handler_register.sendMessage(msg);
					Log.i("NewOldBook", "注册失败！" + new Date().toString());
				}
			}
		}
	};

	private Emitter.Listener uploadResult = new Emitter.Listener()
	{
		@Override
		public void call(final Object... args)
		{
			if(!registOver)
			{
				try {
					Message msg = new Message();
					if (args[1] == null) {
						msg.what = 2;
						handler_register.sendMessage(msg);
						Log.e("order", "ADD_USER_IN_LIST " + new Date().toString());
						client.getSocket().emit("ADD_USER_IN_LIST", OldBookApplication.ID, OldBookApplication.PET_NAME);
						registOver=true;
						Intent intent = new Intent(RegisterActivity.this, HomepageActivity.class);
						RegisterActivity.this.startActivity(intent);
						RegisterActivity.this.finish();

					}
					if (args[1] != null) {
						String error = (String) args[1];

						msg.what = -2;
						handler_register.sendMessage(msg);
					}
					finish();
				} catch (Exception e) {
					Log.e("Socket.io", "读取上传结果失败");
					e.printStackTrace();
				}
			}

		}
	};


	/**
	 * 用Handler来更新UI
	 */
	private Handler handler_register = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 1: show("注册成功");
					break;
				case -1:show("注册失败");
					break;
				case 2: show("上传图片成功");
					break;
				case -2:show("上传失败");
					break;
				default:
					break;
			}

			;
		}
	};

	protected void initializedData()
	{

	}

	protected int getLayoutId()
	{
		return R.layout.register;
	}

	/**
	 * 显示选择对话框
	 */

	private void showCustomAlertDialog()
	{
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.show();
		Window win = alertDialog.getWindow();

		WindowManager.LayoutParams lp = win.getAttributes();
		win.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		lp.alpha = 0.7f;
		win.setAttributes(lp);
		win.setContentView(R.layout.register_dialog);

		Button cancelBtn = (Button) win.findViewById(R.id.camera_cancel_r);
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.cancel();
			}
		});
		Button camera_phone = (Button) win.findViewById(R.id.camera_phone_r);
		camera_phone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				systemPhoto();
			}

		});

	}


	private final int SYS_INTENT_REQUEST = 0XFF01;    //返回码

	/**
	 * 打开系统相册
	 */
	private void systemPhoto()
    {

        Intent intent = new Intent("android.intent.action.PICK");
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");


		//文件命名
		formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		name = formatter.format(System.currentTimeMillis()) + ".jpg";

		//存储路径
		String path = Environment.getExternalStorageDirectory().getPath();
		//File tempFile=new File("/sdcard//"+Calendar.getInstance().getTimeInMillis()+".jpg"); // 以时间秒为文件名
		File temp = new File(path + "/oldBookImage/temp/");//自已项目 文件夹
		if (!temp.exists())
		{
			temp.mkdirs();
		}

		tempFile = new File(path+"/oldBookImage/temp/"+name);

		intent.putExtra("output", Uri.fromFile(tempFile));  // 专入目标文件
		intent.putExtra("outputFormat", "JPEG"); //输入文件格式
        intent.putExtra("crop", "true");// 才能出剪辑的小方框，不然没有剪辑功能，只能选取图片
        intent.putExtra("aspectX", 1); // 出现放大和缩小
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 120);//输出图片大小
        intent.putExtra("outputY", 120);

		//Intent wrapperIntent = Intent.createChooser(intent, "选择图片"); //开始 并设置标题
		startActivityForResult(intent, SYS_INTENT_REQUEST); // 设返回 码为 1  onActivityResult 中的 requestCode 对应
		//startActivityForResult(intent, SYS_INTENT_REQUEST);

	}



	/**
	 * 获取返回数据
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == SYS_INTENT_REQUEST && resultCode == RESULT_OK&& data != null)
		{
			try
			{
                Bitmap bitmap= BitmapFactory.decodeFile(tempFile.getAbsolutePath());
				FileOutputStream b = null;
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				String name = formatter.format(System.currentTimeMillis()) + ".jpg";
				AvatarName=name;
				String path = Environment.getExternalStorageDirectory().getPath();
				File file = new File(path+"/oldBookImage/Image");
				// 检测文件夹是否存在，不存在则创建文件夹
				if (!file.exists() && !file.isDirectory())
					file.mkdirs();
				String fileName = file.getPath() + "/" + name;
				//Log.i("oldbook", "camera file path:" + fileName);
				try
				{
					b = new FileOutputStream(fileName);
					//把数据写入文件
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
				finally
				{
					try
					{
						if (b == null)
						{
							Toast.makeText(getApplicationContext(), "null b", Toast.LENGTH_SHORT).show();
							return;
						}
						b.flush();
						b.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				//showImgs(bitmap, true);
				SetImage(bitmap);
			}
			catch (Exception e)
            {
				e.printStackTrace();
			}

		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	/**
	 * @param bitmap
	 * @return 压缩后的bitmap
	 */
	private Bitmap compressionBigBitmap(Bitmap bitmap, boolean isSysUp) {
		Bitmap destBitmap = null;
		/* 图片宽度调整为100，大于这个比例的，按一定比例缩放到宽度为100 */
		if (bitmap.getWidth() > 80) {
			float scaleValue = (float) (80f / bitmap.getWidth());
			System.out.println("缩放比例---->" + scaleValue);

			Matrix matrix = new Matrix();
			/* 针对系统拍照，旋转90° */
			if (isSysUp)
				matrix.setRotate(90);
			matrix.postScale(scaleValue, scaleValue);

			destBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
			int widthTemp = destBitmap.getWidth();
			int heightTemp = destBitmap.getHeight();
			Log.i("zhiwei.zhao", "压缩后的宽高----> width: " + heightTemp
					+ " height:" + widthTemp);
		} else {
			return bitmap;
		}
		return destBitmap;

	}


	private void SetImage(Bitmap bitmap)
	{
		ivAvatar.setImageBitmap(bitmap);
	}

	@Override
	public void onBackPressed()
	{// 捕获返回按键
		exitDialog(RegisterActivity.this, "提示", "您真的要退出吗？");

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
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which)
					{

						try
						{
							Client client = application.getClient();


							//PicUploadService pus = application.getClient_pic();
							//pus.close();

							//out.setStart(false);
							//client.getClientInputThread().setStart(false);
							//}
							//client_pic.close();
							//application.setIsClose(true);
							//application.setClientStart(false);
							moveTaskToBack(false);
							if (OldBookApplication.getSocketStatue()==1)
							{// 如果连接还在，说明服务还在运行
								// 关闭服务
								Intent service = new Intent(RegisterActivity.this,GetMsgService.class);
								stopService(service);
							}
						}
						catch(Exception e)
						{
							Log.i("oldBook", "关闭应用失败 " + new Date().toString());
						}
						//System.exit(0);
						//close();// 调用父类自定义的循环关闭方法
					}
				}).setNegativeButton("取消", null).create().show();
	}

}
