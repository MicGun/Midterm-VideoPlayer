package com.hugh.videoplayer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl {

        private String mVideoUrl = "https://s3-ap-northeast-1.amazonaws.com/mid-exam/Video/taeyeon.mp4";
//    private String mVideoUrl = "https://s3-ap-northeast-1.amazonaws.com/mid-exam/Video/protraitVideo.mp4";

//    VideoView mVideoView;
//
//    Button mButton;

    private SurfaceView mSurfaceView;
    private View mBackgroundView;
    private MediaPlayer mMediaPlayer;
    private SurfaceHolder mSurfaceHolder;
    private VideoControllerView mVideoControllerView;

    private boolean mFullScreen = true;
    private boolean mVolumeOn = true;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mSurfaceView = findViewById(R.id.surfaceView);
        mBackgroundView = findViewById(R.id.backgroundView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(MainActivity.this);


        mVideoControllerView = new VideoControllerView(MainActivity.this);
        mVideoControllerView.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            // for example the width of a layout
            int height = getWindowManager().getDefaultDisplay().getHeight();
            setVideoSize();
            mFullScreen = false;
            mVideoControllerView.show();
            mVideoControllerView.updateFullScreen();
            Toast.makeText(this, "landscape height: " + height, Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            int height = getWindowManager().getDefaultDisplay().getHeight();
            ;
            setVideoSize();
            mFullScreen = true;
            mVideoControllerView.show(mMediaPlayer.getDuration());
            mVideoControllerView.updateFullScreen();
            Toast.makeText(this, "portrait height: " + height, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDisplay(mSurfaceHolder);

        try {
            mMediaPlayer.setDataSource(mVideoUrl);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(MainActivity.this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        realeaseMediaPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realeaseMediaPlayer();
    }

    private void realeaseMediaPlayer() {

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        mVideoControllerView.setMediaPlayer(this);
        mVideoControllerView.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        mVideoControllerView.show(mMediaPlayer.getDuration());
        setVideoSize();
        mMediaPlayer.start();
        mVideoControllerView.updatePausePlay();
    }

    @Override
    public void start() {
        mMediaPlayer.start();
    }

    @Override
    public void pause() {

        mMediaPlayer.pause();
    }

    @Override
    public int getDuration() {

        return mMediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {

        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {

        mMediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {

        return mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {

        return true;
    }

    @Override
    public boolean canSeekBackward() {

        return true;
    }

    @Override
    public boolean canSeekForward() {

        return true;
    }

    @Override
    public boolean isFullScreen() {

        if (mFullScreen) {
            Log.v("FullScreen", "--set icon full screen--");
            return false;
        } else {
            Log.v("FullScreen", "--set icon small full screen--");
            return true;
        }
    }

    @Override
    public boolean isVolumeOn() {
        if (mVolumeOn) {
            Log.v("FullScreen", "--set icon full screen--");
            return false;
        } else {
            Log.v("FullScreen", "--set icon small full screen--");
            return true;
        }
    }

    @Override
    public void toggleFullScreen() {

        Log.v("FullScreen", "-----------------click toggleFullScreen-----------");
        setVideoSize();
        setFullScreen(isFullScreen());
    }

    @Override
    public void volumeSwitch() {

        if (mVolumeOn) {
            mMediaPlayer.setVolume(0, 0);
            mVolumeOn = false;
        } else {
            mMediaPlayer.setVolume(1, 1);
            mVolumeOn = true;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVideoControllerView.show();
        return false;
    }

    private void setVideoSize() {

        // // Get the dimensions of the video
        int videoWidth = mMediaPlayer.getVideoWidth();
        int videoHeight = mMediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;

        // Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;

        android.view.ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
        android.view.ViewGroup.LayoutParams bgSize = mBackgroundView.getLayoutParams();

        // Get the SurfaceView layout parameters
        if (videoProportion > 1) {
            if (videoProportion > screenProportion) {
                lp.width = screenWidth;
                lp.height = (int) ((float) screenWidth / videoProportion);
                bgSize.width = screenWidth;
                bgSize.height = (int) ((float) screenWidth / videoProportion);
            } else {
                lp.width = (int) (videoProportion * (float) screenHeight);
                lp.height = screenHeight;
                bgSize.width = (int) (videoProportion * (float) screenHeight);
                bgSize.height = screenHeight;
            }
        } else {
            if (screenProportion < 1) {
                lp.height = screenWidth;
                lp.width = (int) (videoProportion * (float) screenWidth);
                bgSize.width = screenWidth;
                bgSize.height = screenWidth;
            } else {
                lp.height = screenHeight;
                lp.width = (int) (videoProportion * (float) screenHeight);
                bgSize.width = screenWidth;
                bgSize.height = screenHeight;
            }
        }


        // Commit the layout parameters
        mBackgroundView.setLayoutParams(bgSize);
        mSurfaceView.setLayoutParams(lp);
    }

    public void setFullScreen(boolean fullScreen) {
        fullScreen = false;

        // // Get the dimensions of the video
        int videoWidth = mMediaPlayer.getVideoWidth();
        int videoHeight = mMediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;

        // Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;

        if (mFullScreen) {
            Log.v("FullScreen", "-----------Set full screen SCREEN_ORIENTATION_LANDSCAPE------------");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;
            android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) mSurfaceView.getLayoutParams();
            params.width = width;
            params.height = height;
            mSurfaceView.setLayoutParams(params);
            mVideoControllerView.show();
//            params.setMargins(0, 0, 0, 0);
            //set icon is full screen
            mFullScreen = fullScreen;
        } else {
            Log.v("FullScreen", "-----------Set small screen SCREEN_ORIENTATION_PORTRAIT------------");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            final FrameLayout mFrame = (FrameLayout) findViewById(R.id.videoSurfaceContainer);
            // int height = displaymetrics.heightPixels;
            int height = mFrame.getHeight();//get height Frame Container video
            int width = displaymetrics.widthPixels;
            android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) mSurfaceView.getLayoutParams();
            params.width = width;
            params.height = height;
            mSurfaceView.setLayoutParams(params);
            mVideoControllerView.show(mMediaPlayer.getDuration());
//            params.setMargins(0, 0, 0, 0);
            //set icon is small screen
            mFullScreen = !fullScreen;
        }
    }
}
