package stethoscope.com.blsassistant.blsmodel;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by stephen on 2014/12/25.
 */
public class BlsMap implements BlsTemplate{
    @Override
    public void setView(View v, int index, Context ctx, Handler handler) {
        WebView webView = (WebView) v.findViewById(ctx.getResources().getIdentifier("google_map_web", "id", ctx.getPackageName()));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/google_map_web/map_view.html");
    }

    @Override
    public String getDataTitle(int index) {
        return "AED Map";
    }

    @Override
    public String getTitle() {
        return "AED Map";
    }
}
