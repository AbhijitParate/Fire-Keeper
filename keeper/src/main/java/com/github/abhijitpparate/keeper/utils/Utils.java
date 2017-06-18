package com.github.abhijitpparate.keeper.utils;


import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.util.Log;

import com.github.abhijitpparate.checklistview.CheckListItem;
import com.github.abhijitpparate.keeper.data.database.Note;
import com.github.abhijitpparate.keeper.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
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

    public static String getPath(Context context, Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, uri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    public static void readFile(Context context, Uri uri){
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Utils", "readFile: " + stringBuilder.toString());
    }
}
