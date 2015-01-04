package stethoscope.com.blsassistant.blsmodel;

import android.content.Context;
import android.os.Handler;
import android.view.View;

/**
 * Created by stephen on 2015/1/4.
 */
public class BlsHome implements BlsTemplate {
    @Override
    public void setView(View v, int index, Context ctx, Handler handler) {

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
