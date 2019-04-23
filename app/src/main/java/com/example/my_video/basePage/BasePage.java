package com.example.my_video.basePage;

import android.content.Context;
import android.view.View;

public abstract class BasePage {
    /**
     * 上下文
     */
    public final Context context;
    /**
     * 接收各页面的实例
     */
    public View rootView;
    public boolean isinitDate;

    public BasePage(Context context){
        this.context=context;
        rootView=initView();
    }

    /**
     * 强制子页面实现该方法，实现想要的特定效果
     * @return
     */
    public abstract View initView();

    /**
     * 当子页面需要初始化数据，联网请求数据，或绑定数据的时候需要绑定
     */
    public void initDate(){
    }

}
