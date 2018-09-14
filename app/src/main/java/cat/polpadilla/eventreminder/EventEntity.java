package cat.polpadilla.eventreminder;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;

@Entity(tableName = "events", indices = @Index(value = "id"))
public class EventEntity {
    @PrimaryKey @NonNull final String id;
    @NonNull final String title;
    final String description;
    @NonNull final Calendar dueDate;

    EventEntity(@NonNull String id, @NonNull String title, String description, @NonNull Calendar dueDate){
        this.id=id;
        this.title=title;
        this.description=description;
        this.dueDate=dueDate;
    }

    @Dao
    public interface Store{
        @Query("SELECT * FROM events ORDER BY dueDate ASC")
        List<EventEntity> all();

        @Query("DELETE FROM events")
        void deleteAll();

        @Insert
        void insert(EventEntity... entities);

        @Update
        void update(EventEntity... entities);

        @Delete
        void delete(EventEntity... entities);
    }

    public static EventEntity fromModel(EventModel model){
        return new EventEntity(model.id(), model.title(), model.description(), model.dueDate());
    }

    public EventModel toModel(){
        return EventModel.builder()
                .setId(id)
                .setTitle(title)
                .setDescription(description)
                .setDueDate(dueDate)
                .build();
    }
}
