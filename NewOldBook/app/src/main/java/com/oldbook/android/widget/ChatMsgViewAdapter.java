package com.oldbook.android.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.oldbook.android.Application.OldBookApplication;
import com.oldbook.android.R;
import com.oldbook.android.entity.ChatMsgEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;


/**
 * 消息ListView的Adapter
 *
 * @author Administrator
 */
public class ChatMsgViewAdapter extends BaseAdapter
{

	public static interface IMsgViewType
	{
		int IMVT_COM_MSG = 0;// 鏀跺埌瀵规柟鐨勬秷鎭?
		int IMVT_TO_MSG = 1;// 鑷繁鍙戦?鍑哄幓鐨勬秷鎭?
	}

	private static final int ITEMCOUNT = 2;// 娑堟伅绫诲瀷鐨勬?鏁?
	private List<ChatMsgEntity> coll;// 娑堟伅瀵硅薄鏁扮粍
	private LayoutInflater mInflater;

	public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll)
	{
		this.coll = coll;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount()
	{
		return coll.size();
	}

	public Object getItem(int position)
	{
		return coll.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	/**
	 * 得到Item的类型，是对方发过来的消息，还是自己发送出去的
	 */
	public int getItemViewType(int position)
	{
		ChatMsgEntity entity = coll.get(position);

		if (entity.getMsgType())
		{
			return IMsgViewType.IMVT_COM_MSG;
		}
		else
		{
			//自己发送的消息
			return IMsgViewType.IMVT_TO_MSG;
		}
	}

	/**
	 * Item类型的总数
	 */
	public int getViewTypeCount()
	{
		return ITEMCOUNT;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{

		ChatMsgEntity entity = coll.get(position);
		boolean isComMsg = entity.getMsgType();

		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			try
			{
				if (isComMsg)
				{
					convertView = mInflater.inflate(
							R.layout.chatting_item_msg_text_left, null);
				}
				else
				{
					convertView = mInflater.inflate(
							R.layout.chatting_item_msg_text_right, null);
				}
			}
			catch(Exception e)
			{
				System.out.println(e.toString());
			}
			try
			{
				String path = Environment.getExternalStorageDirectory().getPath();
				if(isComMsg)
				{
					CircularImage cover_user_photo_left = (CircularImage) convertView.findViewById(R.id.cover_user_photo_left);

					File file_left = new File(path + "/oldBookImage/Image/avatar_" + entity.getSender() + ".jpg");
					try {
						FileInputStream fis = new FileInputStream(file_left);
						Bitmap avatar = BitmapFactory.decodeStream(fis);
						cover_user_photo_left.setImageBitmap(avatar);
					} catch (FileNotFoundException e)
					{

					}
				}
				else
				{
					CircularImage cover_user_photo_right = (CircularImage) convertView.findViewById(R.id.cover_user_photo_right);
					File file_right = new File(path + "/oldBookImage/Image/avatar_" + OldBookApplication.ID + ".jpg");
					try {
						FileInputStream fis = new FileInputStream(file_right);
						Bitmap avatar = BitmapFactory.decodeStream(fis);
						cover_user_photo_right.setImageBitmap(avatar);
					} catch (FileNotFoundException e) {

					}
				}
			}
			catch(Exception e)
			{

			}



			viewHolder = new ViewHolder();
			viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_chat_username);
			viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
			viewHolder.isComMsg = isComMsg;

			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvSendTime.setText(entity.getTime());
		//viewHolder.tvUserName.setText(entity.());
		viewHolder.tvContent.setText(entity.getContent());
		return convertView;
	}

	static class ViewHolder
	{
		public TextView tvSendTime;
		public TextView tvUserName;
		public TextView tvContent;
		public boolean isComMsg = true;
	}

}
