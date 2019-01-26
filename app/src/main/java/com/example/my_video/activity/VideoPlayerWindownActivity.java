package com.example.my_video.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.my_video.R;

/**
 * 自定义播放器
 */
public class VideoPlayerWindownActivity extends Activity {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player_windown);
        video_player_windown = findViewById(R.id.video_player_windown);

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

    public class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            video_player_windown.start();
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
