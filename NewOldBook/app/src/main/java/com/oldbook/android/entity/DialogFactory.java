package com.oldbook.android.entity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import com.oldbook.android.Application.OldBookApplication;
import com.oldbook.android.R;


public class DialogFactory
{

	public static Dialog creatRequestDialog(final Context context, String tip)
	{

		final Dialog dialog = new Dialog(context, R.style.dialog);
		dialog.setContentView(R.layout.dialog_layout);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();

		lp.width = (int) (0.6 * OldBookApplication.SCREEN_WIDTH);

		TextView titleTxtv = (TextView) dialog.findViewById(R.id.tvLoad);
		if (tip == null || tip.length() == 0) {
			titleTxtv.setText("正在发送请求");
		} else {
			titleTxtv.setText(tip);
		}

		return dialog;
	}

	public static void ToastDialog(Context context, String title, String msg) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg)
				.setPositiveButton("确定", null).create().show();
	}
}
