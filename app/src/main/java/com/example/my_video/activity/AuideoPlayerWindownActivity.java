package com.example.my_video.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.my_video.IMyMusicPlayService;
import com.example.my_video.R;
import com.example.my_video.service.MyMusicPlayService;
import com.example.my_video.utils.LogUtils;
import com.example.my_video.utils.TimeUtils;

public class AuideoPlayerWindownActivity extends Activity implements View.OnClickListener{
    private static final int PROGRESS = 1;
    private int position;
    private IMyMusicPlayService iMyMusicPlayService;
    private MyReceiver myReceiver;
    private TimeUtils timeUtils;

    private TextView singerName;
    private TextView lyrics;
    private TextView audioTime;
    private SeekBar sbVoice;
    private Button butAudioBack;
    private Button butAudioLast;
    private Button butAudioStop;
    private Button butAudioNext;
    private Button butAudioLyric;
    private ServiceConnection conn = new ServiceConnection() {
        /**
         * 连接成功回调的方法
         * @param name
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            iMyMusicPlayService = IMyMusicPlayService.Stub.asInterface(iBinder);

            if (iMyMusicPlayService != null){
                try {
                    iMyMusicPlayService.openAudio(position);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 断开回调的方法
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                if (iMyMusicPlayService != null){
                    iMyMusicPlayService.stop();
                    iMyMusicPlayService = null;
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-03-01 23:09:38 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_audionwindow);
        singerName = (TextView)findViewById( R.id.singer_name );
        lyrics = (TextView)findViewById( R.id.lyrics );
        audioTime = (TextView)findViewById( R.id.audio_time );
        sbVoice = (SeekBar)findViewById( R.id.sb_voice );
        butAudioBack = (Button)findViewById( R.id.but_audio_back );
        butAudioLast = (Button)findViewById( R.id.but_audio_last );
        butAudioStop = (Button)findViewById( R.id.but_audio_stop );
        butAudioNext = (Button)findViewById( R.id.but_audio_next );
        butAudioLyric = (Button)findViewById( R.id.but_audio_lyric );

        butAudioBack.setOnClickListener(this);
        butAudioLast.setOnClickListener(this);
        butAudioStop.setOnClickListener(this);
        butAudioNext.setOnClickListener( this );
        butAudioLyric.setOnClickListener( this);

        //设置视频的拖动
        sbVoice.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                //拖动进度
                try {
                    iMyMusicPlayService.toSeekBar(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2019-03-01 23:09:38 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == butAudioBack ) {
            // Handle clicks for butAudioBack
        } else if ( v == butAudioLast ) {
            // Handle clicks for butAudioLast
        } else if ( v == butAudioStop ) {
            // Handle clicks for butAudioStop
            if (iMyMusicPlayService!=null){
                try {
                    if (iMyMusicPlayService.isPlay()){
                        //暂停
                        iMyMusicPlayService.pause();
                        //暂停按钮
                        butAudioStop.setBackgroundResource(R.drawable.stop_press);
                    }else {
                        iMyMusicPlayService.start();
                        butAudioStop.setBackgroundResource(R.drawable.stop);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if ( v == butAudioNext ) {
            // Handle clicks for butAudioNext
        } else if ( v == butAudioLyric ) {
            // Handle clicks for butAudioLyric
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            timeUtils = new TimeUtils();
            super.handleMessage(msg);
            switch (msg.what){
                case PROGRESS:
                    try {
                        int currentPosition = iMyMusicPlayService.getCurrPosition();
                        sbVoice.setProgress(currentPosition);
                        audioTime.setText(timeUtils.stringForTime(currentPosition)+"/"+timeUtils.stringForTime(iMyMusicPlayService.getDuration()));

                        handler.removeMessages(PROGRESS);
                        handler.sendEmptyMessageDelayed(PROGRESS,1000);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        findViews();
        getData();
        bindAndStartService();
    }

    private void initData() {
        //注册广播
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyMusicPlayService.Audio);
        registerReceiver(myReceiver,intentFilter);
    }

    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            showAudioData();
        }

        private void showAudioData() {
            try {
                //歌名
                singerName.setText(iMyMusicPlayService.getSingerName());
                //歌唱者名字
                lyrics.setText(iMyMusicPlayService.getSongName());
                sbVoice.setMax(iMyMusicPlayService.getDuration());

                //发消息
                handler.sendEmptyMessage(PROGRESS);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void bindAndStartService() {
        Intent intent = new Intent(this,MyMusicPlayService.class);
        intent.setAction("com.example.my_video.service.MyMusicPlayService");
        bindService(intent,conn,Context.BIND_AUTO_CREATE);
        startService(intent);//防止启动时实例化多个
    }

    private void getData() {
        position = getIntent().getIntExtra("position",0);
    }

    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (myReceiver != null){
            unregisterReceiver(myReceiver);
            myReceiver = null;
        }
        LogUtils.e("destroy--");
        super.onDestroy();
    }
}
