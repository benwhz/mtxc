package com.iweon.mtxc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {

    // GestureDetectorCompat 是为了版本兼容
    private GestureDetectorCompat mGestureDetector;
    private OnItem2ClickListener mListener;

    //自定义内部监听
    public interface OnItem2ClickListener {
        //单击
        void onItemClick(View view, int position);

        //长按
        void onItemLongClick(View view, int position);
    }

    public RecyclerViewTouchListener(Context context, final RecyclerView mRecyclerView, OnItem2ClickListener listener) {
        this.mListener = listener;
        // SimpleOnGestureListener 是为了选择重写需要的方法
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            //单击事件
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View childViewUnder = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childViewUnder != null && mListener != null) {
                    mListener.onItemClick(childViewUnder, mRecyclerView.getChildLayoutPosition(childViewUnder));
                    return true;
                }
                return false;
            }

            //长按事件
            @Override
            public void onLongPress(MotionEvent e) {
                View childView = mRecyclerView.findChildViewUnder(e.getX(),e.getY());
                if(childView != null && mListener != null){
                    mListener.onItemLongClick(childView,mRecyclerView.getChildLayoutPosition(childView));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
        //是否拦截事件交给 mGestureDetector 处理
        if(mGestureDetector.onTouchEvent(motionEvent)){
            return true;
        }else
            return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }
}
