package stethoscope.com.blsassistant.blsmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import stethoscope.com.blsassistant.MainActivity;

/**
 * Created by stephen on 2014/12/29.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private BlsTemplate[] mTemplate;
    private int[] mIndexArr;
    private static Context ctx;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView mTitleTextView;
        public ImageView mIconImageView;
        public TextView mShortDescriptionView;
        public TextView mStepNumberView;
        public CardView mSearchResultItem;
        public IMyViewHolderClick mListener;
        public int templateIndex = -1;
        private Context ctx;

        public ViewHolder(CardView v, Context ctx, IMyViewHolderClick listener) {
            super(v);
            this.ctx = ctx;
            this.mListener = listener;
            mTitleTextView = (TextView) v.findViewById(ctx.getResources().getIdentifier(
                    "search_result_title","id",ctx.getPackageName()));
            mShortDescriptionView = (TextView) v.findViewById(ctx.getResources().getIdentifier(
                    "search_result_short_description","id",ctx.getPackageName()));
            mStepNumberView = (TextView) v.findViewById(ctx.getResources().getIdentifier(
                    "search_result_step_number","id",ctx.getPackageName()));
            mIconImageView = (ImageView) v.findViewById(ctx.getResources().getIdentifier(
                    "search_result_icon","id",ctx.getPackageName()));

            //set onclick listener
            mSearchResultItem = v;
            mSearchResultItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v instanceof CardView && templateIndex != -1)
                mListener.onResultItemClick((CardView) v, templateIndex, ctx);
        }

        public static interface IMyViewHolderClick{
            public void onResultItemClick(CardView v, int id, Context ctx);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SearchAdapter(BlsTemplate[] myTemplate, int[] indexArr, Context ctx) {
        this.mIndexArr = indexArr;
        this.mTemplate = myTemplate;
        this.ctx = ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(ctx.getResources().getIdentifier("layout_search_result_item","layout",ctx.getPackageName()), parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder((CardView) v, ctx, new ViewHolder.IMyViewHolderClick() {
            @Override
            public void onResultItemClick(CardView v, int index, Context ctx) {
                //goes to each item's content page
                ((MainActivity) ctx).onlyCollapse();
                ((MainActivity) ctx).hideKeyBoard(parent);
                ((MainActivity) ctx).displayTemplate(index);
            }
        });
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (mTemplate != null){
            holder.mTitleTextView.setText(mTemplate[position].getTitle());
            holder.mShortDescriptionView.setText(mTemplate[position].getShortDescription());
            if (mTemplate[position] instanceof BlsGuide){
                //set step number if this template is a guide
                holder.mStepNumberView.setText(String.valueOf(((BlsGuide) mTemplate[position]).getDataCount()));
            }
            else{
                //make the view invisible
                holder.mStepNumberView.setVisibility(View.GONE);
            }

            //set search item icon
            String searchItemIconPath;

            if (mTemplate[position] instanceof BlsMap)
                searchItemIconPath = "search_item_icon_map.png";
            else
                searchItemIconPath = "search_item_icon_guide.png";

            InputStream ims = null;
            try {
                ims = ctx.getAssets().open(searchItemIconPath);
                Drawable d = Drawable.createFromStream(ims, null);
                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                Drawable dNew = new BitmapDrawable(ctx.getResources(), Bitmap.createScaledBitmap(bitmap, 49, 49, true));
                holder.mIconImageView.setImageDrawable(dNew);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // set template index
            holder.templateIndex = mIndexArr[position];
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mTemplate != null)
            return mTemplate.length;
        return 0;
    }
}