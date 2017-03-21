package com.oldbook.android.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.oldbook.android.Application.OldBookApplication;


public class SharePreferenceUtil
{
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	public SharePreferenceUtil(Context context, String file)
	{
		sp = context.getSharedPreferences(file, context.MODE_PRIVATE);
		editor = sp.edit();
	}

	// 设置密码
	public void setPassword(String password)
	{
		editor.putString("password", password);
		editor.commit();
	}

	public String getPassword()
	{
		return sp.getString("password", "");
	}
	//记住密码
	public void setSavePassword(boolean isChecked)
	{
		editor.putBoolean("SavePassword", isChecked);
		editor.commit();
	}
	public boolean getSavePassword()
	{
		return sp.getBoolean("SavePassword", false);
	}
	//自动登陆
	public void setAutoLogin(boolean isChecked)
	{
		editor.putBoolean("AutoLogin", isChecked);
		editor.commit();
	}
	public boolean getAutoLogin()
	{
		return sp.getBoolean("AutoLogin", false);
	}

	// 设置id
	public void setId(int id)
	{
		editor.putString("id", String.valueOf(id));
		editor.commit();
	}

	public int getId()
	{
		return Integer.parseInt(sp.getString("id", "-1"));
	}

	// 获取用户名
	public String getUsername()
	{
		return sp.getString("username", "");
	}

	public void setUsername(String username)
	{
		editor.putString("username", username);
		editor.commit();
	}



	// ip
	public void setIp(String ip)
	{
		editor.putString("ip", ip);
		editor.commit();
	}

	public String getIp()
	{
		return sp.getString("ip", OldBookApplication.SERVER_IP);
	}

	// 设置端口
	public void setPort(int port)
	{
		editor.putInt("port", port);
		editor.commit();
	}

	public void setPort_pic(int port)
	{
		editor.putInt("pic_port",port);
		editor.commit();
	}

	public int getPort()
	{
		return sp.getInt("port", OldBookApplication.PORT);
	}



	// 设置是否后台运行
	public void setIsStart(boolean isStart)
	{
		editor.putBoolean("isStart", isStart);
		editor.commit();
	}

	public boolean getIsStart()
	{
		return sp.getBoolean("isStart", false);
	}

	// 是否第一次运行
	public void setIsFirst(boolean isFirst)
	{
		editor.putBoolean("isFirst", isFirst);
		editor.commit();
	}

	public boolean getisFirst()
	{
		return sp.getBoolean("isFirst", true);
	}

	// 设置id
	public void setRecentId(int id)
	{
		editor.putString("recentId", String.valueOf(id));
		editor.commit();
	}

	public int getRecentId()
	{
		return Integer.parseInt(sp.getString("recentId", "-1"));
	}

	public void setRecentUsername(String username)
	{
		editor.putString("recentUsername", username);
		editor.commit();
	}
	public String getRecentUsername()
	{
		return sp.getString("recentUsername", "-1");
	}

}
