package com.polpg.eventreminder;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int hour, min;
        final Calendar c;
        if (getArguments() != null){
            c= (Calendar) getArguments().getSerializable("date");
        } else {
            c= Calendar.getInstance();
        }

        hour = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener)getTargetFragment (), hour, min, DateFormat.is24HourFormat(getActivity()));
    }
}

