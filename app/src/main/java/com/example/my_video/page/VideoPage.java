package com.example.my_video.page;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.my_video.R;
import com.example.my_video.activity.VideoPlayerWindownActivity;
import com.example.my_video.adapter.VideoAdapter;
import com.example.my_video.basePage.BasePage;
import com.example.my_video.dto.VideoItem;
import com.example.my_video.utils.LogUtils;
import com.example.my_video.utils.TimeUtils;

import java.util.ArrayList;


public class VideoPage extends BasePage {
    private ListView listView;
    private TextView nonView;
    private ProgressBar pb_video;
    private VideoItem videoItem;
    private ArrayList<VideoItem> videoItemArrayList;
    private TimeUtils timeUtils;
    private VideoAdapter videoAdapter;

    public VideoPage(Context context) {
        super(context);
        timeUtils = new TimeUtils();
    }

    @Override
    public View initView() {
        View view = View.inflate(context,R.layout.video_pager,null);
        listView = view.findViewById(R.id.listview);
        nonView = view.findViewById(R.id.nonView);
        pb_video = view.findViewById(R.id.pb_video);
        listView.setOnItemClickListener(new OnContextClickListener());//点击事件，当点击某一条播放视频时触发的事件
        return view;
    }

    /**
     * 视频播放列表的点击事件
     * 使用OnContextClickListener方法来为ListView注册了一个监听器，
     * 当用户点击LisetView中任何一个子项时就会回调onItemClick，
     * 这个方法可以通过position判断出用户点击的是哪一个子项，然后获取到想要视频
     */

    class OnContextClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//点击列表时触发的事件
           VideoItem videoItem = videoItemArrayList.get(position);
           //打印信息
            //Toast.makeText(context, videoItem.toString(),Toast.LENGTH_SHORT).show();
            //调用播放器播放手机文件的视频
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            String path = videoItem.getData();//该路径可以自定义
//            File file = new File(path);
//            Uri uri = Uri.fromFile(file);
//            intent.setDataAndType(uri, "video/*");
//            context.startActivity(intent);
         //自定义播放器
//            Intent intent = new Intent(context,VideoPlayerWindownActivity.class);
//            String path = videoItem.getData();//该路径可以自定义
//            File file = new File(path);
//            Uri uri = Uri.fromFile(file);
//            intent.setDataAndType(uri, "video/*");
//            context.startActivity(intent);
            //传递列表数据-对象-序列化
            Intent intent = new Intent(context,VideoPlayerWindownActivity.class);//到播放界面
            Bundle bundle = new Bundle();
            bundle.putSerializable("videoList",videoItemArrayList);//传递数据 key-value
            intent.putExtras(bundle);
            intent.putExtra("position",position);
            context.startActivity(intent);
        }
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
                //设置适配器
                videoAdapter = new VideoAdapter(context,videoItemArrayList,true);
                listView.setAdapter(videoAdapter);
                //隐藏文本
                nonView.setVisibility(View.GONE);
            }else {
                //没数据
                //显示文本
                nonView.setVisibility(View.VISIBLE);
                nonView.setText("没有发现音频。。。");
            }
            pb_video.setVisibility(View.GONE);
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
                videoItemArrayList = new ArrayList<>();
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
                        videoItem = new VideoItem();

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

                        if (videoItemArrayList!=null){
                            videoItemArrayList.add(videoItem);
                        }

                    }
                    cursor.close();
                }
                //Handle send massage 不管有没有获取到视频资源都要发送消息
                handler.sendEmptyMessage(10);
            }
        }.start();
    }
}
