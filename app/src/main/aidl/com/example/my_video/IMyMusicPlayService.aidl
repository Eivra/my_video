// IMyMusicPlayService.aidl
package com.example.my_video;

// Declare any non-default types here with import statements

interface IMyMusicPlayService {
 /**
     * 音频开始
     */
    void start();

    /**
     * 打开对于音频文件
     * @param position
     */
    void openAudio(int position);

    /**
     * 暂停
     */
    void pause();

    /**
     * 停止
     */
    void stop();

    /**
     * 获取当前进度
     * @return
     */
    int getCurrPosition();

    /**
     * 获取当前音频总时长
     * @return
     */
    int getDuration();

    /**
     * 获取歌唱者名字
     * @return
     */
    String getSingerName();

    /**
     * 获取歌曲名
     */
    String getSongName();

    /**
     * 获取音频路径
     * @return
     */
    String getAudioPath();

    /**
     * 播放下一个
     */
    void next();

    /**
     * 播放上一个
     */
    void pre();

    /**
     * 设置播放模式
     * @param platModel
     */
    void setPlayMaodel(int platModel);

    /**
     * 获取播放模式
     * @return
     */
    int getPlayMaodel();

    boolean isPlay();

    //拖动进度位置
    void toSeekBar(int position);
}
