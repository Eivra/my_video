package com.example.my_video.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my_video.IMyMusicPlayService;
import com.example.my_video.R;
import com.example.my_video.service.MyMusicPlayService;
import com.example.my_video.utils.LogUtils;
import com.example.my_video.utils.LyricUtil;
import com.example.my_video.utils.TimeUtils;
import com.example.my_video.view.ShowLyricView;

import java.io.File;

public class AuideoPlayerWindownActivity extends Activity implements View.OnClickListener{
    private static final int PROGRESS = 1;
    private static final int SHOW_LYRIC = 2;
    private int position;
    private IMyMusicPlayService iMyMusicPlayService;
    private MyReceiver myReceiver;
    private TimeUtils timeUtils;
    private Boolean notification;//true是从状态栏进入 否则重新播放

    private TextView singerName;
    private TextView lyrics;
    private TextView audioTime;
    private SeekBar sbVoice;
    private Button butAudioBack;
    private Button butAudioLast;
    private Button butAudioStop;
    private Button butAudioNext;
    private Button butAudioLyric;
    private ShowLyricView showLyricView;
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
                    if (!notification){
                        iMyMusicPlayService.openAudio(position);
                    }else {
                        System.out.println("Thread Name:"+Thread.currentThread());
                        showAudioData();
                    }
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
        showLyricView = (ShowLyricView) findViewById(R.id.showLyricView);

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
            try {
                setPlayModel();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // Handle clicks for butAudioBack
        } else if ( v == butAudioLast ) {
            // Handle clicks for butAudioLast
            if (iMyMusicPlayService != null){
                try {
                    iMyMusicPlayService.pre();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
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
            if(iMyMusicPlayService != null){
                try {
                    iMyMusicPlayService.next();
                    LogUtils.e("点击下一首。。。。");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if ( v == butAudioLyric ) {
            // Handle clicks for butAudioLyric
        }
    }


    //模式切换
    private void setPlayModel() throws RemoteException {
        int playMadel = iMyMusicPlayService.getPlayMaodel();
        if (playMadel == MyMusicPlayService.NORMAL){
            playMadel = MyMusicPlayService.SINGLE;
        }else if(playMadel == MyMusicPlayService.SINGLE){
            playMadel = MyMusicPlayService.ALL;
        }else if(playMadel == MyMusicPlayService.ALL){
            playMadel = MyMusicPlayService.NORMAL;
        }else {
            playMadel = MyMusicPlayService.NORMAL;
        }
        //保存状态
        iMyMusicPlayService.setPlayMaodel(playMadel);
        //设置图片
        setPlayModelImage();
    }

    private void setPlayModelImage() throws RemoteException {
        int playMadel = iMyMusicPlayService.getPlayMaodel();
        if (playMadel == MyMusicPlayService.NORMAL){
            butAudioBack.setBackgroundResource(R.drawable.audio_order);
            Toast.makeText(AuideoPlayerWindownActivity.this,"顺序播放",Toast.LENGTH_SHORT);
        }else if(playMadel == MyMusicPlayService.SINGLE){
            butAudioBack.setBackgroundResource(R.drawable.rollback);
            Toast.makeText(AuideoPlayerWindownActivity.this,"单曲循环",Toast.LENGTH_SHORT);
        }else if(playMadel == MyMusicPlayService.ALL){
            butAudioBack.setBackgroundResource(R.drawable.audio_random);
            Toast.makeText(AuideoPlayerWindownActivity.this,"随机播放",Toast.LENGTH_SHORT);
        }else {
            butAudioBack.setBackgroundResource(R.drawable.audio_order);
            Toast.makeText(AuideoPlayerWindownActivity.this,"顺序播放",Toast.LENGTH_SHORT);
        }
    }

    private void checkModelPlay() throws RemoteException {
        int playMadel = iMyMusicPlayService.getPlayMaodel();
        if (playMadel == MyMusicPlayService.NORMAL){
            butAudioBack.setBackgroundResource(R.drawable.audio_order);
        }else if(playMadel == MyMusicPlayService.SINGLE){
            butAudioBack.setBackgroundResource(R.drawable.rollback);
        }else if(playMadel == MyMusicPlayService.ALL){
            butAudioBack.setBackgroundResource(R.drawable.audio_random);
        }else {
            butAudioBack.setBackgroundResource(R.drawable.audio_order);
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            timeUtils = new TimeUtils();
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_LYRIC:
                    try {
                        //1.得到当前的进度
                        int currentPosition = iMyMusicPlayService.getCurrPosition();
                        //2.把进度传入showLyricView，并计算该高亮那句
                        showLyricView.setNetLyric(currentPosition);
                        //3.实时发送消息
                        handler.removeMessages(SHOW_LYRIC);
                        handler.sendEmptyMessage(SHOW_LYRIC);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;

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
            /**
             * 发消息开始歌词同步
             */
            showLyric();
            showAudioData();
            try {
                checkModelPlay();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    private void showLyric(){
        //解析歌词
        LyricUtil lyricUtil = new LyricUtil();
        try {
            //String path = iMyMusicPlayService.getAudioPath();
            String path = Environment.getExternalStorageDirectory().getPath()+"/tencent/MicroMsg/Download/fsj.txt";
            path = path.substring(0,path.indexOf("."));
            File file = new File(path + ".lrc");
            if (!file.exists()){
                file = new File(path + ".txt");
            }

            lyricUtil.readLyricFile(file);
            showLyricView.setLyrics(lyricUtil.getLyrics());
            LogUtils.e("++++++歌词"+lyricUtil.getLyrics());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //传歌词文件

        if (lyricUtil.isExistsLyric()){
            handler.sendEmptyMessage(SHOW_LYRIC);
        }

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

    private void bindAndStartService() {
        Intent intent = new Intent(this,MyMusicPlayService.class);
        intent.setAction("com.example.my_video.service.MyMusicPlayService");
        bindService(intent,conn,Context.BIND_AUTO_CREATE);
        startService(intent);//防止启动时实例化多个
    }

    private void getData() {
        notification = getIntent().getBooleanExtra("Notification",false);
        if (!notification){
            position = getIntent().getIntExtra("position",0);
        }

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
