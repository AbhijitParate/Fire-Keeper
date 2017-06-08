package com.github.abhijitpparate.keeps.utils;


import com.github.abhijitpparate.checklistview.CheckListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<CheckListItem> parseNoteChecklist(String checkList){
        List<CheckListItem> checkListItems = new ArrayList<>();
        JSONTokener j = new JSONTokener(checkList);
        try {
            JSONArray jsonArray = new JSONArray(j);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject o = jsonArray.getJSONObject(i);
                boolean checked = o.getBoolean("checked");
                String string  = o.getString("text");
                checkListItems.add(new CheckListItem(string, checked));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return checkListItems;
    }

}
