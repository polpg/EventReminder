package com.polpg.eventreminder;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {EventEntity.class}, version = 1)
@TypeConverters({TypeTransmogrifier.class})
public abstract class EventDatabase extends RoomDatabase {
    public abstract EventEntity.Store eventStore();

    private static final String DB_NAME="event.db";
    private static volatile EventDatabase INSTANCE=null;

    synchronized static EventDatabase get(Context context){
        if (INSTANCE==null){
            INSTANCE= create(context);
        }
        return INSTANCE;
    }

    private static  EventDatabase create(Context context){
        RoomDatabase.Builder<EventDatabase> b= Room.databaseBuilder(context.getApplicationContext(), EventDatabase.class, DB_NAME);

        return b.build();
    }
}
