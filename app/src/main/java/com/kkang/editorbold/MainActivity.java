package com.kkang.editorbold;

import android.graphics.Color;
import android.os.Build;
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
import static android.text.Html.FROM_HTML_MODE_LEGACY;
import static android.text.Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE;
import static android.text.Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL;

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

    String originString = "사가다자바하라아나파차타 " + START_BOLD + "비지디기시히리이니미키티치 " + END_BOLD + "삼성화재 앱에서 휴대폰번호를 버튼을 눌러주세요." + START_BOLD + "본인인증을 진행해주세요." + END_BOLD +
            "인증번호가 유형을 선택 후 필요서류" + START_BOLD + "를 등록하시면 보험금 청구" + END_BOLD + "가 완료됩니다. ";
    String oldOriginString = "";
    boolean isBold = false;
    TextView tvBold;
    TextView tvComplete;
    TextView tvResult;
    BoldEditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBold = (TextView) findViewById(R.id.tvBold);
        tvComplete = (TextView) findViewById(R.id.tvComplete);
        tvResult = (TextView) findViewById(R.id.tvResult);
        et = (BoldEditText) findViewById(R.id.et);

        et.setListener(new BoldEditText.onSelectionChangedListener() {
            @Override
            public void onSelectionChanged(int selStart, int selEnd) {
                boolean isBold = isBoldStyle(getOriginIdx(selStart));
                if (MainActivity.this.isBold == isBold) {
                    return;
                }
                if (isBold) {
                    MainActivity.this.isBold = true;
                    tvBold.setBackgroundColor(Color.BLUE);
                } else {
                    MainActivity.this.isBold = false;
                    tvBold.setBackgroundColor(Color.BLACK);
                }
            }
        });
        tvBold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBold) {
                    tvBold.setBackgroundColor(Color.BLACK);
                } else {
                    tvBold.setBackgroundColor(Color.BLUE);
                }
                isBold = !isBold;
            }
        });

        tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Debug("result : " + result(et.getText()));
                tvResult.setText(result(et.getText()));
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
        if (linkGetIdx < 0) {
            idx = idx + END_B.length();
            if (idx > originString.length()) {
                idx = originString.length();
            }
            aTagReplacePosition = originString.substring(0, idx);
            linkGetIdx = aTagReplacePosition.indexOf(START_B, linkOffset);
        }
        Debug(" -------------------------------------------------");
        Debug(" aTagReplacePosition  " + aTagReplacePosition);
        Debug(" -------------------------------------------------");
        boolean isEndBold = false;

        while (linkGetIdx >= 0) {
            isEndBold = false;

            idx = idx + START_B.length();
            startIdx += START_B.length();
            linkOffset = linkGetIdx + START_B.length();
            int closeB = aTagReplacePosition.indexOf(END_B, linkOffset);
            if (closeB >= 0) {
                idx = idx + END_B.length();
                startIdx = startIdx + END_B.length();
                linkOffset = closeB + END_B.length();
                isEndBold = true;
            }

            aTagReplacePosition = originString.substring(0, idx);
            linkGetIdx = aTagReplacePosition.indexOf(START_B, linkOffset);
            if (linkGetIdx < 0) {
                int _idx = idx + END_B.length();
                if (_idx > originString.length()) {
                    _idx = originString.length();
                }
                if (_idx - END_B.length() > 0) {
                    String tagCheck2 = originString.substring(_idx - START_B.length(), _idx);
                    if (!tagCheck2.equals(START_B)) {
                        idx = _idx;
                        aTagReplacePosition = originString.substring(0, idx);
                        linkGetIdx = aTagReplacePosition.indexOf(START_B, linkOffset);
                    }
                }

            }
            Debug(" -------------------------------------------------");
            try {
                Debug(" charAt  " + originString.charAt(startIdx));
            } catch (StringIndexOutOfBoundsException e) {
                Debug("e  : " + e);
            }
            Debug(" aTagReplacePosition  " + aTagReplacePosition);
            Debug(" -------------------------------------------------");
        }

        if (!isEndBold) {
            linkGetIdx = aTagReplacePosition.indexOf(END_B, linkOffset);
            if (linkGetIdx >= 0) {
                startIdx += END_B.length();
            }
        }

        if (startIdx + 1 - START_B.length() > 0 && startIdx + 1 < originString.length()) {
            Debug(" aTagReplacePosition  " + originString.substring(startIdx + 1 - START_B.length(), startIdx + 1));
            if (originString.substring(startIdx - START_B.length() + 1, startIdx + 1).equals(START_B)) {
                startIdx = startIdx - START_B.length();
            }
        }

        try {
            Debug(" charAt  " + originString.charAt(startIdx));
        } catch (StringIndexOutOfBoundsException e) {
            Debug("e  : " + e);
        }

        return startIdx;
    }

    private boolean isBoldStyle(int idx) {
        idx = idx - 1;
        if (idx < 0) {
            return false;
        }
        if (idx > originString.length()) {
            return false;
        }

        Debug(" idx " + idx);
        Debug(" charAt  " + originString.charAt(idx) + "\n\n");
        Debug(" ============================================");

        String aTagReplacePosition = originString.substring(0, idx);

        int linkOffset = 0;
        int linkGetIdx = aTagReplacePosition.indexOf(START_B, linkOffset);

        boolean isBoldStyle = false;
        while (linkGetIdx >= 0) {
            isBoldStyle = true;
            linkOffset = linkGetIdx + START_B.length();
            Debug(" linkOffset " + linkOffset);
            if (aTagReplacePosition.indexOf(END_B, linkOffset) >= 0) {
                Debug(" linkGetIdx  END_B " + (aTagReplacePosition.indexOf(END_B, linkOffset)));
                linkOffset = (aTagReplacePosition.indexOf(END_B, linkOffset)) + END_B.length();
                isBoldStyle = false;
            }
            Debug(" linkOffset " + linkOffset);
            Debug(" -------------------------------------------------");
            linkGetIdx = aTagReplacePosition.indexOf(START_B, linkOffset);
        }

        if (isBoldStyle) {
            linkGetIdx = aTagReplacePosition.indexOf(END_B, linkOffset);
            if (linkGetIdx >= 0) {
                isBoldStyle = false;
            }
        }

        return isBoldStyle;

    }

    private void boldChange(boolean boldState, int start, int end) {
        if (boldState) {  // bold 버튼을 켰을 경우

        } else {          // bold 버튼을 껐을 경우

        }
    }

    private int getHtmlIdx(String str, int idx) {
        int result = 0;

        int copyStartIdx = idx;
        int linkOffset = 0;
        int linkGetIdx = str.indexOf(START_B, linkOffset);
        while (linkGetIdx >= 0) {
            copyStartIdx -= START_B.length();
            linkOffset = linkGetIdx + START_B.length();
            linkGetIdx = str.indexOf(START_B, linkOffset);
        }
        linkOffset = 0;
        linkGetIdx = str.indexOf(END_B, linkOffset);
        while (linkGetIdx >= 0) {
            copyStartIdx -= END_B.length();
            linkOffset = linkGetIdx + END_B.length();
            linkGetIdx = str.indexOf(END_B, linkOffset);
        }
        return result;
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
            if (s.toString().equals(htmlText(originString).toString())) {
                return;
            }

            Debug(" s " + s);
            Debug(" start " + start);
            Debug(" before " + before);
            Debug(" count " + count);

            MainActivity.this.text = s;
            MainActivity.this.start = start;
            MainActivity.this.before = before;
            MainActivity.this.count = count;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!oldOriginString.equals(originString)) {
                setText();

                oldOriginString = originString;
                et.setText(htmlText(originString));
                et.setSelection(start + count);
            }
        }

    };

    private void setText() {
        int _start = getOriginIdx(start);
        int _end = getOriginIdx(start + before);

        String beforeStr = originString.substring(_start, _end);
        String afterStr = text.toString().substring(start, start + count);

        Debug(" substring BEFORE  : : " + text.toString().substring(start, start + before));
        Debug(" substring AFTER : : " + afterStr);
        Debug(" Origin substring BEFORE : : " + beforeStr);


        String frontStr = originString.substring(0, _start);
        String backStr = originString.substring(_end, originString.length());

        originString = frontStr + checkBoldContains(beforeStr, afterStr) + backStr;
    }

    private String checkBoldContains(String beforeStr, String afterStr) {
        String after = afterStr;

        return after;
    }

    private String result(Spanned text) {
        String result = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.toHtml(text, TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
        } else {
            result = Html.toHtml(text);
        }
        Debug(" result 1   " + result);

        result = result.replace(START_B, UNI1).replace(END_B, UNI2);
        Spanned _result = Html.fromHtml(result);
        result = _result.toString().replace(UNI1, START_BOLD).replace(UNI2, END_BOLD);

        return result;
    }

    private Spanned htmlText(String str) {

        str = str.replace(START_BOLD, START_B).replace(END_BOLD, END_B);
        originString = str;
        str = str.replace("\n", "<br>");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(str);
        }
    }
}
