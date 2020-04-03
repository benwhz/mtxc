package com.iweon.mtxc;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.Checkable;

public class CheckableConstraintLayout extends ConstraintLayout implements Checkable {
    private boolean mChecked;

    public CheckableConstraintLayout(Context context) {
        super(context);
    }

    public CheckableConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        setBackgroundResource(checked? R.drawable.item_checked : R.drawable.grid_item_border);

    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }
}
