package com.jjs.zero.flowlayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jjs.zero.chartlibrary.chart.EcgReView;
import com.jjs.zero.chartlibrary.chart.HRVChart;
import com.jjs.zero.chartlibrary.chart.ManyRangeChart;
import com.jjs.zero.chartlibrary.chart.ManyRangeManyValueChart;
import com.jjs.zero.chartlibrary.chart.ManyValueBpmChart;
import com.jjs.zero.chartlibrary.chart.ManyValueChart;
import com.jjs.zero.chartlibrary.chart.NormalRangeChart;
import com.jjs.zero.chartlibrary.chart.ScatterDiagramChart;
import com.jjs.zero.chartlibrary.chart.ValueAndDate;
import com.jjs.zero.ecgchart.EcgChart;
import com.jjs.zero.ecgchart.LineChart;
import com.jjs.zero.flowlibrary.FlowLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private  NormalRangeChart mNormalRangeChart;
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

        LineChart chart = findViewById(R.id.ecg_chartTwo);
        List<String> yList=new ArrayList<>();
//        yList.add("60%");
//        yList.add("40%");
//        yList.add("20%");
//        yList.add("0.0%");
        yList.add("很好");
        yList.add("还行");
        yList.add("一般");
        yList.add("很差");
        List<String> xList=getCurrentWeekDay();
        chart.setScaleData(xList,yList);
//        this.getExternalFilesDir();
        File file = new File(Environment.getExternalStorageDirectory(),"DDD");
        if (!file.exists()) {
            file.mkdir();

        }
        file.delete();
        Log.i("zero","====================filePath:"+ Environment.getDownloadCacheDirectory());


        mNormalRangeChart = findViewById(R.id.normalRangeChart);
//        mNormalRangeChart.setNormalValueRange(6.1f, 3.9f, "空腹血糖正常范围");

        List<ValueAndDate> kongfu = new ArrayList<>();

        ValueAndDate date = new ValueAndDate(4.5f,"4.5",new Date());
        ValueAndDate date2 = new ValueAndDate(4.8f,"4.8",new Date());
        ValueAndDate date3 = new ValueAndDate(5.3f,"5.3",new Date());
        ValueAndDate date4 = new ValueAndDate(3.5f,"3.5",new Date());

        kongfu.add(date);
        kongfu.add(date2);
        kongfu.add(date3);
        kongfu.add(date4);
        mNormalRangeChart.setValueAndDates(kongfu);

        ManyRangeChart manyRangeChart = findViewById(R.id.manyRangeChart);

        manyRangeChart.setRange(new ManyRangeChart.Ranges(this, 100f, 94f, R.drawable.shape_glu_normal_range, "正常范围", "#00CC29"),
                new ManyRangeChart.Ranges(this, 94f, 90f, R.drawable.shape_spo2_90_94_range, "供血不足", "#A0C600"),
                new ManyRangeChart.Ranges(this, 90f, 80f, R.drawable.shape_spo2_80_90_range, "低氧血症", "#FFAA00"),
                new ManyRangeChart.Ranges(this, 80f, 0f, R.drawable.shape_spo2_0_80_range, "重度缺氧", "#FF4600"));
        List<ValueAndDate> values = new ArrayList<>();

        ValueAndDate value1 = new ValueAndDate(98f,"98",new Date());
        ValueAndDate value2 = new ValueAndDate(93f,"93",new Date());
        ValueAndDate value3 = new ValueAndDate(90f,"90",new Date());
        ValueAndDate value4 = new ValueAndDate(88f,"88",new Date());
        ValueAndDate value5 = new ValueAndDate(89f,"89",new Date());
        ValueAndDate value6 = new ValueAndDate(98f,"98",new Date());
        ValueAndDate value7 = new ValueAndDate(82f,"82",new Date());

        values.add(value1);
        values.add(value2);
        values.add(value3);
        values.add(value4);
        values.add(value5);
        values.add(value6);
        values.add(value7);
        manyRangeChart.setValueAndDates(values);


        ManyRangeManyValueChart manyRangeManyValueChart = findViewById(R.id.normalRangeManyValueChart);
        manyRangeManyValueChart.setRange(new ManyRangeManyValueChart.ManyValueRanges(this, 140f, 90f, R.drawable.shape_bl_dp, "收缩压正常范围", "#00C7C1", "#00C7C1"),
                new ManyRangeManyValueChart.ManyValueRanges(this, 90f, 60f, R.drawable.shape_bl_sp, "舒张压正常范围", "#56C96D", "#56C53B")
        );
         List<ValueAndDate> mSys = new ArrayList<>();//收缩压
         List<ValueAndDate> mDia = new ArrayList<>();//舒张压
        ValueAndDate mSys1 = new ValueAndDate(98f,"98",new Date());
        ValueAndDate mSys2 = new ValueAndDate(93f,"93",new Date());
        ValueAndDate mSys3 = new ValueAndDate(90f,"90",new Date());
        ValueAndDate mSys4 = new ValueAndDate(88f,"88",new Date());
        ValueAndDate mSys5 = new ValueAndDate(89f,"89",new Date());
        ValueAndDate mSys6 = new ValueAndDate(98f,"98",new Date());
        ValueAndDate mSys7 = new ValueAndDate(82f,"82",new Date());

        mSys.add(mSys1);
        mSys.add(mSys2);
        mSys.add(mSys3);
        mSys.add(mSys4);
        mSys.add(mSys5);
        mSys.add(mSys6);
        mSys.add(mSys7);

        ValueAndDate mDia1 = new ValueAndDate(78f,"78",new Date());
        ValueAndDate mDia2 = new ValueAndDate(73f,"73",new Date());
        ValueAndDate mDia3 = new ValueAndDate(70f,"70",new Date());
        ValueAndDate mDia4 = new ValueAndDate(78f,"78",new Date());
        ValueAndDate mDia5 = new ValueAndDate(79f,"79",new Date());
        ValueAndDate mDia6 = new ValueAndDate(78f,"78",new Date());
        ValueAndDate mDia7 = new ValueAndDate(72f,"72",new Date());

        mDia.add(mDia1);
        mDia.add(mDia2);
        mDia.add(mDia3);
        mDia.add(mDia4);
        mDia.add(mDia5);
        mDia.add(mDia6);
        mDia.add(mDia7);

        manyRangeManyValueChart.setValuesAndDates(mSys,mDia);


        ManyValueChart manyValueChart = findViewById(R.id.manyValueChart);
        List<String> title = new ArrayList<>();
