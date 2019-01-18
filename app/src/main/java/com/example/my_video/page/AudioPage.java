package com.example.my_video.page;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.my_video.R;
import com.example.my_video.basePage.BasePage;
import com.example.my_video.utils.LogUtils;

public class AudioPage extends BasePage {
    private ListView listView;
    private TextView nonView;
    private ProgressBar pb_video;
    public AudioPage(Context context) {
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
        LogUtils.e("本地音频页面初始化数据。。。。");
        super.initDate();
    }
}
