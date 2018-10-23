package com.kkang.editorbold;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import static android.text.Html.FROM_HTML_MODE_COMPACT;
import static android.text.Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE;

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

    final String START_B = "<b>";
    final String END_B = "</b>";

    final String START_BOLD = "<bold>";
    final String END_BOLD = "</bold>";

    final String UNI1 = "\u0182";
    final String UNI2 = "\u0183";

    String originString = "사가다자바하라아나파차타 " + START_BOLD + "비지디기시히리이니미키티치 " + END_BOLD + "삼성화재 앱에서 휴\n대폰번호를 버튼을 눌러주세요." + START_BOLD + "본인인증을 진행해주세요." + END_BOLD +
            "인증번호가 유형을 선택 후 필요서류" + START_BOLD + "를 등록하시면 보험금 청구" + END_BOLD + "가 완료됩니다.";

    boolean isBold = false;
    TextView tvBold;
    TextView tvComplete;
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBold = (TextView) findViewById(R.id.tvBold);
        tvComplete = (TextView) findViewById(R.id.tvComplete);
        et = (EditText) findViewById(R.id.et);

        tvBold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBold) {
                    tvBold.setBackgroundColor(Color.BLACK);
                } else {
                    tvBold.setBackgroundColor(Color.BLUE);
                }
//                int start = getOriginIdx(et.getSelectionStart());
//                int end = getOriginIdx(et.getSelectionEnd());
                isBold = !isBold;
            }
        });

        tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Debug("result : " + result(et.getText()));
            }
        });

        et.setText(htmlText(originString));
        et.addTextChangedListener(textWatcher);
    }

    private int getOriginIdx(int idx) {
        int startIdx = idx;
        Debug(" idx " + idx);
        String aTagReplacePosition = originString.substring(0, idx);

        int linkOffset = 0;
        int linkGetIdx = aTagReplacePosition.indexOf(START_B, linkOffset);

        boolean isEndBold = false;
        while (linkGetIdx >= 0) {
            isEndBold = false;
            startIdx += START_B.length();
            if (aTagReplacePosition.indexOf(END_B, linkGetIdx) >= 0) {
                idx = idx + END_B.length();
                startIdx += END_B.length();
                isEndBold = true;
            }
            idx = idx + START_B.length();
            aTagReplacePosition = originString.substring(0, idx);
            linkOffset = linkGetIdx + START_B.length();
            linkGetIdx = aTagReplacePosition.indexOf(START_B, linkOffset);
        }

        if (!isEndBold) {
            linkGetIdx = aTagReplacePosition.indexOf(END_B, linkOffset);
            if (linkGetIdx >= 0) {
                startIdx += END_B.length();
            }
        }

        startIdx -= 1;

        Debug(" startIdx  " + startIdx);
        try {
            Debug(" charAt  " + originString.charAt(startIdx));
        } catch (StringIndexOutOfBoundsException e) {
            Debug("e  : " + e);
        }

        return startIdx;
    }

    private int start = 0;
    private int before = 0;
    private int count = 0;
    CharSequence text;

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
//        int count : 새로 추가된 문자열의 수가 들어있다.encode
//

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Debug(" s " + s);
            Debug(" start " + start);
            Debug(" before " + before);
            Debug(" count " + count);
            Debug(" substring " + s.toString().substring(start, start + before));
            Debug(" substring " + s.toString().substring(start, start + count));

            MainActivity.this.text = s;
            MainActivity.this.start = start;
            MainActivity.this.before = before;
            MainActivity.this.count = count;
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

    };

    private String result(Spanned text) {
        String result = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.toHtml(text, FROM_HTML_MODE_COMPACT);
        } else {
            result = Html.toHtml(text);
        }

        result = result.replace("<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\">", "").replace("</p>", "");

        Debug(" result 1   " + result);

        result = result.replace(START_B, UNI1).replace(END_B, UNI2);
        Spanned _result = Html.fromHtml(result);
        result = _result.toString().replace(UNI1, START_BOLD).replace(UNI2, END_BOLD);

        return result;
    }

    private Spanned htmlText(String str) {

        str = str.replace(START_BOLD, START_B).replace(END_BOLD, END_B);

        Spanned formattedString = Html.fromHtml(str);
        return formattedString;
    }
}
