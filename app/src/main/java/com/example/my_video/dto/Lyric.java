package com.example.my_video.dto;

public class Lyric {
    private String content;//歌词
    private long timePoint;//时间戳
    private long seelpTime;//高亮时间

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }

    public long getSeelpTime() {
        return seelpTime;
    }

    public void setSeelpTime(long seelpTime) {
        this.seelpTime = seelpTime;
    }
}
