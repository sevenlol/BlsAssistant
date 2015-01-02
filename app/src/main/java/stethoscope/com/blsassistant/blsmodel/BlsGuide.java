package stethoscope.com.blsassistant.blsmodel;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;
import java.io.InputStream;

import stethoscope.com.blsassistant.MainActivity;
import stethoscope.com.blsassistant.VideoControllerView;


public class BlsGuide implements BlsTemplate{
    private String guideTitle;
    private BlsData[] guideData;
    private boolean isRepChecked = true;
    private MediaController mController = null;


    //Constructor
    //TODO: Rep Exposure, fix later depends on performance
    public BlsGuide(String title, BlsData[] data){
        guideData = data;
        guideTitle = title;
        isRepChecked = checkRep();
    }

    public boolean isRepChecked(){
        isRepChecked = checkRep();
        return isRepChecked;
    }

    @Override
    public void setView(View v, int index, Context ctx, Handler handler) {


            if (checkBlsDataRep(index)){
                //data not corrupted

                //load textview
                String guideStr = "";
                TextView mText = (TextView) v.findViewById(ctx.getResources().getIdentifier("guide_text", "id", ctx.getPackageName()));
                for (int i=0;i<guideData[index].getDescription().length;i++)
                    guideStr += "  " + guideData[index].getDescription()[i] + "\n";
                Log.d("RepCheck", "" + guideStr);
                mText.setText(guideStr);


                String mediaFileStr = guideData[index].getUrl()[0];
                if (mediaFileStr != null && mediaFileStr.contains(".")){
                    String extensionStr = mediaFileStr.substring(mediaFileStr.lastIndexOf(".") + 1, mediaFileStr.length());
                    if (extensionStr.equalsIgnoreCase("jpg") || extensionStr.equalsIgnoreCase("png")){
                        //set imageview
                        setImageView(v, ctx, index);
                    }

                    if (extensionStr.equalsIgnoreCase("mp4") || extensionStr.equalsIgnoreCase("3gp")){
                        //set videoview
                        setVideoView(v, ctx, index);
                        //test message for fullscreen video
                        /*
                        Message msg = new Message();
                        msg.what = 3;
                        handler.sendMessage(msg);*/
                    }
                }




                //set next step button
                Button nextStepButton = (Button) v.findViewById(
                        ctx.getResources().getIdentifier("guide_button_next_step", "id", ctx.getPackageName()));
                if (guideData != null && index != (guideData.length-1)){
                    nextStepButton.setOnClickListener(new NextStepButtonListener(handler));
                    nextStepButton.setEnabled(true);
                }
                else{
                    nextStepButton.setEnabled(false);
                }


                //set last step button
                Button lastStepButton = (Button) v.findViewById(
                        ctx.getResources().getIdentifier("guide_button_last_step", "id", ctx.getPackageName()));
                if (index != 0){
                    lastStepButton.setOnClickListener(new LastStepButtonListener(handler));
                    lastStepButton.setEnabled(true);
                }
                else
                    lastStepButton.setEnabled(false);


            }

    }


    private void setVideoView(View v, Context ctx, int index){
        ImageView mImage = (ImageView) v.findViewById(ctx.getResources().getIdentifier("guide_image", "id", ctx.getPackageName()));
        mImage.setVisibility(View.GONE);

        FrameLayout mVideo = (FrameLayout) v.findViewById(ctx.getResources().getIdentifier("guide_video", "id", ctx.getPackageName()));
        mVideo.setVisibility(View.VISIBLE);

        SurfaceView videoSurface = (SurfaceView) v.findViewById(ctx.getResources().getIdentifier("guide_video_surface", "id", ctx.getPackageName()));
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback((MainActivity) ctx);

        MediaPlayer player = new MediaPlayer();
        VideoControllerView controller = new VideoControllerView(ctx);
        videoSurface.setOnTouchListener(new VideoOnTouchListener(controller));
        String fileName = guideData[index].getUrl()[0];
        ((MainActivity) ctx).setVideoPlayer(player,controller,videoSurface,fileName.substring(0,fileName.indexOf(".")));
        /*
        SurfaceView mVideo = (SurfaceView) v.findViewById(ctx.getResources().getIdentifier("guide_video_surface", "id", ctx.getPackageName()));
        MediaController mc = new MediaController(ctx);
        mVideo.setMediaController(mc);
        mController = mc;
        try{
            String fileName = guideData[index].getUrl()[0];
            String path = "android.resource://" + ctx.getPackageName() + "/" +
                    ctx.getResources().getIdentifier(fileName.substring(0,fileName.indexOf(".")),"raw",ctx.getPackageName());
            mVideo.setVideoURI(Uri.parse(path));
            mVideo.setVisibility(View.VISIBLE);
            mVideo.seekTo(100);
            //mVideo.start();
        } catch (Exception e){

        }*/
    }

    private void setImageView(View v, Context ctx, int index){
        //load imageview and set videoview to invisible mode
        try{
            ImageView mImage = (ImageView) v.findViewById(ctx.getResources().getIdentifier("guide_image", "id", ctx.getPackageName()));
            InputStream ims = ctx.getAssets().open(guideData[index].getUrl()[0]);
            Drawable d = Drawable.createFromStream(ims, null);
            mImage.setImageDrawable(d);
            mImage.setVisibility(View.VISIBLE);

            FrameLayout mVideo = (FrameLayout) v.findViewById(ctx.getResources().getIdentifier("guide_video", "id", ctx.getPackageName()));
            mVideo.setVisibility(View.GONE);
        } catch (IOException e){

        }

    }

    public void hideMediaController(){
        if (mController != null)
            mController.hide();
    }


    @Override
    public String getDataTitle(int i){
        if (checkBlsDataRep(i))
            return guideData[i].getTitle();
        else
            return "Title";
    }

    @Override
    public String getTitle() {
        return guideTitle;
    }

    @Override
    public boolean contains(String searchStr) {
        return guideTitle.toLowerCase().contains(searchStr.toLowerCase());
    }

    public int getDataCount(){
        if (guideData == null)
            return 0;
        return guideData.length;
    }

    private boolean checkBlsDataRep(int i){
        return guideData != null && guideData.length > i && guideData[i].isRepChecked()
                && guideData[i].getUrl().length == 1 && guideData[i].getDescription().length > 0;
    }

    private boolean checkRep(){
        if (!isRepChecked)
            return false;

        if (guideTitle == null || guideData == null || guideData.length == 0)
            return false;

        for (int i=0;i< guideData.length;i++){
            if (!(guideData[i].isRepChecked() && guideData[i].getUrl().length == 1 && guideData[i].getDescription().length > 0))
                return false;
        }

        return true;
    }

    private class VideoOnTouchListener implements View.OnTouchListener{
        VideoControllerView controller;

        public VideoOnTouchListener(VideoControllerView controller){
            this.controller = controller;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            controller.show();
            return false;
        }
    }

    private class NextStepButtonListener implements View.OnClickListener{
        private Handler mHandler;

        public NextStepButtonListener(Handler mHandler){
            this.mHandler = mHandler;
        }

        @Override
        public void onClick(View v) {
            Message msg = new Message();
            msg.what = 1;
            mHandler.sendMessage(msg);
        }
    }

    private class LastStepButtonListener implements View.OnClickListener{
        private Handler mHandler;

        public LastStepButtonListener(Handler mHandler){
            this.mHandler = mHandler;
        }

        @Override
        public void onClick(View v) {
            Message msg = new Message();
            msg.what = 2;
            mHandler.sendMessage(msg);
        }
    }
}