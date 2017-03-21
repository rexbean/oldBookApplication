package com.oldbook.android.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.oldbook.android.Application.OldBookApplication;
import com.oldbook.android.R;
import com.oldbook.android.entity.FriendEntity;
import com.oldbook.android.util.SharePreferenceUtil;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class ChatListViewAdapter extends BaseAdapter
{
    private Context context;                        //运行上下文
    //private List<Map<String, Object>> listItems;
    List<FriendEntity> listItems;
    private LayoutInflater listContainer;           //视图容器
    private SharePreferenceUtil util;
    public final class ListItemView                 //自定义控件集合    
    {
        public ImageView image;
        public TextView tvPetName;
        public TextView tvEvaluation;

    }

    public ChatListViewAdapter(Context context,List<FriendEntity> list)
    {
        this.context = context;
        this.listItems = list;
        listContainer = LayoutInflater.from(context);
        util=new SharePreferenceUtil(context, OldBookApplication.SAVE_USER);
    }

    //public ChatListViewAdapter(Context context, List<Map<String, Object>> listItems) {   
    //    this.context = context;            
    //    listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文   
    //    this.listItems = listItems;   
    //}   

    public int getCount()
    {
        if(listItems==null)
            return 0;
        else
            return listItems.size();
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub   
        Log.e("method", "getView");
        //final int selectID = position;   
        //自定义视图   
        ListItemView  listItemView = null;
        //if (convertView == null)
        //{
            listItemView = new ListItemView();
            //获取list_item布局文件的视图   
            convertView = listContainer.inflate(R.layout.chat_infor, null);
            //获取控件对象   
            listItemView.tvPetName = (TextView)convertView.findViewById(R.id.tv_petname_chat);


            //设置控件集到convertView   
            convertView.setTag(listItemView);
        //}
        //else
        //{
          //  listItemView = (ListItemView)convertView.getTag();
        //}

        if(listItems!=null)
        {
            FriendEntity fe = listItems.get(position);
            /**
             * 摄者圆形头像
             */
            CircularImage cover_user_photo = (CircularImage) convertView.findViewById(R.id.cover_user_photo);
            //cover_user_photo.setImageResource(R.drawable.logo);
            String path = Environment.getExternalStorageDirectory().getPath();
            try {

                int id = fe.getId();
                if (id != -1)
                {
                    File file = new File(path + "/oldBookImage/Image/avatar_" + id + ".jpg");
                    if(file.exists())
                    {
                        try {
                            FileInputStream fis = new FileInputStream(file);
                            Bitmap avatar = BitmapFactory.decodeStream(fis);
                            cover_user_photo.setImageBitmap(avatar);
                        } catch (FileNotFoundException e) {

                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            listItemView.tvPetName.setText(fe.getPetName());
            //listItemView.tvEvaluation.setText(fe.getEvaluation());

        }


        return convertView;
    }
}  