package com.kkang.editorbold;

import android.content.Context;
import android.widget.EditText;

public class BoldEditText extends EditText {
    public BoldEditText(Context context) {
        super(context);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
    }
}
