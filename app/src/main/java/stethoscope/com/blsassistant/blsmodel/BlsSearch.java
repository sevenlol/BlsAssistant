package stethoscope.com.blsassistant.blsmodel;

import android.content.Context;
import android.os.Handler;
import android.view.View;

/**
 * Created by stephen on 2014/12/27.
 */
public class BlsSearch implements BlsTemplate {
    private String[] resultTitleArr; //title of results
    private int[] resultIndexArr; // index of results (in MainActivity.templateDataArr)

    //constructor
    public BlsSearch(String[] resultTitleArr, int[] resultIndexArr){
        this.resultIndexArr = resultIndexArr;
        this.resultTitleArr = resultTitleArr;
    }

    @Override
    public void setView(View v, int index, Context ctx, Handler handler) {

    }

    @Override
    public String getDataTitle(int index) {
        return "Search Result";
    }

    @Override
    public String getTitle() {
        return "Search Result";
    }

    private boolean checkRep(){
        return (resultIndexArr != null) && (resultTitleArr != null) && (resultIndexArr.length == resultTitleArr.length);
    }
}
