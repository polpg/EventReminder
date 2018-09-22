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
    private final Drawable background;

    public SelectorDecorator(Context context){
        this.context = context;
        background = context.getResources().getDrawable(R.drawable.calendar_selector);
    }
    @Override
    public boolean shouldDecorate(CalendarDay calendarDay) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(background);
        view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimaryText)));
    }
}
