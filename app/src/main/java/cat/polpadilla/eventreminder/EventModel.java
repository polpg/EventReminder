package cat.polpadilla.eventreminder;

import com.google.auto.value.AutoValue;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import androidx.annotation.Nullable;

@AutoValue
public abstract class EventModel {
    public abstract String id();
    public abstract String title();
    @Nullable public abstract String description();
    public abstract Calendar dueDate();

    static Builder builder() {
        return new AutoValue_EventModel.Builder();
    }
    @AutoValue.Builder
    public abstract static class Builder{
        abstract Builder setId(String id);
        public abstract Builder setTitle(String title);
        public abstract Builder setDescription(String desc);
        public abstract Builder setDueDate(Calendar dueDate);
        public abstract EventModel build();
    }

    public static Builder creator(){
        return builder()
                .setId(UUID.randomUUID().toString())
                .setDueDate(Calendar.getInstance());

    }

    public Builder toBuilder(){
        return builder()
                .setId(id())
                .setTitle(title())
                .setDescription(description())
                .setDueDate(dueDate());
    }

    static final Comparator<EventModel> SORT_BY_DATE =
            (one,two) -> (one.dueDate().compareTo(two.dueDate()));

    public static List<EventModel> filter(List<EventModel> models, LocalDate selectedDate){
        List<EventModel> result;

        if (selectedDate!=null){
            result=new ArrayList<>();

            for (EventModel model : models){
                LocalDate modelDate = TypeTransmogrifier.dateFromCalendar(model.dueDate());
                if (modelDate.equals(selectedDate)){
                    result.add(model);
                }
            }
        } else {
            result=new ArrayList<>(models);
        }
        return result;
    }
}
