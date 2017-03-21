package com.oldbook.android.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.oldbook.android.Application.OldBookApplication;

import com.oldbook.android.R;
import com.oldbook.android.client.Client;
import com.oldbook.android.entity.BookEntity;
import com.oldbook.android.entity.BorrowEntity;
import com.oldbook.android.entity.ChatMsgEntity;
import com.oldbook.android.entity.Download;
import com.oldbook.android.entity.FriendEntity;
import com.oldbook.android.entity.MessageEntity;
import com.oldbook.android.entity.MessageType;
import com.oldbook.android.net.GetMsgService;
import com.oldbook.android.util.MessageDB;
import com.oldbook.android.util.SharePreferenceUtil;
import com.oldbook.android.widget.BookListViewAdapter;
import com.oldbook.android.widget.ChatListViewAdapter;
import com.oldbook.android.widget.CircularImage;
import com.oldbook.android.widget.MyListView;
import com.oldbook.android.widget.MyPagerAdapter;
import com.oldbook.android.widget.PersonalInforListViewAdapter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.socket.emitter.Emitter;

//import com.oldbook.android.net.PicUploadService;


public class HomepageActivity extends AppBaseActivity implements OnClickListener
{
	private ViewPager mPager;
	int index=0;
	private int offset = 0;// 动画图片偏移量
	private int bmpW;// 动画图片宽度
	private MessageDB messageDB;// 消息数据库对象

	private static final int BookPAGE = 0;//图书页面
	private static final int PersonalPAGE = 1;// 个人信息页面
	private static final int ChatPAGE = 2;// 聊天页面

	private int currentIndex = PersonalPAGE; // 默认选中第2个，可以动态的改变此参数值

	private ImageView imgBook;// 图书
	private ImageView imgPersonal;// 个人信息
	private ImageView imgChat;// 聊天

	private MenuInflater mi;

	private SharePreferenceUtil util;

	public List<View> mListViews;// Tab页面

	private ImageView myHeadImage;// 头像
	private ImageView cursor;// 标题背景图片

	private TextView myName;
	public static TextView myEvaluation;

	private LinearLayout layout_body_activity;

	// 好友列表自定义listView


	//图书部分
    public static String bookStr=null;
	public static BookListViewAdapter bookListViewAdapter;
	public static List<BookEntity> bookListItems=new ArrayList<BookEntity>();
	private MyListView lvBook;

	//聊天部分
	private MyListView lvChat;
	public static String friendStr=null;
	private ChatListViewAdapter chatListViewAdapter;
	private List<FriendEntity> feList=new ArrayList<FriendEntity>();
	private int activeId;
	//个人信息
	private MyListView lvBorrowRecord;
	public static PersonalInforListViewAdapter borrowRecordListViewAdapter;
	public static List<BorrowEntity> borrowRecordList=new ArrayList<BorrowEntity>();
	public static String borrowStr=null;


	private Button addNewBook;
	private OldBookApplication application;
	private int newNum = 0;

