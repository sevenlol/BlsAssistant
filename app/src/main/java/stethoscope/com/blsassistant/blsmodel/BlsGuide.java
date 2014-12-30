package stethoscope.com.blsassistant.blsmodel;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;


public class BlsGuide implements BlsTemplate{
    private String guideTitle;
    private BlsData[] guideData;
    private boolean isRepChecked = true;


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
        try{

            if (checkBlsDataRep(index)){
                //data not corrupted

                //load textview
                String guideStr = "";
                TextView mText = (TextView) v.findViewById(ctx.getResources().getIdentifier("guide_text", "id", ctx.getPackageName()));
                for (int i=0;i<guideData[index].getDescription().length;i++)
                    guideStr += "  " + guideData[index].getDescription()[i] + "\n";
                Log.d("RepCheck", "" + guideStr);
                mText.setText(guideStr);

                //load imageview

                ImageView mImage = (ImageView) v.findViewById(ctx.getResources().getIdentifier("guide_image", "id", ctx.getPackageName()));
                InputStream ims = ctx.getAssets().open(guideData[index].getUrl()[0]);
                Drawable d = Drawable.createFromStream(ims, null);
                mImage.setImageDrawable(d);


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
        } catch (IOException e){

        }
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