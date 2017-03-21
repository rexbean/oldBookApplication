package com.oldbook.android.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.oldbook.android.Application.OldBookApplication;
import com.oldbook.android.R;
import com.oldbook.android.client.Client;
import com.oldbook.android.entity.BorrowEntity;
import com.oldbook.android.entity.ChatMsgEntity;
import com.oldbook.android.entity.MessageEntity;
import com.oldbook.android.entity.MessageType;
import com.oldbook.android.ui.HomepageActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import io.socket.emitter.Emitter;

//import com.oldbook.android.net.PicUploadService;

public class PersonalInforListViewAdapter extends BaseAdapter {
    private Context context;                        //运行上下文
    private List<BorrowEntity> listItems;    //商品信息集合
    private LayoutInflater listContainer;           //视图容器
    private OldBookApplication application;
    private  ListItemView  listItemView;
    private BorrowEntity boe;
    private int evaluation;
    private String p_evaluation;
    public int realPosition=0;
    private Client client;
    public final class ListItemView                 //自定义控件集合    
    {

        public TextView tvBookName;
        public TextView tvSReturnDate;
        public TextView tvEvaluation;
        public Button btnStatue;
    }


    public PersonalInforListViewAdapter(Context context, List<BorrowEntity> listItems) {
        this.context = context;
        listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文
        this.listItems = listItems;
        application = (OldBookApplication)context.getApplicationContext();
        client=OldBookApplication.getClient();

    }

    public int getCount() {
        // TODO Auto-generated method stub
        if(listItems!=null)
            return listItems.size();
        else
            return 0;
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub   
        return null;
    }

    public long getItemId(int arg0) {
        // TODO Auto-generated method stub   
        return 0;
    }



    /**
     * ListView Item设置  
     */
    public View getView( int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        //Log.e("method", "getView");
        final int selectID = position;
        //自定义视图

        //if (convertView == null)
        //{
            listItemView = new ListItemView();
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.borrow_record, null);



            //获取控件对象
            //listItemView.image = (ImageView)convertView.findViewById(R.id.iv_bookSuface);
            listItemView.tvBookName = (TextView) convertView.findViewById(R.id.tv_bookName);
            listItemView.tvSReturnDate = (TextView) convertView.findViewById(R.id.tv_sreturn_date);
            listItemView.tvEvaluation = (TextView) convertView.findViewById(R.id.tv_evaluate);
            listItemView.btnStatue = (Button) convertView.findViewById(R.id.btn_statue);
            realPosition=position;
            if(position==0&&realPosition>position)
            {
                realPosition++;
            }
            listItemView.btnStatue.setTag(realPosition);
            //设置控件集到convertView
            convertView.setTag(listItemView);


