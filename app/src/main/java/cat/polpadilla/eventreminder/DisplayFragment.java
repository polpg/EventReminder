package cat.polpadilla.eventreminder;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import cat.polpadilla.eventreminder.databinding.EventDisplayBinding;

public class DisplayFragment extends Fragment {
    private static final String ARG_ID="id";
    private EventDisplayBinding binding;
    private RosterViewModel viewModel;

    static DisplayFragment newInstance(EventModel model){
        DisplayFragment result=new DisplayFragment();

        if(model!=null){
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
        binding= EventDisplayBinding.inflate(getLayoutInflater(),container,false);

        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.replaceMenu(R.menu.empty);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener((View v) ->((Contract)getActivity()).editModel(binding.getModel()));

        fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_edit_white_24dp));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.stateStream().observe(this, this::render);
    }

    interface Contract{
        void editModel(EventModel model);
    }

    private void render(ViewState state){
        if (state != null){
            EventModel model = state.current();

            if (model !=null){
                binding.setModel(model);
                binding.setDueDate(DateUtils.getRelativeDateTimeString(getActivity(), model.dueDate().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0));
            }
        }
    }
}
