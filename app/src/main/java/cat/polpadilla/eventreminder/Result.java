package cat.polpadilla.eventreminder;

import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.List;

public abstract class Result {

    @AutoValue
    public static abstract class Added extends Result {
        public abstract EventModel model();
    }

    @AutoValue
    public static abstract class Modified extends Result {
        public abstract EventModel model();
    }

    @AutoValue
    public static abstract class Deleted extends Result {
        public abstract EventModel model();
    }

    @AutoValue
    public static abstract class Showed extends Result {
        public abstract EventModel current();
    }

    @AutoValue
    public static abstract class Loaded extends Result {
        public abstract List<EventModel> models();
    }

    public static Result added(EventModel model) {
        return new AutoValue_Result_Added(model);
    }

    public static Result modified(EventModel model) {
        return new AutoValue_Result_Modified(model);
    }

    public static Result deleted(EventModel model) {
        return new AutoValue_Result_Deleted(model);
    }

    public static Result showed(EventModel current) {
        return new AutoValue_Result_Showed(current);
    }

    public static Result loaded(List<EventModel> models) {
        return new AutoValue_Result_Loaded(Collections.unmodifiableList(models));
    }
}