//        title.add("60%");
//        title.add("40%");
//        title.add("20%");
//        title.add("0%");
//        title.add("很差");
//        title.add("一般");
//        title.add("还行");
//        title.add("很好");
        title.add("低风险");
        title.add("中风险");
        title.add("高风险");
        title.add("极高风险");
        manyValueChart.setYTitles(title);
        manyValueChart.setRange(new ManyValueChart.ManyValueRanges(60,0,"#FFC43A"),
                new ManyValueChart.ManyValueRanges(60,0,"#FF6A6A"));


        List<ValueAndDate> values3 = new ArrayList<>();

        ValueAndDate value11 = new ValueAndDate(48f,"48",new Date(1558245210000l));
        ValueAndDate value21= new ValueAndDate(53f,"53",new Date());
        ValueAndDate value31 = new ValueAndDate(20f,"20",new Date());
        ValueAndDate value41 = new ValueAndDate(48f,"48",new Date());
        ValueAndDate value51 = new ValueAndDate(39f,"39",new Date());
        ValueAndDate value61 = new ValueAndDate(20f,"28",new Date());
        ValueAndDate value71 = new ValueAndDate(12f,"12",new Date());

        values3.add(value11);
        values3.add(value21);
        values3.add(value31);
        values3.add(value41);
        values3.add(value51);
        values3.add(value61);
        values3.add(value71);



        List<ValueAndDate> mSysT = new ArrayList<>();//收缩压
        ValueAndDate mSys11 = new ValueAndDate(38f,"38",new Date(1558245210000l));
        ValueAndDate mSys21 = new ValueAndDate(33f,"33",new Date());
        ValueAndDate mSys31 = new ValueAndDate(30f,"30",new Date());
        ValueAndDate mSys41 = new ValueAndDate(38f,"38",new Date());
        ValueAndDate mSys51 = new ValueAndDate(49f,"49",new Date());
        ValueAndDate mSys61 = new ValueAndDate(48f,"48",new Date());
        ValueAndDate mSys71 = new ValueAndDate(42f,"42",new Date());

        mSysT.add(mSys11);
        mSysT.add(mSys21);
        mSysT.add(mSys31);
        mSysT.add(mSys41);
        mSysT.add(mSys51);
        mSysT.add(mSys61);
        mSysT.add(mSys71);

        manyValueChart.setValuesAndDates(values3,mSysT);


        HRVChart hrvChart = findViewById(R.id.hrv_chart);
        title.clear();
        title.add("50");
        title.add("65");
