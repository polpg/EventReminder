package cat.polpadilla.eventreminder.calendarDecorators;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import androidx.core.content.ContextCompat;


public class TodayDecorator implements DayViewDecorator {
    private Context context;
    private int color;

    public TodayDecorator(Context context, int color){
        this.context = context;
        this.color = color;
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        if (day.equals(CalendarDay.today())) return true;
        else return false;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new RelativeSizeSpan(1.1f));
        view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(context, color)));
    }
}
