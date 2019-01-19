package com.example.my_video.page;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.my_video.basePage.BasePage;
import com.example.my_video.utils.LogUtils;

public class AudioPage extends BasePage {
    private TextView textView;
    public AudioPage(Context context) {
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
        LogUtils.e("本地音频页面初始化数据。。。。");
        super.initDate();
        textView.setText("本地音频页面");
    }

}
