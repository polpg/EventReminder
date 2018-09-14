package cat.polpadilla.eventreminder.calendarDecorators;

import android.content.Context;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import androidx.core.content.ContextCompat;

public class DisabledDecorator implements DayViewDecorator {

    private Context context;
    private int color;

    public DisabledDecorator (Context context, int color){
        this.context=context;
        this.color=color;
    }
    @Override
    public boolean shouldDecorate(CalendarDay calendarDay) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(context, color)));
        view.setDaysDisabled(true);
    }
}
