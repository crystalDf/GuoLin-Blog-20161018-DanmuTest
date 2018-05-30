package com.star.danmutest;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VideoView videoView = findViewById(R.id.video_view);
        videoView.setVideoPath(Environment.getExternalStorageDirectory() + "/Pixels.mp4");
        videoView.start();
    }
}
