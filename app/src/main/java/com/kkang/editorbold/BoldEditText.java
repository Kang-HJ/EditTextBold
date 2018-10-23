package com.kkang.editorbold;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

@SuppressLint("AppCompatCustomView")
public class BoldEditText extends EditText {

    onSelectionChangedListener listener;

    public BoldEditText(Context context) {
        super(context);
    }
    public BoldEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BoldEditText(Context context, onSelectionChangedListener listener) {
        super(context);
        this.listener = listener;
    }

    public void setListener(onSelectionChangedListener listener){
        this.listener = listener;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (listener != null) {
            listener.onSelectionChanged(selStart, selEnd);
        }
    }

    public interface onSelectionChangedListener {
        void onSelectionChanged(int selStart, int selEnd);
    }
}
