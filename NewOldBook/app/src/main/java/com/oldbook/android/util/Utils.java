package com.oldbook.android.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.oldbook.android.Application.OldBookApplication;

public class Utils
{

	public static void getScreenWidth(Activity context)   //获取屏幕宽度
	{
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		OldBookApplication.SCREEN_WIDTH = dm.widthPixels;
	}

	public static void getScreenHeight(Activity context)  //获取屏幕高度
	{
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		OldBookApplication.SCREEN_HEIGHT = dm.heightPixels;
	}

	public static void getScreenDensity(Context context)   //获取屏幕密度
	{
		try
		{
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			manager.getDefaultDisplay().getMetrics(dm);
			OldBookApplication.DENSITY=dm.density;
		}
		catch (Exception ex)
		{

		}
		OldBookApplication.DENSITY=1.0f;
	}

}