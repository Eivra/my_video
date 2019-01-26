package com.example.my_video.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.my_video.R;
import com.example.my_video.utils.TimeUtils;

import static com.example.my_video.R.id.sb_voice;
import static com.example.my_video.R.id.video_player_windown;

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

    private static final int PROGRESS = 1;
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
        ivBeteery = (ImageView)findViewById( R.id.iv_beteery );
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
        } else if ( v == butLast ) {
            // Handle clicks for butLast
        } else if ( v == butStop ) {
            //播放->暂停
            if (video_player_windown.isPlaying()){
                video_player_windown.pause();
                butStop.setBackgroundResource(R.drawable.stop_press);
            }
            //暂停->播放
            else {
                video_player_windown.start();
                butStop.setBackgroundResource(R.drawable.stop);
            }
            // Handle clicks for butStop
        } else if ( v == butNext ) {
            // Handle clicks for butNext
        } else if ( v == butScreen ) {
            // Handle clicks for butScreen
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeUtils = new TimeUtils();
        findViews();


        //获取播放地址
        uri = getIntent().getData();
        video_player_windown.setVideoURI(uri);
        if (uri != null){
            video_player_windown.setVideoURI(uri);
        }
        video_player_windown.setOnPreparedListener(new MyOnPreparedListener());
        video_player_windown.setOnErrorListener(new MyOnErrorListener());
        video_player_windown.setOnCompletionListener(new MyOnCompletionListener());

        //设置暂停.播放.控制条
        //video_player_windown.setMediaController(new MediaController(this));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

   private Handler handler = new Handler(){
       @Override
       public void handleMessage(Message msg) {
           super.handleMessage(msg);
           switch (msg.what){
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

            //设置文本时间
            tvAllTime.setText(timeUtils.stringForTime(duration));
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
            Toast.makeText(VideoPlayerWindownActivity.this,"Finish",Toast.LENGTH_SHORT).show();
        }
    }
}
