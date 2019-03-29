package com.example.my_video;

import android.os.Environment;

import com.example.my_video.utils.LyricUtil;
import com.example.my_video.view.ShowLyricView;

import org.junit.Test;

import java.io.File;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        ShowLyricView showLyricView;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}