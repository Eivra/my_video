package com.example.my_video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class VideoView extends android.widget.VideoView {
    public VideoView(Context context) {
        this(context,null);
    }

    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }
    public void setVideoSize(int widthMeasureSpec, int heightMeasureSpec){
        ViewGroup.LayoutParams params=getLayoutParams();
        params.height = heightMeasureSpec;
        params.width = widthMeasureSpec;
        setLayoutParams(params);
    }
}
