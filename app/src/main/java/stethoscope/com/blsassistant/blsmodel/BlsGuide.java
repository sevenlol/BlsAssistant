package stethoscope.com.blsassistant.blsmodel;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import stethoscope.com.blsassistant.MainActivity;
import stethoscope.com.blsassistant.VideoControllerView;


public class BlsGuide implements BlsTemplate{
    private String guideTitle;
    private String guideShortDescription;
    private BlsData[] guideData;
    private boolean isRepChecked = true;
    private MediaController mController = null;


    //Constructor
    public BlsGuide(String title, String shortDescription, BlsData[] data){
        guideData = data;
        guideTitle = title;
        guideShortDescription = shortDescription;
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

                //set up call button
                Button mCallButton = (Button) v.findViewById(ctx.getResources().getIdentifier("guide_button_call", "id", ctx.getPackageName()));
                mCallButton.setOnClickListener(new CallButtonListener(ctx));

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



                //set step indicator
                if (index == 0){


                    final View wrapperView = v.findViewById(ctx.getResources().getIdentifier("guide_wrapper_parent_bot","id",ctx.getPackageName()));
                    final View indicatorView = v.findViewById(ctx.getResources().getIdentifier("guide_step_indicator","id",ctx.getPackageName()));

                    ViewTreeObserver vto = indicatorView.getViewTreeObserver();
                    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            //Log.d("Width","" + wrapperView.getWidth());

                            indicatorView.setLayoutParams(new RelativeLayout.LayoutParams(
                                    wrapperView.getWidth()/guideData.length, indicatorView.getHeight()));


                            ViewTreeObserver obs = indicatorView.getViewTreeObserver();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                obs.removeOnGlobalLayoutListener(this);
                            } else {
                                obs.removeGlobalOnLayoutListener(this);
                            }
                        }
                    });
                    //params.width = wrapperParams.width / guideData.length;
                    //indicatorView.setLayoutParams(params);

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

        String fileName = guideData[index].getUrl()[0];
        ((MainActivity) ctx).setVideoName(fileName.substring(0,fileName.indexOf(".")));

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
    public String getShortDescription() {
        return guideShortDescription;
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

        if (guideTitle == null || guideShortDescription == null || guideData == null || guideData.length == 0)
            return false;

        for (int i=0;i< guideData.length;i++){
            if (!(guideData[i].isRepChecked() && guideData[i].getUrl().length == 1 && guideData[i].getDescription().length > 0))
                return false;
        }

        return true;
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

    public static class CallButtonListener implements View.OnClickListener {
        private Context ctx;

        public CallButtonListener(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "0963595926"));
            ctx.startActivity(intent);
        }
    }
}