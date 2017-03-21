package com.oldbook.android;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.oldbook.android.Application.OldBookApplication;
import com.oldbook.android.client.Client;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends Activity
{
    Button btnSocket;
    TextView tvStatue;
    OldBookApplication application;
    String ip;
    Client client;
    int port;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());


        application=(OldBookApplication)getApplicationContext();
        ip=OldBookApplication.SERVER_IP;
        port=OldBookApplication.PORT;

        i=0;

        tvStatue=(TextView)findViewById(R.id.tv_socketStatue);
        btnSocket=(Button)findViewById(R.id.btn_socket);
        btnSocket.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               // if(i%2==0)
              //  {
                    client = new Client(ip, port);


              //  }
              //  else
               // {

                    //client.getOut().setMsg("out");
                    //client.getOut().setIsStart(false);
                   // try
                   // {
                   //     while(client.getIn().getIsStart())
                   //     {

                   //     }
                    //    client.getSocket().close();
                    //}catch(IOException e)
                   // {
                   //     e.printStackTrace();
                   // }
                   // i++;
               // }





            }
        });
    }


}
