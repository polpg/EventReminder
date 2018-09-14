package cat.polpadilla.eventreminder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import io.reactivex.subjects.PublishSubject;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ControllerTest {

    @Before
    public void setUp(){
        EventDatabase db=EventDatabase.get(InstrumentationRegistry.getTargetContext());

        db.eventStore().deleteAll();

    }

    @Test
    public void controller() throws  InterruptedException{
        final Controller controller=new Controller(InstrumentationRegistry.getTargetContext());
        final PublishSubject<Action> actionSubject=PublishSubject.create();
        final LinkedBlockingQueue<Result> receivedResults=new LinkedBlockingQueue<>();

        controller.subscribeToActions(actionSubject);
        controller.resultStream().subscribe(receivedResults::offer);

        actionSubject.onNext(Action.load());
        Result.Loaded resultLoaded=
                (Result.Loaded)receivedResults.poll(1, TimeUnit.SECONDS);

        assertEquals(0, resultLoaded.models().size());

        final EventModel fooModel=EventModel.creator().setTitle("foo").setDescription("hello, world!").build();
        actionSubject.onNext(Action.add(fooModel));
        Result.Added resultAdded=
                (Result.Added)receivedResults.poll(1, TimeUnit.SECONDS);
        assertEquals(fooModel, resultAdded.model());
        final EventModel barModel=EventModel.creator().setTitle("bar").build();
        actionSubject.onNext(Action.add(barModel));
        resultAdded=
                (Result.Added)receivedResults.poll(1, TimeUnit.SECONDS);
        assertEquals(barModel, resultAdded.model());
        final EventModel gooModel=EventModel.creator()
                .setTitle("goo")
                .build();
        actionSubject.onNext(Action.add(gooModel));
        resultAdded=
                (Result.Added)receivedResults.poll(1, TimeUnit.SECONDS);
        assertEquals(gooModel, resultAdded.model());

        final EventModel mutatedFoo=fooModel.toBuilder().build();
        actionSubject.onNext(Action.edit(mutatedFoo));
        Result.Modified resultModified=
                (Result.Modified)receivedResults.poll(1, TimeUnit.SECONDS);
        assertEquals(mutatedFoo, resultModified.model());
        final EventModel mutatedBar=barModel.toBuilder().setTitle("bar!").setDescription("hi!").build();
        actionSubject.onNext(Action.edit(mutatedBar));
        resultModified=
                (Result.Modified)receivedResults.poll(1, TimeUnit.SECONDS);
        assertEquals(mutatedBar, resultModified.model());
        final EventModel mutatedGoo=gooModel.toBuilder()
                .setTitle("goo!")
                .build();
        actionSubject.onNext(Action.edit(mutatedGoo));
        resultModified=
                (Result.Modified)receivedResults.poll(1, TimeUnit.SECONDS);
        assertEquals(mutatedGoo, resultModified.model());

        actionSubject.onNext(Action.delete(barModel));
        Result.Deleted resultDeleted=
                (Result.Deleted)receivedResults.poll(1, TimeUnit.SECONDS);
        assertEquals(barModel, resultDeleted.model());
        actionSubject.onNext(Action.delete(fooModel));
        resultDeleted=
                (Result.Deleted)receivedResults.poll(1, TimeUnit.SECONDS);
        assertEquals(fooModel, resultDeleted.model());
        actionSubject.onNext(Action.delete(gooModel));
        resultDeleted=
                (Result.Deleted)receivedResults.poll(1, TimeUnit.SECONDS);
        assertEquals(gooModel, resultDeleted.model());
    }
}
