package com.kkang.editorbold;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mr5.icarus.Callback;
import com.github.mr5.icarus.Icarus;
import com.github.mr5.icarus.TextViewToolbar;
import com.github.mr5.icarus.button.TextViewButton;
import com.github.mr5.icarus.entity.Options;

import org.json.JSONException;
import org.json.JSONObject;

import static android.text.Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE;
import static com.github.mr5.icarus.button.Button.NAME_BOLD;


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


    TextView tvBold;
    TextView tvState;
    TextView tvLeft;
    TextView tvCenter;
    TextView tvRight;

    WebView editor;

    Icarus icarus;
    TextViewButton boldButton;


    boolean isBold = false;

    final String START_B = "<b>";
    final String END_B = "</b>";

    final String START_BOLD = "<bold>";
    final String END_BOLD = "</bold>";

    final String UNI1 = "\u0182";
    final String UNI2 = "\u0183";
    String originString = "사가다자바하라아나파차타 " + START_BOLD + "비지디기시히리이니미키\n티치 " + END_BOLD + "삼성화재 앱에서 휴대폰번호를 버튼을 눌러주세요." + START_BOLD + "본인인증을 진행해주세요." + END_BOLD +
            "인증번호가 유형을 선택 후 필요서류\n" + START_BOLD + "를 등록하시면 보험금 청구" + "\n가 완료됩니다" + END_BOLD + "\n\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUI();
        setView();
        setEvent();
    }

    private void setUI() {
        editor = (WebView) findViewById(R.id.editor);

        tvBold = (TextView) findViewById(R.id.tvBold);
        tvState = (TextView) findViewById(R.id.tvState);
        tvLeft = (TextView) findViewById(R.id.tvLeft);
        tvCenter = (TextView) findViewById(R.id.tvCenter);
        tvRight = (TextView) findViewById(R.id.tvRight);
    }

    private void setView() {

        TextViewToolbar toolbar = new TextViewToolbar();
        Options options = new Options();
        options.setPlaceholder("Placeholder...");
        icarus = new Icarus(toolbar, options, editor);

        boldButton = new TextViewButton(tvBold, icarus);
        boldButton.setName(NAME_BOLD);
        toolbar.addButton(boldButton);
        icarus.loadCSS("file:///android_asset/editor.css");
        icarus.loadJs("file:///android_asset/test.js");
        icarus.render();

        EditText et = new EditText(MainActivity.this);
        et.setText(htmlText(originString));

        icarus.setContent(result2(et.getText()));

    }

    private void setEvent() {
        editor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isBold == boldButton.isActivated()) {
                    return false;
                }
                isBold = boldButton.isActivated();
                Log.d("content", "isBold : : " + isBold);
                //Toast.makeText(MainActivity.this, ""+boldButton.isActivated(), Toast.LENGTH_SHORT ).show();

                return false;
            }
        });
        tvState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icarus.getContent(new Callback() {
                    @Override
                    public void run(String params) {
                        Log.d("content", params);
                        try {
                            JSONObject jo = new JSONObject(params);
                            String gg = jo.optString("content");
                            Log.d("result", result(gg));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (icarus == null) {
                    return;
                }
                icarus.getContent(new Callback() {
                    @Override
                    public void run(String params) {
                        Log.d("content", params);
                        try {
                            JSONObject jo = new JSONObject(params);
                            String gg = jo.optString("content");
                            String ss = gg.replace("<p>", "<p style=\"text-align: left;\">");
                            ss = ss.replace("<p style=\"text-align: right;\">", "<p style=\"text-align: left;\">");
                            ss = ss.replace("<p style=\"text-align: center;\">", "<p style=\"text-align: left;\">");
                            icarus.setContent(ss);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        tvCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icarus.getContent(new Callback() {
                    @Override
                    public void run(String params) {
                        Log.d("content", params);
                        try {
                            JSONObject jo = new JSONObject(params);
                            String gg = jo.optString("content");
                            String ss = gg.replace("<p>", "<p style=\"text-align: center;\">");
                            ss = ss.replace("<p style=\"text-align: right;\">", "<p style=\"text-align: center;\">");
                            ss = ss.replace("<p style=\"text-align: left;\">", "<p style=\"text-align: center;\">");
                            icarus.setContent(ss);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (icarus == null) {
                    return;
                }
                icarus.getContent(new Callback() {
                    @Override
                    public void run(String params) {
                        Log.d("content", params);
                        try {
                            JSONObject jo = new JSONObject(params);
                            String gg = jo.optString("content");
                            String ss = gg.replace("<p>", "<p style=\"text-align: right;\">");
                            ss = ss.replace("<p style=\"text-align: left;\">", "<p style=\"text-align: right;\">");
                            ss = ss.replace("<p style=\"text-align: center;\">", "<p style=\"text-align: right;\">");
                            icarus.setContent(ss);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    private String result(String str) {
        //Spanned text = (Spanned) str;
        EditText et = new EditText(MainActivity.this);
        et.setText(htmlText(str));
        Spanned text = et.getText();
        String result = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.toHtml(text, TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
        } else {
            result = Html.toHtml(text);
        }
        result = result.replace(START_B, UNI1).replace(END_B, UNI2);
        Spanned _result = Html.fromHtml(result);
        result = _result.toString().replace(UNI1, START_BOLD).replace(UNI2, END_BOLD);


        return result;
    }

    private String result2(Spanned text) {
        String result = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.toHtml(text, TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
        } else {
            result = Html.toHtml(text);
        }
        //result = result.replace(START_B, UNI1).replace(END_B, UNI2);
        //Spanned _result = Html.fromHtml(result);
        //result = _result.toString().replace(UNI1, START_BOLD).replace(UNI2, END_BOLD);


        return result;
    }

    private Spanned htmlText(String str) {

        str = str.replace(START_BOLD, START_B).replace(END_BOLD, END_B);
        str = str.replace("\n", "<br>");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(str);
        }
    }
}
