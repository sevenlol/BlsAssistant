package stethoscope.com.blsassistant.blsmodel;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import stethoscope.com.blsassistant.R;

/**
 * Created by stephen on 2015/1/11.
 */
public class ThankListDialog extends Dialog {
    public ThankListDialog(Context context, int layoutID, int style) {
        super(context, style);
        setContentView(layoutID);

        //set window params
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

        setCanceledOnTouchOutside(true);

        //set width, height by density and gravity
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        params.width = size.x * 9 / 10;
        params.height = size.y * 9 / 10;
        params.gravity = Gravity.CENTER;

        window.setAttributes(params);
    }

}
