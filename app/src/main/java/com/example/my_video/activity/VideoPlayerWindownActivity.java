package com.example.my_video.activity;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
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
    private AuideoPlayerWindownActivity.MyReceiver myReceiver;//监听电量广播
    private GestureDetector detector;//手势识别器
    private MediaMetadataRetriever media = new MediaMetadataRetriever();

    private static final int PROGRESS = 1;
    private static final int HIDE_MEDIACONTROLLER =2;//控制面板
    private static final int FULL_SCREEN = 1;//全屏
    private static final int DEFAULT_SCREEN = 2;//默认

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
    //private ImageView imageView;

    private Boolean isShowMediaConctroller=false;//是否显示控制面板
    private Boolean ifFullScreen = false;//是否全屏

    private int screenWidth =0;//屏幕宽
    private int screenHeight = 0;//屏幕高

    private int videoWidth;//真实视频宽
    private int videoHeight;//真实视频高

    private AudioManager am;
    private int currentVoice;//当前音量
    private int maxVoice;//最大音量
    private boolean isMute = false;

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
        //imageView = (ImageView) findViewById(R.id.iv_image);


        butVoice.setOnClickListener(this);
        selectVideo.setOnClickListener( this );
        butBack.setOnClickListener( this );
        butLast.setOnClickListener( this );
        butStop.setOnClickListener( this );
        butNext.setOnClickListener( this );
        butScreen.setOnClickListener( this );

        //音量关联seekBar
        sbVoice.setMax(maxVoice);//最大音量
        sbVoice.setProgress(currentVoice);
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
            isMute = !isMute;
            updateVoice(currentVoice,isMute);
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
            screenIsFull();
        }
        handler.removeMessages(HIDE_MEDIACONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
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
        sbVoice.setOnSeekBarChangeListener(new voiceOnSeekBarListener());
    }

    /**
     * 设置音量大小
     * @param progress
     */
    private void updateVoice(int progress,boolean isMute) {
        if(isMute){
            am.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            sbVoice.setProgress(0);
        }else{
            am.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            sbVoice.setProgress(progress);
            currentVoice = progress;
        }
    }

    class voiceOnSeekBarListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                if(progress >0 ){
                    isMute = false;
                }else{
                    isMute = true;
                }
                updateVoice(progress,isMute);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,5000);
        }
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
            handler.removeMessages(HIDE_MEDIACONTROLLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,5000);
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
         setData();
        //设置暂停.播放.控制条
       // video_player_windown.setMediaController(new MediaController(this));

    }


    private void setVideoType(int defaultScreen) {
        switch (defaultScreen){
            case FULL_SCREEN:
                //设置屏幕大小
                video_player_windown.setVideoSize(screenWidth,screenHeight);
                //按钮状态
                butScreen.setBackgroundResource(R.drawable.big);

                ifFullScreen = true;
                break;
            case DEFAULT_SCREEN:
                int mVideoWidth = videoWidth;//视频真实宽
                int mVideoHeight = videoHeight;//视频真实高

                int width = screenWidth;//屏幕的宽
                int height = screenHeight;//屏幕的高
                // for compatibility, we adjust size based on aspect ratio
                if ( mVideoWidth * height  < width * mVideoHeight ) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if ( mVideoWidth * height  > width * mVideoHeight ) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                video_player_windown.setVideoSize(width,height);
                butScreen.setBackgroundResource(R.drawable.little);
                ifFullScreen = false;
                break;
        }
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
    public void screenIsFull(){
        if(ifFullScreen){
            //默认
            setVideoType(DEFAULT_SCREEN);
        }else {
            //全屏
            setVideoType(FULL_SCREEN);
        }
    }

    public void inite(){
        timeUtils = new TimeUtils();

        detector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                playAndstop();
                super.onLongPress(e);
                //Toast.makeText(VideoPlayerWindownActivity.this,"onLongPress",Toast.LENGTH_SHORT).show();
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
                    handler.removeMessages(HIDE_MEDIACONTROLLER);
                }else {
                    //隐藏->显示
                    showMiediaController();
                    handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,5000);
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                screenIsFull();
                //Toast.makeText(VideoPlayerWindownActivity.this,"onDoubleTap",Toast.LENGTH_SHORT).show();
                return super.onDoubleTap(e);
            }

        });
        //得到屏幕的宽和高
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        //获取音量
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

   private Handler handler = new Handler(){
       @Override
       public void handleMessage(Message msg) {
           super.handleMessage(msg);
           switch (msg.what){
               case HIDE_MEDIACONTROLLER: hideMiediaController(); break;
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
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();
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
            //video_player_windown.setVideoSize(mp.getVideoWidth(),mp.getVideoHeight());
            setVideoType(DEFAULT_SCREEN);//默认屏幕播放
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
        handler.removeCallbacksAndMessages(null);
        if (myReceiver!=null){
            unregisterReceiver(myReceiver);
            myReceiver = null;
        }
        LogUtils.e("onDestroy....");
        super.onDestroy();
    }

    private float startyY;
    private float touchRang;
    private int mVol;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN://手指按下 0
                startyY = event.getY();
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang = Math.min(screenHeight,screenWidth);
                handler.removeMessages(HIDE_MEDIACONTROLLER);
                break;
            case MotionEvent.ACTION_MOVE://手指移开 1
                float endY = event.getY();
                float distanceY = startyY-endY;
                float alterVoice = (distanceY/touchRang)*maxVoice;
                int voice = (int) Math.min(Math.max(mVol+alterVoice,0),maxVoice);
                if (alterVoice != 0){
                    isMute = false;
                    updateVoice(voice,isMute);
                }
                break;
            case MotionEvent.ACTION_UP://手指离开 2
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
                break;
        }
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

    /**
     * 与系统声音同步增减
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_VOLUME_DOWN){
            currentVoice--;
            updateVoice(currentVoice,false);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
            return true;
        }else if (keyCode == event.KEYCODE_VOLUME_UP){
            currentVoice++;
            updateVoice(currentVoice,false);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
