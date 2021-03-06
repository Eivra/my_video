package com.example.my_video.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.my_video.R;
import com.example.my_video.dto.VideoItem;
import com.example.my_video.utils.TimeUtils;

import java.util.ArrayList;

public class VideoAdapter extends BaseAdapter {
    private final boolean isVideo;
    private Context context;
    private ArrayList<VideoItem> videoItemArrayList;
    private TimeUtils timeUtils;
    private  MediaMetadataRetriever media = new MediaMetadataRetriever();
    private ImageView imageView;

    public  VideoAdapter(Context context,ArrayList<VideoItem> videoPageVideoItemArrayList,boolean isAudio){
        this.context=context;
        this.videoItemArrayList=videoPageVideoItemArrayList;
        this.isVideo = isAudio;
        timeUtils = new TimeUtils();
    }

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
        ViewHold viewHold;//对控件的实例进行缓存
        if (convertView == null){
            convertView = View.inflate(context,R.layout.view_item,null);
            viewHold = new ViewHold();
            viewHold.iv_image = convertView.findViewById(R.id.iv_image);
            viewHold.tv_videoName = convertView.findViewById(R.id.tv_videoName);
            viewHold.tv_videoTime = convertView.findViewById(R.id.tv_videoTime);
            viewHold.tv_videoSize = convertView.findViewById(R.id.tv_videoSize);

            convertView.setTag(viewHold);//将viewHold存储在view
        }else {
            viewHold = (ViewHold) convertView.getTag();
        }

        //根据position得到列表中的数据
        VideoItem videoItems = videoItemArrayList.get(position);

        media.setDataSource(videoItems.getData());
        /**
         * 显示视频第一帧缩略图
         */
        Bitmap bitmap = media.getFrameAtTime();
        imageView = convertView.findViewById(R.id.iv_image);
        imageView.setImageBitmap(bitmap);

        viewHold.tv_videoName.setText(videoItems.getName());
        viewHold.tv_videoSize.setText(Formatter.formatFileSize(context,videoItems.getSize()));
        viewHold.tv_videoTime.setText(timeUtils.stringForTime((int)videoItems.getDuration()));

        if (!isVideo){
            viewHold.iv_image.setImageResource(R.drawable.audiologo);
        }

        return convertView;
    }

    static class ViewHold{
        ImageView iv_image;
        TextView tv_videoName;
        TextView tv_videoTime;
        TextView tv_videoSize;
    }
}
