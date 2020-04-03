package com.iweon.mtxc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

enum DeviceAction
{
    AddDevice,
    DeleteDevice,
    EditDevice,
    OpenDevice
}

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> implements DeviceItemSlideHelper.Callback  {
    private List<DeviceInfo> deviceInfoList;
    private RecyclerView mRecyclerView;
    static private Context mContext;

    @Override
    public int getHorizontalRange(RecyclerView.ViewHolder holder) {
        /*
        if (holder.itemView instanceof LinearLayout) {
            ViewGroup viewGroup = (ViewGroup) holder.itemView;
            //viewGroup包含3个控件，即消息主item、标记已读、删除，返回为标记已读宽度+删除宽度
            return viewGroup.getChildAt(1).getLayoutParams().width
                    + viewGroup.getChildAt(2).getLayoutParams().width;
        }
        */

        if (holder.itemView instanceof ConstraintLayout) {
            ViewGroup viewGroup = (ViewGroup) holder.itemView;

            TextView editDTv = holder.itemView.findViewById(R.id.editDevTextView);
            TextView delDTv = holder.itemView.findViewById(R.id.delDevTextView);

            //viewGroup包含3个控件，即消息主item、标记已读、删除，返回为标记已读宽度+删除宽度
            int width = editDTv.getLayoutParams().width + delDTv.getLayoutParams().width;
            return width;
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder getChildViewHolder(View childView) {
        return mRecyclerView.getChildViewHolder(childView);
    }

    @Override
    public View findTargetView(float x, float y) {
        return mRecyclerView.findChildViewUnder(x, y);
    }

    //自定义内部监听
    public interface OnItemActionListener {
        //单击
//        void onItemClick(View view, int position);
        //长按
//        boolean onItemLongClick(View view, int position);

        void onActionTriggered(DeviceAction action, int position);
    }

    private OnItemActionListener mListener = null;

    // Provide a suitable constructor (depends on the kind of dataset)
    public DeviceAdapter(List<DeviceInfo> list, OnItemActionListener listener, Context context) {
        this.mListener = listener;
        this.deviceInfoList = list;
        this.mContext = context;
    }
    public void setDeviceInfoList(List<DeviceInfo> list) {
        deviceInfoList = list;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DeviceViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_info_item, viewGroup, false));
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        mRecyclerView.addOnItemTouchListener(new DeviceItemSlideHelper(mRecyclerView.getContext(), this));
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder deviceViewHolder, int i) {
        final int position = i;
        deviceViewHolder.setDeviceInfo(deviceInfoList.get(i));


        deviceViewHolder.itemView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if( mListener != null )
                    mListener.onActionTriggered(DeviceAction.OpenDevice,position);
            }
        });

        /*
        deviceViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if( mListener != null )
                    return mListener.onItemLongClick(v,position);
                else
                    return false;
            }
        });
        */

        //标记已读监听
        deviceViewHolder.tvEditDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onActionTriggered(DeviceAction.EditDevice,position);
//                mDatas.get(holder.getAdapterPosition()).setChecked(true);
//                notifyItemChanged(holder.getAdapterPosition());
            }
        });
        //删除监听
        deviceViewHolder.tvDeleteDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onActionTriggered(DeviceAction.DeleteDevice,position);
//                removeData(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceInfoList.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView nameTextView;
        private TextView ipTextView;
        TextView tvEditDevice;
        TextView tvDeleteDevice;

        public DeviceViewHolder(View v) {
            super(v);
            nameTextView = itemView.findViewById(R.id.devNameTextView);
            ipTextView = itemView.findViewById(R.id.devIpTextView);

            tvEditDevice = itemView.findViewById(R.id.editDevTextView);
            tvDeleteDevice = itemView.findViewById(R.id.delDevTextView);
        }

        public void setDeviceInfo(DeviceInfo info)
        {
            String name = info.name + "[" + info.inCount + "x" + info.outCount +"]";
            nameTextView.setText(name);
            String ip = mContext.getString(R.string.device_ip_title_info) + info.ip + ", " + mContext.getString(R.string.device_ip_port_info) + info.port;
            ipTextView.setText(ip);
        }
    }

}
