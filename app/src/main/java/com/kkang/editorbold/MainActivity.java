package com.kkang.editorbold;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public void Debug(String msg) {
        Log.d("KKANG", buildLogMsg(msg));
    }

    private String buildLogMsg(String message) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(ste.getFileName());
        sb.append(" > ");
        sb.append(ste.getMethodName());
        sb.append(" > #");
        sb.append(ste.getLineNumber());
        sb.append("] ");
        sb.append(message);

        return sb.toString();
    }

    //final String START_BOLD = "<bold>";
    //final String END_BOLD = "</bold>";

    final String START_BOLD = "\u0182";
    final String END_BOLD = "\u0183";

    TextView tvBold;
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBold = (TextView) findViewById(R.id.tvBold);
        et = (EditText) findViewById(R.id.et);

        tvBold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        et.addTextChangedListener(textWatcher);
        et.setText("");
    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
//         CharSequence s : 현재 에디트텍스트에 입력된 문자열을 담고 있다.
//
//        int start : s 에 저장된 문자열 내에 새로 추가될 문자열의 위치값을 담고있다.
//
//        int count : s 에 담긴 문자열 가운데 새로 사용자가 입력할 문자열에 의해 변경될 문자열의 수가 담겨있다.
//
//        int after : 새로 추가될 문자열의 수

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Debug(" s " + s);
            Debug(" start " + start);
            Debug(" count " + count);
            Debug(" after " + after);
        }

        @Override
//        CharSequence s : 사용자가 새로 입력한 문자열을 포함한 에디트텍스트의 문자열이 들어있음
//
//        int start : 새로 추가된 문자열의 시작 위치의 값
//
//        int before : 새 문자열 대신 삭제된 기존 문자열의 수가 들어 있다
//
//        int count : 새로 추가된 문자열의 수가 들어있다.
//

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Debug(" s " + s);
            Debug(" start " + start);
            Debug(" before " + before);
            Debug(" count " + count);

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void setTagAndLinkText(String allStr) {
        Debug(" allStr  " + allStr);
        int offset = 0;
        int stringLength = (allStr.replace(START_BOLD, "").replace(END_BOLD, "")).length();
        String aTagReplceStr = allStr.replace(START_BOLD, "").replace(END_BOLD, "");
        Spannable wordtoSpan = new SpannableString(aTagReplceStr);


        offset = 0;

        int tagStartIndex = (allStr).indexOf(START_BOLD, offset);
        Debug("allStr length   " + allStr.length());

        while (tagStartIndex != -1) {
            Debug(" tagStartIndex  " + tagStartIndex);
            int tagEndSpaceIndex = (allStr).indexOf(END_BOLD, tagStartIndex);
            if (tagEndSpaceIndex == -1) {
                tagEndSpaceIndex = allStr.length();
            }
            tagEndSpaceIndex += 4;

            tagEndSpaceIndex = tagEndSpaceIndex == -1 ? stringLength : tagEndSpaceIndex;
            Debug("tagEndSpaceIndex    " + tagEndSpaceIndex);

            offset = tagEndSpaceIndex;
            final String linkStr;
            if (allStr.length() >= tagEndSpaceIndex) {
                linkStr = allStr.substring(tagStartIndex, tagEndSpaceIndex);
                Debug("lingk  1  " + linkStr);
            } else {
                linkStr = allStr.substring(tagStartIndex, allStr.length());
                Debug("lingk  2  " + linkStr);
            }
            int linkStartIdx = aTagReplceStr.indexOf(linkStr.replace(START_BOLD, "").replace(END_BOLD, ""));
            int linkEndIdx = linkStartIdx + linkStr.replace(START_BOLD, "").replace(END_BOLD, "").length();

            wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), linkStartIdx, linkEndIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            tagStartIndex = (allStr).indexOf(START_BOLD, offset);
            Debug(" tagStartIndex  " + tagStartIndex);
        }
        //lastString = wordtoSpan.toString();
        et.setText(wordtoSpan);
    }
}
