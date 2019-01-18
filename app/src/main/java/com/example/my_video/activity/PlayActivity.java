package com.example.my_video.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.example.my_video.R;

public class PlayActivity extends AppCompatActivity {
private static final String TAG=PlayActivity.class.getSimpleName();//自动识别Acyivity
private  Handler handler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_main);
        //设置两秒进入主页面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //两秒后才执行到达要跳转的页面
                //执行在主线程
                startMainActivity();
                Log.e(TAG,"当前线程==="+Thread.currentThread().getName());
            }
        },2000);
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
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
