package com.oldbook.android.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
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
import com.oldbook.android.entity.BookEntity;
import com.oldbook.android.entity.MessageEntity;
import com.oldbook.android.entity.MessageType;
import com.oldbook.android.util.SharePreferenceUtil;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;

import io.socket.emitter.Emitter;

//import com.oldbook.android.net.PicUploadService;


public class TakePicActivity extends AppBaseActivity implements OnClickListener {

	private final static String TAG = "CameraActivity";
	private ImageView ivBookSurface;
	private EditText etBookName;
	private EditText etBookAuthor;
	private EditText etBookPublishing;
	private EditText etBookNumber;

	private OldBookApplication application;
	private Bitmap bitmap;
	private Button btnAdd;

	private String bookName;
	private String bookAuthor;
	private String bookPublishing;
	private int bookSurface;
	private String bookNumber;
	private String SurfaceName;
	private SharePreferenceUtil util;
	private Client client;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		application = (OldBookApplication) this.getApplicationContext();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		util=new SharePreferenceUtil(TakePicActivity.this, OldBookApplication.SAVE_USER);
		client= OldBookApplication.getClient();
		client.getSocket().on("NEWBOOK",newBookResult);

		showCustomAlertDialog();
		ivBookSurface.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{

				showCustomAlertDialog();
			}

		});
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
		win.setContentView(R.layout.dialog);

		Button cancelBtn = (Button) win.findViewById(R.id.camera_cancel);
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.cancel();
			}
		});
		Button camera_phone = (Button) win.findViewById(R.id.camera_phone);
		camera_phone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				systemPhoto();
			}

		});
		Button camera_camera = (Button) win.findViewById(R.id.camera_camera);
		camera_camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cameraPhoto();
			}

		});

	}

	@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                AddNewBook();
                break;
            default:
                break;

        }
    }
    private void AddNewBook()
    {
		bookName=etBookName.getText().toString();
		bookAuthor=etBookAuthor.getText().toString();
		bookPublishing=etBookPublishing.getText().toString();
		bookNumber=etBookNumber.getText().toString();



		/*
		MessageEntity me=new MessageEntity();
		me.setType(MessageType.NEW_BOOK);
		me.setSender(OldBookApplication.ID);
		me.setReceiver(0);
        BookEntity be =new BookEntity(0,bookName,bookAuthor,bookPublishing,bookNumber, OldBookApplication.ID,false);
		me.setObject(be);
		*/
		Log.e("order", "NEWBOOK " + new Date().toString());
		client.getSocket().emit("NEWBOOK",bookName,bookAuthor,bookPublishing,bookNumber,OldBookApplication.ID,false);
		//ClientOutputThread out = client.getClientOutputThread();
		//out.setMsg(me);
	}
	private final int SYS_INTENT_REQUEST = 0XFF01;    //返回码
	private final int CAMERA_INTENT_REQUEST = 0XFF02;

	/**
	 * 打开系统相册
	 */
	private void systemPhoto() {

		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, SYS_INTENT_REQUEST);

	}

	/**
	 * 调用相机拍照
	 */
	private void cameraPhoto() {
		String sdStatus = Environment.getExternalStorageState();
		/* 检测sd是否可用 */
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "SD卡不可用！", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		startActivityForResult(intent, CAMERA_INTENT_REQUEST);
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
				Uri selectedImage = data.getData();
				Bitmap bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
				FileOutputStream b = null;
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				String name = formatter.format(System.currentTimeMillis()) + ".jpg";
				//String name="0.jpg";
				SurfaceName=name;
				String path = Environment.getExternalStorageDirectory().getPath();
				File file = new File(path+"/oldBookImage/Image");
				/** 检测文件夹是否存在，不存在则创建文件夹 **/
				if (!file.exists() && !file.isDirectory())
					file.mkdirs();
				String fileName = file.getPath() + "/" + name;
				//Log.i("oldbook", "camera file path:" + fileName);
				try
				{
					b = new FileOutputStream(fileName);
						/* 把数据写入文件 */
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
			catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (requestCode == CAMERA_INTENT_REQUEST
				&& resultCode == RESULT_OK && data != null) {
			cameraCamera(data);
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

	/**
	 * @param data
	 *            拍照后获取照片
	 */
	private void cameraCamera(Intent data)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String name = formatter.format(System.currentTimeMillis()) + ".jpg";
		//String name="1.jpg";
		SurfaceName=name;
		//Log.i("oldbook", "image name:" + name);
		Toast.makeText(this, name, Toast.LENGTH_LONG).show();
		Bundle bundle = data.getExtras();
		/* 获取相机返回的数据，并转换为Bitmap图片格式 */
		Bitmap bitmap = (Bitmap) bundle.get("data");
		FileOutputStream b = null;

		String path = Environment.getExternalStorageDirectory().getPath();
		File file = new File(path+"/oldBookImage/Image");
		/** 检测文件夹是否存在，不存在则创建文件夹 **/
		if (!file.exists() && !file.isDirectory())
			file.mkdirs();
		String fileName = file.getPath() + "/" + name;
		//Log.i("oldbook", "camera file path:" + fileName);
		try {
			b = new FileOutputStream(fileName);
			/* 把数据写入文件 */
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);
		} catch (FileNotFoundException e) {
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//showImgs(bitmap, true);
		SetImage(bitmap);
	}



	protected void setupView()
	{

        ivBookSurface=(ImageView)findViewById(R.id.iv_bookSurface_n);
        etBookName=(EditText)findViewById(R.id.et_bookName_n);
        etBookAuthor=(EditText)findViewById(R.id.et_author_n);
        etBookPublishing=(EditText)findViewById(R.id.et_publishing_n);
        etBookNumber=(EditText)findViewById(R.id.et_number_n);

        btnAdd=(Button)findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);

        bookName=etBookName.getText().toString();
        bookAuthor=etBookAuthor.getText().toString();
        bookPublishing=etBookPublishing.getText().toString();
        bookNumber=etBookNumber.getText().toString();

	}

	private void SetImage(Bitmap bitmap)
	{
		ivBookSurface.setImageBitmap(bitmap);
	}



	private Emitter.Listener newBookResult = new Emitter.Listener()
	{

		@Override
		public void call(final Object... args)
		{
			Log.i("Socket.io","add newbook "+new Date().toString());
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{

							JSONObject jsonObject = (JSONObject)args[0];
							int id=jsonObject.getInt("bookId");
							String bookName=jsonObject.getString("bookName");
							String bookAuthor=jsonObject.getString("bookAuthor");
							String bookPublishing=jsonObject.getString("bookPublishing");
							String bookNumber=jsonObject.getString("bookNumber");
							int owner=jsonObject.getInt("bookOwner");
							boolean isLent=jsonObject.getBoolean("isLent");

							BookEntity be=new BookEntity(id,bookName,bookAuthor,bookPublishing,bookNumber,owner,isLent);
							int j=0;
							for(int i=0;i<HomepageActivity.bookListItems.size();i++)
							{
								if(HomepageActivity.bookListItems.get(i).getId()==be.getId())
								{
									j=1;

								}
							}
							if(j==0)
							{
								HomepageActivity.bookListItems.add(be);
							}

						try
						{
							String path = Environment.getExternalStorageDirectory().getPath();
							File file = new File(path + "/oldBookImage/Image/" + SurfaceName);
							int size=(int)file.length();
							byte[] buffer=new byte[size];
							InputStream in =new FileInputStream(file);
							in.read(buffer);
							Log.e("order", "PIC_CLIENT_TO_SERVER " + new Date().toString());
							client.getSocket().emit("PIC_CLIENT_TO_SERVER", "book_" + id+".jpg", buffer);
							//重命名头像文件
							File newFile = new File(path + "/oldBookImage/Image/" + "book_"+id + ".jpg");
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
					catch(JSONException e)
					{
						Log.e("oldBook", "添加图书错误 " + new Date().toString());
						e.printStackTrace();
					}
					handler_book.sendEmptyMessage(0);
				}

			}).start();

		}

	};

	private Emitter.Listener uploadResult = new Emitter.Listener()
	{
		@Override
		public void call(final Object... args)
		{
			try
			{
				if(args.length==1)
				{
					show("图片上传成功");
					finish();

				}
				else if(args.length>1)
				{
					String error=(String)args[1];
					show(error);
					finish();
				}

			}
			catch(Exception e)
			{
				Log.e("Socket.io","读取上传结果失败");
				e.printStackTrace();
			}

		}
	};



	private android.os.Handler handler_book = new android.os.Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			HomepageActivity.bookListViewAdapter.notifyDataSetChanged();
			show("添加新书完成");
			finish();
		}
	};
















	public void getMessage(MessageEntity msg)
	{
		if (msg != null)
		{
/*
			switch (msg[0])
			{
				case "NEW_BOOK":
					int result=Integer.parseInt(msg[1]);
					if(result>0)
					{

						//String path = Environment.getExternalStorageDirectory().getPath();
						//File file = new File(path+"/oldBookImage/"+SurfaceName);
						//String sendinfo="surface_"+result+"_"+SurfaceName+"_"+file.length();
						//PicUploadService pus=application.getClient_pic();
						//pus.setFile(file);
						//pus.setUploadInfo(sendinfo);
						//int status=pus.sendPic();
						//发送图片              //2016-3-9
						show("添加成功");

						/*
						File newFile = new File(path + "/oldBookImage/surface/" + result + ".jpg");  //重命名为zhidian1
						Boolean result_rename = file.renameTo(newFile);
						if (result_rename) {
							System.out.println("重命名成功！");
						} else {
							System.out.println("重命名失败！");
						}

						Client client=OldBookApplication.getClient();
						client.getSocket().emit("SURFACE_SUCCESS");

						//MessageEntity me=new MessageEntity();
						//me.setType(MessageType.SURFACE_SUCCESS);
						//Client client= application.getClient();
						//ClientOutputThread out = client.getClientOutputThread();
						//out.setMsg(me);
						TakePicActivity.this.finish();

					}
					else
						show("添加失败");
					break;
				default:
					break;

			}
	*/
		}

	}


	protected void initializedData()
	{

	}
	protected int getLayoutId()
	{
		return R.layout.new_book;
	}

}  