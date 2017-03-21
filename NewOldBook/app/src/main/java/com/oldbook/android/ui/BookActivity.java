package com.oldbook.android.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.StrictMode;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.oldbook.android.Application.OldBookApplication;
import com.oldbook.android.R;
import com.oldbook.android.client.Client;
import com.oldbook.android.entity.BookEntity;
import com.oldbook.android.entity.BorrowEntity;
import com.oldbook.android.entity.MessageEntity;
import com.oldbook.android.entity.MessageType;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import io.socket.emitter.Emitter;

public class BookActivity extends AppBaseActivity implements OnClickListener
{
	private TextView tvBookName;
	private TextView tvAuthor;
	private TextView tvPublishing;
	private TextView tvNumber;
	private TextView tvPetname;
	private ImageView ivBook;
	private Button btnBorrow;
	private Button btnExchange;





	private int bookId;
	private String bookName;
	private String author;
	private String publishing;
	private String number;
	private int owner;
	private int size;
	private boolean isLent;
	private Client client;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book);
   		client=OldBookApplication.getClient();
		client.getSocket().on("BORROW",borrowResult);
		//client.getSocket().on("UPDATE_BOOK",updateResult);

		Intent intent=getIntent();
		ivBook=(ImageView)findViewById(R.id.iv_bookSurface_b);
		tvBookName=(TextView)findViewById(R.id.tv_bookName_b);
		tvAuthor=(TextView)findViewById(R.id.tv_author_b);
		tvPublishing=(TextView)findViewById(R.id.tv_publishing_b);
		tvNumber=(TextView)findViewById(R.id.tv_number_b);



		bookId=intent.getIntExtra("bookId",0);
		bookName=intent.getStringExtra("bookName");// 第一个参数指定name，android规范是以包名+变量名来命名，后面是各种类型的数据类型
		author=intent.getStringExtra("author");
		publishing=intent.getStringExtra("publishing");
		number=intent.getStringExtra("number");
		owner=intent.getIntExtra("owner", 0);
		size=intent.getIntExtra("size", 0);
		isLent=intent.getBooleanExtra("isLent", false);

		if(!isLent)
		{

			btnBorrow = (Button) findViewById(R.id.btn_borrow);
			btnBorrow.setEnabled(true);
			btnBorrow.setText("借阅");
			btnBorrow.setOnClickListener(this);
		}
		else
		{
			btnBorrow=(Button)findViewById(R.id.btn_borrow);
			btnBorrow.setEnabled(false);
			btnBorrow.setText("已被借阅");
		}
		//tvPetname.setText(owner);
		tvBookName.setText(bookName);
		tvAuthor.setText(author);
		tvPublishing.setText(publishing);
		tvNumber.setText(number);

		String path = Environment.getExternalStorageDirectory().getPath();
		String fileName = path+"/oldBookImage/Image/book_"+bookId+".jpg";
		File file = new File(fileName);

		if(file.exists())
		{
			Bitmap bitmap = BitmapFactory.decodeFile(fileName);
			ivBook.setImageBitmap(bitmap);
		}
		else
		{
			//PicUploadService pus=application.getClient_pic();
			//String sendInfo = "surface_"+bookId;
			//pus.setDownloadInfo(sendInfo);
			//pus.setSize(size);
			//pus.setId(bookId);
			//pus.setType(1);
			//pus.receivePic();
			//下载图片               //2016-3-9
			//Bitmap bitmap = BitmapFactory.decodeFile(fileName);
			//ivBook.setImageBitmap(bitmap);
		}



	}
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.btn_borrow:
				borrow(BookActivity.this, "提示", "请一个月内归还，您确定要借书吗？");

				break;

		}

	}
	public void borrow(Context context, String title, String msg)
	{
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg)
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						MessageEntity me=new MessageEntity();
						me.setType(MessageType.BORROW);
						BorrowEntity boe=new BorrowEntity();

						BookEntity be=new BookEntity(bookId,bookName,author,publishing,number,owner,true);

						Calendar c = Calendar.getInstance();
						int year=c.get(Calendar.YEAR);
						int month=c.get(Calendar.MONTH);
						int day=c.get(Calendar.DATE);

						int fromUser=OldBookApplication.ID;
						int getUser=owner;
						String bookName=be.getBookName();
						String borrowTime=year + "_" + month + "_" + day;
						String returnTime=null;
						String SReturnTime=year + "_" + (month + 1) + "_" + day;
						String statue="LEND";
						int evaluation=-1;

						Client client=OldBookApplication.getClient();
						Log.e("order","BORROW "+new Date().toString());
						client.getSocket().emit("BORROW",bookName,fromUser,getUser,borrowTime,SReturnTime,returnTime,statue,evaluation,bookId);
						Log.e("Socket.io","书籍编号是"+bookId+" "+new Date().toString());
						for(int i=0;i<HomepageActivity.bookListItems.size();i++)
						{
							if(HomepageActivity.bookListItems.get(i).getId()==bookId)
							{
								HomepageActivity.bookListItems.get(i).setIsLent(true);
							}
						}
						Message msg=new Message();
						msg.what=1;
						handler_borrow_result.sendMessage(msg);
					}
				}).setNegativeButton("取消", null).create().show();
	}

	private Emitter.Listener borrowResult = new Emitter.Listener()
	{

		@Override
		public void call(final Object... args)
		{
			Log.i("Socket.io", "借书 " + new Date().toString());
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						JSONObject jsonObject = (JSONObject)args[0];
						int id=jsonObject.getInt("borrowId");
						String bookName=jsonObject.getString("bookName");
						int fromUser=jsonObject.getInt("fromUser");
						int getUser =jsonObject.getInt("getUser");
						String sReturnTime=jsonObject.getString("sReturnTime");
						//String returnTime=jsonObject.getString("returnTime");
						String statue=jsonObject.getString("statue");
						int evaluation =Integer.parseInt(jsonObject.getString("evaluation"));
						int bookId=Integer.parseInt(jsonObject.getString("bookId"));
						//int borrowId,String bookName,int fromUser,int getUser,String sreturnTime,String statue,int evaluation)
						BorrowEntity boe=new BorrowEntity(id,bookName,0,getUser,sReturnTime,statue,evaluation,bookId);
						int j=0;
						for(int i=0;i<HomepageActivity.borrowRecordList.size();i++)
						{
							if(HomepageActivity.borrowRecordList.get(i).getBorrowId()==id)
								j++;
						}
						if(j==0)
						{
							HomepageActivity.borrowRecordList.add(boe);
						}
						Log.i("Socket.io",String.valueOf(bookId));
						Log.e("order", "UPDATE_BOOK "+new Date().toString());
						client.getSocket().emit("UPDATE_BOOK","true",bookName,bookId);

					}
					catch(JSONException e)
					{
						Log.e("oldBook", "借阅图书错误 " + new Date().toString());
						e.printStackTrace();
					}
					handler_borrow.sendEmptyMessage(0);
				}

			}).start();

		}

	};

	private android.os.Handler handler_borrow = new android.os.Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			HomepageActivity.borrowRecordListViewAdapter.notifyDataSetChanged();
			show("借书完成");
			finish();
		}
	};

	private android.os.Handler handler_borrow_result = new android.os.Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			if(msg.what==1) {
				btnBorrow.setEnabled(false);
				btnBorrow.setText("已被借阅");
			}
		}
	};









	protected void setupView()
	{


	}
	protected void initializedData()
	{

	}
	protected int getLayoutId()
	{
		return R.layout.book;
	}






}
