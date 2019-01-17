package com.example.my_video.basePage;

import android.content.Context;
import android.view.View;

import java.util.logging.Logger;

public abstract class BasePage {
    public final Context context;
    public View rootView;
    public boolean isinitDate;

    public BasePage(Context context){
        this.context=context;
        rootView=initView();
    }
    public abstract View initView();

    /**
     * 当子页面需要初始化数据，联网请求数据，或绑定数据的时候需要绑定
     */
    public void initDate(){
    }

}
