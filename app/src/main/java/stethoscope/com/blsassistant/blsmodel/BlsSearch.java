package stethoscope.com.blsassistant.blsmodel;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by stephen on 2014/12/27.
 */
public class BlsSearch implements BlsTemplate {
    private BlsTemplate[] searchResultTemplateArr; //title of results
    private int[] resultIndexArr; // index of results (in MainActivity.templateDataArr)

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //constructor
    public BlsSearch(){
        searchResultTemplateArr = null;
    }

    public void setSearchResultArr(BlsTemplate[] templateArr, int[] resultIndexArr){
        searchResultTemplateArr = templateArr;
        this.resultIndexArr = resultIndexArr;
    }

    @Override
    public void setView(View v, int index, Context ctx, Handler handler) {
        mRecyclerView = (RecyclerView) v.findViewById(ctx.getResources().getIdentifier("search_result_list","id",ctx.getPackageName()));
        mLayoutManager = new LinearLayoutManager(ctx);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SearchAdapter(searchResultTemplateArr, resultIndexArr, ctx);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public String getDataTitle(int index) {
        return "Search Result";
    }

    @Override
    public String getTitle() {
        return "Search Result";
    }

    @Override
    public boolean contains(String searchStr) {
        return false;
    }

    private boolean checkRep(){
        return (resultIndexArr != null) && (searchResultTemplateArr != null) && (resultIndexArr.length == searchResultTemplateArr.length);
    }


}
