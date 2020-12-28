package com.jjs.zero.chartlibrary.chart;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;


/**
 * Created by shawpaul on 2019/2/22
 */
public class TimesSelector {

    public enum Times {
        SEVEN(7), FOURTEEN(14);
        public int times;

        Times(int times) {
            this.times = times;
        }
    }

    private Times mCurrentTimes = Times.SEVEN;
    private OnTimesSelectedListener mOnTimesSelectedListener;

    public TimesSelector(View view) {
//        AppCompatSpinner spinner = view.findViewById(R.id.spinner);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Times times = position == 0 ? Times.SEVEN : Times.FOURTEEN;
//                if (mCurrentTimes != times) {
//                    mCurrentTimes = times;
//                    if (mOnTimesSelectedListener != null) {
//                        mOnTimesSelectedListener.onTimesSelected(mCurrentTimes);
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }

    public Times getTimes() {
        return mCurrentTimes;
    }

    public interface OnTimesSelectedListener {
        void onTimesSelected(Times currentTimes);
    }

    public void setOnTimesSelectedListener(OnTimesSelectedListener onTimesSelectedListener) {
        mOnTimesSelectedListener = onTimesSelectedListener;
    }

}
