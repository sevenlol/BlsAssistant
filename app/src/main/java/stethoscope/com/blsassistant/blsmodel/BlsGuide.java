package stethoscope.com.blsassistant.blsmodel;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
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
    public void setView(View v, int index, Context ctx) {
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



                //set buttons
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
}