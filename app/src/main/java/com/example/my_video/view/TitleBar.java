package com.example.my_video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.my_video.R;

/**
 * 自定义控件
 */
public class TitleBar extends LinearLayout implements View.OnClickListener{
    private Context context;
    //实例化孩子
    private View search;
    private View rl_game;
    private View iv_record;

    /**
     * 代码使用
     * @param context
     */
    public TitleBar(Context context) {
        this(context,null);
    }

    /**
     * 布局文件
     * @param context
     * @param attrs
     */
    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * 设置样式
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public TitleBar(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //实例化孩子
        search = getChildAt(1);
        rl_game = getChildAt(2);
        iv_record = getChildAt(3);

        search.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search:Toast.makeText(context,"Search",Toast.LENGTH_SHORT).show();
            break;
            case R.id.rl_game:Toast.makeText(context,"Game",Toast.LENGTH_SHORT).show();
            break;
            case R.id.iv_record:Toast.makeText(context,"Record",Toast.LENGTH_SHORT).show();
            break;
        }
    }
}
