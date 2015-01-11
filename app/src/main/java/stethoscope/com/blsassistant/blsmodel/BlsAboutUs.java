package stethoscope.com.blsassistant.blsmodel;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by stephen on 2015/1/7.
 */
public class BlsAboutUs implements BlsTemplate {
    private static final String STETHOSCOPE_FACEBOOK_URL = "http://www.google.com";
    private static final String STETHOSCOPE_WEBSITE_URL = "http://www.google.com";
    private static final String STETHOSCOPE_GOOGLE_PLUS_URL = "http://www.google.com";

    @Override
    public void setView(View v, int index, Context ctx, Handler handler) {
        //set thank list link
        TextView thankListBtn = (TextView) v.findViewById(ctx.getResources().getIdentifier("about_us_thanks", "id", ctx.getPackageName()));
        thankListBtn.setOnClickListener(new ThankListButtonListener(ctx));

        //set rate this app link
        TextView rateThisAppLink = (TextView) v.findViewById(ctx.getResources().getIdentifier("about_us_rate_this_app", "id", ctx.getPackageName()));
        rateThisAppLink.setOnClickListener(new RateThisAppLinkListener(ctx));

        //set up about us link buttons (facebook, google plus, website)
        ImageButton mFbButton = (ImageButton) v.findViewById(ctx.getResources().getIdentifier("about_us_fb", "id", ctx.getPackageName()));
        ImageButton mWebsiteButton = (ImageButton) v.findViewById(ctx.getResources().getIdentifier("about_us_website", "id", ctx.getPackageName()));
        ImageButton mGooglePlusButton = (ImageButton) v.findViewById(ctx.getResources().getIdentifier("about_us_google_plus", "id", ctx.getPackageName()));
        mFbButton.setOnClickListener(new BrowserButtonListener(STETHOSCOPE_FACEBOOK_URL, ctx));
        mWebsiteButton.setOnClickListener(new BrowserButtonListener(STETHOSCOPE_WEBSITE_URL, ctx));
        mGooglePlusButton.setOnClickListener(new BrowserButtonListener(STETHOSCOPE_GOOGLE_PLUS_URL, ctx));

        try{
            ImageView mImage = (ImageView) v.findViewById(ctx.getResources().getIdentifier("about_us_icon", "id", ctx.getPackageName()));
            InputStream ims = ctx.getAssets().open("stethoscope_logo.png");
            Drawable d = Drawable.createFromStream(ims, null);
            if (mImage != null)
                mImage.setImageDrawable(d);
        } catch (IOException e){

        }
    }

    @Override
    public String getDataTitle(int index) {
        return "關於";
    }

    @Override
    public String getTitle() {
        return "關於";
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

    public static class BrowserButtonListener implements View.OnClickListener {
        private Context ctx;
        private String  url;

        public BrowserButtonListener(String url, Context ctx) {
            this.ctx = ctx;
            this.url = url;
        }

        @Override
        public void onClick(View v) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            ctx.startActivity(browserIntent);
        }
    }

    private class RateThisAppLinkListener implements View.OnClickListener {
        private Context ctx;

        public RateThisAppLinkListener(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(ctx, "Link to Google Play Page", Toast.LENGTH_SHORT).show();
        }
    }

    public static class ThankListButtonListener implements View.OnClickListener {
        private Context ctx;

        public ThankListButtonListener(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onClick(View v) {
            int layoutID = ctx.getResources().getIdentifier("layout_thank_list", "layout", ctx.getPackageName());
            final ThankListDialog dialog = new ThankListDialog(ctx, layoutID, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
            Button cancelBtn = (Button) dialog.findViewById(ctx.getResources().getIdentifier("thank_cancel_button", "id", ctx.getPackageName()));
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}
