package com.polpg.eventreminder.calendarDecorators;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import androidx.core.content.ContextCompat;
import com.polpg.eventreminder.R;

public class SelectorDecorator implements DayViewDecorator {

    private final Drawable drawable;

    public SelectorDecorator(Context context){
        drawable = ContextCompat.getDrawable(context, R.drawable.calendar_selector);
    }
    @Override
    public boolean shouldDecorate(CalendarDay calendarDay) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }
}
