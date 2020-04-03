package com.iweon.mtxc;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

enum SchemeAction
{
    AddScheme,
    DeleteScheme,
    EditScheme,
    ShowScheme
}



public class SchemeAdapter extends RecyclerView.Adapter<SchemeAdapter.SchemeViewHolder> implements DeviceItemSlideHelper.Callback{
    private List<SchemeInfo> schemeInfoList;
    private RecyclerView mRecyclerView;
    private OnItemActionListener mListener = null;

    public SchemeAdapter(List<SchemeInfo> list, OnItemActionListener listener)
    {
        this.mListener = listener;
        this.schemeInfoList = list;
    }

    @NonNull
    @Override
    public SchemeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SchemeAdapter.SchemeViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.scheme_item, viewGroup, false));
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        mRecyclerView.addOnItemTouchListener(new DeviceItemSlideHelper(mRecyclerView.getContext(), this));
    }

    @Override
    public void onBindViewHolder(@NonNull SchemeViewHolder schemeViewHolder, int i) {
        final int position = i;
        schemeViewHolder.schemeNameTextView.setText(schemeInfoList.get(i).name);

        schemeViewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if( mListener != null )
                    mListener.onActionTriggered(SchemeAction.ShowScheme,position);
            }
        });

        schemeViewHolder.saveSchemeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onActionTriggered(SchemeAction.EditScheme,position);
//                mDatas.get(holder.getAdapterPosition()).setChecked(true);
//                notifyItemChanged(holder.getAdapterPosition());
            }
        });
        //删除监听
        schemeViewHolder.delSchemeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onActionTriggered(SchemeAction.DeleteScheme,position);
//                removeData(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return schemeInfoList.size();
    }

    public void setSchemeInfo(List<SchemeInfo> list)
    {
        schemeInfoList = list;
    }

    @Override
    public int getHorizontalRange(RecyclerView.ViewHolder holder) {
        if (holder.itemView instanceof ConstraintLayout) {
            ViewGroup viewGroup = (ViewGroup) holder.itemView;

            TextView saveSTv = holder.itemView.findViewById(R.id.saveSchemeTextView);
            TextView delSTv = holder.itemView.findViewById(R.id.delSchemeTextView);

            //viewGroup包含3个控件，即消息主item、标记已读、删除，返回为标记已读宽度+删除宽度
            int width = saveSTv.getLayoutParams().width + delSTv.getLayoutParams().width;
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

    public static class SchemeViewHolder extends RecyclerView.ViewHolder {
        TextView schemeNameTextView;
        TextView saveSchemeTextView;
        TextView delSchemeTextView;

        public SchemeViewHolder(@NonNull View itemView) {
            super(itemView);

            schemeNameTextView = itemView.findViewById(R.id.schemeNameTextView);
            saveSchemeTextView = itemView.findViewById(R.id.saveSchemeTextView);
            delSchemeTextView = itemView.findViewById(R.id.delSchemeTextView);
        }
    }

    public interface OnItemActionListener {
        void onActionTriggered(SchemeAction action, int position);
    }
}
