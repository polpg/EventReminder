package com.polpg.eventreminder;

import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity
        implements HomeFragment.Contract, DisplayFragment.Contract, EditFragment.Contract{

    private static final String BACK_STACK_SHOW="showModel";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar= findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);

        //Set default state of app (HomeFragment)
        if (findViewById(R.id.fragment_container) !=null){
            if (savedInstanceState !=null){
                return;
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finishEdit(false);
        return super.onSupportNavigateUp();
    }

    @Override
    public void showModel(EventModel model){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, DisplayFragment.newInstance(model))
                .addToBackStack(BACK_STACK_SHOW)
                .commit();

    }

    @Override
    public void editModel(EventModel model){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, EditFragment.newInstance(model))
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