//        title.add("70");
        title.add("80");
        title.add("95");
        title.add("110");
        title.add("125");
        title.add("140");
        hrvChart.setYTitles(title);
        hrvChart.setYTitleUnit("bpm");
        hrvChart.setRange(new HRVChart.HRVRanges("#FFC43A")
                , new HRVChart.HRVRanges("#FF6A6A")
        );
        List<Integer> valueIn = new ArrayList<>();
        List<Integer> valueIn2 = new ArrayList<>();
        valueIn.add(88);
//        valueIn.add(53);
//        valueIn.add(70);
//        valueIn.add(120);
//        valueIn.add(79);
//        valueIn.add(110);
//        valueIn.add(93);
//        valueIn.add(90);


        valueIn2.add(68);
//        valueIn2.add(93);
//        valueIn2.add(90);
//        valueIn2.add(88);
//        valueIn2.add(69);
//        valueIn2.add(148);
//        valueIn2.add(142);


        hrvChart.setValues(false,valueIn,valueIn2);


        ManyValueBpmChart manyValueBpmChart = findViewById(R.id.manyValueBpmChart);
        List<String> titleBpm = new ArrayList<>();
        titleBpm.add("0");
        titleBpm.add("50");
        titleBpm.add("90");
        titleBpm.add("140");
        manyValueBpmChart.setYTitles(titleBpm);
        manyValueBpmChart.setYUnit("bpm");
        List<String> xTitleBpm = new ArrayList<>();
        xTitleBpm.add("平均心率");
        xTitleBpm.add("最快心率");
        xTitleBpm.add("最慢心率");
        manyValueBpmChart.setXTitles(xTitleBpm);
        manyValueBpmChart.setRange(new ManyValueBpmChart.ManyValueRanges("#00C0C5","#FF00C0C5","#3300C0C5"),
                new ManyValueBpmChart.ManyValueRanges("#FE4E90","#FFFE4E90","#33FE4E90"),
                new ManyValueBpmChart.ManyValueRanges("#245FFF","#FF245FFF","#33245FFF")
        );

        manyValueBpmChart.setValues(0,0,0);


        ScatterDiagramChart scatterDiagramChart = findViewById(R.id.scatter_diagram_chart);
        scatterDiagramChart.setValue(540,530,463,600,569,529,673);


        EcgReView ecgReView = findViewById(R.id.ecg_review);

        long startTime = 1593396393000l;
        long endTime = 1593396753000l;
        int len = (int)(endTime-startTime)/1000/30;
        Log.i("zero==", "onCreate: timeLen:"+len);
        List<EcgReView.ReViewBean> list = new ArrayList<>();
        for (int i = 0; i < len; i++) {

            if (len>10) {
                if (i>6 && i<10) {
                    continue;
                }
            }

            double d = Math.random()*10+32;
            Log.i("zero==", "onCreate: temp:"+d);
            EcgReView.ReViewBean bean = new EcgReView.ReViewBean(startTime+i*30*1000,d);
            list.add(bean);
        }
        ecgReView.setTempList(list);
        List<EcgReView.ReViewBean> list1 = new ArrayList<>();
        List<Integer> list3 = new ArrayList<>();
        Integer num = 1;
        for (int i = 0; i <= len; i++) {
//            if (len > 8) {
//                if (i>4 && i<8) {
//                    continue;
//                }
//            }

            if (i>1 && i <=6) {
                num = 2;
            } else {
                num = 3;
            }
            list3.add(num);

            double d = Math.random()*200;
            Log.i("zero==", "onCreate: heartRate:"+d);
            EcgReView.ReViewBean bean = new EcgReView.ReViewBean(startTime+i*30*1000,d);
            list1.add(bean);
        }

        ecgReView.setHeartRateList(list1);
        ecgReView.setStatusList(list3);


//        Intent intent = new Intent("com.garea.home.ui.speak");
//        intent.putExtra("text","平稳坐下按图示做好准备");
//        sendBroadcast(intent); // 发送广播

//        goHuaweiSetting();
//
//        requestIgnoreBatteryOptimizations();

    }
    private void goHuaweiSetting() {
        try {
            showActivity("com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
        } catch (Exception e) {
            showActivity("com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.bootstart.BootStartActivity");
        }
    }
    /**
     * 跳转到指定应用的首页
     */
    private void showActivity(@NonNull String packageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        startActivity(intent);
    }

    /**
     * 跳转到指定应用的指定页面
     */
    private void showActivity(@NonNull String packageName, @NonNull String activityDir) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activityDir));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestIgnoreBatteryOptimizations() {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取最近一周的时间 MM-dd
     */
    private List<String> getCurrentWeekDay() {
        List<String> data = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        for (int i = 0; i < 1; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            data.add(sdf.format(calendar.getTime()));
        }
        return data;
    }
}
