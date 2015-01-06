package stethoscope.com.blsassistant.blsmodel;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by stephen on 2015/1/7.
 */
public class BlsAboutUs implements BlsTemplate {
    @Override
    public void setView(View v, int index, Context ctx, Handler handler) {
        try{
            ImageView mImage = (ImageView) v.findViewById(ctx.getResources().getIdentifier("aboutus_icon", "id", ctx.getPackageName()));
            InputStream ims = ctx.getAssets().open("stethoscope_logo.png");
            Drawable d = Drawable.createFromStream(ims, null);
            if (mImage != null)
                mImage.setImageDrawable(d);
        } catch (IOException e){

        }
    }

    @Override
    public String getDataTitle(int index) {
        return "About Us";
    }

    @Override
    public String getTitle() {
        return "About Us";
    }

    @Override
    public String getShortDescription() {
        return null;
    }

    @Override
    public boolean contains(String searchStr) {
        //never get searched
        return false;
    }
}
