package com.example.my_video.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my_video.R;
import com.example.my_video.dto.VideoItem;
import com.example.my_video.utils.LogUtils;
import com.example.my_video.utils.TimeUtils;
import com.example.my_video.view.VideoView;

import java.util.ArrayList;


/**
 * 自定义播放器
 */
public class VideoPlayerWindownActivity extends Activity implements View.OnClickListener {
    /**
     * 这个方法不是生命周期的onCreate
     * @param savedInstanceState
     * @param persistentState

     @Override
     public void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState, @androidx.annotation.Nullable PersistableBundle persistentState) {
     super.onCreate(savedInstanceState, persistentState);
     }
     */


    private VideoView video_player_windown;
    private Uri uri;
    private TimeUtils timeUtils;
    private ArrayList<VideoItem> videoItemArrayList;//视频列表
    int position;//播放列表中的位置
    //private MyReceiver myReceiver;//监听电量广播
    private GestureDetector detector;//手势识别器

    private static final int PROGRESS = 1;
    private static final int Hide_MiediaController=2;
    private LinearLayout top;
    private TextView tvName;
    private ImageView ivBeteery;
    private TextView time;
    private LinearLayout voiceBottom;
    private Button butVoice;
    private SeekBar sbVoice;
    private Button selectVideo;
    private LinearLayout llBottom;
    private TextView tvCurrTime;
    private SeekBar sbTime;
    private TextView tvAllTime;
    private Button butBack;
    private Button butLast;
    private Button butStop;
    private Button butNext;
    private Button butScreen;
    private RelativeLayout mediaConctroller;
    private Boolean isShowMediaConctroller=false;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-01-26 21:41:17 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.video_player_windown);
        top = (LinearLayout)findViewById( R.id.top );
        tvName = (TextView)findViewById( R.id.tv_name );
