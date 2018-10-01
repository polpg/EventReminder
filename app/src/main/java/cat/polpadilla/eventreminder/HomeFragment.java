package cat.polpadilla.eventreminder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cat.polpadilla.eventreminder.calendarDecorators.EventDecorator;
import cat.polpadilla.eventreminder.calendarDecorators.SelectorDecorator;
import cat.polpadilla.eventreminder.calendarDecorators.TodayDecorator;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class HomeFragment extends Fragment {
    private RecyclerView rv;
    private View progress;
    private TextView empty;
    private RosterViewModel viewModel;
    private RosterListAdapter adapter;
    private View div;
    private MaterialCalendarView calendar;
    private List<CalendarDay> dates = new ArrayList<>();
    private BottomAppBar bottomAppBar;
    static boolean filtered = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result=inflater.inflate(R.layout.fragment_home, container, false);

        rv=result.findViewById(R.id.rvList);
        empty=result.findViewById(R.id.textEmpty);
        progress=result.findViewById(R.id.progressBar);
        div=result.findViewById(R.id.divider);

        calendar=result.findViewById(R.id.calendarView);

        calendar.addDecorators(
                new SelectorDecorator(getContext()),
                new TodayDecorator(getContext(), R.color.colorPrimaryDark));

        calendar.setOnDateChangedListener((@NonNull MaterialCalendarView calendarView, @NonNull CalendarDay calendarDay, boolean b) ->{
                    filtered=true;
                    viewModel.process(Action.filter(calendarDay.getDate()));
                });

        bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setOnMenuItemClickListener((MenuItem item) ->{
            if (item.getItemId()==R.id.reset_filter){
                clearFilters();
                Toast.makeText(getContext(), R.string.cleared_filters, Toast.LENGTH_SHORT).show();
                return true;
            }
            return super.onOptionsItemSelected(item);
        });

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener((View v) -> ((Contract)getActivity()).addModel());

        fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_add_white_24dp));

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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

    private void addModelsToDatesList(List<EventModel> models){
        for (int i=0;i<models.size();i++){
            EventModel model = models.get(i);

            if (model.dueDate() !=null){
                LocalDate localDate = TypeTransmogrifier.dateFromCalendar(model.dueDate());
                dates.add(CalendarDay.from(localDate));
            }
        }
    }

    public void clearFilters (){
        if (filtered){
            filtered=false;
            calendar.clearSelection();
            viewModel.process(Action.filter(CalendarDay.today().getDate()));
        }
    }

    @Override
    public void onStop() {
        clearFilters();
        super.onStop();
    }

    public void render(ViewState state){
        adapter.setState(state);

        //Go to selected Date
        if (calendar.getSelectedDate()!=null){
            calendar.setCurrentDate(calendar.getSelectedDate());
        } else {
            calendar.setCurrentDate(CalendarDay.today(), true);
        }

        // If state is loaded, hide progress bar
        if (state.isLoaded()) {
            progress.setVisibility(View.GONE);

            /*
             * If there's no added events:
             *  Set TextView empty VISIBLE & change text to "msg_empty"
             *  Do NOT show Calendar nor Divider
             *  Set bottomAppBar to empty
             */
            if (state.items().size() == 0) {
                empty.setVisibility(View.VISIBLE);
                empty.setText(R.string.msg_empty);
                div.setVisibility(View.GONE);
                calendar.setVisibility(View.GONE);
                bottomAppBar.replaceMenu(R.menu.empty);

            }
            /*
             * If there's added events but not in the day that is filtered:
             * Set TextView empty VISIBLE & change text to "msg_empty_day"
             * Show Calendar and Divider
             * If the date is filtered: show filter button
             */
            else if (state.filteredItems().size() == 0) {
                empty.setVisibility(View.VISIBLE);
                empty.setText(R.string.msg_empty_day);
                div.setVisibility(View.VISIBLE);
                calendar.setVisibility(View.VISIBLE);

            }
            /*
             * If there are events to show:
             * Hide TextView empty
             * Show Calendar and Divider
             * Add Dates from events to dates list
             */
            else {
                empty.setVisibility(View.GONE);
                div.setVisibility(View.VISIBLE);
                calendar.setVisibility(View.VISIBLE);
                //Add EventDecorators
                dates.clear();
                addModelsToDatesList(state.items());
                calendar.addDecorator(new EventDecorator(ContextCompat.getColor(getContext(), R.color.colorPrimary), dates));
            }

            if (filtered) {
                bottomAppBar.replaceMenu(R.menu.actions_home);
            } else {
                bottomAppBar.replaceMenu(R.menu.empty);
            }

        }
        /*
        * If state hasn't loaded yet:
        * Show Progress bar
        * Hide everything else
        */
        else {
            progress.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
            calendar.setVisibility(View.GONE);
            div.setVisibility(View.GONE);
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
