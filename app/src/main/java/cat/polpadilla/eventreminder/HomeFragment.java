package cat.polpadilla.eventreminder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cat.polpadilla.eventreminder.calendarDecorators.DisabledDecorator;
import cat.polpadilla.eventreminder.calendarDecorators.EventDecorator;
import cat.polpadilla.eventreminder.calendarDecorators.TodayDecorator;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class HomeFragment extends Fragment {
    private RecyclerView rv;
    private View empty , progress;
    private RosterViewModel viewModel;
    private RosterListAdapter adapter;
    private View div;
    private MaterialCalendarView calendar;
    private List<CalendarDay> dates = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result=inflater.inflate(R.layout.fragment_home, container, false);

        rv=result.findViewById(R.id.rvList);
        empty=result.findViewById(R.id.textEmpty);
        progress=result.findViewById(R.id.progressBar);
        div=result.findViewById(R.id.divider);

        calendar=result.findViewById(R.id.calendarView);

        calendar.addDecorator(new DisabledDecorator(getContext(), R.color.colorPrimaryText));
        calendar.addDecorator(new TodayDecorator(getContext(), R.color.colorPrimaryDark));

        //calendar.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);
        //calendar.setSelectedDate(CalendarDay.today());

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener((View v) -> ((Contract)getActivity()).addModel());

        return result;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel= ViewModelProviders.of(getActivity()).get(RosterViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        OverScrollDecoratorHelper.setUpOverScroll(rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        adapter =new RosterListAdapter(this);
        rv.setAdapter(adapter);
        viewModel.stateStream().observe(this, this::render);

    }

    public void render(ViewState state){
        adapter.setState(state);

        calendar.setCurrentDate(CalendarDay.today(), true);

        if (state.isLoaded()){
            progress.setVisibility(View.GONE);

            if (adapter.getItemCount()==0){
                empty.setVisibility(View.VISIBLE);
                calendar.setVisibility(View.GONE);
                div.setVisibility(View.GONE);

            } else {
                empty.setVisibility(View.GONE);
                calendar.setVisibility(View.VISIBLE);
                div.setVisibility(View.VISIBLE);

                dates.clear();
                for (int i=0;i<state.items().size();i++){
                    EventModel model = state.items().get(i);

                    if (model.dueDate() !=null){
                        LocalDate localDate  = Instant.ofEpochMilli
                                (model.dueDate().getTimeInMillis())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        dates.add(CalendarDay.from(localDate));
                    }
                }
                calendar.addDecorator(new EventDecorator(ContextCompat.getColor(getContext(), R.color.colorPrimary), dates));

            }
        } else {
            progress.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }
    }

    void showModel (EventModel model){
        ((HomeFragment.Contract)getActivity()).showModel(model);
        viewModel.process(Action.show(model));
    }

    interface Contract{
        void showModel(EventModel model);
        void addModel();
    }
}
