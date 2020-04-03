package com.iweon.mtxc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

enum SwitchAction
{
    Selected,
    Deselected,
    EditTitle,
    SwitchNow
}

enum ChannelType
{
    InputChannel,
    OutputChannel
}

enum LayoutType
{
    LinerLayout,
    GridLayout,
}

public class SwitchAdapter extends RecyclerView.Adapter<SwitchAdapter.SwitchViewHolder> implements DeviceItemSlideHelper.Callback {
    private RecyclerView mRecyclerView;
    private OnItemActionListener mListener = null;

    private List<Boolean> status;
    private Context mContext = null;

    ChannelType mChannelType;
    LayoutType mLayoutType;

    private List<String> mTitles;
    private List<Integer> mStatus = null;

    public SwitchAdapter(Context context, LayoutType ltype, ChannelType type, List<String> titles, OnItemActionListener listener)
    {
        this.mChannelType = type;
        this.mLayoutType = ltype;
        this.mTitles = titles;
        this.mContext = context;
        this.mListener = listener;

        status = new ArrayList<>();

        for(int i=0;i<mTitles.size();i++)
            status.add(false);
    }

    public List<Boolean> getStatus()
    {
        return status;
    }

    public void setTitles(List<String> titles)
    {
        mTitles = titles;

        notifyDataSetChanged();
    }

    public void checkAll(boolean checked)
    {
        if( mChannelType != ChannelType.OutputChannel ) return;

        for (int i = 0; i < status.size();i++) {
            status.set(i,checked);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SwitchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (mLayoutType) {
            case LinerLayout:
                return new SwitchAdapter.SwitchViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.switch_item, viewGroup, false));
            case GridLayout:
                return new SwitchAdapter.SwitchViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.switch_grid_item, viewGroup, false));
        }
        return null;
    }

    private void inputChannelChecked(int position, boolean checked)
    {
        if( mChannelType != ChannelType.InputChannel ) return;

        if( checked )
        {
            for (int i = 0; i < status.size();i++)
            {
                if( i!=position)
                    status.set(i,false);
                else
                    status.set(i,checked);
            }

            notifyDataSetChanged();
        }
        else
            status.set(position, checked);
    }

    @Override
    public void onBindViewHolder(@NonNull SwitchViewHolder switchViewHolder, int i) {
        final int position = i;
        final SwitchViewHolder viewHolder = switchViewHolder;

       if(mLayoutType == LayoutType.GridLayout)
        {
            final CheckableConstraintLayout layout = (com.iweon.mtxc.CheckableConstraintLayout) viewHolder.itemView;

            if (viewHolder.itemView instanceof CheckableConstraintLayout) {
//                layout = (com.iweon.mtxc.CheckableConstraintLayout) viewHolder.itemView;
            }

            /*
            if( status.get(i) )
                switchViewHolder.checkFlagImageView.setVisibility(VISIBLE);
            else
                viewHolder.checkFlagImageView.setVisibility(INVISIBLE);
            */
            if( layout != null)
                layout.setChecked(status.get(i));

            switchViewHolder.chNumberTextView.setText("" + (i + 1));

            switchViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = status.get(position);

                    isChecked = !isChecked;

                    layout.toggle();
                    /*
                    if (isChecked)
                        viewHolder.checkFlagImageView.setVisibility(VISIBLE);
                    else
                        viewHolder.checkFlagImageView.setVisibility(INVISIBLE);
                    */

                    status.set(position, isChecked);
                    inputChannelChecked(position, isChecked);

                    if (mListener != null) {
                        if (isChecked)
                            mListener.onActionTriggered(mChannelType, SwitchAction.Selected, position);
                        else
                            mListener.onActionTriggered(mChannelType, SwitchAction.Deselected, position);

                    }
                }
            });
        }

        if(mLayoutType == LayoutType.LinerLayout ) {
            viewHolder.chCheckBox.setChecked(status.get(i));
//            layout.setChecked(status.get(i));

            switchViewHolder.chIdTextView.setText(mTitles.get(i));
            switchViewHolder.chInfoTextView.setText(getStatusInfo(i));

            switchViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked;
//                    layout.toggle();

                    if (viewHolder.chCheckBox.isChecked())
                        viewHolder.chCheckBox.setChecked(false);
                    else
                        viewHolder.chCheckBox.setChecked(true);

                    isChecked = viewHolder.chCheckBox.isChecked();

                    status.set(position, isChecked);
                    inputChannelChecked(position, isChecked);

                    if (mListener != null) {
                        if (isChecked)
                            mListener.onActionTriggered(mChannelType, SwitchAction.Selected, position);
                        else
                            mListener.onActionTriggered(mChannelType, SwitchAction.Deselected, position);

                    }
                }
            });

            switchViewHolder.editChTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onActionTriggered(mChannelType, SwitchAction.EditTitle, position);
                }
            });
        }
    }

    private String getStatusInfo(int index)
    {
        String info = new String();

        if( mStatus == null )   return info;

        if( mChannelType == ChannelType.InputChannel )
        {
            info = (index + 1) + "->";
            for( int i = 0; i < mStatus.size(); i ++ )
            {
                if( index + 1 == mStatus.get(i) )
                {
                    info = info + (i + 1) + ",";
                }
            }
        }

        if( mChannelType == ChannelType.OutputChannel )
        {
            if( index < mStatus.size() )
            {
                info = mStatus.get(index) + "->" + (index + 1);
            }
        }

        return info;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if(mLayoutType == LayoutType.LinerLayout ) {
            mRecyclerView = recyclerView;
            mRecyclerView.addOnItemTouchListener(new DeviceItemSlideHelper(mRecyclerView.getContext(), this));
        }
    }

    @Override
    public int getItemCount() {
        return this.mTitles.size();
    }

    @Override
    public int getHorizontalRange(RecyclerView.ViewHolder holder) {
        if (holder.itemView instanceof ConstraintLayout) {
            ViewGroup viewGroup = (ViewGroup) holder.itemView;

            TextView editTv = holder.itemView.findViewById(R.id.editChTextView);

            //viewGroup包含3个控件，即消息主item、标记已读、删除，返回为标记已读宽度+删除宽度
            int width = editTv.getLayoutParams().width;
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

    public static class SwitchViewHolder extends RecyclerView.ViewHolder {
        TextView chIdTextView;
        TextView chInfoTextView;
        CheckBox chCheckBox;
        TextView editChTextView;

        TextView chNumberTextView;
        ImageView checkFlagImageView;

        public SwitchViewHolder(@NonNull View itemView) {
            super(itemView);

            chIdTextView = itemView.findViewById(R.id.chIdTextView);
            chInfoTextView = itemView.findViewById(R.id.chInfoTextView);
            chCheckBox = itemView.findViewById(R.id.checkBox);
            editChTextView = itemView.findViewById(R.id.editChTextView);

            chNumberTextView = itemView.findViewById(R.id.channelIdTV);
            checkFlagImageView = itemView.findViewById(R.id.checkFlagIV);
        }
    }

    public interface OnItemActionListener {
        void onActionTriggered(ChannelType type, SwitchAction action, int position);
    }

    public void updateStatus( List<Integer> list )
    {
        this.mStatus = list;

        for(int i=0;i<mTitles.size();i++)
            status.set(i,false);

        notifyDataSetChanged();
    }
}
