package com.example.my_video.page;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.my_video.R;
import com.example.my_video.basePage.BasePage;
import com.example.my_video.dto.VideoItem;
import com.example.my_video.utils.LogUtils;

import java.util.ArrayList;


public class VideoPage extends BasePage {
    private ListView listView;
    private TextView nonView;
    private ProgressBar pb_video;
    private ArrayList<VideoItem> videoItemArrayList = new ArrayList<>();
    public VideoPage(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context,R.layout.video_pager,null);
        listView = view.findViewById(R.id.listview);
        nonView = view.findViewById(R.id.nonView);
        pb_video = view.findViewById(R.id.pb_video);
        return view;
    }
    @Override
    public void initDate(){
        LogUtils.e("本地视频页面初始化数据。。。。");
        super.initDate();//联网时请求或者绑定数据
        getFromLocal();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (videoItemArrayList != null && videoItemArrayList.size()>0){
                //有数据
            }else {
                //没数据
            }
        }
    };

    /**
     * 获取本地数据
     */
    private void getFromLocal() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                ContentResolver contentResolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] object = {
                        MediaStore.Video.Media.DISPLAY_NAME,//视频在sd卡的名称
                        MediaStore.Video.Media.DURATION,//视频总时长
                        MediaStore.Video.Media.SIZE,//视频文件大小
                        MediaStore.Video.Media.DATA,//视频地址（绝对地址）
                        MediaStore.Video.Media.ARTIST,//歌者名字
                };
                Cursor cursor = contentResolver.query(uri,object,null,null,null);
                if (cursor!=null){
                    while (cursor.moveToNext()){
                        VideoItem videoItem = new VideoItem();

                        String name = cursor.getString(0);
                        videoItem.setName(name);

                        long duration = cursor.getLong(1);
                        videoItem.setDuration(duration);

                        long size = cursor.getLong(2);
                        videoItem.setSize(size);

                        String data = cursor.getString(3);
                        videoItem.setData(data);

                        String artist = cursor.getString(4);
                        videoItem.setArtist(artist);

                        videoItemArrayList.add(videoItem);
                    }
                    cursor.close();
                }
                //Handle send massage 不管有没有获取到视频资源都要发送消息
                handler.sendEmptyMessage(10);
            }
        }.start();
    }
}
