package com.example.hari.nytsearch.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;


public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {



    public static DatePickerFragment newInstance(int day, int month, int year)
    {
        DatePickerFragment frag = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt("day", day);
        args.putInt("month", month);
        args.putInt("year", year);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        int year = getArguments().getInt("year");
        int month = getArguments().getInt("month");
        int day = getArguments().getInt("day");
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
    {
        try
        {
            DatePickerDialogListener listener = (DatePickerDialogListener)getTargetFragment();
            listener.onFinishDatePickDialog(year, monthOfYear + 1, dayOfMonth);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    // 1. Defines the listener interface with a method passing back data result.
    public interface DatePickerDialogListener {
        void onFinishDatePickDialog(int year, int monthOfYear, int dayOfMonth);
    }

}
