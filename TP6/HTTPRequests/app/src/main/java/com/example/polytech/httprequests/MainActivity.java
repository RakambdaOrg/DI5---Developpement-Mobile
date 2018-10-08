package com.example.polytech.httprequests;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Pattern p = Pattern.compile("^((\\d{1,3}\\.){3}\\d{1,3})?$");
        setContentView(R.layout.activity_main);

        final TextView ip = findViewById(R.id.ipText);
        final TextView tv = findViewById(R.id.requestResult);
        tv.setMovementMethod(new ScrollingMovementMethod());

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, android.text.Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
                    if (!resultingTxt.matches ("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (String split : splits) {
                            if (Integer.valueOf(split) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }
        };
        ip.setFilters(filters);

        findViewById(R.id.startRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipt = ip.getText().toString();
                if(p.matcher(ipt).matches())
                {
                    AsyncRequest ar = new AsyncRequest(tv);
                    ar.execute("http://ip-api.com/json/"+ ipt);
                }
                else
                {
                    tv.setBackgroundColor(Color.rgb(255, 20, 147));
                    tv.setText("Nope");
                    Toast.makeText(v.getContext(), "Merci de donner une IP VALIDE", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
