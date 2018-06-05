package com.star.danmutest;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.VideoView;

import java.util.Random;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

public class MainActivity extends AppCompatActivity {

    private boolean mShowDanmaku;
    private DanmakuView mDanmakuView;
    private DanmakuContext mDanmakuContext;
    private BaseDanmakuParser mBaseDanmakuParser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };

    private LinearLayout mOperationLayout;
    private Button mSend;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VideoView videoView = findViewById(R.id.video_view);
        videoView.setVideoPath(Environment.getExternalStorageDirectory() + "/Safe & Sound.mp4");
        videoView.start();

        mDanmakuView = findViewById(R.id.danmaku_view);
        mDanmakuView.enableDanmakuDrawingCache(true);
        mDanmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                mShowDanmaku = true;
                mDanmakuView.start();
                generateSomeDanmaku();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });

        mDanmakuContext = DanmakuContext.create();
        mDanmakuView.prepare(mBaseDanmakuParser, mDanmakuContext);

        mOperationLayout = findViewById(R.id.operation_layout);
        mSend = findViewById(R.id.send);
        mEditText = findViewById(R.id.edit_text);

        mDanmakuView.setOnClickListener(v -> mOperationLayout.setVisibility(
                mOperationLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
        mSend.setOnClickListener(v -> {
            String content = mEditText.getText().toString();

            if (!TextUtils.isEmpty(content)) {
                addDanmaku(content, true);
                mEditText.setText("");
            }
        });

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == View.SYSTEM_UI_FLAG_VISIBLE) {
                onWindowFocusChanged(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mShowDanmaku = false;

        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    private void generateSomeDanmaku() {

        new Thread(() -> {
            while (mShowDanmaku) {
                int time = new Random().nextInt(300);

                String content = "" + time + time;

                addDanmaku(content, false);

                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addDanmaku(String content, boolean withBorder) {

        BaseDanmaku baseDanmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(
                BaseDanmaku.TYPE_SCROLL_RL
        );

        baseDanmaku.text = content;
        baseDanmaku.padding = 5;
        baseDanmaku.textSize = sp2px(20);
        baseDanmaku.textColor = Color.WHITE;
        baseDanmaku.setTime(mDanmakuView.getCurrentTime());

        if (withBorder) {
            baseDanmaku.borderColor = Color.GREEN;
        }

        mDanmakuView.addDanmaku(baseDanmaku);
    }

    private int sp2px(float spValue) {

        final float fontScale = getResources().getDisplayMetrics().scaledDensity;

        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {

            View decorView = getWindow().getDecorView();

            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }
}