    if(listItems!=null)
    {
        boe = listItems.get(position);
        listItemView.tvBookName.setText(boe.getBookName());
        listItemView.tvSReturnDate.setText(boe.getSReturnTime());
        listItemView.tvEvaluation.setText(String.valueOf(boe.getEvaluation()));
        if (boe.getFromUser() == 0)
        {
            switch (boe.getStatue())
            {
                case "LEND":
                    boe.setStatue("RETURN");
                    break;
                case "WAIT_EVALUATION":
                    boe.setStatue("WAIT_EVALUATION");
                    break;
                case "EVALUATION_OVER":
                    boe.setStatue("EVALUATION_OVER");
                    break;
                default:
                    break;
            }
        }
        if (boe.getGetUser() == 0)
        {
            switch (boe.getStatue())
            {
                case "LEND":
                    boe.setStatue("LEND");
                    break;
                case "WAIT_EVALUATION":
                    boe.setStatue("EVALUATION");
                    break;
                case "EVALUATION_OVER":
                    boe.setStatue("EVALUATION_OVER");
                    break;
                default:
                    break;
            }
        }


    switch (boe.getStatue()) {
        case "RETURN":
            listItemView.btnStatue.setText("归还");
            listItemView.btnStatue.setEnabled(true);
            listItemView.btnStatue.setOnClickListener(new ReturnListener((Integer) listItemView.btnStatue.getTag()));
            break;
        case "LEND":
            listItemView.btnStatue.setText("等待归还");
            listItemView.btnStatue.setEnabled(false);

            break;
        case "WAIT_EVALUATION":
            listItemView.btnStatue.setText("等待评价");
            listItemView.btnStatue.setEnabled(false);
            break;
        case "EVALUATION":
            listItemView.btnStatue.setText("评价");
            listItemView.btnStatue.setOnClickListener(new EvaluationListener((Integer) listItemView.btnStatue.getTag()));
            //listItemView.btnStatue.setText("已评价");
            //listItemView.btnStatue.setEnabled(false);
            //this.notifyDataSetChanged();
            break;
        case "EVALUATION_OVER":
            listItemView.btnStatue.setText("已评价");
            listItemView.btnStatue.setEnabled(false);
            break;
        default:
            break;
    }

}
        return convertView;
    }
    class EvaluationListener implements View.OnClickListener {

        private int position;
        public EvaluationListener(int pos) {
            position = pos;
        }

        @Override
        public void onClick(View view)
        {
            System.out.println("evaluation " + position);
            Evaluation(context, "提示", "请输入0-100分数进行评价", position);
            System.out.println("评价");
            //BorrowEntity boeNew=listItems.get((Integer)view.getTag());
            //boeNew.setStatue("EVALUATION_OVER");
            //boeNew.setEvaluation(evaluation);
            //notifyDataSetChanged();

        }
    }
    class ReturnListener implements View.OnClickListener {

        private int position;
        public ReturnListener(int pos) {
            position = pos;
        }

        @Override
        public void onClick(View view) {
            Return(context, "提示", "确认还书吗?", position);
            System.out.println("还书");
            //BorrowEntity boeNew=listItems.get((Integer)view.getTag());
            //boeNew.setStatue("WAIT_EVALUATION");
           // notifyDataSetChanged();
        }
    }
    public void Return(Context context, String title, String msg, final Integer click_position)
    {

        new AlertDialog.Builder(context).setTitle(title).setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        BorrowEntity boe=listItems.get(click_position);
                        Log.e("order", "UPDATE_STATUE " + new Date().toString());
                        Log.e("order", "UPDATE_BOOK " + new Date().toString());
                        client.getSocket().emit("UPDATE_STATUE",boe.getBorrowId(),boe.getGetUser());

                        client.getSocket().emit("UPDATE_BOOK","false",boe.getBookName(),boe.getBookId());
                        Log.e("Socket.io","还书编号是"+boe.getBookId()+new Date().toString());
                        System.out.println("还书2");

                        //close();// 父类关闭方法
                    }
                }).setNegativeButton("取消", null).create().show();
    }

    public void Evaluation(Context context, String title, String msg, final Integer click_position)
    {
        final EditText etEvaluation=new EditText(context);

        new AlertDialog.Builder(context).setTitle(title).setMessage(msg).setView(etEvaluation)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        BorrowEntity boe=listItems.get(click_position);
                        try
                        {
                            String evaluation_s = etEvaluation.getText().toString();
                            evaluation = Integer.parseInt(evaluation_s);
                            if(evaluation<=100&&evaluation>=0)
                            {
                                Log.e("order", "UPDATE_EVALUATION " + new Date().toString());
                                Log.e("order", "UPDATE_EVALUATION2 " + new Date().toString());
                                client.getSocket().emit("UPDATE_EVALUATION", boe.getBorrowId(), evaluation,boe.getFromUser());
                                client.getSocket().emit("UPDATE_EVALUATION2", boe.getFromUser(), evaluation);
                                Log.e("Socket.io", "借阅人" + boe.getFromUser() + " " + new Date());
                            }
                            else
                            {
                                etEvaluation.setText("");
                                Message msg=new Message();
                                msg.what=0;
                                handler_evaluation.sendMessage(msg);
                            }
                        }
                        catch(Exception e)
                        {
                            etEvaluation.setText("");
                            Message msg=new Message();
                            msg.what=0;
                            handler_evaluation.sendMessage(msg);
                        }
                        //close();// 父类关闭方法
                    }
                }).setNegativeButton("取消", null).create().show();
    }

    private Emitter.Listener evaluationResult= new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            try
            {
                JSONObject json = (JSONObject) args[0];
                String Statue = json.getString("statue");
                if(Statue.equals("EVALUATION_OVER"));
                {
                    //client.getSocket().emit("BORROWLIST_GETUSER",OldBookApplication.ID);
                   // client.getSocket().emit("BORROWLIST_FROMUSER",OldBookApplication.ID);
                }
            }
            catch(JSONException e)
            {
                Log.e("Socket.io","归还失败 "+new Date().toString());
                e.printStackTrace();
            }

        }
    };

    private Emitter.Listener returnResult= new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            try
            {
                JSONObject json = (JSONObject) args[0];
                String Statue = json.getString("statue");
                if(Statue.equals("WAIT_EVALUATION"));
                {
                    //client.getSocket().emit("BORROWLIST_GETUSER",OldBookApplication.ID);
                    //client.getSocket().emit("BORROWLIST_FROMUSER",OldBookApplication.ID);
                }
            }
            catch(JSONException e)
            {
                Log.e("Socket.io","归还失败 "+new Date().toString());
                e.printStackTrace();
            }

        }
    };

    private Emitter.Listener evaluation2Result= new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            try
            {
                JSONObject json = (JSONObject) args[0];
                 p_evaluation= json.getString("evaluation");

            }
            catch(JSONException e)
            {
                Log.e("Socket.io","归还失败 "+new Date().toString());
                e.printStackTrace();
            }

        }
    };

    private Handler handler_evaluation=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 0:
                    Toast.makeText(context,"请输入0-100进行评价",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

}  