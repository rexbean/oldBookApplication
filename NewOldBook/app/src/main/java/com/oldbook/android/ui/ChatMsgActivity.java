package com.oldbook.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.oldbook.android.Application.OldBookApplication;
import com.oldbook.android.R;
import com.oldbook.android.client.Client;
import com.oldbook.android.entity.ChatMsgEntity;
import com.oldbook.android.entity.MessageEntity;
import com.oldbook.android.entity.MessageType;
import com.oldbook.android.util.MessageDB;
import com.oldbook.android.util.MyTime;
import com.oldbook.android.widget.ChatMsgViewAdapter;


import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.socket.emitter.Emitter;


/**
 * @author Administrator
 */
public class ChatMsgActivity extends AppBaseActivity implements OnClickListener {

	private Button btnSend;// 发送按钮
	private Button btnBack;// 返回按钮
	private int newNum;
	private MessageDB messageDB;//消息数据库对象
	private EditText etContent;
	private ListView lvChatMsg;
	private ListView lvChatMsgNew;
	private TextView tvPetName;
	private String petNameRec;
	private TextView tvLeftName;
	private TextView tvRightName;
	private int chatId;
	private OldBookApplication application;
	private ChatMsgViewAdapter chatMsgViewAdapter;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();//
	private static final int WHAT=0;
	public static Handler mHandler;
	private Client client;
	//MyBroadcastReceiver br;
	public void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_main);
		application= (OldBookApplication) this.getApplicationContext();
		client=OldBookApplication.getClient();
		client.getSocket().on("CHATMSG",getMessage);

		setupView();//初始化控件

		
		
        //tvLeftName.setText(petName);
		initializedData();//初始化数据
		lvChatMsg.setSelection(chatMsgViewAdapter.getCount() - 1);
	}
	/**
	 * 初始化控件
	 */
	public void setupView() 
	{
		lvChatMsg = (ListView) findViewById(R.id.lv_chat_msg);
		lvChatMsg.setAdapter(chatMsgViewAdapter);
		btnSend = (Button) findViewById(R.id.btn_send);
		btnSend.setOnClickListener(this);
		btnBack = (Button) findViewById(R.id.btn_back);
		btnBack.setOnClickListener(this);
		tvPetName=(TextView)findViewById(R.id.tv_chat_petname);
		
		etContent = (EditText) findViewById(R.id.et_send_msg);
		Intent intent=getIntent();
		chatId=intent.getIntExtra("id",-1);
		petNameRec=intent.getStringExtra("petname");
		messageDB=new MessageDB(this);
		mDataArrays.addAll(messageDB.getMsg(chatId));
		chatMsgViewAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		lvChatMsg.setAdapter(chatMsgViewAdapter);
		tvPetName.setText(petNameRec);



		
	}

	private final static int COUNT = 12;// 

	/**
	 * 初始化数据
	 */
	protected void initializedData()
	{


	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_send://发送按钮
				send();
				break;
			case R.id.btn_back://返回按钮
				finish();// 关闭
				break;
		}
	}


	/**
	 * 发送信息
	 */
	private void send()
	{
		try
		{

				String contString = etContent.getText().toString();
				etContent.setText("");
				if (contString.length() > 0)
				{
					Log.e("order", "P2PMSG "+new Date().toString());
					client.getSocket().emit("P2PMSG",chatId,petNameRec,OldBookApplication.ID,OldBookApplication.PET_NAME,contString,MyTime.getTime());


					ChatMsgEntity cme=new ChatMsgEntity();
					cme.setMsgType(false);
					cme.setSender(OldBookApplication.ID);
					cme.setReceiver(chatId);
					cme.setContent(contString);
					cme.setTime(MyTime.getTime());
					mDataArrays.add(cme);
					chatMsgViewAdapter.notifyDataSetChanged();// 更新listview Item

					etContent.setText("");// 清空输入框

					lvChatMsg.setSelection(lvChatMsg.getCount() - 1);
				}
		}
		catch(Exception e)
		{
			
		}
		
	}

	public void getMessage(MessageEntity msg)
	{
/*
		switch(msg[0])
		{
			case "MESSAGE":
				String[] messageArray = msg[1].split("_");
				int sender = Integer.parseInt(messageArray[0]);
				String petNameSend = messageArray[1];
				String petNameRec = OldBookApplication.PET_NAME;
				int receiver = Integer.parseInt(messageArray[2]);
				String msgContent = messageArray[3];
				boolean isComing = Boolean.parseBoolean(messageArray[4]);
				String sendTime = messageArray[5];
				ChatMsgEntity cme = new ChatMsgEntity(sender, receiver, petNameSend, petNameRec, sendTime, msgContent, isComing);

				if(sender==application.getActiveId())
				{
					mDataArrays.add(cme);
					chatMsgViewAdapter.notifyDataSetChanged();
				}
				else {
					newNum++;
					application.setRecentNum(newNum);// 保存到全局变量
					if (sender != application.getActiveId()) {
						messageDB.saveMsg(sender, cme);// 保存到数据库
						show("有" + petNameSend + "的新消息");
					}// 提示用户
				}
		}*/
	}
	protected int getLayoutId()
	{
		return R.layout.chat_main;
	}

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
						int receiver=(int)args[0];
						String recName=(String)args[1];
						int sender=(int)args[2];
						String sendName=(String)args[3];
						String message=(String)args[4];
						String time=(String)args[5];

						ChatMsgEntity cme=new ChatMsgEntity(sender,receiver,sendName,recName,time,message,true);
						Message msg=new Message();
						if(sender!=application.getActiveId())
						{
							newNum++;
							application.setRecentNum(newNum);// 保存到全局变量
							messageDB.saveMsg(sender, cme);// 保存到数据库
							Bundle bundle=new Bundle();
							bundle.putString("sender",sendName);

							msg.what=0;
							msg.setData(bundle);
							handler_message.sendMessage(msg);
						}
						else
						{
							mDataArrays.add(cme);
							msg.what=1;
							handler_message.sendMessage(msg);
						}
						// 提示用户


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


	/**
	 * 用Handler来更新UI
	 */
	private Handler handler_message= new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case 0:
					Bundle bundle=msg.getData();
					String senderName=bundle.getString("sender");
					show("有" + senderName + "的新消息");
					break;
				case 1:
					chatMsgViewAdapter.notifyDataSetChanged();
			}

		}
	};

}