package stethoscope.com.blsassistant.blsmodel;

import android.content.Context;
import android.os.Handler;
import android.view.View;

public interface BlsTemplate{
    void setView(View v, int index, Context ctx, Handler handler);
    String getDataTitle(int index);
    String getTitle();
}