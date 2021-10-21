package com.example.AcousticApp_final.Pager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.AcousticApp_final.App.MyApp;

/**
 * @author: wzt
 * @date: 2020/11/19
 */
public class MyViewPager extends ViewPager {
    public boolean touchFlag=true;
    public MyViewPager(@NonNull Context context) {
        super(context);
        MyApp.myViewPager=this;
    }
    public MyViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);MyApp.myViewPager=this;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
        //return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(!touchFlag)
            return false;
        return super.onTouchEvent(ev);
    }
}
