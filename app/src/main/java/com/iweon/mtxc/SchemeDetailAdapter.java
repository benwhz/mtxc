package com.iweon.mtxc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SchemeDetailAdapter extends RecyclerView.Adapter<SchemeDetailAdapter.SchemeDetailViewHolder> {
    private SchemeInfo mSchemeInfo = null;
    private RecyclerView mRecyclerView;
    static private Context mContext;
    private List<Integer> mChannelList = null;

    // Provide a suitable constructor (depends on the kind of dataset)
    public SchemeDetailAdapter(SchemeInfo info, List<Integer> status, Context context) {
        this.mSchemeInfo = info;
        this.mContext = context;
        this.mChannelList = status;
    }

    public void setSchemeInfo(SchemeInfo info)
    {
        this.mSchemeInfo = info;
        notifyDataSetChanged();
    }

    public void updateStatus( List<Integer> list )
    {
        mChannelList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SchemeDetailAdapter.SchemeDetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SchemeDetailAdapter.SchemeDetailViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.scheme_detail_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SchemeDetailViewHolder viewHolder, int i) {
        final int position = i;

        int out = i + 1;
        int in = -1;
        int sin = -1;

        if( mSchemeInfo != null )
        {
            if( mSchemeInfo.channels != null )
            {
                if( i < mSchemeInfo.channels.size() )
                    in = mSchemeInfo.channels.get(i) + 1;
            }
        }

        if( mChannelList != null )
        {
            if( i < mChannelList.size() )
                sin = mChannelList.get(i);
        }

        viewHolder.setSchemeInfo(out,in,sin);
    }

    @Override
    public int getItemCount() {
        if( mSchemeInfo != null )
            return mSchemeInfo.channels.size();
        else
            return 0;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class SchemeDetailViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView outputTextView;
        private TextView inputTextView;
        private TextView sinputTextView;

        public SchemeDetailViewHolder(View v) {
            super(v);
            outputTextView = itemView.findViewById(R.id.schemeOutTextView);
            inputTextView = itemView.findViewById(R.id.schemeInTextView);
            sinputTextView = itemView.findViewById(R.id.schemeStatusInTextView);
        }

        public void setSchemeInfo(int out, int in, int sin)
        {
            outputTextView.setText(String.format(mContext.getString(R.string.out_index_string),out));
            inputTextView.setText(String.format(mContext.getString(R.string.in_index_scheme_string),in));
            if( in != sin ) {
                inputTextView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            }
            else{
                inputTextView.setTextColor(mContext.getResources().getColor(R.color.itemEditArea));
            }

            if( sin == -1 ) {
                sinputTextView.setText(mContext.getString(R.string.in_index_invalid_string));
            }
            else
            {
                sinputTextView.setText(String.format(mContext.getString(R.string.in_index_string),sin));
            }
        }
    }

}
