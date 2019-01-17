package com.example.my_video.page;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.my_video.basePage.BasePage;
import com.example.my_video.utils.LogUtils;


public class VideoPage extends BasePage {
    private TextView textView;
    public VideoPage(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        textView=new TextView(context);
        textView.setTextSize(25);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        return textView;
    }
    @Override
    public void initDate(){
        LogUtils.e("本地视频页面初始化数据。。。。");
        super.initDate();//联网时请求或者绑定数据
        textView.setText("本地视频页面");
    }
}