//        ivBeteery = (ImageView)findViewById( R.id.iv_beteery );
        time = (TextView)findViewById( R.id.time );
        voiceBottom = (LinearLayout)findViewById( R.id.voice_bottom );
        butVoice = (Button)findViewById( R.id.but_voice );
        sbVoice = (SeekBar)findViewById( R.id.sb_voice );
        selectVideo = (Button)findViewById( R.id.selectVideo );
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        tvCurrTime = (TextView)findViewById( R.id.tv_curr_time );
        sbTime = (SeekBar)findViewById( R.id.sb_time );
        tvAllTime = (TextView)findViewById( R.id.tv_allTime );
        butBack = (Button)findViewById( R.id.but_back );
        butLast = (Button)findViewById( R.id.but_last );
        butStop = (Button)findViewById( R.id.but_stop );
        butNext = (Button)findViewById( R.id.but_next );
        butScreen = (Button)findViewById( R.id.but_screen );
        video_player_windown = findViewById(R.id.video_player_windown);
        mediaConctroller=(RelativeLayout) findViewById(R.id.media_conctroller);

        butVoice.setOnClickListener(this);
        selectVideo.setOnClickListener( this );
        butBack.setOnClickListener( this );
        butLast.setOnClickListener( this );
        butStop.setOnClickListener( this );
        butNext.setOnClickListener( this );
        butScreen.setOnClickListener( this );
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2019-01-26 21:41:17 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == butVoice ) {
            // Handle clicks for butVoice
        } else if ( v == selectVideo ) {
            // Handle clicks for selectVideo
        } else if ( v == butBack ) {
            // Handle clicks for butBack
            finish();
        } else if ( v == butLast ) {
            playLast();
            // Handle clicks for butLast
        } else if ( v == butStop ) {
            playAndstop();
            // Handle clicks for butStop
        } else if ( v == butNext ) {
            // Handle clicks for butNext
            playNext();
        } else if ( v == butScreen ) {
            // Handle clicks for butScreen

        }
        handler.removeMessages(Hide_MiediaController);
        handler.sendEmptyMessageDelayed(Hide_MiediaController,3000);
    }

    private void playAndstop(){
        //播放->暂停
        if (video_player_windown.isPlaying()){
            video_player_windown.pause();
            butStop.setBackgroundResource(R.drawable.stop_press);
            Toast.makeText(VideoPlayerWindownActivity.this,"暂停",Toast.LENGTH_SHORT).show();
        }
        //暂停->播放
        else {
            video_player_windown.start();
            butStop.setBackgroundResource(R.drawable.stop);
            Toast.makeText(VideoPlayerWindownActivity.this,"播放",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 播放上一个视频
     */
    private void playLast() {
        if (videoItemArrayList!=null && videoItemArrayList.size()>0){
            position--;
            if (position>=0){
                VideoItem videoItem = videoItemArrayList.get(position);
                tvName.setText(videoItem.getName());
                video_player_windown.setVideoPath(videoItem.getData());
                //设置按钮状态
                setButStatus();
            }
        }else if(uri !=null){
            setButStatus();
        }
    }

    /**
     * 播放下一个视频
     */
    private void playNext() {
        if (videoItemArrayList!=null && videoItemArrayList.size()>0){
            position++;
            if (position<videoItemArrayList.size()){
                VideoItem videoItem = videoItemArrayList.get(position);
                tvName.setText(videoItem.getName());
                video_player_windown.setVideoPath(videoItem.getData());
                //设置按钮状态
                setButStatus();
            }
        }else if(uri !=null){
            setButStatus();
        }
    }

    private void setButStatus() {
        //1.只有一个视频
        if (videoItemArrayList !=null && videoItemArrayList.size()>0){
            if(videoItemArrayList.size()==1){
                butLast.setBackgroundResource(R.drawable.last_press);
                butLast.setEnabled(false);
                butNext.setBackgroundResource(R.drawable.next_press);
                butNext.setEnabled(false);
            }
            //2.两个视频
            else if (videoItemArrayList.size()==2){
                if (position==0){
                    butLast.setBackgroundResource(R.drawable.last_press);
                    butLast.setEnabled(false);
                    butNext.setBackgroundResource(R.drawable.next);
                    butNext.setEnabled(true);
                }else if(position == videoItemArrayList.size()-1){
                    butNext.setBackgroundResource(R.drawable.next_press);
                    butNext.setEnabled(false);
                    butLast.setBackgroundResource(R.drawable.last);
                    butLast.setEnabled(true);
                }else {
                    if(position == 0){
                        butLast.setBackgroundResource(R.drawable.last_press);
                        butLast.setEnabled(false);
                    } else if (position == 1){
                        butNext.setBackgroundResource(R.drawable.next_press);
                        butNext.setEnabled(false);
                    }
                }
            }
        }else if(uri != null){
            butLast.setBackgroundResource(R.drawable.last_press);
            butLast.setEnabled(false);
            butNext.setBackgroundResource(R.drawable.next_press);
            butNext.setEnabled(false);
        }
    }

    //设置监听
    private void setListener(){
        video_player_windown.setOnPreparedListener(new MyOnPreparedListener());
        video_player_windown.setOnErrorListener(new MyOnErrorListener());
        video_player_windown.setOnCompletionListener(new MyOnCompletionListener());

        //设置seekBar监听
        sbTime.setOnSeekBarChangeListener(new videoSeekBarChangeListener());
    }

    class videoSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                video_player_windown.seekTo(progress);
            }
        }

        //当手指离开屏幕 时回调的方法
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(Hide_MiediaController);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(Hide_MiediaController,3000);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//初始化父类
        inite();
        findViews();
        setListener();
        //获取播放地址
         getData();
         setData();;
        //设置暂停.播放.控制条
        //video_player_windown.setMediaController(new MediaController(this));

        detector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                Toast.makeText(VideoPlayerWindownActivity.this,"onLongPress",Toast.LENGTH_SHORT).show();
            }

            /**
             * 单击暂停、播放、显示控制面板
             * @param e
             * @return
             */
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
               // playAndstop();
                //显示->隐藏
                if (isShowMediaConctroller){
                    hideMiediaController();
                    handler.removeMessages(Hide_MiediaController);
                }else {
                    //隐藏->显示
                    showMiediaController();
                    handler.sendEmptyMessageDelayed(Hide_MiediaController,3000);
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                playAndstop();
                Toast.makeText(VideoPlayerWindownActivity.this,"onDoubleTap",Toast.LENGTH_SHORT).show();
                return super.onDoubleTap(e);
            }
        });
    }

    private void setData(){
        if (videoItemArrayList!=null && videoItemArrayList.size()>0){
            VideoItem videoItem = videoItemArrayList.get(position);
            tvName.setText(videoItem.getName());//视频名称
            video_player_windown.setVideoPath(videoItem.getData());//获取视频资源
        }else if (uri != null){
            tvName.setText(uri.toString());
            video_player_windown.setVideoURI(uri);
        }else {
            Toast.makeText(VideoPlayerWindownActivity.this,"没有视频",Toast.LENGTH_SHORT).show();
        }
        setButStatus();
    }
    private void getData(){
        uri = getIntent().getData();
        videoItemArrayList = (ArrayList<VideoItem>)getIntent().getSerializableExtra("videoList");
        position = getIntent().getIntExtra("position",0);
        video_player_windown.setVideoURI(uri);
        if (uri != null){
            video_player_windown.setVideoURI(uri);
        }
    }

    public void inite(){
        timeUtils = new TimeUtils();
        //注册电量广播
//        myReceiver =new  MyReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        //电量变化发送广播
//        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
//        registerReceiver(myReceiver,intentFilter);
    }
