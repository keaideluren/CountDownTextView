package com.example.countdowntextviewlib;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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
    private ColorStateList beforeTextColor, afterTextColor;
    private int day,hour,minute,seconds,currentSeconds;
    private long totalSeconds;
    private Observable<CharSequence> longObservable;
    private Observer<CharSequence> observer;
    private Disposable disposable;
    private OnDiDaListener mOnDidaListener;
    private boolean isActive = false;

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
        maxTime = a.getInt(R.styleable.CountDownTextView_maxTime,2);//小时
        beforeTextColor = a.getColorStateList(R.styleable.CountDownTextView_beforeTextColor);
        afterTextColor = a.getColorStateList(R.styleable.CountDownTextView_afterTextColor);
        beforeTextSize = a.getDimension(R.styleable.CountDownTextView_beforeTextSize,getTextSize());
        afterTextSize = a.getDimension(R.styleable.CountDownTextView_afterTextSize,getTextSize());
        a.recycle();
    }

    /**
     * @param mOnDidaListener 一秒回调一次
     */
    public void setmOnDidaListener(OnDiDaListener mOnDidaListener) {
        this.mOnDidaListener = mOnDidaListener;
    }

    /**
     * @param currentSeconds 设置倒计时秒数
     */
    public void setCurrentSeconds(int currentSeconds) {
        totalSeconds = currentSeconds + totalSeconds - this.currentSeconds + 1;
        this.currentSeconds = currentSeconds;
    }

    /**
     * @return 是否正在倒计时
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * 开始倒计时
     */
    public void startCountDown(long totalSeconds) {
        this.totalSeconds = totalSeconds;
        currentSeconds = (int) totalSeconds;
        if (isActive) {
            setCurrentSeconds(currentSeconds);
            return;
        }
        observer = new Observer<CharSequence>() {

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
                isActive=true;
            }

            @Override
            public void onNext(CharSequence value) {
                if (mOnDidaListener != null) {
                    mOnDidaListener.onOneSeconds(day,hour,minute,seconds,currentSeconds,value.toString());
                }
                setText(value);
                if (currentSeconds <= 0) {
                    disposable.dispose();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("CountDownTextView", "onError");
            }

            @Override
            public void onComplete() {
                if (mOnDidaListener != null) {
                    mOnDidaListener.onOneSeconds(day,hour,minute,seconds,currentSeconds,timeToString(0));
                }
                setText(setTextAppearance(timeToString(0)));
            }
        };

        getCountDownObservalbe().subscribe(observer);
    }

    /**
     * 停止倒计时
     */
    public void stopCountDown() {
        if (disposable!=null) {
            disposable.dispose();
        }
    }

    private Observable<CharSequence> getCountDownObservalbe() {
        longObservable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                       //正计时转化为倒计时
                        if (totalSeconds - aLong <= 0) {
                        }
                        return totalSeconds - aLong;
                    }
                })
                .map(new Function<Long, String>() {
                    @Override
                    public String apply(Long seconds) throws Exception {
                        //秒数转化为时间字符串
                        return timeToString(seconds);
                    }
                })
                .map(new Function<String, CharSequence>() {
                    @Override
                    public CharSequence apply(String s) throws Exception {
                        //字符串拼接，设置颜色字体什么的
                        return setTextAppearance(s);
                    }
                })
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        isActive=false;
                    }
                })
//                    .take(totalSeconds)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());

        return longObservable;
    }

    /**
     * @return 根据Xml设置文字的外观
     */
    private CharSequence setTextAppearance(String countDownString) {
        TextAppearanceSpan beforeSpan = new TextAppearanceSpan(textBefore, android.graphics.Typeface.NORMAL
                , (int) beforeTextSize, beforeTextColor, getLinkTextColors());
        TextAppearanceSpan afterSpan = new TextAppearanceSpan(textBefore, android.graphics.Typeface.NORMAL
                , (int) afterTextSize, afterTextColor, getLinkTextColors());
        TextAppearanceSpan countDownSpan = new TextAppearanceSpan(countDownString, android.graphics.Typeface.NORMAL
                , (int) getTextSize(), getTextColors(), getLinkTextColors());
        SpannableString ss = new SpannableString(textBefore + countDownString + textAfter);
        ss.setSpan(beforeSpan,0,textBefore.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(countDownSpan,textBefore.length(),textBefore.length()+countDownString.length()
                ,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(afterSpan,textBefore.length()+countDownString.length()
                ,textBefore.length()+countDownString.length()+textAfter.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    /**
     * @param second 倒计时秒数
     * @return 根据倒计生成的字符串
     */
    private String timeToString(long second) {
        second = second < 0 ? 0 : second;
        currentSeconds = (int) second;
        StringBuilder result = new StringBuilder();

        //最大计时级别，0-3 秒-天
        switch (maxTime) {
            case 3:
                day = (int) (second / 86400);
                second %= 86400;
                if (day > 0 || isShowHighOrderZero) {
                    result.append(day).append("天");
                }
            case 2:
                hour = (int) (second / 3600);
                second %= 3600;
                if (isShowHighOrderZero) {
                    if (isFillLength && hour < 10) {
                        result.append(0);
                    }
                    result.append(hour).append(":");
                } else if (day > 0 || hour > 0) {
                    if (isFillLength && hour < 10) {
                        result.append(0);
                    }
                    result.append(hour).append(":");
                }
            case 1:
                minute = (int) (second / 60);
                second %= 60;
                if (isShowHighOrderZero) {
                    if (isFillLength && minute < 10) {
                        result.append(0);
                    }
                    result.append(minute).append(":");
                } else if (day > 0 || hour > 0 || minute > 0) {
                    if (isFillLength && minute < 10) {
                        result.append(0);
                    }
                    result.append(minute).append(":");
                }
            case 0:
                seconds = (int) second;
                if (isFillLength && second < 10) {
                    result.append(0);
                }
                result.append(second);
        }

        return result.toString();
    }

    public interface OnDiDaListener{
        void onOneSeconds(int day,int hour,int minute,int seconds,int totalSeconds,String timeString);
    }
}
