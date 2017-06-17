package com.github.abhijitpparate.keeper.utils;


import com.github.abhijitpparate.checklistview.CheckListItem;
import com.github.abhijitpparate.keeper.data.database.Note;
import com.github.abhijitpparate.keeper.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.abhijitpparate.keeper.screen.note.presenter.NotePresenter.colorMap;

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

    public static int getNoteColor(String colorString){
        if (colorString != null && !colorString.isEmpty()) {
            return colorMap.containsKey(colorString) ? colorMap.get(colorString) : R.color.colorBackground;
        }
        return R.color.colorBackground;
    }

    public static String getNoteColorString(int colorInt){
        if (colorMap.containsValue(colorInt)){
            for (Map.Entry<String, Integer> c : colorMap.entrySet()) {
                if (c.getValue() ==  colorInt){
                    return c.getKey();
                }
            }
        }
        return Note.NoteColor.DEFAULT;
    }
}
