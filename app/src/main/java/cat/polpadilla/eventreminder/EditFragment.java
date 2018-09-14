package cat.polpadilla.eventreminder;

import androidx.appcompat.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

import cat.polpadilla.eventreminder.databinding.EventEditBinding;

public class EditFragment extends Fragment
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private static final String ARG_ID="id";
    private EventEditBinding binding;
    private RosterViewModel viewModel;
    private Calendar dueDate;
    private int year, month, day, hour, min;


    static EditFragment newInstance(EventModel model){
        EditFragment result=new EditFragment();

        if(model != null){
            Bundle args=new Bundle();

            args.putString(ARG_ID, model.id());
            result.setArguments(args);
        }
        return result;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel= ViewModelProviders.of(getActivity()).get(RosterViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = EventEditBinding.inflate(getLayoutInflater(), container, false);

        Button bttnDueDate = binding.getRoot().findViewById(R.id.dueDate);
        bttnDueDate.setOnClickListener((View v) -> showDatePickerDialog());

        Button bttnDueTime = binding.getRoot().findViewById(R.id.dueTime);
        bttnDueTime.setOnClickListener((View v) -> showTimePickerDialog());

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener((View v) -> save());

        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        if (getModelId()!=null){
            bottomAppBar.replaceMenu(R.menu.actions_edit);
        }
        bottomAppBar.setOnMenuItemClickListener((MenuItem item) ->{
            if (item.getItemId()==R.id.delete){
                delete();
                return  true;
            }
            return super.onOptionsItemSelected(item);
        });

        return binding.getRoot();
    }

    private String getModelId(){
        return getArguments()==null ? null : getArguments().getString(ARG_ID);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.stateStream().observe(this, this::render);
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        if (dueDate != null){
            Bundle bundle = new Bundle();
            bundle.putSerializable("date", dueDate);
            newFragment.setArguments(bundle);
        }
        newFragment.setTargetFragment(this,0);
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        if (dueDate != null){
            Bundle bundle = new Bundle();
            bundle.putSerializable("date", dueDate);
            newFragment.setArguments(bundle);
        }
        newFragment.setTargetFragment(this,0);
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int yearSet, int monthSet, int dayOfMonthSet) {
        year = yearSet;
        month = monthSet;
        day = dayOfMonthSet;

        setDueDateValues();
        setDateBindings();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hour = hourOfDay;
        min = minute;

        setDueDateValues();
        setDateBindings();
    }

    private void setDueDateValues() {
        dueDate = Calendar.getInstance();

        dueDate.set(Calendar.YEAR, year);
        dueDate.set(Calendar.MONTH, month);
        dueDate.set(Calendar.DAY_OF_MONTH, day);
        dueDate.set(Calendar.HOUR_OF_DAY, hour);
        dueDate.set(Calendar.MINUTE, min);
    }

    private void setDateBindings(){
        binding.setDueDate(DateUtils.formatDateTime(getActivity(), dueDate.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
        binding.setDueTime(DateUtils.formatDateTime(getActivity(), dueDate.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME));
    }

    public void save(){
        EventModel.Builder builder;

        if (binding.getModel() == null){
            builder =EventModel.creator();
        } else {
            builder=binding.getModel().toBuilder();
        }

        EventModel newModel;
        if (dueDate !=null){
            newModel = builder
                    .setTitle(binding.title.getText().toString())
                    .setDescription(binding.desc.getText().toString())
                    .setDueDate(dueDate)
                    .build();
        } else {
            newModel = builder
                    .setTitle(binding.title.getText().toString())
                    .setDescription(binding.desc.getText().toString())
                    .build();
        }

        if (binding.getModel()==null){
            viewModel.process(Action.add(newModel));
        } else {
            viewModel.process(Action.edit(newModel));
        }
        ((Contract)getActivity()).finishEdit(false);
    }

    private void delete(){
        AlertDialog deleteDialog = new AlertDialog.Builder(getActivity(), R.style.Theme_MaterialComponents_Light_Dialog_Alert)
                .setTitle(R.string.menu_delete)
                .setMessage(R.string.delete_confirm)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setPositiveButton(R.string.menu_delete, (DialogInterface dialog, int which) -> {
                        viewModel.process(Action.delete(binding.getModel()));
                        ((Contract)getActivity()).finishEdit(true);
                        dialog.dismiss();
                    })
                .setNegativeButton(R.string.cancel, (DialogInterface dialog, int which) -> {
                    dialog.dismiss();
                })
                .setCancelable(true)
                .create();
        deleteDialog.show();
    }

    private void render(ViewState state){
        if (state!=null){
            if (getModelId()==null){
                dueDate = Calendar.getInstance();
            } else {
                EventModel model = state.current();
                dueDate = model.dueDate();

                binding.setModel(model);
            }
            year = dueDate.get(Calendar.YEAR);
            month = dueDate.get(Calendar.MONTH);
            day = dueDate.get(Calendar.DAY_OF_MONTH);
            hour = dueDate.get(Calendar.HOUR_OF_DAY);
            min = dueDate.get(Calendar.MINUTE);
            setDateBindings();
        }
    }

    interface Contract {
        void finishEdit(boolean deleted);
    }
}
