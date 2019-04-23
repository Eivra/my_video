package com.example.my_video.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.my_video.dto.Lyric;

import java.util.ArrayList;

public class ShowLyricView extends TextView {
    private ArrayList<Lyric> lyrics;//歌词列表
    private Paint paint;
    private Paint whitePaint;
    private int width;
    private int height;
    private int indext;//歌词索引
    private int textHeight = 80;//每行的高
    private long sleepTime;//高亮显示的时间或者休眠时间
    private long timePoint;//时间戳什么时候到高亮的那句

    private int currentPosition;//当前播放位置
    public void setLyrics(ArrayList<Lyric> lyrics) {//设置歌词列表
        this.lyrics = lyrics;
    }

    public ShowLyricView(Context context) {
        this(context,null);
    }

    public ShowLyricView(Context context,AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ShowLyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
    private void initView() {
        paint = new Paint();//画笔
        paint.setColor(Color.RED);
        paint.setTextSize(50);
        paint.setAntiAlias(true);//锯齿
        paint.setTextAlign(Paint.Align.CENTER);

        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextSize(40);
        whitePaint.setAntiAlias(true);//锯齿
        whitePaint.setTextAlign(Paint.Align.CENTER);

//        lyrics = new ArrayList<>();
//        Lyric lyric = new Lyric();
//        for (int i = 0;i<1000;++i){
//            lyric.setTimePoint(1000*i);
//            lyric.setSeelpTime(2000+i);
//           lyric.setContent(""+i);
//            //把歌词添加到集合中
//            lyrics.add(lyric);
//            lyric = new Lyric();
//        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if (lyrics != null && lyrics.size() > 0) {

            //往上推移

            float plush = 0;
            if(sleepTime ==0){
                plush = 0;
            }else{
                //平移
                //这一句所花的时间 ：休眠时间 = 移动的距离 ： 总距离（行高）
                //移动的距离 =  (这一句所花的时间 ：休眠时间)* 总距离（行高）
//                float delta = ((currentPositon-timePoint)/sleepTime )*textHeight;

                //屏幕的的坐标 = 行高 + 移动的距离
                plush = textHeight + ((currentPosition -timePoint)/sleepTime )*textHeight;
            }
            canvas.translate(0,-plush);

            //绘制歌词:绘制当前句
            String currentText = lyrics.get(indext).getContent();
            canvas.drawText(currentText, width / 2, height / 2, paint);
            // 绘制前面部分
            float tempY = height / 2;//Y轴的中间坐标
            for (int i = indext - 1; i >= 0; i--) {
                //每一句歌词
                String preContent = lyrics.get(i).getContent();
                tempY = tempY - textHeight;
                if (tempY < 0) {
                    break;
                }
                canvas.drawText(preContent, width / 2, tempY, whitePaint);
            }

            // 绘制后面部分
            tempY = height / 2;//Y轴的中间坐标
            for (int i = indext + 1; i < lyrics.size(); i++) {
                //每一句歌词
                String nextContent = lyrics.get(i).getContent();
                tempY = tempY + textHeight;
                if (tempY > height) {
                    break;
                }
                canvas.drawText(nextContent, width / 2, tempY, whitePaint);
            }
        }else {
            canvas.drawText("没找到歌词",width/2,height/2,paint);//歌词在屏幕的位置
        }
    }

    /**
     * 根据当前播放的位置，找出该高亮显示那句
     * @param currentPosition
     */
    public void setNetLyric(int currentPosition) {
        this.currentPosition=currentPosition;
        if (lyrics==null || lyrics.size()==0){
            return;
        }

        for (int i =1;i<lyrics.size();i++){
            if (currentPosition < lyrics.get(i).getTimePoint()){
                int tempIndex = i-1;
                if (currentPosition >= lyrics.get(tempIndex).getTimePoint()){
                    indext = tempIndex;
                    sleepTime = lyrics.get(indext).getSeelpTime();
                    timePoint = lyrics.get(indext).getTimePoint();
                }
            }
        }
        //重绘
        invalidate();//当到下一句歌词的时候要重绘（主线程） 子线程：postInvalidate();

    }
}
