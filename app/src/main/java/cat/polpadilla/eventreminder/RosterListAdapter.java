package cat.polpadilla.eventreminder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import cat.polpadilla.eventreminder.databinding.EventRowBinding;

public class RosterListAdapter extends RecyclerView.Adapter<RosterRowHolder> {
    private List<EventModel> models;
    final private HomeFragment host;


    @NonNull
    @Override
    public RosterRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        EventRowBinding binding= EventRowBinding.inflate(host.getLayoutInflater(), parent, false);
        return new RosterRowHolder(binding, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RosterRowHolder holder, int position) {
        holder.bind(models.get(position));

    }

    @Override
    public int getItemCount() {
        return models==null ? 0: models.size();
    }


    RosterListAdapter(HomeFragment host) {
        this.host = host;
    }

    void showModel(EventModel model){
       host.showModel(model);
    }

    void setState (ViewState state){
        models=state.items();
        notifyDataSetChanged();
    }
}
