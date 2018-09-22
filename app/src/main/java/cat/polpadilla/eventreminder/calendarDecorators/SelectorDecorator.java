package cat.polpadilla.eventreminder.calendarDecorators;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import androidx.core.content.ContextCompat;
import cat.polpadilla.eventreminder.R;

public class SelectorDecorator implements DayViewDecorator {

    private final Context context;
    private final Drawable drawable;

    public SelectorDecorator(Context context){
        this.context = context;
        drawable = ContextCompat.getDrawable(context, R.drawable.calendar_selector);
    }
    @Override
    public boolean shouldDecorate(CalendarDay calendarDay) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
        view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimaryText)));
    }
}
