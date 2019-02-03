package com.polpg.eventreminder;

import android.content.Context;

import org.threeten.bp.LocalDate;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class Controller {
    private final EventRepository eventRepo;
    private final PublishSubject<Result> resultSubject=
            PublishSubject.create();

    public Controller(Context context){
        eventRepo=EventRepository.get(context);
    }

    public void subscribeToActions(Observable<Action> actionStream) {
        actionStream
                .observeOn(Schedulers.io())
                .subscribe(this::processImpl);
    }

    private void processImpl(Action action) {
        if (action instanceof Action.Add){
            add(((Action.Add)action).model());
        } else if (action instanceof Action.Edit){
            modify(((Action.Edit)action).model());
        } else if (action instanceof Action.Delete){
            delete(((Action.Delete)action).model());
        } else if ((action instanceof  Action.Load)){
            load();
        } else if (action instanceof  Action.Show){
            show(((Action.Show)action).current());
        } else  if (action instanceof Action.Filter){
            filter(((Action.Filter)action).selectedDate());
        }
    }


    private void add(EventModel model) {
        eventRepo.add(model);
        resultSubject.onNext(Result.added(model));
    }
    private void modify(EventModel model) {
        eventRepo.replace(model);
        resultSubject.onNext(Result.modified(model));
    }
    private void delete(EventModel toDelete) {
        eventRepo.delete(toDelete);
        resultSubject.onNext(Result.deleted(toDelete));
    }
    private void load(){
        resultSubject.onNext(Result.loaded(eventRepo.all()));
    }
    private void show(EventModel current){
        resultSubject.onNext(Result.showed(current));
    }
    private void filter(LocalDate date){
        resultSubject.onNext(Result.filtered(date));
    }


    public Observable<Result> resultStream(){
        return resultSubject;
    }

}
