package cat.polpadilla.eventreminder;

import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateUtils;

import java.util.Calendar;

import cat.polpadilla.eventreminder.databinding.EventRowBinding;

public class RosterRowHolder extends RecyclerView.ViewHolder {
    final private EventRowBinding binding;
    private final RosterListAdapter adapter;

    public RosterRowHolder(EventRowBinding binding, RosterListAdapter adapter) {
        super(binding.getRoot());

        this.binding=binding;
        this.adapter=adapter;
    }

    void bind(EventModel model) {
        binding.setModel(model);
        binding.setDueDate(DateUtils.getRelativeTimeSpanString(model.dueDate().getTimeInMillis(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
        binding.setHolder(this);
        binding.executePendingBindings();
    }

    public void onClick(){
        adapter.showModel(binding.getModel());
    }

}
