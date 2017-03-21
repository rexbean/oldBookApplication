package com.oldbook.android.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.oldbook.android.Application.OldBookApplication;
import com.oldbook.android.R;
import com.oldbook.android.entity.BookEntity;

import java.io.File;
import java.util.List;


//import com.oldbook.android.net.PicUploadService;

public class BookListViewAdapter extends BaseAdapter
{
    private Context context;                        //运行上下文
    private List<BookEntity> listItems;
    private LayoutInflater listContainer;           //视图容器
    private OldBookApplication application;
    private BookEntity be;
    Handler mHandler;
    public final class ListItemView                 //自定义控件集合    
    {
        public ImageView image;
        public TextView tvBookName;
        public TextView tvAuthor;
        public TextView tvPublishing;
        public TextView tvNumber;
    }


    public BookListViewAdapter(Context context, List<BookEntity> listItems)
    {
        this.context = context;
        listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文
        this.listItems = listItems;
        application = (OldBookApplication)context.getApplicationContext();
    }



    public int getCount() {
        // TODO Auto-generated method stub
    if(listItems!=null)
        return listItems.size();
    else
        return 0;
    }


    @Override
    public BookEntity getItem(int arg0) {
        // TODO Auto-generated method stub
        if(listItems!=null)
             return listItems.get(arg0-1) ;
        else
            return new BookEntity();
    }

    public long getItemId(int arg0) {
        // TODO Auto-generated method stub   
        return 0;
    }












    /**
     * ListView Item设置  
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub   
        //Log.e("method", "getView");   
        final int selectID = position;
        //自定义视图   
         ListItemView   listItemView = null;
        if (convertView == null)
        {
            listItemView = new ListItemView();
            //获取list_item布局文件的视图   
            convertView = listContainer.inflate(R.layout.book_infor, null);



            //获取控件对象   
            listItemView.image = (ImageView)convertView.findViewById(R.id.iv_bookSuface);
            listItemView.tvBookName = (TextView)convertView.findViewById(R.id.tv_bookName);
            listItemView.tvAuthor = (TextView)convertView.findViewById(R.id.tv_author);
            listItemView.tvPublishing= (TextView)convertView.findViewById(R.id.tv_publishing);
            listItemView.tvNumber = (TextView)convertView.findViewById(R.id.tv_number);



            //设置控件集到convertView   
            convertView.setTag(listItemView);
        }
        else
        {
            listItemView = (ListItemView)convertView.getTag();
       }
        if(listItems!=null)
        {
            BookEntity be = listItems.get(position);
            int id = be.getId();

            String path = Environment.getExternalStorageDirectory().getPath();
            String fileName = path + "/oldBookImage/Image/book_" + id + ".jpg";


            File file = new File(fileName);
            if(file.exists())
            {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                Bitmap bitmap = BitmapFactory.decodeFile(fileName, options);
                listItemView.image.setImageBitmap(bitmap);
            }
            listItemView.tvBookName.setText(be.getBookName());
            listItemView.tvAuthor.setText(be.getBookAuthor());
            listItemView.tvPublishing.setText(be.getBookPublishing());
            listItemView.tvNumber.setText(be.getbookNumber());

        }

        return convertView;
    }

}  