package cat.polpadilla.eventreminder;

import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity
        implements HomeFragment.Contract, DisplayFragment.Contract, EditFragment.Contract{

    private static final String BACK_STACK_SHOW="showModel";
    private BottomAppBar bottomAppBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set default state of app (HomeFragment)
        if (findViewById(R.id.fragment_container) !=null){
            if (savedInstanceState !=null){
                return;
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new HomeFragment(), "current")
                    .commit();
        }

        bottomAppBar = findViewById(R.id.bottomAppBar);
        FloatingActionButton fab  = findViewById(R.id.fab);

        getSupportFragmentManager().addOnBackStackChangedListener(() ->{
            Fragment current =  getSupportFragmentManager().findFragmentByTag("current");
            if (current instanceof HomeFragment){
                bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
            } else if (current instanceof EditFragment){
                bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_white_24dp));
            } else if (current instanceof DisplayFragment){
                bottomAppBar.replaceMenu(R.menu.empty);
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_white_24dp));
                bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
            }
        });

    }

    @Override
    public void showModel(EventModel model){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, DisplayFragment.newInstance(model), "current")
                .addToBackStack(BACK_STACK_SHOW)
                .commit();

    }

    @Override
    public void editModel(EventModel model){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, EditFragment.newInstance(model),"current")
                .addToBackStack(null)
                .commit();

    }
    @Override
    public  void finishEdit(boolean deleted){
        hideSoftInput();
        if (deleted){
            getSupportFragmentManager().popBackStack(BACK_STACK_SHOW,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

    private void hideSoftInput() {
        if (getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null) {
            ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void addModel(){
        editModel(null);
    }

}
