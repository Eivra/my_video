package com.example.my_video.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

import com.example.my_video.R;
import com.example.my_video.global.AppConstants;
import com.example.my_video.utils.SpUtils;

public class PlayActivity extends AppCompatActivity {
private static final String TAG=PlayActivity.class.getSimpleName();//自动识别Acyivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isFirstOpen = SpUtils.getBoolean(this, AppConstants.FIRST_OPEN);
        // 如果是第一次启动，则先进入功能引导页
        if (!isFirstOpen) {
            Intent intent = new Intent(this, WelcomeGuideActivity.class);
            startActivity(intent);
            finish();
            return;
        }
            // 如果不是第一次启动app，则正常显示启动屏
            setContentView(R.layout.play_main);
            //设置两秒进入主页面
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    enterHomeActivity();
                }
            }, 2000);
        }


    private void enterHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        Log.e(TAG,"当前线程==="+Thread.currentThread().getName());
    }

    /**
     * 跳转到主页面，并且把当前页面关闭
     * 防止启动多次方法：
     *   1.在mainacyivity设置为单例
     *   2.以下方法
     */
    private void startMainActivity(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        //关闭当前页面
        finish();
    }
    //触摸屏幕立刻进入主页面

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG,"onTouchEvent==="+event.getAction());
        //主页面activity
        startMainActivity();
        return super.onTouchEvent(event);
    }


    @Override
    protected void onDestroy() {
        //消息和回调移除
        new Handler().removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
