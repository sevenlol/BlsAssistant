package stethoscope.com.blsassistant;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.IOException;

public class VideoPlayerActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl {

    SurfaceView videoSurface;
    MediaPlayer player;
    VideoControllerView controller;
    private boolean mFullScreen = true;
    int rawID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_player);


        Bundle extras = getIntent().getExtras();
        rawID = extras.getInt("VIDEO_RESOURCE_ID");

        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);

        player = new MediaPlayer();
        controller = new VideoControllerView(this);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show();
        return false;
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    	player.setDisplay(holder);
        AssetFileDescriptor afd = getResources().openRawResourceFd(rawID);

        try
        {
            player.reset();
            //player.setDisplay(videoHolder);
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
            player.prepare();
            controller.setMediaPlayer(this);
            controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
            player.start();
            afd.close();
        }
        catch (IllegalArgumentException e)
        {
            //Log.e(TAG, "Unable to play audio queue do to exception: " + e.getMessage(), e);
        }
        catch (IllegalStateException e)
        {
            //Log.e(TAG, "Unable to play audio queue do to exception: " + e.getMessage(), e);
        }
        catch (IOException e)
        {
            //Log.e(TAG, "Unable to play audio queue do to exception: " + e.getMessage(), e);
        }
        //player.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    // End SurfaceHolder.Callback

    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        //controller.setMediaPlayer(this);
        //controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        //player.start();
    }
    // End MediaPlayer.OnPreparedListener

    // Implement VideoMediaController.MediaPlayerControl
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
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (player != null)
            return player.getCurrentPosition();
        else
            return 0;
    }

    @Override
    public int getDuration() {
        if (player != null)
            return player.getDuration();
        else
            return 0;
    }

    @Override
    public boolean isPlaying() {
        if (player != null)
            return player.isPlaying();

        return false;
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public void start() {
        player.start();
    }

    @Override
    public boolean isFullScreen() {
        if(mFullScreen){
            Log.v("FullScreen", "--set icon full screen--");
            return false;
        }else{
            Log.v("FullScreen", "--set icon small full screen--");
            return true;
        }
    }

    @Override
    public void toggleFullScreen() {
        Log.v("FullScreen", "-----------------click toggleFullScreen-----------");
        setFullScreen(isFullScreen());
    }

    public void setFullScreen(boolean fullScreen){
        fullScreen = false;
        controller.hide();
        player.stop();
        player.reset();
        player.release();
        player = null;


        finish();
    }
    // End VideoMediaController.MediaPlayerControl

}

