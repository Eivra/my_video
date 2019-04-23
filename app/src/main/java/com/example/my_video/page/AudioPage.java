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
import com.example.my_video.activity.AuideoPlayerWindownActivity;
import com.example.my_video.adapter.VideoAdapter;
import com.example.my_video.basePage.BasePage;
import com.example.my_video.dto.VideoItem;
import com.example.my_video.utils.LogUtils;
import com.example.my_video.utils.TimeUtils;

import java.util.ArrayList;

public class AudioPage extends BasePage {
    private ListView listView;
    private TextView nonView;
    private ProgressBar pb_video;
    private VideoItem videoItem;
    /**
     * 装数据集合
     */
    private ArrayList<VideoItem> videoItemArrayList;
    private TimeUtils timeUtils;
    private VideoAdapter videoAdapter;

    public AudioPage(Context context) {
        super(context);
        timeUtils = new TimeUtils();
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.video_pager, null);//存放视频或音频列表的布局界面
        listView = view.findViewById(R.id.listview);
        nonView = view.findViewById(R.id.nonView);
        pb_video = view.findViewById(R.id.pb_video);
        listView.setOnItemClickListener(new AudioPage.OnContextClickListener());
        return view;
    }

    class OnContextClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            VideoItem videoItem = videoItemArrayList.get(position);
            //传递列表数据-对象-序列化
            Intent intent = new Intent(context, AuideoPlayerWindownActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videoList", videoItemArrayList);
            intent.putExtras(bundle);
            intent.putExtra("position", position);
            context.startActivity(intent);
        }
    }

    @Override
    public void initDate() {
        LogUtils.e("本地音频页面初始化数据。。。。");
        super.initDate();//联网时请求或者绑定数据
        getFromLocal();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (videoItemArrayList != null && videoItemArrayList.size() > 0) {
                //有数据
                //设置适配器
                videoAdapter = new VideoAdapter(context, videoItemArrayList,false);//isAudio判断当前是对音频的加载还是视频，因为图片不一样
                listView.setAdapter(videoAdapter);
                //隐藏文本
                nonView.setVisibility(View.GONE);
            } else {
                //没数据
                //显示文本
                nonView.setVisibility(View.VISIBLE);
                nonView.setText("没有发现音频。。。。");
            }
            pb_video.setVisibility(View.GONE);
        }
    };

    /**
     * 获取本地数据
     */
    private void getFromLocal() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                videoItemArrayList = new ArrayList<>();
                ContentResolver contentResolver = context.getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] object = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//音频在sd卡的名称
                        MediaStore.Audio.Media.DURATION,//音频总时长
                        MediaStore.Audio.Media.SIZE,//音频文件大小
                        MediaStore.Audio.Media.DATA,//音频地址（绝对地址）
                        MediaStore.Audio.Media.ARTIST,//歌者名字
                };
                Cursor cursor = contentResolver.query(uri, object, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
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

                        videoItemArrayList.add(videoItem);
                    }
                    cursor.close();
                }
                //Handle send massage 不管有没有获取到音频资源都要发送消息
                handler.sendEmptyMessage(10);
            }
        }.start();

    }
}
