package cat.polpadilla.eventreminder;

import com.google.auto.value.AutoValue;

public abstract class Action {
    @AutoValue
    public static abstract class Add extends Action{
        public abstract EventModel model();
    }

    @AutoValue
    public static abstract class Edit extends Action{
        public abstract EventModel model();
    }

    @AutoValue
    public static abstract class Delete extends Action{
        public abstract EventModel model();
    }

    @AutoValue
    static abstract class Show extends Action{
        public abstract EventModel current();
    }

    public static class Load extends Action{
    }

    public static Action add(EventModel model) {
        return new AutoValue_Action_Add(model);
    }
    public static Action edit(EventModel model) {
        return new AutoValue_Action_Edit(model);
    }
    public static Action delete(EventModel model) {
        return new AutoValue_Action_Delete(model);
    }
    public static Action show(EventModel model) {
        return new AutoValue_Action_Show(model);
    }
    public static Action load() {
        return new Action.Load();
    }
}
