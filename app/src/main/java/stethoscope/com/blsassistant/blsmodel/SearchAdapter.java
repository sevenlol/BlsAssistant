package stethoscope.com.blsassistant.blsmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by stephen on 2014/12/29.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private BlsTemplate[] mTemplate;
    private static Context ctx;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTitleTextView;
        public ImageView mIconImageView;
        public TextView mShortDescriptionView;
        public TextView mStepNumberView;
        public ViewHolder(CardView v) {
            super(v);
            mTitleTextView = (TextView) v.findViewById(ctx.getResources().getIdentifier(
                    "search_result_title","id",ctx.getPackageName()));
            mShortDescriptionView = (TextView) v.findViewById(ctx.getResources().getIdentifier(
                    "search_result_short_description","id",ctx.getPackageName()));
            mStepNumberView = (TextView) v.findViewById(ctx.getResources().getIdentifier(
                    "search_result_step_number","id",ctx.getPackageName()));
            mIconImageView = (ImageView) v.findViewById(ctx.getResources().getIdentifier(
                    "search_result_icon","id",ctx.getPackageName()));
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SearchAdapter(BlsTemplate[] myTemplate, Context ctx) {
        this.mTemplate = myTemplate;
        this.ctx = ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(ctx.getResources().getIdentifier("layout_search_result_item","layout",ctx.getPackageName()), parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder((CardView) v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTitleTextView.setText("Title" + String.valueOf(position));
        holder.mShortDescriptionView.setText("Short Description" + String.valueOf(position));
        holder.mStepNumberView.setText("Step# of " + String.valueOf(position));
        InputStream ims = null;
        try {
            ims = ctx.getAssets().open("search_item_icon.png");
            Drawable d = Drawable.createFromStream(ims, null);
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            Drawable dNew = new BitmapDrawable(ctx.getResources(), Bitmap.createScaledBitmap(bitmap, 49, 49, true));
            holder.mIconImageView.setImageDrawable(dNew);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mTemplate.length;
    }
}