package com.iweon.mtxc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {
    private DeviceInfo mDeviceInfo;
    private List<Integer> mChannelList = null;
    private Context mContext = null;

    public StatusAdapter( Context context, DeviceInfo deviceInfo)
    {
        this.mContext = context;
        this.mDeviceInfo = deviceInfo;
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new StatusAdapter.StatusViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.status_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder viewHolder, int i) {
        final int position = i;

        if( mChannelList != null ) {
            if (i < mChannelList.size())
                viewHolder.inTextView.setText(String.format(mContext.getString(R.string.in_index_string),mChannelList.get(i)));
            else
                viewHolder.inTextView.setText(mContext.getString(R.string.in_index_invalid_string));
        }
        else
            viewHolder.inTextView.setText(mContext.getString(R.string.in_index_invalid_string));

        viewHolder.outTextView.setText(String.format(mContext.getString(R.string.out_index_string),i+1));
    }

    @Override
    public int getItemCount() {
        return mDeviceInfo.outCount;
    }

    public static class StatusViewHolder extends RecyclerView.ViewHolder {
        TextView inTextView;
        TextView outTextView;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);

            inTextView = itemView.findViewById(R.id.inTextView);
            outTextView = itemView.findViewById(R.id.outTextView);
        }
    }

    public void updateStatus( List<Integer> list )
    {
        mChannelList = list;
        notifyDataSetChanged();
    }

}
