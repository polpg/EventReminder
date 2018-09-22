package cat.polpadilla.eventreminder;

import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;

@AutoValue
public abstract class ViewState {
    public abstract List<EventModel> items();
    @Nullable public abstract EventModel current();
    public abstract boolean isLoaded();
    public abstract LocalDate selectedDate();

    static Builder builder() {
        return new AutoValue_ViewState.Builder()
                .isLoaded(false)
                .selectedDate(CalendarDay.today().getDate());
    }

    @AutoValue.Builder
    abstract static class Builder {
        abstract Builder items(List<EventModel> items);
        abstract Builder current(EventModel current);
        abstract Builder isLoaded(boolean isLoaded);
        abstract Builder selectedDate(LocalDate selectedDate);
        abstract ViewState build();
    }

    static Builder empty() {
        return builder().items(Collections.unmodifiableList(new ArrayList<>()));
    }

    Builder toBuilder() {
        return builder()
                .items(items())
                .current(current())
                .isLoaded(isLoaded())
                .selectedDate(selectedDate());
    }

    private void sort(List<EventModel> models){
        Collections.sort(models, EventModel.SORT_BY_DATE);
    }
    private int findPosition(List<EventModel> models, String id){
        for (int i=0;i<models.size();i++){
            EventModel candidate = models.get(i);

            if (id.equals(candidate.id())){
                return i;
            }
        }
        return -1;
    }
    private EventModel find(List<EventModel> models, String id){
        int position = findPosition(models, id);

        return position>=0 ? models.get(position) : null;
    }

    @Memoized
    public List<EventModel> filteredItems(){
        return EventModel.filter(items(), selectedDate());
    }

    ViewState add(EventModel model){
        List<EventModel> models=new ArrayList<>(items());

        models.add(model);
        sort(models);

        return toBuilder()
                .items(Collections.unmodifiableList(models))
                .current(model)
                .build();
    }

    ViewState modify(EventModel model){
        List<EventModel> models=new ArrayList<>(items());
        EventModel original = find(models, model.id());

        if (original!=null){
            int index=models.indexOf(original);
            models.set(index, model);
        }

        sort(models);

        return toBuilder()
                .items(Collections.unmodifiableList(models))
                .current(model)
                .build();
    }

    ViewState delete(EventModel model){
        List<EventModel> models=new ArrayList<>(items());
        EventModel original = find(models, model.id());

        if (original==null){
            throw new IllegalArgumentException("Cannot find model to delete: " + model.toString());
        } else {
            models.remove(original);
        }

        sort(models);

        return toBuilder()
                .items(Collections.unmodifiableList(models))
                .current(model)
                .build();
    }

    ViewState show (EventModel current){
        return toBuilder()
                .current(current)
                .build();
    }

    ViewState filter (LocalDate date){
        return toBuilder()
                .selectedDate(date)
                .build();
    }

}