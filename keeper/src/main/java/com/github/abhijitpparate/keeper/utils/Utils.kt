package com.github.abhijitpparate.keeper.utils


import com.github.abhijitpparate.checklistview.CheckListItem
import com.github.abhijitpparate.keeper.R
import com.github.abhijitpparate.keeper.data.database.Note.NoteColor.DEFAULT
import com.github.abhijitpparate.keeper.screen.note.presenter.NotePresenter.Companion.colorMap
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONTokener
import java.util.*

object Utils {

    fun parseNoteChecklist(checkList: String): List<CheckListItem> {
        val checkListItems = ArrayList<CheckListItem>()
        val j = JSONTokener(checkList)
        try {
            val jsonArray = JSONArray(j)
            for (i in 0..jsonArray.length() - 1) {
                val o = jsonArray.getJSONObject(i)
                val checked = o.getBoolean("checked")
                val string = o.getString("text")
                checkListItems.add(CheckListItem(string, checked))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return checkListItems
    }

    fun getNoteColor(colorString: String?): Int {
        if (colorString != null && !colorString.isEmpty()) {
            return if (colorMap.containsKey(colorString)) colorMap[colorString] as Int else R.color.colorBackground
        }
        return R.color.colorBackground
    }

    fun getNoteColorString(colorInt: Int): String {
        if (colorMap.containsValue(colorInt)) {
            for ((key, value) in colorMap) {
                if (value === colorInt) {
                    return key
                }
            }
        }
        return DEFAULT
    }
}
