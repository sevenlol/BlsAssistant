package stethoscope.com.blsassistant.blsmodel;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

import stethoscope.com.blsassistant.MainActivity;

/**
 * Created by stephen on 2015/1/4.
 */
public class BlsHome implements BlsTemplate {
    @Override
    public void setView(View v, int index, Context ctx, Handler handler) {
        //set up call button
        Button mCallButton = (Button) v.findViewById(ctx.getResources().getIdentifier("home_button_call", "id", ctx.getPackageName()));
        mCallButton.setOnClickListener(new BlsGuide.CallButtonListener(ctx));

        android.support.v7.widget.CardView mCardView1 = (android.support.v7.widget.CardView) v.findViewById(
                ctx.getResources().getIdentifier("home_popular_item_01", "id", ctx.getPackageName()));
        android.support.v7.widget.CardView mCardView2 = (android.support.v7.widget.CardView) v.findViewById(
                ctx.getResources().getIdentifier("home_popular_item_02", "id", ctx.getPackageName()));
        android.support.v7.widget.CardView mCardView3 = (android.support.v7.widget.CardView) v.findViewById(
                ctx.getResources().getIdentifier("home_popular_item_03", "id", ctx.getPackageName()));

        mCardView1.setOnClickListener(new PopularItemListener(1, ctx));
        mCardView2.setOnClickListener(new PopularItemListener(2, ctx));
        mCardView3.setOnClickListener(new PopularItemListener(3, ctx));

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
        return "首頁";
    }

    @Override
    public String getTitle() {
        return "首頁";
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

    public static class PopularItemListener implements View.OnClickListener{
        private int index;
        private Context ctx;

        public PopularItemListener(int index, Context ctx){
            this.index = index;
            this.ctx   = ctx;
        }

        @Override
        public void onClick(View v) {
            if (ctx instanceof MainActivity){
                ((MainActivity) ctx).displayTemplate(index);
            }
        }
    }
}
