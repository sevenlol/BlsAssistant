package stethoscope.com.blsassistant.blsmodel;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by stephen on 2015/1/4.
 */
public class BlsHome implements BlsTemplate {
    @Override
    public void setView(View v, int index, Context ctx, Handler handler) {
        try{
            ImageView mImage = (ImageView) v.findViewById(ctx.getResources().getIdentifier("home_icon", "id", ctx.getPackageName()));
            InputStream ims = ctx.getAssets().open("stethoscope_logo.png");
            Drawable d = Drawable.createFromStream(ims, null);
            if (mImage != null)
            mImage.setImageDrawable(d);
        } catch (IOException e){

        }


    }

    @Override
    public String getDataTitle(int index) {
        return "Home";
    }

    @Override
    public String getTitle() {
        return "Home";
    }

    @Override
    public String getShortDescription() {
        return "";
    }

    @Override
    public boolean contains(String searchStr) {
        //never appear on search result
        return false;
    }
}
