package com.example.hari.nytsearch.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hari.nytsearch.R;
import com.example.hari.nytsearch.model.Settings;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterSettingsFragment extends DialogFragment
    implements DatePickerFragment.DatePickerDialogListener
{

    public FilterSettingsFragment() {
        // Required empty public constructor
    }


    private Settings settings;
    public static FilterSettingsFragment newInstance(Settings setings) {

        FilterSettingsFragment frag = new FilterSettingsFragment();
        Bundle args = new Bundle();
        frag.settings = setings;
        frag.setArguments(args);
        return frag;
    }

    @BindView(R.id.tvBeginDate)
    TextView tvBeginDate;

    @BindView(R.id.spSortOrder)
    Spinner spSortOrder;

    @BindView(R.id.cbArts)
    CheckBox cbArts;

    @BindView(R.id.cbFashion)
    CheckBox cbFashion;

    @BindView(R.id.cbSports)
    CheckBox cbSports;

    @BindView(R.id.cbBooks)
    CheckBox cbBooks;

    @BindView(R.id.cbCars)
    CheckBox cbCars;

    @BindView(R.id.cbEducation)
    CheckBox cbEducation;

    @BindView(R.id.toolbar)
    Toolbar toolbar;



    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_filter_settings,
                container, false);
        unbinder = ButterKnife.bind(this, view);

        cbSports.setChecked(settings.isSelectSports);
        tvBeginDate.setText(settings.beginDateDisplay);
        spSortOrder.setSelection(settings.spSortOrderSelectedIndex);
        cbArts.setChecked(settings.isSelectArts);
        cbFashion.setChecked(settings.isSelectFashion);
        cbBooks.setChecked(settings.isSelectedBooks);
        cbCars.setChecked(settings.isSelectedCars);
        cbEducation.setChecked(settings.isSelectedEducation);

        // Inflate the layout for this fragment
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        if(toolbar != null) {
            toolbar.setOnMenuItemClickListener(item -> {

                int id = item.getItemId();

                if (id == R.id.save_option) {
                    saveSettings();
                    return true;
                } else
                    return false;
            });

            toolbar.inflateMenu(R.menu.menu_filter_settings);

            tvBeginDate.setOnClickListener(view1 -> {
                setDatePickerListener();
            });
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_filter_settings, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.save_option)
        {
            saveSettings();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    private void saveSettings()
    {
        try {
            Settings settings = new Settings();
            String beginDate = tvBeginDate.getText().toString();
            if (!beginDate.isEmpty())
                settings.beginDate = beginDate;

            //int spSortOrderSelectedIndex = spSortOrder.getSelectedItemPosition();
            settings.sortOrder = spSortOrder.getSelectedItem().toString().toLowerCase();
            settings.spSortOrderSelectedIndex = spSortOrder.getSelectedItemPosition();

            if(cbArts.isChecked()) {
                settings.isSelectArts = cbArts.isChecked();
                settings.newsDeskValues.add(cbArts.getText().toString());
            }

            if(cbFashion.isChecked()) {
                settings.isSelectFashion = cbFashion.isChecked();
                settings.newsDeskValues.add(cbFashion.getText().toString());
            }

            if(cbSports.isChecked()) {
                settings.isSelectSports = cbSports.isChecked();
                settings.newsDeskValues.add(cbSports.getText().toString());
            }

            if(cbCars.isChecked()) {
                settings.isSelectedCars = cbCars.isChecked();
                settings.newsDeskValues.add(cbCars.getText().toString());
            }

            if(cbBooks.isChecked()) {
                settings.isSelectedBooks = cbBooks.isChecked();
                settings.newsDeskValues.add(cbBooks.getText().toString());
            }

            if(cbEducation.isChecked()) {
                settings.isSelectedEducation = cbEducation.isChecked();
                settings.newsDeskValues.add(cbEducation.getText().toString());
            }

            FilterSettingsFragmentListener listener =
                    (FilterSettingsFragmentListener) getActivity();
            listener.onFinishFilterSettingsFragment(settings);

            dismiss();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private String beginDate;
    @Override
    public void onFinishDatePickDialog(int year, int monthOfYear, int dayOfMonth)
    {
        try
        {
            String date = String.format(Locale.US, "%02d/%02d/%d", monthOfYear, dayOfMonth, year);
            tvBeginDate.setText(date);
            beginDate = date;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void setDatePickerListener()
    {

        if(beginDate == null)
            beginDate = tvBeginDate.getText().toString();
        if(beginDate.equals("") | !beginDate.contains("/"))
            return;
        String[] splitDate = beginDate.split("/");
        int month = Integer.parseInt(splitDate[0]);
        int day = Integer.parseInt(splitDate[1]);
        int year = Integer.parseInt(splitDate[2]);

        DialogFragment datePickerFragment = DatePickerFragment.newInstance(day, month - 1, year);
        datePickerFragment.setTargetFragment(this, 0);
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }


    public interface FilterSettingsFragmentListener
    {
        void onFinishFilterSettingsFragment(Settings settings);
    }

}