//    class MyReceiver extends BroadcastReceiver{
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            //设置默认值
//            int level = intent.getIntExtra("level",0);
//            //setBattery(level);
//        }

//        private void setBattery(int level) {
//            if (level<=0){
//                ivBeteery.setBackgroundResource(R.drawable.battery_0);
//            }
//            else if (level<=10){
//                ivBeteery.setBackgroundResource(R.drawable.battery_1);
//            }
//            else if (level<=30){
//                ivBeteery.setBackgroundResource(R.drawable.battery_2);
//            }
//            else if (level<=50){
//                ivBeteery.setBackgroundResource(R.drawable.battery_3);
//            }
//           else if (level<=70){
//                ivBeteery.setBackgroundResource(R.drawable.battery_4);
//            }
//            else if (level<=100){
//                ivBeteery.setBackgroundResource(R.drawable.battery_5);
//            }else {
//                ivBeteery.setBackgroundResource(R.drawable.battery_5);
//            }
//        }
    //}
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

   private Handler handler = new Handler(){
       @Override
       public void handleMessage(Message msg) {
           super.handleMessage(msg);
           switch (msg.what){
               case Hide_MiediaController: hideMiediaController(); break;
               case PROGRESS:
               //获取当前视频位置
                  int currentPosition = video_player_windown.getCurrentPosition();
               //设置当前进度
                   sbTime.setProgress(currentPosition);
                   //更新文本时间进度
                   tvCurrTime.setText(timeUtils.stringForTime(currentPosition));
                //更新
                   removeMessages(PROGRESS);
                   sendEmptyMessageDelayed(PROGRESS,1000);
                   break;
           }
       }
   };

    public class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            video_player_windown.start();
            //获取视频总时间
            int duration = video_player_windown.getDuration();
            sbTime.setMax(duration);
            //发消息（Handler）
            handler.sendEmptyMessage(PROGRESS);
            //默认隐藏控制面板
            hideMiediaController();
            //设置文本时间

            tvAllTime.setText(timeUtils.stringForTime(duration));
            //设置视频窗口实际大小
            video_player_windown.setVideoSize(mp.getVideoWidth(),mp.getVideoHeight());
        }
    }

    public class MyOnErrorListener implements MediaPlayer.OnErrorListener{
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(VideoPlayerWindownActivity.this,"Error",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(VideoPlayerWindownActivity.this,"播放下一个视频",Toast.LENGTH_SHORT).show();
            playNext();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.e("onRestart....");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        if (myReceiver!=null){
//            unregisterReceiver(myReceiver);
//            myReceiver = null;
//        }
        LogUtils.e("onDestroy....");
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //显示控件
    public void showMiediaController(){
        mediaConctroller.setVisibility(View.VISIBLE);
        isShowMediaConctroller=true;
    }
    //隐藏控件
    public void hideMiediaController(){
        mediaConctroller.setVisibility(View.GONE);
        isShowMediaConctroller=false;
    }

}