	private String g_evaluation;
	private Client client;
	private boolean downloadOver=false;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.book_list);
		application= (OldBookApplication) this.getApplicationContext();
		messageDB=new MessageDB(this);
        util=new SharePreferenceUtil(HomepageActivity.this, OldBookApplication.SAVE_USER);

		client=OldBookApplication.getClient();


        client.getSocket().on("PERSONAL_EVALUATION", getEvaluationResult);



		client.getSocket().on("BORROWLIST_FROMUSER", getBorrowResult_from);
		client.getSocket().on("BORROWLIST_GETUSER", getBorrowResult_get);

		client.getSocket().on("UPLOAD_PIC_RESULT_ELSE",uploadResult);


		client.getSocket().on("GET_BOOK_LIST", getBookListResult);
		client.getSocket().on("ONLINE_USER_LIST",getOnlineResult);

		client.getSocket().on("GLOBAL_HELLO",OnlineResult);
		client.getSocket().on("GLOBAL_GOODBYE",OfflineResult);

		client.getSocket().on("CHATMSG",getMessage);

		client.getSocket().on("UPDATE_BOOK",updateBookResult);
		client.getSocket().on("BORROW2",borrowResult);

		client.getSocket().on("UPDATE_STATUE",returnResult);
		client.getSocket().on("UPDATE_STATUE_GETUSER",returnResult_get);

		client.getSocket().on("UPDATE_EVALUATION",evaluationResult);
		client.getSocket().on("UPDATE_EVALUATION_FROMUSER",evaluationResult_from);

		client.getSocket().on("UPDATE_EVALUATION2",evaluation2Result);

		client.getSocket().on("NEWBOOK_ELSE",newBookResult);

		Log.e("order", "PERSONAL_EVALUATION " + new Date().toString());
		Log.e("order", "BORROWLIST_FROMUSER "+new Date().toString());
		client.getSocket().emit("PERSONAL_EVALUATION", OldBookApplication.ID);
		client.getSocket().emit("BORROWLIST_FROMUSER", OldBookApplication.ID);
		initImageView();
		setupView();

        CircularImage cover_user_photo = (CircularImage)findViewById(R.id.cover_user_photo);
        String path = Environment.getExternalStorageDirectory().getPath();
        File file = new File(path+"/oldBookImage/Image/avatar_"+util.getId()+".jpg");
        try
        {
			if(file.exists())
			{
				FileInputStream fis = new FileInputStream(file);
				Bitmap avatar = BitmapFactory.decodeStream(fis);
				cover_user_photo.setImageBitmap(avatar);
			}
        }
        catch(FileNotFoundException e)
        {

        }




		lvBook.setOnItemClickListener(new OnItemClickListener() {
										  @Override
										  public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
											  // TODO Auto-generated method stub
											  // ListView Clicked item index
											  int itemPosition = position;

											  // ListView Clicked item value
											  // String  itemValue    = (String) lvBook.getItemAtPosition(position);
											  BookEntity be = (BookEntity) bookListViewAdapter.getItem(position);

											  // Show Alert
											  //Toast.makeText(getApplicationContext(),
											  //"Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
											  //.show();

											  Intent intent = new Intent();
											  try
											  {
												  intent.putExtra("bookId", be.getId());
												  intent.putExtra("bookName", be.getBookName());// 第一个参数指定name，android规范是以包名+变量名来命名，后面是各种类型的数据类型
												  intent.putExtra("author", be.getBookAuthor());
												  intent.putExtra("publishing", be.getBookPublishing());
												  intent.putExtra("number", be.getbookNumber());
												  intent.putExtra("owner", be.getOwner());
												  intent.putExtra("size", be.getSize());
												  intent.putExtra("isLent", be.getIsLent());
											  } catch (Exception e) {
												  Log.e("oldBook", "开启bookActivity错误 " + new Date().toString());
											  }

											  intent.setClass(HomepageActivity.this, BookActivity.class);
											  HomepageActivity.this.startActivity(intent);

										  }
									  }
		);
	}





	@Override
	protected void onResume()
	{// 如果从后台恢复，服务被系统干掉，就重启一下服务
		// TODO Auto-generated method stub
		//newNum = application.getRecentNum();// 从新获取一下全局变量
		if (OldBookApplication.getSocketStatue()!=1)
		{
			Intent service = new Intent(this, GetMsgService.class);
			startService(service);
		}
		//new SharePreferenceUtil(this, OldBookApplication.SAVE_USER).setIsStart(false);

		super.onResume();
	}
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.tab1:
				mPager.setCurrentItem(BookPAGE);// 点击页面1
				break;
			case R.id.tab2:
				mPager.setCurrentItem(PersonalPAGE);// 点击页面1
				break;
			case R.id.tab3:

				mPager.setCurrentItem(ChatPAGE);// 点击页面1
				break;
			case R.id.btn_addNewBook:
				Intent intent =new Intent(HomepageActivity.this,TakePicActivity.class);
				HomepageActivity.this.startActivity(intent);
			default:
				break;
		}
	}
	// ViewPager页面切换监听
	public class MyOnPageChangeListener implements OnPageChangeListener
	{

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量

		public void onPageSelected(int arg0)
		{
			// TODO Auto-generated method stub
			Animation animation = null;
			switch (arg0)
			{
				case BookPAGE:// 切换到页卡1
					if (currentIndex == PersonalPAGE)
					{// 如果之前显示的是页卡2
						animation = new TranslateAnimation(0, -one, 0, 0);
					}
					else if (currentIndex == ChatPAGE)
					{// 如果之前显示的是页卡3
						animation = new TranslateAnimation(one, -one, 0, 0);
					}
					break;
				case PersonalPAGE:// 切换到页卡2
					if (currentIndex == BookPAGE)
					{// 如果之前显示的是页卡1
						animation = new TranslateAnimation(-one, 0, 0, 0);
					}
					else if (currentIndex == ChatPAGE)
					{// 如果之前显示的是页卡3
						animation = new TranslateAnimation(one, 0, 0, 0);
					}
					break;
				case ChatPAGE:// 切换到页卡3
					if (currentIndex == BookPAGE)
					{// 如果之前显示的是页卡1
						animation = new TranslateAnimation(-one, one, 0, 0);
					}
					else if (currentIndex == PersonalPAGE)
					{// 如果之前显示的是页卡2
						animation = new TranslateAnimation(0, one, 0, 0);
					}
					break;
				default:
					break;
			}
			currentIndex = arg0;// 动画结束后，改变当前图片位置
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * 初始化动画
	 */
	private void initImageView()
	{
		cursor = (ImageView) findViewById(R.id.tab2_bg);
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.topbar_select).getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		// System.out.println("屏幕宽度:" + screenW);
		offset = (screenW / 3 - bmpW) / 2;// 计算偏移量:屏幕宽度/3，平分为3分，如果是3个view的话，再减去图片宽度，因为图片居中，所以要得到两变剩下的空隙需要再除以2
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset * 3 + bmpW, 0);// 初始化位置
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}


	public void setupView()
	{
        this.friendStr=getIntent().getStringExtra("friendList");
        this.bookStr=getIntent().getStringExtra("bookList");
		this.borrowStr=getIntent().getStringExtra("borrowList");

		mi = new MenuInflater(this);
		layout_body_activity = (LinearLayout) findViewById(R.id.bodylayout);

		imgBook = (ImageView) findViewById(R.id.tab1);
		imgBook.setOnClickListener(this);
		imgPersonal = (ImageView) findViewById(R.id.tab2);
		imgPersonal.setOnClickListener(this);
		imgChat = (ImageView) findViewById(R.id.tab3);
		imgChat.setOnClickListener(this);
		addNewBook=(Button)findViewById(R.id.btn_addNewBook);
		addNewBook.setOnClickListener(this);
		myHeadImage = (ImageView) findViewById(R.id.friend_list_myImg);
		myName = (TextView) findViewById(R.id.tv_m_Petname);
		myEvaluation = (TextView) findViewById(R.id.tv_m_Evaluation);

		cursor = (ImageView) findViewById(R.id.tab2_bg);

		//myHeadImage.setImageResource(imgs[list.get(0).getImg()]);
		myName.setText(OldBookApplication.PET_NAME);
		myEvaluation.setText(OldBookApplication.Evaluation);

		layout_body_activity.setFocusable(true);

		mPager = (ViewPager) findViewById(R.id.viewPager);
		mListViews = new ArrayList<View>();
		LayoutInflater inflater = LayoutInflater.from(this);
		View lay1 = inflater.inflate(R.layout.booklist, null);
		View lay2 = inflater.inflate(R.layout.personal_infor, null);
		View lay3 = inflater.inflate(R.layout.chat_list, null);
		mListViews.add(lay1);
		mListViews.add(lay2);
		mListViews.add(lay3);
		mPager.setAdapter(new MyPagerAdapter(mListViews));
		mPager.setCurrentItem(PersonalPAGE);
		//mPager.setOnPageChangeListener(new MyOnPageChangeListener());



		// 下面是图书
		lvBook = (MyListView) lay1.findViewById(R.id.lv_booklist);
		try
		{
			//bookListItems = resolveBook(this.bookStr);
			bookListViewAdapter = new BookListViewAdapter(this, bookListItems);
			lvBook.setAdapter(bookListViewAdapter);
			lvBook.setonRefreshListener(new BookRefreshListener());
		}
		catch(Exception e)
		{
			Log.e("oldBook", "载入图书错误 " + new Date().toString());
			e.printStackTrace();
		}


		//lvBook.setGroupIndicator(null);// 不设置大组指示器图标，因为我们自定义设置了
		//lvBook.setDivider(null);// 设置图片可拉伸的
		//lvBook.setFocusable(true);// 聚焦才可以下拉刷新


		//创建适配器
		// 聊天部分

		lvChat = (MyListView) lay3.findViewById(R.id.lv_chatlist);
		lvChat.setOnItemClickListener(new ChatItemClickListener());

		try
		{

			chatListViewAdapter = new ChatListViewAdapter(this, feList); //创建适配器
			lvChat.setAdapter(chatListViewAdapter);
			lvChat.setonRefreshListener(new ChatRefreshListener());
		}
		catch(Exception e)
		{
			Log.e("oldBook", "载入好友列表错误" + new Date().toString());
		}


		// 下面个人信息
		lvBorrowRecord = (MyListView) lay2.findViewById(R.id.lv_borrow_record);
		try
		{

			borrowRecordListViewAdapter = new PersonalInforListViewAdapter(this, borrowRecordList); //创建适配器
			lvBorrowRecord.setAdapter(borrowRecordListViewAdapter);
			lvBorrowRecord.setonRefreshListener(new BookBorrowRefreshListener());
		}
		catch(Exception e)
		{
			Log.e("oldBook", "载入个人信息错误 " + new Date().toString());
		}



	}

	@Override
	protected void initializedData()
	{

	}



	protected int getLayoutId()
	{
		return R.layout.book_list;
	}


	//图书部分


	//聊天部分

	private Emitter.Listener getEvaluationResult = new Emitter.Listener()
	{
		@Override
		public void call(final Object... args)
		{
			Log.i("Socket.io", "evaluation " + new Date().toString());
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					JSONObject messageData_Json = (JSONObject)args[0];
					try
					{

						String evaluation = messageData_Json.getString("evaluation");
						g_evaluation=evaluation;
					}
					catch(Exception e)
					{
						int messageData = (int)args[0];
						if(messageData==-1)
						{
							show("获取评价失败！");
							Log.e("Socket.io","获取评价失败" +new Date().toString());

						}
					}
					handler_evaluatiion.sendEmptyMessage(0);
				}
			}).start();



		}
	};


	private Emitter.Listener evaluation2Result = new Emitter.Listener()
	{
		@Override
		public void call(final Object... args)
		{
			Log.i("Socket.io", "evaluation " + new Date().toString());
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					JSONObject messageData_Json = (JSONObject)args[0];
					try
					{
						int id=messageData_Json.getInt("id");
						if(OldBookApplication.ID==id)
						{
							String evaluation = messageData_Json.getString("evaluation");
							g_evaluation = evaluation;
						}
					}
					catch(Exception e)
					{
						int messageData = (int)args[0];
						if(messageData==-1)
						{
							show("获取评价失败！");
							Log.e("Socket.io","获取评价失败" +new Date().toString());

						}
					}
					handler_evaluatiion.sendEmptyMessage(0);
				}
			}).start();



		}
	};


	private Emitter.Listener getBookListResult = new Emitter.Listener()
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
						List<BookEntity> bookEntityList=new ArrayList<BookEntity>();
						Object json = new JSONTokener(args[0].toString()).nextValue();
						if(json instanceof JSONObject)
						{

							JSONObject jsonObject = (JSONObject)json;
							int id=jsonObject.getInt("bookId");
							String bookName=jsonObject.getString("bookName");
							String bookAuthor=jsonObject.getString("bookAuthor");
							String bookPublishing=jsonObject.getString("bookPublishing");
							String bookNumber=jsonObject.getString("bookNumber");
							int owner=jsonObject.getInt("bookOwner");
							boolean isLent=jsonObject.getBoolean("isLent");

							BookEntity be=new BookEntity(id,bookName,bookAuthor,bookPublishing,bookNumber,owner,isLent);
							bookEntityList.add(be);
							//bookListItems.add(be);
						}
						else if (json instanceof JSONArray)
						{

							JSONArray jsonArray = (JSONArray)json;
							int count=jsonArray.length();
							BookEntity[] bookArray=new BookEntity[count];
							List<BookEntity> bookList=new ArrayList<BookEntity>();
							for(int i=0;i<count;i++)
							{
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								int id=jsonObject.getInt("bookId");
								String bookName=jsonObject.getString("bookName");
								String bookAuthor=jsonObject.getString("bookAuthor");
								String bookPublishing=jsonObject.getString("bookPublishing");
								String bookNumber=jsonObject.getString("bookNumber");
								int owner=jsonObject.getInt("bookOwner");
								boolean isLent=jsonObject.getBoolean("isLent");

								BookEntity be=new BookEntity(id,bookName,bookAuthor,bookPublishing,bookNumber,owner,isLent);
								bookList.add(be);
							}
							bookEntityList.addAll(bookList);
							//bookListItems.clear();
							//bookListItems.addAll(bookList);
						}
						Message msg=new Message();
						msg.what=0;//0初始化 1新书 2 更新状态
						Bundle bundle=new Bundle();
						ArrayList bundleList=new ArrayList();
						bundleList.add(bookEntityList);
						bundle.putParcelableArrayList("list", bundleList);

						msg.setData(bundle);
						handler_book.sendMessage(msg);
					}
					catch(Exception e)
					{
						Log.e("oldBook", "图书列表载入错误 " + new Date().toString());
						e.printStackTrace();
					}
					Log.e("order", "GET_ONLINE_USERLIST " + new Date().toString());
					client.getSocket().emit("GET_ONLINE_USERLIST");
					//bookListViewAdapter.notifyDataSetChanged();

				}

			}).start();

		}

	};


	private Emitter.Listener getBorrowResult_from = new Emitter.Listener()
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
						List<BorrowEntity> borrowEntityList=new ArrayList<BorrowEntity>();
						Object json = new JSONTokener(args[0].toString()).nextValue();
						if(json instanceof JSONObject)
						{

							JSONObject jsonObject = (JSONObject)json;
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

							borrowEntityList.add(boe);
							//borrowRecordList.add(boe);

						}else if (json instanceof JSONArray)
						{

							JSONArray jsonArray = (JSONArray)json;
							int count=jsonArray.length();
							List<BorrowEntity> borrowList=new ArrayList<BorrowEntity>();
							for(int i=0;i<count;i++)
							{
								JSONObject jsonObject =  jsonArray.getJSONObject(i);
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

								borrowList.add(boe);
							}
							borrowEntityList.addAll(borrowList);
						}
						Message msg=new Message();
						msg.what=0;//0from 1get
						Bundle bundle=new Bundle();
						ArrayList bundleList=new ArrayList();
						bundleList.add(borrowEntityList);
						bundle.putParcelableArrayList("list", bundleList);

						msg.setData(bundle);
						handler_borrow.sendMessage(msg);
					}
					catch(Exception e)
					{
						Log.e("oldBook", "借阅列表载入错误 " + new Date().toString());
						e.printStackTrace();
					}
					Log.e("order", "BORROWLIST_GETUSER " + new Date().toString());
					client.getSocket().emit("BORROWLIST_GETUSER", OldBookApplication.ID);
					//handler_borrow.sendEmptyMessage(0);
					//borrowRecordListViewAdapter.notifyDataSetChanged();
				}

			}).start();

		}

	};

	private Emitter.Listener getBorrowResult_get=new Emitter.Listener()
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
						List<BorrowEntity> borrowEntityList=new ArrayList<BorrowEntity>();
						Object json = new JSONTokener(args[0].toString()).nextValue();
						if(json instanceof JSONObject)
						{

							JSONObject jsonObject = (JSONObject)json;
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
							BorrowEntity boe=new BorrowEntity(id,bookName,fromUser,0,sReturnTime,statue,evaluation,bookId);

							borrowEntityList.add(boe);
							//borrowRecordList.add(boe);

						}else if (json instanceof JSONArray)
						{

							JSONArray jsonArray = (JSONArray)json;
							int count=jsonArray.length();
							List<BorrowEntity> borrowList=new ArrayList<BorrowEntity>();
							for(int i=0;i<count;i++)
							{
								JSONObject jsonObject =  jsonArray.getJSONObject(i);
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
								BorrowEntity boe=new BorrowEntity(id,bookName,fromUser,0,sReturnTime,statue,evaluation,bookId);

								borrowList.add(boe);
							}
							//borrowRecordList.clear();
							borrowEntityList.addAll(borrowList);
							//borrowRecordList.addAll(borrowList);
						}
						Message msg=new Message();
						msg.what=1;//0from 1get
						Bundle bundle=new Bundle();
						ArrayList bundleList=new ArrayList();
						bundleList.add(borrowEntityList);
						bundle.putParcelableArrayList("list", bundleList);

						msg.setData(bundle);
						handler_borrow.sendMessage(msg);
					}
					catch(Exception e)
					{
						Log.e("oldBook", "借阅列表载入错误 " + new Date().toString());
						e.printStackTrace();
					}
					Log.e("order", "GET_BOOK_LIST " + new Date().toString());
					client.getSocket().emit("GET_BOOK_LIST");
					//handler_borrow.sendEmptyMessage(0);
					//borrowRecordListViewAdapter.notifyDataSetChanged();
				}

			}).start();

		}

	};

	private Emitter.Listener getOnlineResult= new Emitter.Listener()
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
						List<FriendEntity> friendEntityList=new ArrayList<FriendEntity>();
						//feList.clear();
						JSONArray onlineList=(JSONArray)args[0];
						for(int i=0;i<onlineList.length();i++)
						{
							String s=onlineList.getString(i);
							String[] sa=s.split("_");
							int id=Integer.parseInt(sa[0]);
							String petName=sa[1];
							FriendEntity fe=new FriendEntity(id,petName);
							//feList.add(fe);
							friendEntityList.add(fe);
						}

						Message msg=new Message();
						Bundle bundle=new Bundle();
						ArrayList bundleList=new ArrayList();
						bundleList.add(friendEntityList);
						bundle.putParcelableArrayList("list", bundleList);

						msg.setData(bundle);
						handler_chat.sendMessage(msg);

					}
					catch(Exception e)
					{
						Log.e("oldBook", "在线好友列表载入错误 " + new Date().toString());
						e.printStackTrace();
					}

					//chatListViewAdapter.notifyDataSetChanged();
				}

			}).start();

		}

	};

	private Emitter.Listener OnlineResult= new Emitter.Listener()
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
						List<FriendEntity> friendEntityList=new ArrayList<FriendEntity>();
						//feList.clear();
						String s=args[0].toString();
						String[] sa=s.split("_");
						int id=Integer.parseInt(sa[0]);
						/*
						Download download=new Download("avatar_"+id);
						boolean result=download.checkDirectory();
						if(!result)
							download.download();
						else
							download.downloadOver=true;
							*/
						String petName=sa[1];
						FriendEntity fe=new FriendEntity(id,petName);
						int j=0;
						for(int i=0;i<feList.size();i++)
						{
							if(feList.get(i).getId()==fe.getId())
							{
								j=1;
								break;
							}
						}
						if(j==0)
						{
							friendEntityList.add(fe);
							//feList.add(fe);
						}

						Message msg=new Message();
						msg.what=1;//0初始化，1上线，2下线
						Bundle bundle=new Bundle();
						ArrayList bundleList=new ArrayList();
						bundleList.add(friendEntityList);
						bundle.putParcelableArrayList("list",bundleList);
						msg.setData(bundle);
						//while(!download.downloadOver)
						//{

						//}
						handler_chat.sendMessage(msg);
						//chatListViewAdapter.notifyDataSetChanged();
					}
					catch(Exception e)
					{
						Log.e("oldBook", "在线好友列表载入错误 " + new Date().toString());
						e.printStackTrace();
					}

				}

			}).start();

		}

	};



	private Emitter.Listener OfflineResult= new Emitter.Listener()
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
						//feList.clear();
						List<FriendEntity> friendEntityList=new ArrayList<FriendEntity>();
						String s=args[0].toString();
						String[] sa=s.split("_");
						int id=Integer.parseInt(sa[0]);
						String petName=sa[1];
						FriendEntity fe=new FriendEntity(id,petName);
						friendEntityList.add(fe);
						/*
						for(int i=0;i<feList.size();i++)
						{
							if(feList.get(i).getId()==id)
								feList.remove(i);
						}

						//feList.add(fe);
						*/
						Message msg=new Message();
						msg.what=2;
						Bundle bundle=new Bundle();
						ArrayList bundleList=new ArrayList();
						bundleList.add(friendEntityList);
						bundle.putParcelableArrayList("list", bundleList);
						msg.setData(bundle);
						handler_chat.sendMessage(msg);
						//chatListViewAdapter.notifyDataSetChanged();
					}
					catch(Exception e)
					{
						Log.e("oldBook", "在线好友列表载入错误 " + new Date().toString());
						Log.e("oldBook",args[0].toString());
						e.printStackTrace();
					}

				}

			}).start();

		}

	};


	private Emitter.Listener getMessage= new Emitter.Listener()
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
						;
						int receiver=(int)args[0];
						String recName=(String)args[1];
						int sender=(int)args[2];
						String sendName=(String)args[3];
						String message=(String)args[4];
						String time=(String)args[5];

						ChatMsgEntity cme=new ChatMsgEntity(sender,receiver,sendName,recName,time,message,true);

						if(sender!=application.getActiveId())
						{
							newNum++;
							messageDB.saveMsg(sender, cme);// 保存到数据库
							Bundle bundle=new Bundle();
							bundle.putString("sender",sendName);
							Message msg=new Message();
							msg.setData(bundle);
							handler_message.sendMessage(msg);
						}// 提示用户


					}
					catch(Exception e)
					{
						Log.e("oldBook", "新消息载入错误 " + new Date().toString());
						e.printStackTrace();
					}
				}

			}).start();

		}

	};



	private Emitter.Listener updateBookResult= new Emitter.Listener()
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
						JSONObject jsonObject = (JSONObject)args[0];
						int id=jsonObject.getInt("bookId");
						String bookName=jsonObject.getString("bookName");
						String bookAuthor=jsonObject.getString("bookAuthor");
						String bookPublishing=jsonObject.getString("bookPublishing");
						String bookNumber=jsonObject.getString("bookNumber");
						int owner=jsonObject.getInt("bookOwner");
						boolean isLent=jsonObject.getBoolean("isLent");
						BookEntity be=new BookEntity(id,bookName,bookAuthor,bookPublishing,bookNumber,owner,isLent);
                        /*
						for(int i=0;i<bookListItems.size();i++)
						{
							if(bookListItems.get(i).getId()==id)
							{
								Log.i("Socket.io","更新状态"+isLent);
								bookListItems.get(i).setIsLent(isLent);
							}
						}
						*/
						List<BookEntity> bookEntityList=new ArrayList<BookEntity>();
						bookEntityList.add(be);

						Message msg=new Message();
						msg.what=2;//0 booklist 1 newbook 2 bookstatue

						Bundle bundle=new Bundle();
						ArrayList bundleList=new ArrayList();
						bundleList.add(bookEntityList);
						bundle.putParcelableArrayList("list", bundleList);

						msg.setData(bundle);
						handler_book.sendMessage(msg);
						//bookListViewAdapter.notifyDataSetChanged();
					}
					catch(Exception e)
					{
						Log.e("oldBook", "新消息载入错误 " + new Date().toString());
						e.printStackTrace();
					}
				}

			}).start();

		}

	};


	private Emitter.Listener borrowResult= new Emitter.Listener()
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
						List<BorrowEntity> borrowEntityList=new ArrayList<BorrowEntity>();
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
						if(getUser==OldBookApplication.ID)
						{
							BorrowEntity boe = new BorrowEntity(id, bookName, fromUser, 0, sReturnTime, statue, evaluation,bookId);
							int j=0;
							for(int i=0;i<borrowRecordList.size();i++)
							{
								if(borrowRecordList.get(i).getBorrowId()==id)
								{
									j++;
								}
							}
							if(j==0)
							{
								borrowEntityList.add(boe);
								//borrowRecordList.add(boe);
							}
							Message msg=new Message();
							msg.what=1;//0from 1get/借书
							Bundle bundle=new Bundle();
							ArrayList bundleList=new ArrayList();
							bundleList.add(borrowEntityList);
							bundle.putParcelableArrayList("list", bundleList);

							msg.setData(bundle);
							handler_borrow.sendMessage(msg);
						}

					}
					catch(JSONException e)
					{
						Log.e("oldBook", "借阅列表载入错误 " + new Date().toString());
						e.printStackTrace();
					}

					//borrowRecordListViewAdapter.notifyDataSetChanged();
				}

			}).start();

		}

	};

	private Emitter.Listener returnResult_get= new Emitter.Listener()
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
						List<BorrowEntity> borrowEntityList=new ArrayList<BorrowEntity>();
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
						if(getUser==OldBookApplication.ID)
						{
							BorrowEntity boe = new BorrowEntity(id, bookName, fromUser, 0, sReturnTime, statue, evaluation,bookId);
							borrowEntityList.add(boe);

							Message msg=new Message();
							msg.what=2;//0from 1get/借书 2 还书
							Bundle bundle=new Bundle();
							ArrayList bundleList=new ArrayList();
							bundleList.add(borrowEntityList);
							bundle.putParcelableArrayList("list", bundleList);

							msg.setData(bundle);
							handler_borrow.sendMessage(msg);

							/*for(int i=0;i<borrowRecordList.size();i++)
							{
								if(borrowRecordList.get(i).getBorrowId()==id)
								{
									borrowRecordList.get(i).setStatue("WAIT_EVALUATION");
								}
							}
							*/
						}

					}
					catch(JSONException e)
					{
						Log.e("oldBook", "借阅列表载入错误 " + new Date().toString());
						e.printStackTrace();
					}
					//handler_borrow.sendEmptyMessage(0);
					//borrowRecordListViewAdapter.notifyDataSetChanged();
				}

			}).start();

		}

	};

	private Emitter.Listener evaluationResult_from= new Emitter.Listener()
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
						List<BorrowEntity> borrowEntityList=new ArrayList<BorrowEntity>();
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
						if(fromUser==OldBookApplication.ID)
						{
							BorrowEntity boe = new BorrowEntity(id, bookName, fromUser, 0, sReturnTime, statue, evaluation,bookId);
							borrowEntityList.add(boe);

							Message msg=new Message();
							msg.what=3;//0from 1get/借书 2 还书 3评价
							Bundle bundle=new Bundle();
							ArrayList bundleList=new ArrayList();
							bundleList.add(borrowEntityList);
							bundle.putParcelableArrayList("list", bundleList);

							msg.setData(bundle);
							handler_borrow.sendMessage(msg);




							/*
							for(int i=0;i<borrowRecordList.size();i++)
							{
								if(borrowRecordList.get(i).getBorrowId()==id)
								{
									borrowRecordList.get(i).setStatue("EVALUATION_OVER");
									borrowRecordList.get(i).setEvaluation(evaluation);
								}
							}
							*/
						}

					}
					catch(JSONException e)
					{
						Log.e("oldBook", "借阅列表载入错误 " + new Date().toString());
						e.printStackTrace();
					}
					//handler_borrow.sendEmptyMessage(0);
				}

			}).start();

		}

	};

	private Emitter.Listener returnResult= new Emitter.Listener()
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
						List<BorrowEntity> borrowEntityList=new ArrayList<BorrowEntity>();
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
						BorrowEntity boe = new BorrowEntity(id, bookName, fromUser, 0, sReturnTime, statue, evaluation,bookId);
						borrowEntityList.add(boe);

						Message msg=new Message();
						msg.what=2;//0from 1get/借书 2 还书
						Bundle bundle=new Bundle();
						ArrayList bundleList=new ArrayList();
						bundleList.add(borrowEntityList);
						bundle.putParcelableArrayList("list", bundleList);

						msg.setData(bundle);
						handler_borrow.sendMessage(msg);


						/*for(int i=0;i<borrowRecordList.size();i++)
						{
							if(borrowRecordList.get(i).getBorrowId()==id)
							{
								borrowRecordList.get(i).setStatue("WAIT_EVALUATION");
							}
						}
						*/


					}
					catch(JSONException e)
					{
						Log.e("oldBook", "借阅列表载入错误 " + new Date().toString());
						e.printStackTrace();
					}
					//handler_borrow.sendEmptyMessage(0);
					//borrowRecordListViewAdapter.notifyDataSetChanged();
				}

			}).start();

		}

	};
	private Emitter.Listener evaluationResult= new Emitter.Listener()
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
						List<BorrowEntity> borrowEntityList=new ArrayList<BorrowEntity>();
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
						BorrowEntity boe = new BorrowEntity(id, bookName, fromUser, 0, sReturnTime, statue, evaluation,bookId);
						borrowEntityList.add(boe);

						Message msg=new Message();
						msg.what=3;//0from 1get/借书 2 还书 3评价
						Bundle bundle=new Bundle();
						ArrayList bundleList=new ArrayList();
						bundleList.add(borrowEntityList);
						bundle.putParcelableArrayList("list", bundleList);

						msg.setData(bundle);
						handler_borrow.sendMessage(msg);
						/*
						for(int i=0;i<borrowRecordList.size();i++)
						{
							if(borrowRecordList.get(i).getBorrowId()==id)
							{
								borrowRecordList.get(i).setStatue("EVALUATION_OVER");
								borrowRecordList.get(i).setEvaluation(evaluation);
							}
						}
						*/
					}
					catch(JSONException e)
					{
						Log.e("oldBook", "借阅列表载入错误 " + new Date().toString());
						e.printStackTrace();
					}
					//handler_borrow.sendEmptyMessage(0);
					//borrowRecordListViewAdapter.notifyDataSetChanged();
				}

			}).start();

		}

	};

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
						List<BookEntity> bookEntityList=new ArrayList<BookEntity>();
						JSONObject jsonObject = (JSONObject)args[0];
						int id=jsonObject.getInt("bookId");
						/*
						Download download=new Download("book_"+id);
						boolean result=download.checkDirectory();
						if(!result)
							download.download();
						else
							download.downloadOver=true;
							*/
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
							bookEntityList.add(be);
							//bookListItems.add(be);
							//while(!download.downloadOver)
							//{

							//}
						}
						Message msg=new Message();
						msg.what=1;
						Bundle bundle=new Bundle();
						ArrayList bundleList=new ArrayList();
						bundleList.add(bookEntityList);
						bundle.putParcelableArrayList("list", bundleList);
						msg.setData(bundle);

						handler_book.sendMessage(msg);
						//bookListViewAdapter.notifyDataSetChanged();

					}
					catch(JSONException e)
					{
						Log.e("oldBook", "添加图书错误 " + new Date().toString());
						e.printStackTrace();
					}

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
				Log.i("Socket.io","上传下载");
				Message msg=new Message();
				if(args[1]==null)
				{
					String name=(String)args[0];
					String[] array=name.split("\\.");
					String filename=array[0];
					Download download=new Download(filename);
					boolean result=download.checkDirectory();
					if(!result)
					{
						Log.i("Socket.io","开始下载");
						download.download();
					}
					if(filename.contains("avatar"))
					{
						chatListViewAdapter.notifyDataSetChanged();
					}
					else if(filename.contains("book"))
					{
						bookListViewAdapter.notifyDataSetChanged();
					}

				}
				else
				{
					String error=(String)args[1];
					Log.e("Socket.io","上传结果错误"+error+" "+new Date().toString());
				}
			}
			catch(Exception e)
			{
				Log.e("Socket.io","读取上传结果失败");
				e.printStackTrace();
			}

		}
	};
	/**
	 * 用Handler来更新UI
	 */
	private Handler handler_book = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{

			Bundle bundle=msg.getData();
			ArrayList bundleList=new ArrayList();
			bundleList=bundle.getParcelableArrayList("list");
			switch(msg.what)
			{
				case 0:
					bookListItems.clear();
					bookListItems.addAll((List<BookEntity>) bundleList.get(0));
					break;
				case 1:
					bookListItems.addAll((List<BookEntity>) bundleList.get(0));
					break;
				case 2:
					List<BookEntity> bookEntityList=(List<BookEntity>) bundleList.get(0);
					BookEntity be=bookEntityList.get(0);
					int id=be.getId();
					boolean isLent=be.getIsLent();
					for(int i=0;i<bookListItems.size();i++)
					{
						if(bookListItems.get(i).getId()==id)
						{
							bookListItems.get(i).setIsLent(isLent);
						}
					}
					break;
				default:
					break;
			}
			bookListViewAdapter.notifyDataSetChanged();
			show("图书刷新完成");
		}
	};

	/**
	 * 用Handler来更新UI
	 */
	private Handler handler_evaluatiion = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			myEvaluation.setText(g_evaluation);
			show("评价载入完成");
		}
	};


	/**
	 * 用Handler来更新UI
	 */
	private Handler handler_borrow = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			Bundle bundle=msg.getData();
			ArrayList bundleList=new ArrayList();
			bundleList=bundle.getParcelableArrayList("list");

			switch (msg.what)
			{
				case 0:
					borrowRecordList.clear();
					borrowRecordList.addAll((List<BorrowEntity>) bundleList.get(0));
					break;
				case 1:
					borrowRecordList.addAll((List<BorrowEntity>) bundleList.get(0));
					borrowRecordListViewAdapter.notifyDataSetChanged();
					break;
				case 2:
					List<BorrowEntity> borrowEntityList=(List<BorrowEntity>) bundleList.get(0);
					BorrowEntity boe=borrowEntityList.get(0);
					int id=boe.getBorrowId();
					for(int i=0;i<borrowRecordList.size();i++)
					{
						if(borrowRecordList.get(i).getBorrowId()==id)
						{
							borrowRecordList.get(i).setStatue("WAIT_EVALUATION");
						}
					}
					borrowRecordListViewAdapter.notifyDataSetChanged();
					break;
				case 3:
					List<BorrowEntity> borrowEntityList1=(List<BorrowEntity>) bundleList.get(0);
					BorrowEntity boe1=borrowEntityList1.get(0);
					int id1=boe1.getBorrowId();
					int evaluation=boe1.getEvaluation();
					for(int i=0;i<borrowRecordList.size();i++)
					{
						if(borrowRecordList.get(i).getBorrowId()==id1)
						{
							borrowRecordList.get(i).setStatue("EVALUATION_OVER");
							borrowRecordList.get(i).setEvaluation(evaluation);
						}
					}
					borrowRecordListViewAdapter.notifyDataSetChanged();
					break;
				default:
					break;
			}

			show("借阅载入完成");
		}
	};


	/**
	 * 用Handler来更新UI
	 */
	private Handler handler_chat = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			Bundle bundle=msg.getData();
			ArrayList bundleList=new ArrayList();
			bundleList=bundle.getParcelableArrayList("list");
			switch(msg.what)
			{
				case 0:
					feList.clear();
					feList.addAll((List<FriendEntity>) bundleList.get(0));
					break;
				case 1:
					List<FriendEntity> fe=(List<FriendEntity>)bundleList.get(0);
					if(fe.size()!=0)
					{
						String onlineName = fe.get(0).getPetName();
						feList.addAll(fe);
						show(onlineName + "上线了");
					}
					break;
				case 2:
					List<FriendEntity> fe1=(List<FriendEntity>)bundleList.get(0);
					String offlineName=fe1.get(0).getPetName();
					int id=fe1.get(0).getId();
					for(int i=0;i<feList.size();i++)
					{
						if(feList.get(i).getId()==id)
							feList.remove(i);
					}
					show(offlineName+"下线了");
					break;
				default:
					break;
			}
			chatListViewAdapter.notifyDataSetChanged();
			show("在线好友载入完成");
		}
	};


	/**
	 * 用Handler来更新UI
	 */
	/*
	private Handler handler_online= new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			Bundle bundle=msg.getData();
			ArrayList bundleList=bundle.getParcelableArrayList("list");
			List<FriendEntity> fe=(List<FriendEntity>)bundleList.get(0);
			String onlineName=fe.get(0).getPetName();
			feList.addAll(fe);
			chatListViewAdapter.notifyDataSetChanged();
			show(onlineName+"上线了");
		}
	};
*/
	/**
	 * 用Handler来更新UI
	 */
	/*
	private Handler handler_offline= new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{

			Bundle bundle=msg.getData();
			ArrayList bundleList=bundle.getParcelableArrayList("list");
			List<FriendEntity> fe=(List<FriendEntity>)bundleList.get(0);
			String offlineName=fe.get(0).getPetName();
			int id=fe.get(0).getId();
			for(int i=0;i<feList.size();i++)
			{
				if(feList.get(i).getId()==id)
					feList.remove(i);
			}
			chatListViewAdapter.notifyDataSetChanged();
			show(offlineName+"上线了");
		}
	};
*/
	/**
	 * 用Handler来更新UI
	 */
	private Handler handler_message= new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{

			Bundle bundle=msg.getData();
			String senderName=bundle.getString("sender");
			show("有"+senderName+"的新消息");
		}
	};

	/**
	 * 用Handler来更新UI
	 */
	/*
	private Handler handler_bookList= new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			bookListViewAdapter.notifyDataSetChanged();
		}
	};
*/





















	private class ChatItemClickListener implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView< ? > adapter, View view,int position, long arg)
		{
			// TODO Auto-generated method stub
			// ListView Clicked item index
			//int itemPosition     = position;
			//// ListView Clicked item value
			//String  itemValue    = (String) lvChat.getItemAtPosition(position);
			//// Show Alert
			//Toast.makeText(getApplicationContext(),
			//"Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
			//.show();

			application.setActiveId(feList.get(position-1).getId());
			Intent intent=new Intent();
			intent.putExtra("id",feList.get(position-1).getId());
			intent.putExtra("petname",feList.get(position-1).getPetName());// 第一个参数指定name，android规范是以包名+变量名来命名，后面是各种类型的数据类型

			intent.setClass(HomepageActivity.this,ChatMsgActivity.class);
			HomepageActivity.this.startActivity(intent);

		}

	}



	public class BookRefreshListener implements MyListView.OnRefreshListener
	{

		@Override
		public void onRefresh()
		{
			new AsyncTask<Void, Void, Void>()
			{

				protected Void doInBackground(Void... params)
				{
					// 从服务器重新获取好友列表
					if (OldBookApplication.getSocketStatue()==1)
					{
						Log.e("order", "GET_BOOK_LIST " + new Date().toString());
					    client.getSocket().emit("GET_BOOK_LIST");
                    }
                    return null;
				}

				@Override
				protected void onPostExecute(Void result)
				{
					//bookListViewAdapter.notifyDataSetChanged();
					lvBook.onRefreshComplete();
					show("刷新成功");
				}

			}.execute();
		}
	}







	/**
	 * 好友列表下拉刷新监听与实现，异步任务
	 *
	 * @author Administrator
	 *
	 */
	public class ChatRefreshListener implements MyListView.OnRefreshListener
	{

		@Override
		public void onRefresh()
		{
			new AsyncTask<Void, Void, Void>()
			{
				protected Void doInBackground(Void... params)
				{
					// 从服务器重新获取好友列表
					if (OldBookApplication.getSocketStatue()==1)
					{
						Log.e("order", "GET_ONLINE_USERLIST " + new Date().toString());
						client.getSocket().emit("GET_ONLINE_USERLIST");

					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result)
				{
					//chatListViewAdapter.notifyDataSetChanged();
					lvChat.onRefreshComplete();
					show("刷新成功");
				}

			}.execute();
		}
	}
	/**
	 * 好友列表下拉刷新监听与实现，异步任务
	 *
	 * @author Administrator
	 *
	 */
	public class BookBorrowRefreshListener implements MyListView.OnRefreshListener
	{

		@Override
		public void onRefresh()
		{
			new AsyncTask<Void, Void, Void>()
			{
				protected Void doInBackground(Void... params)
				{
					// 从服务器重新获取好友列表
					if (OldBookApplication.getSocketStatue()==1)
					{
						Log.e("order", "BORROWLIST_FROMUSER " + new Date().toString());
						client.getSocket().emit("BORROWLIST_FROMUSER", OldBookApplication.ID);
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result)
				{
					//chatListViewAdapter.notifyDataSetChanged();
					lvChat.onRefreshComplete();
					show("刷新成功");
				}

			}.execute();
		}
	}
	@Override
	public void onBackPressed()
	{// 捕获返回按键
		exitDialog(HomepageActivity.this, "提示", "您真的要退出吗？");

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
							Log.e("order", "CLOSE_USER_IN_LIST "+new Date().toString());
							client.getSocket().emit("CLOSE_USER_IN_LIST", OldBookApplication.ID, OldBookApplication.PET_NAME);
							Thread.sleep(1000);


						}
						catch(Exception e)
						{
							Log.e("oldBook", "关闭应用错误 " + new Date().toString());
						}

						if (OldBookApplication.getSocketStatue()==1)
						{// 如果连接还在，说明服务还在运行
							// 关闭服务
							Intent service = new Intent(HomepageActivity.this,GetMsgService.class);
							stopService(service);
						}
						client.getSocket().disconnect();
						System.exit(0);
						close();// 调用父类自定义的循环关闭方法
					}
				}).setNegativeButton("取消", null).create().show();
	}

}




