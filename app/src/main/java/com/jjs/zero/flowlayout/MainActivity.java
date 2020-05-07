package com.jjs.zero.flowlayout;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jjs.zero.ecgchart.EcgChart;
import com.jjs.zero.flowlibrary.FlowLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final List<String> arryList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            arryList.add("test"+i);
        }

        FlowLayout layout = findViewById(R.id.flow_layout);

        layout.addView(arryList, new FlowLayout.OnChildView() {
            @Override
            public TextView getChildView(final int i) {
                TextView text = new TextView(MainActivity.this);
                text.setTextColor(Color.parseColor("#FF0000"));
                text.setBackgroundResource(R.color.colorPrimary);
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("zero","点击了："+i+" name:"+arryList.get(i));
                        v.setBackgroundResource(R.color.colorAccent);
                    }
                });
                return text;
            }
        });

        List<Float> daa = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            double random;
            if (i%2 == 0) {
                random = Math.random();
            } else {
                random = -Math.random();
            }

            float value = (float) (100 * random);
            daa.add(value);
        }

        EcgChart c = findViewById(R.id.ecg_chart);
        c.setData(daa);
    }

}
