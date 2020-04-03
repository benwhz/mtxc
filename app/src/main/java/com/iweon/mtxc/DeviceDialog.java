package com.iweon.mtxc;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class DeviceDialog extends Dialog {
    public DeviceDialog(Context context) {
        super(context);
    }

    public DeviceDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DeviceDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
