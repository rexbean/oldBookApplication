package com.oldbook.android.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.oldbook.android.Application.OldBookApplication;
import com.oldbook.android.entity.ChatMsgEntity;

import java.util.ArrayList;
import java.util.List;


public class MessageDB
{
	private SQLiteDatabase db;

	public MessageDB(Context context)
	{
		db = context.openOrCreateDatabase(OldBookApplication.DBNAME, Context.MODE_PRIVATE, null);
	}

	public void saveMsg(int id, ChatMsgEntity entity)
	{
		db.execSQL("CREATE table IF NOT EXISTS _"+ id+ " (_id INTEGER " +
				"PRIMARY KEY AUTOINCREMENT,sender TEXT,receiver TEXT," +
				"petNameSend TEXT,petNameReceive TEXT,time TEXT," +
				"isCome TEXT,message TEXT,isNew TEXT)");
		int isCome=0;
		if (entity.getMsgType())
		{
			isCome = 1;
		}

		db.execSQL("insert into _" + id+ " (sender,receiver,petNameSend," +
				"petNameReceive,time,isCome,message,isNew) values(?,?,?,?,?,?,?,?)",
				new Object[] { entity.getSender(), entity.getReceiver(),
				entity.getPetNameSend(),entity.getPetNameReceive(),
				entity.getTime(), isCome,entity.getContent(),"new"});
	}

	public List<ChatMsgEntity> getMsg(int id)
	{
		List<ChatMsgEntity> list = new ArrayList<ChatMsgEntity>();
		db.execSQL("CREATE table IF NOT EXISTS _"+ id+ " (_id INTEGER " +
				"PRIMARY KEY AUTOINCREMENT,sender TEXT,receiver TEXT," +
				"petNameSend TEXT,petNameReceive TEXT,time TEXT," +
				"isCome TEXT,message TEXT,isNew TEXT)");
		Cursor c = db.rawQuery("SELECT * from _" + id+ " where isNew ='new'" , null);
		while (c.moveToNext())
		{
			int sender = c.getInt(c.getColumnIndex("sender"));
			int receiver=c.getInt(c.getColumnIndex("receiver"));
			String petNameSend = c.getString(c.getColumnIndex("petNameSend"));
			String petNameReceive=c.getString(c.getColumnIndex("petNameReceive"));
			String time = c.getString(c.getColumnIndex("time"));
			String message = c.getString(c.getColumnIndex("message"));
			int isCome = c.getInt(c.getColumnIndex("isCome"));
			String isNew=c.getString(c.getColumnIndex("isNew"));
			
			boolean isComMsg = false;
			if (isCome == 1)
			{
				isComMsg = true;
			}
			ChatMsgEntity entity = new ChatMsgEntity(sender,receiver,petNameSend,petNameReceive,time,message,isComMsg);
			list.add(entity);
		}
		db.execSQL("update _"+id +" set isNew='old' where isNew='new'");
		c.close();
		return list;
	}

	public void close()
	{
		if (db != null)
			db.close();
	}
}
