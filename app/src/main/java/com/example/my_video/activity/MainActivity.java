package com.example.my_video.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.example.my_video.R;
import com.example.my_video.basePage.BasePage;
import com.example.my_video.page.AudioPage;
import com.example.my_video.page.NetAudioPage;
import com.example.my_video.page.NetVideoPage;
import com.example.my_video.page.VideoPage;

import java.util.ArrayList;


/**
 * crate by 2019-01-04
 * 主页面
 */
public class MainActivity extends FragmentActivity {
    private FrameLayout fl_main_conten;
    private RadioGroup rg_buttom;
    private int position;
    /**
     * 页面的集合
     */
    private ArrayList<BasePage> basePages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acyivity_main);
        fl_main_conten=(FrameLayout) findViewById(R.id.fl_main_conten);
        rg_buttom=(RadioGroup) findViewById(R.id.rg_buttom);


        basePages=new ArrayList<>();
        basePages.add(new VideoPage(this));//本地视频-0
        basePages.add(new AudioPage(this));//本地音频-2
        basePages.add(new NetAudioPage(this));//网络音频-3
        basePages.add(new NetVideoPage(this));//网络视频-4

        //设置RadioGroup监听
        rg_buttom.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_buttom.check(R.id.rb_video);//默认选中首页的第一个
    }

    /**
     * 回传RadioGroup
     */
    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
             switch (checkedId){
                 default:position=0;break;
                 case R.id.rb_audio:position=1;break;
                 case R.id.rb_load:position=2;break;
                 case R.id.rb_setUp:position=3;break;
             }
             //将上面几个页面放到FramLayout里面去
            setFragement();
        }

        /**
         * 把页面添加到Fragement
         */
        private void setFragement() {
            //1.得到FragementManger
            FragmentManager manager =getSupportFragmentManager();
            //2.开启事物
            FragmentTransaction ft=manager.beginTransaction();
            //3.替换
           ft.replace(R.id.fl_main_conten, new ReplaceFragment(getBasePage()));
            //4.提交事物
            ft.commit();
        }
        private BasePage getBasePage() {
            BasePage basePage=basePages.get(position);
            if (basePage!=null && !basePage.isinitDate){
                basePage.initDate();
                //使界面不重复初始化
                basePage.isinitDate = true;
            }
            return basePage;
        }
    }

}
