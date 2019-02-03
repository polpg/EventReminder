package com.polpg.eventreminder;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class RepoTests {
    private EventRepository repo;

    @Before
    public void setUp(){
        EventDatabase db= EventDatabase.get(InstrumentationRegistry.getTargetContext());

        db.eventStore().deleteAll();
        repo=EventRepository.get(InstrumentationRegistry.getTargetContext());
        repo.add(EventModel.creator()
                .setTitle("Buy a copy of _Exploring Android_")
                .setDescription("See https://wares.commonsware.com")
                .build());
        repo.add(EventModel.creator()
                .setTitle("Complete all of the tutorials")
                .build());
        repo.add(EventModel.creator()
                .setTitle("Write an app for somebody in my community")
                .setDescription("Talk to some people at non-profit organizations to see what they need!")
                .build());

    }

    @Test
    public void getAll(){
        assertEquals(3,repo.all().size());
    }

    @Test
    public void add(){
        EventModel model= EventModel.creator()
                .setTitle("foo")
                .build();

        repo.add(model);
        List<EventModel> models = repo.all();

        assertEquals(4, models.size());
        assertThat(models, hasItem(model));
    }

    @Test
    public void replace(){
        EventModel original = repo.all().get(1);
        EventModel edited= original.toBuilder()
                .setTitle("Currently on Tut #15")
                .build();

        repo.replace(edited);
        assertEquals(3, repo.all().size());
        assertEquals(edited, repo.all().get(1));
    }

    @Test
    public void delete(){
        assertEquals(3,repo.all().size());
        repo.delete(repo.all().get(0));
        assertEquals(2,repo.all().size());
        repo.delete(repo.all().get(0).toBuilder().build());
        assertEquals(1,repo.all().size());

    }
}
