package stethoscope.com.blsassistant.blsmodel;

import android.content.Context;
import android.view.View;

public interface BlsTemplate{
    void setView(View v, int index, Context ctx);
    String getDataTitle(int index);
    String getTitle();
}