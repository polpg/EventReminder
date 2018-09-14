package cat.polpadilla.eventreminder;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AutoValue
public abstract class ViewState {
    public abstract List<EventModel> items();
    @Nullable public abstract EventModel current();
    public abstract boolean isLoaded();

    static Builder builder() {
        return new AutoValue_ViewState.Builder().isLoaded(false);
    }

    @AutoValue.Builder
    abstract static class Builder {
        abstract Builder items(List<EventModel> items);
        abstract Builder current(EventModel current);
        abstract Builder isLoaded(boolean isLoaded);
        abstract ViewState build();
    }

    static Builder empty() {
        return builder().items(Collections.unmodifiableList(new ArrayList<>()));
    }

    Builder toBuilder() {
        return builder().items(items()).current(current()).isLoaded(isLoaded());
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


}