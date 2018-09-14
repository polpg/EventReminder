package cat.polpadilla.eventreminder;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventRepository {
    private static volatile EventRepository INSTANCE=null;
    private final EventDatabase db;

    private EventRepository(Context context) {
        db=EventDatabase.get(context);
    }

    public synchronized static EventRepository get(Context context){
        if (INSTANCE==null){
            INSTANCE=new EventRepository(context.getApplicationContext());
        }
        return INSTANCE;
    }
    
    public List<EventModel> all(){
        List<EventEntity> entities=db.eventStore().all();
        ArrayList<EventModel> result = new ArrayList<>(entities.size());

        for (EventEntity entity : entities){
            result.add(entity.toModel());
        }
        return result;
    }

    public void add(EventModel model){
       db.eventStore().insert(EventEntity.fromModel(model));
    }

    public void replace(EventModel model){
        db.eventStore().update(EventEntity.fromModel(model));
    }

    public void delete(EventModel model){
        db.eventStore().delete(EventEntity.fromModel(model));
    }

    public EventModel find(String id){
        for (EventModel candidate: all()){
            if (candidate.id().equals(id)){
                return candidate;
            }
        }
        return null;
    }

}
