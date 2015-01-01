package stethoscope.com.blsassistant.blsmodel;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by stephen on 2015/1/1.
 */
public class DrawerListAdapter extends BaseAdapter {
    public static final int ITEM_VIEW_TYPE_SEPARATOR = 0;
    public static final int ITEM_VIEW_TYPE_BLSGUIDE = 1;
    public static final int ITEM_VIEW_TYPE_BLSMAP = 2;

    public static final int[] sectionTypeArr = {ITEM_VIEW_TYPE_BLSGUIDE, ITEM_VIEW_TYPE_BLSMAP};
    public static final String[] sectionTitleArr = {"Guide", "Map"};

    public static final int ITEM_VIEW_TYPE_COUNT = 3;

    private Object[] itemList;
    private int templateLayoutID;
    private int separatorLayoutID;
    private int titleTextViewID;
    private int stepCountTextViewID;
    private int separatorTextViewID;
    private Context ctx;

    public static class Template{
        private String title;
        private int type;
        private int stepCount;
        public Template(String title, int type, int stepCount){
            this.title = title;
            this.type = type;
            this.stepCount = stepCount;
        }

        public String getTitle(){return title;}

        public int getType(){return type;}

        public int getStepCount(){return stepCount;}


    }

    public DrawerListAdapter(Object[] itemList, Context ctx, int templateLayoutID, int separatorLayoutID,
                             int titleTextViewID, int stepCountTextViewID, int separatorTextViewID){
        this.itemList = itemList;
        this.ctx = ctx;
        this.templateLayoutID = templateLayoutID;
        this.separatorLayoutID = separatorLayoutID;
        this.titleTextViewID = titleTextViewID;
        this.stepCountTextViewID = stepCountTextViewID;
        this.separatorTextViewID = separatorTextViewID;
    }

    @Override
    public int getCount() {
        return itemList.length;
    }

    @Override
    public Object getItem(int position) {
        return itemList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount(){
        return ITEM_VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return (itemList[position] instanceof String) ? ITEM_VIEW_TYPE_SEPARATOR : ((Template)itemList[position]).getType();
    }

    @Override
    public boolean isEnabled(int position) {
        // A separator cannot be clicked !
        return getItemViewType(position) != ITEM_VIEW_TYPE_SEPARATOR;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int type = getItemViewType(position);
        Log.d("DrawerAdapter", "getView()");

        if (convertView == null){

            convertView = LayoutInflater.from(ctx).inflate(
                    type == ITEM_VIEW_TYPE_SEPARATOR ? separatorLayoutID : templateLayoutID, parent, false);
            Log.d("DrawerAdapter", "Inflater: " + (convertView instanceof RelativeLayout));
            Log.d("DrawerAdapter", "ID: " + (convertView.getId() == templateLayoutID));
        }

        if (type == ITEM_VIEW_TYPE_SEPARATOR) {
            ((TextView) convertView.findViewById(separatorTextViewID)).setText((String) getItem(position));
        }
        else{
            final Template mTemplate = (Template) getItem(position);
            ((TextView) convertView.findViewById(titleTextViewID)).setText(mTemplate.getTitle());


            if (mTemplate.getType() == ITEM_VIEW_TYPE_BLSGUIDE)
                ((TextView) convertView.findViewById(stepCountTextViewID)).setText(String.valueOf(mTemplate.getStepCount()));
            else
                convertView.findViewById(stepCountTextViewID).setVisibility(View.GONE);


        }

        return convertView;
    }
}
