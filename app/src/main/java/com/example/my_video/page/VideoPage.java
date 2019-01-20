package com.example.my_video.page;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.my_video.R;
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
                //设置适配器
                videoAdapter = new VideoAdapter();
                listView.setAdapter(videoAdapter);
                //隐藏文本
                nonView.setVisibility(View.GONE);
            }else {
                //没数据
                //显示文本
                nonView.setVisibility(View.VISIBLE);
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

                        videoItemArrayList.add(videoItem);
                    }
                    cursor.close();
                }
                //Handle send massage 不管有没有获取到视频资源都要发送消息
                handler.sendEmptyMessage(10);
            }
        }.start();
    }

    class VideoAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return videoItemArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHold viewHold;
            if (convertView == null){
                convertView = View.inflate(context,R.layout.view_item,null);
                viewHold = new ViewHold();
                viewHold.iv_image = convertView.findViewById(R.id.iv_image);
                viewHold.tv_videoName = convertView.findViewById(R.id.tv_videoName);
                viewHold.tv_videoTime = convertView.findViewById(R.id.tv_videoTime);
                viewHold.tv_videoSize = convertView.findViewById(R.id.tv_videoSize);

                convertView.setTag(viewHold);
            }else {
               viewHold = (ViewHold) convertView.getTag();
            }
            //根据position得到列表中的数据
            VideoItem videoItems = videoItemArrayList.get(position);
            viewHold.tv_videoName.setText(videoItems.getName());
            viewHold.tv_videoSize.setText(Formatter.formatFileSize(context,videoItems.getSize()));
            viewHold.tv_videoTime.setText(timeUtils.stringForTime((int)videoItems.getDuration()));
            return convertView;
        }
    }

    static class ViewHold{
        ImageView iv_image;
        TextView tv_videoName;
        TextView tv_videoTime;
        TextView tv_videoSize;
    }
}
