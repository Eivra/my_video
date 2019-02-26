package com.example.my_video.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.example.my_video.IMyMusicPlayService;
import com.example.my_video.R;
import com.example.my_video.service.MyMusicPlayService;

public class AuideoPlayerWindownActivity extends Activity {
    private int position;
    private IMyMusicPlayService iMyMusicPlayService;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audionwindow);
        getData();
        bindAndStartService();
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
}
