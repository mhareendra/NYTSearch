package com.example.hari.nytsearch.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Hari on 7/30/2016.
 */
public class Settings {
    public void setSelectArts(boolean selectArts) {
        isSelectArts = selectArts;
        newsDeskValues.add("Arts");
    }

    public void setSelectFashion(boolean selectFashion) {
        isSelectFashion = selectFashion;
    }

    public void setSelectSports(boolean selectSports) {
        isSelectSports = selectSports;
    }

    public String beginDate;

    public String sortOrder;

    public boolean isSelectArts;
    public boolean isSelectFashion;
    public boolean isSelectSports;
    public boolean isSelectedBooks;
    public boolean isSelectedCars;
    public boolean isSelectedEducation;

    public int spSortOrderSelectedIndex = 0;
    public String beginDateDisplay;

    public String getBeginDate() {
        if(beginDate == null || beginDate.isEmpty())
            return getDefaultCompletionDate();
        if(beginDate.contains("/")) {
            try {
                beginDateDisplay = beginDate;
                String[] s = beginDate.split("/");
                if(s.length == 3) {
                    //%02d
                    int month = Integer.parseInt(s[0]);
                    int day = Integer.parseInt(s[1]);
                    int year = Integer.parseInt(s[2]);
                    beginDate = String.format("%02d%02d%02d", year, month, day);
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        return beginDate;
    }

    public String getSortOrder() {
        if(sortOrder == null || sortOrder.isEmpty())
            return "newest";
        return sortOrder;
    }

    public ArrayList<String> newsDeskValues = new ArrayList<>();

    public String getNewsDeskValues()
    {
        if(newsDeskValues.size() == 0)
            return null;

        String newsDeskString = "news_desk:(";
        for (String s: newsDeskValues
             ) {
            newsDeskString += String.format("\"%s\"", s);
        }
        newsDeskString += ")";

        return newsDeskString;
    }

    private String getDefaultCompletionDate()
    {
        try {

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            beginDateDisplay = String.format(Locale.US, "%02d/%02d/%d", month, day, year );
            return String.format(Locale.US, "%02d%02d%02d", year, month, day );
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

}
