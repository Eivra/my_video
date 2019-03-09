package com.example.my_video.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.my_video.IMyMusicPlayService;
import com.example.my_video.R;
import com.example.my_video.activity.AuideoPlayerWindownActivity;
import com.example.my_video.dto.VideoItem;

import java.io.IOException;
import java.util.ArrayList;

public class MyMusicPlayService extends Service {

    private ArrayList<VideoItem> videoItemArrayList;
    private VideoItem videoItem;
    private int position;
    private MediaPlayer mediaPlayer;//视频音频都可以播放
    public static final String Audio = "com.example.my_video.service.MyMusicPlayService" ;

    @Override
    public void onCreate() {
        super.onCreate();
        getFromLocal();
    }

    private void getFromLocal() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                videoItemArrayList = new ArrayList<>();
                ContentResolver contentResolver = getContentResolver();//服务本身就是一个上下文，所以不需要上下
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] object = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频在sd卡的名称
                        MediaStore.Audio.Media.DURATION,//视频总时长
                        MediaStore.Audio.Media.SIZE,//视频文件大小
                        MediaStore.Audio.Media.DATA,//视频地址（绝对地址）
                        MediaStore.Audio.Media.ARTIST,//歌者名字
                };
                Cursor cursor = contentResolver.query(uri, object, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        videoItem = new VideoItem();

                        String name = cursor.getString(0);
                        videoItem.setName(name);

                        long duration = cursor.getLong(1);
                        videoItem.setDuration(duration);

                        long size = cursor.getLong(2);
                        videoItem.setSize(size);

                        String data = cursor.getString(3);
                        videoItem.setData(data);

                        String artist = cursor.getString(4);
                        videoItem.setArtist(artist);

                        videoItemArrayList.add(videoItem);
                    }
                    cursor.close();
                }

            }
        }.start();

    }

    private IMyMusicPlayService.Stub iMyMusicPlayService = new IMyMusicPlayService.Stub() {
        MyMusicPlayService service = MyMusicPlayService.this;
        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
            if (videoItemArrayList != null && videoItemArrayList.size() >0){
                videoItem = videoItemArrayList.get(position);
                if (mediaPlayer != null){
                    //mediaPlayer.release();//释放
                    mediaPlayer.reset();
                }

                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setOnPreparedListener(new OnMyPreparedListener());
                    mediaPlayer.setOnCompletionListener(new OnMySeekCompleteListener());
                    mediaPlayer.setOnErrorListener(new OnMyErrorListener());

                    mediaPlayer.setDataSource(videoItem.getData());
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(MyMusicPlayService.this,"没有数据",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void stop() throws RemoteException {
            service.stop();
        }

        @Override
        public int getCurrPosition() throws RemoteException {
            return service.getCurrPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String getSingerName() throws RemoteException {
            return service.getSingerName();
        }

        @Override
        public String getSongName() throws RemoteException {
            return service.getSongName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public void setPlayMaodel(int platModel) throws RemoteException {
            service.setPlayMaodel(platModel);
        }

        @Override
        public int getPlayMaodel() throws RemoteException {
            return service.getPlayMaodel();
        }

        @Override
        public boolean isPlay() throws RemoteException {
            return service.isPlay();
        }

        @Override
        public void toSeekBar(int position) throws RemoteException {
            mediaPlayer.seekTo(position);
        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        return iMyMusicPlayService;
    }

    private NotificationManager manager;
    /**
     * 音频开始
     */
    private void start(){
        mediaPlayer.start();
        //当播放歌曲的时候，在状态栏显示正在播放的音乐，点击时是可以进入播放页面
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent =new  Intent(this,AuideoPlayerWindownActivity.class);
        intent.putExtra("Notification",true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = new Notification.Builder(this).
                setSmallIcon(R.drawable.musiclogo).setContentTitle("My Music")
                .setContentText("正在播放"+getSongName())
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(1,notification);
    }

    /**
     * 打开对于音频文件
     * @param position
     */
    private void openAudio(int position){
        this.position = position;
    }

    /**
     * 暂停
     */
    private void pause(){
        mediaPlayer.pause();
    }

    /**
     * 停止
     */
    private void stop(){
    }

    /**
     * 获取当前进度
     * @return
     */
    private int getCurrPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    /**
     * 获取当前音频总时长
     * @return
     */
    private int getDuration(){
        return mediaPlayer.getDuration();
    }

    /**
     * 获取歌唱者名字
     * @return
     */
    private String getSingerName(){
        return videoItem.getName();
    }

    /**
     * 获取歌曲名
     */
    private String getSongName(){
        return videoItem.getArtist();
    }

    /**
     * 获取音频路径
     * @return
     */
    private String getAudioPath(){
        return "";
    }

    /**
     * 播放下一个
     */
    private void next(){

    }

    /**
     * 播放上一个
     */
    private void pre(){

    }

    /**
     * 设置播放模式
     * @param platModel
     */
    private void setPlayMaodel(int platModel){

    }

    /**
     * 获取播放模式
     * @return
     */
    private int getPlayMaodel(){
        return 0;
    }

    private boolean isPlay(){
        return mediaPlayer.isPlaying();
    }

    class OnMyPreparedListener implements MediaPlayer.OnPreparedListener{

        @Override
        public void onPrepared(MediaPlayer mp) {
            //通知Activity获取信息
            notifyChange(Audio);
            start();
        }

        /**
         * 根据动作发广播
         * @param audio
         */
        private void notifyChange(String audio) {
            Intent intent = new Intent(audio);
            sendBroadcast(intent);
        }
    }

    class OnMyErrorListener implements MediaPlayer.OnErrorListener{

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return true;
        }
    }

    class OnMySeekCompleteListener implements MediaPlayer.OnCompletionListener{
        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    }
}
