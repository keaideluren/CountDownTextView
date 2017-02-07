package com.example.countdowntextviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/2/7.
 * 倒计时控件
 */

public class CountDownTextView extends TextView {

    private boolean isFillLength;//是否填充0 比如 ： true,还剩08秒  false还剩8秒，默认填充
    private boolean isShowHighOrderZero;//高位减到零是否继续显示，比如 true还剩00分08秒 ，false还剩08秒，默认显示
    private String textAfter;//倒计时前的文字
    private String textBefore;//倒计时后的文字
    private int maxTime;//最大的时间级别
    private float beforeTextSize,afterTextSize;
    private int beforeTextColor,afterTextColor,totalSeconds;

    public CountDownTextView(Context context) {
        super(context);
    }

    public CountDownTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CountDownTextView, 0, 0);
        isFillLength = a.getBoolean(R.styleable.CountDownTextView_isFillLength, true);
        isShowHighOrderZero = a.getBoolean(R.styleable.CountDownTextView_isShowHighOrderZero, true);
        textAfter =  a.getString(R.styleable.CountDownTextView_textAfter);
        textBefore = a.getString(R.styleable.CountDownTextView_textBefore);
        maxTime = a.getInt(R.styleable.CountDownTextView_maxTime,0);
        beforeTextColor = a.getColor(R.styleable.CountDownTextView_beforeTextColor,getCurrentTextColor());
        afterTextColor = a.getColor(R.styleable.CountDownTextView_afterTextColor,getCurrentTextColor());
        beforeTextSize = a.getDimension(R.styleable.CountDownTextView_beforeTextSize,getTextSize());
        afterTextSize = a.getDimension(R.styleable.CountDownTextView_afterTextSize,getTextSize());
        a.recycle();
    }

    /**
     * 开始倒计时
     */
    public void startCountDown() {
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void setCountTimers(int totalSeconds) {

    }

    public void setCountTimers(int year, int month, int day, int hour, int minute, int seconds) {

    }

    public void stopCountDown() {

    }

}
