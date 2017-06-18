package com.github.abhijitpparate.keeper.utils;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.github.abhijitpparate.keeper.data.storage.File;
import com.github.abhijitpparate.keeper.screen.note.NoteActivity;

public class FileUtils {

    public static File getFile(Context context, File.Type type, Uri uri) {
        File file = null;
        Cursor cursor = context.getContentResolver()
                .query(uri, null, null, null, null, null);

        if (cursor!= null && cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            String displayName = cursor.getString(columnIndex);
            file = new File(displayName, type);
            cursor.close();
        }

        return file;
    }
}
