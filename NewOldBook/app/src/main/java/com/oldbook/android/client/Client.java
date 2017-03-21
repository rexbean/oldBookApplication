package com.oldbook.android.client;

import android.util.Log;

import com.oldbook.android.Application.OldBookApplication;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;


import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Created by Administrator on 2016/3/7.
 */
public class Client
{
    private String ip;                          //ip地址
    private int port;                           //端口号
    private Socket client;                      //客户端socket

    public Client(String ip,int port)
    {
        this.ip=ip;
        this.port=port;

    }

    public Socket getSocket()
    {
        return client;
    }

    /**
     * 连接socket
     */
    public void Start()
    {
        try
        {
            Log.i("OldBook", "socket.connect@socket连接 " + new Date().toString());
            try
            {
                //IO.Options opts=new IO.Options();
                //opts.reconnection=false;
                client = IO.socket("http://" + ip + ":" + port);
                client.on(Socket.EVENT_CONNECT, onConnect);
                client.on(Socket.EVENT_DISCONNECT, onDisconnect);
                client.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
                client.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            }
            catch(URISyntaxException e)
            {
                e.printStackTrace();
                Log.e("OldBook", "socket连接失败 " + new Date().toString());
                e.printStackTrace();
                OldBookApplication.setSocketStatue(-2);
            }
            client.connect();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Log.e("OldBook", "socket连接失败 " + new Date().toString());
            e.printStackTrace();
            OldBookApplication.setSocketStatue(-1);
        }

    }

   //成功连接
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args)
        {
            //String data=(String)args[0];
            Log.i("Socket.io","socket.io已连接 "+new Date().toString());
            OldBookApplication.setSocketStatue(1);
        }
    };
    //连接失败
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args)
        {
            Log.i("Socket.io","socket.io连接失败 "+new Date().toString());
            OldBookApplication.setSocketStatue(-1);
        }
    };
    //断开连接GetMsgService
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args)
        {
            Log.i("Socket.io","socket.io已断开连接 "+new Date().toString());
            OldBookApplication.setSocketStatue(-1);
        }
    };

    public void setIp(String ip)
    {
        this.ip=ip;
    }

    public void setPort(int port)
    {
        this.port=port;
    }

}

