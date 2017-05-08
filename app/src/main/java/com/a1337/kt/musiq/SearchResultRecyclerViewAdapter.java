package com.a1337.kt.musiq;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a1337.kt.musiq.SearchResultFragment.OnListFragmentInteractionListener;
import com.a1337.kt.musiq.models.File;

import java.text.DecimalFormat;
import java.util.List;


public class SearchResultRecyclerViewAdapter extends RecyclerView.Adapter<SearchResultRecyclerViewAdapter.ViewHolder> {

    private List<File> files;
    private final OnListFragmentInteractionListener mListener;

    public SearchResultRecyclerViewAdapter(List<File> files, OnListFragmentInteractionListener listener) {
        this.files = files;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_searchresult, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat("#.##");
        File file = files.get(position);
        String sizeInMb = df.format((double) file.getSize() / (1024 * 1024)) + "MB";
        String description = "Size:" + sizeInMb;

        if (file.getId3() != null) {
            description = description + " Bitrate:" +
                    String.valueOf(file.getId3().getBitrate()) + " Duration:"
                    + String.valueOf(file.getId3().getLength());
        }
        holder.mItem = file;
        holder.mIdView.setText(file.getName());
        holder.mContentView.setText(description);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public File mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.listItemId);
            mContentView = (TextView) view.findViewById(R.id.listItemContent);
        }


        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
