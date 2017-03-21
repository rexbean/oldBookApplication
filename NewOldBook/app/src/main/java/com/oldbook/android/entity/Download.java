package com.oldbook.android.entity;

import android.os.Environment;
import android.util.Log;

import com.oldbook.android.Application.OldBookApplication;
import com.oldbook.android.client.Client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import io.socket.emitter.Emitter;

/**
 * Created by Administrator on 2016/3/22.
 */
public class Download
{
    private String path;
    public Boolean downloadOver=false;
    private Client client;
    public Download(String path)
    {
        this.path=path;
        client= OldBookApplication.getClient();
        client.getSocket().on("DOWNLOAD_PIC",downloadResult);
    }

    public void download()
    {
        Log.e("order","PIC_SERVER_TO_CLIENT "+new Date().toString());
        client.getSocket().emit("PIC_SERVER_TO_CLIENT", path + ".jpg");
    }

    public boolean checkDirectory()
    {
        String rootPath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(rootPath+"/oldBookImage/Image/"+path+".jpg");
        System.out.println("检测目录：" + file.getPath());
        if(file.exists())
        {
            System.out.println("存在");
            return true;

        }
        else {
            System.out.println("不存在");
            return false;
        }

    }
    private Emitter.Listener downloadResult = new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
    {
        Log.e("Socket.io", "开始下载图片");
        new Thread(new Runnable()
        {
            @Override
            public void run() {
                if (args[2] == null) {

                    String name = (String) args[0];
                    if (args[0].equals(path+".jpg"))
                    {
                        byte[] buffer = (byte[]) args[1];
                        String rootPath = Environment.getExternalStorageDirectory().getPath();
                        File file = new File(rootPath + "/oldBookImage/Image/" + path + ".jpg");
                        try {
                            ;
                            BufferedOutputStream fo = new BufferedOutputStream(new FileOutputStream(file));
                            //file.createNewFile();
                            fo.write(buffer);

                            fo.flush();
                            fo.close();


                            boolean result = checkDirectory();
                            Log.e("Socket.io", path + "下载完成状态为" + result + " " + new Date().toString());
                            downloadOver = true;
                        } catch (IOException e) {
                            Log.e("Socket.io", "下载错误 " + new Date().toString());
                            e.printStackTrace();
                        }
                    }


                } else {
                    Log.e("Socket.io", "下载错误"+args[2].toString() + new Date().toString());
                    downloadOver = true;
                }

            }
        }).start();
    }
};
}
