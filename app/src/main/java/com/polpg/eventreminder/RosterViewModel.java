package com.polpg.eventreminder;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

public class RosterViewModel extends AndroidViewModel {
    private final LiveData<ViewState> states;
    private final PublishSubject<Action> actionSubject=PublishSubject.create();
    private ViewState lastState =ViewState.empty().build();
    private final ReplaySubject<ViewState> stateSubject = ReplaySubject.createWithSize(1);

    public RosterViewModel(@NonNull Application application) {

        super(application);

        Controller controller = new Controller(application);

        controller.resultStream()
                .subscribe(result -> {
                    lastState = foldResultIntoState(lastState, result);
                    stateSubject.onNext(lastState);
                }, stateSubject::onError);
        states= LiveDataReactiveStreams.fromPublisher(stateSubject.toFlowable(BackpressureStrategy.LATEST));

        controller.subscribeToActions(actionSubject);

        process(Action.load());
    }

    public LiveData<ViewState> stateStream(){
        return states;
    }

    public void process(Action action) {
        actionSubject.onNext(action);
    }

    private ViewState foldResultIntoState(@NonNull ViewState state, @NonNull Result result) throws Exception {
        if (result instanceof Result.Added) {
            return state.add(((Result.Added) result).model());
        } else if (result instanceof Result.Modified) {
            return state.modify(((Result.Modified) result).model());
        } else if (result instanceof Result.Deleted) {
            return state.delete(((Result.Deleted) result).model());
        } else if (result instanceof Result.Loaded) {
            List<EventModel> models = ((Result.Loaded) result).models();

            return ViewState.builder().items(models).current(models.size() == 0 ? null : models.get(0)).isLoaded(true).build();
        } else if (result instanceof Result.Showed) {
            return state.show(((Result.Showed) result).current());
        } else if (result instanceof Result.Filtered){
            return state.filter(((Result.Filtered) result).selectedDate());
        } else {
            throw new IllegalStateException("Unexpected result type: " + result.toString());
        }
    }

}
