package com.github.abhijitpparate.keeper.screen.home

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.github.abhijitpparate.keeper.R
import com.github.abhijitpparate.keeper.data.database.Note

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONTokener

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import butterknife.BindView
import butterknife.ButterKnife

import com.github.abhijitpparate.keeper.utils.Utils.getNoteColor


class NotesAdapter(private val context: Context, private val noteList: MutableList<Note>) : RecyclerView.Adapter<NotesAdapter.NotesHolder>() {

    private var noteClickListener: OnNoteClickListener? = null

    fun setNoteClickListener(noteClickListener: OnNoteClickListener) {
        this.noteClickListener = noteClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        val noteView = LayoutInflater.from(parent.context).inflate(R.layout.note_layout, parent, false)
        return NotesHolder(noteView)
    }

    override fun onBindViewHolder(holder: NotesHolder, position: Int) {
        val note = noteList[position]
        holder.setNote(note)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    fun updateDataset(notes: List<Note>?) {
        if (notes != null && notes.isNotEmpty()) {
            noteList.clear()
            noteList.addAll(notes)
            notifyDataSetChanged()
        }
    }

    inner class NotesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.rlNoteCard)
        lateinit var rlNoteCard: View

        @BindView(R.id.tvTitle)
        lateinit var tvTitle: TextView

        @BindView(R.id.tvCreated)
        lateinit var tvCreated: TextView

        @BindView(R.id.tvBody)
        lateinit var tvBody: TextView

        init {
            ButterKnife.bind(this, itemView)
            itemView.setOnClickListener { noteClickListener!!.onNoteClicked(noteList[layoutPosition]) }
        }

        fun setNote(note: Note) {
            setTitle(note.title.toString())
            setBody(note.body.toString())
            setCheckList(note.checklist)
            setCreated(note.created.toString())
            setColor(note.color.toString())
        }

        fun setTitle(title: String) {
            this.tvTitle.text = title
        }

        fun setCreated(created: String) {
            //Tue Jun 06 22:08:23 PDT 2017
            val sdf = SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.getDefault())
            var date: Date
            try {
                date = sdf.parse(created)
            } catch (e: ParseException) {
                e.printStackTrace()
                date = Calendar.getInstance().time
            }

            this.tvCreated.text = DateUtils.getRelativeTimeSpanString(date.time)
        }

        fun setBody(body: String) {
            this.tvBody.text = body
        }

        fun setCheckList(checkList: String?) {
            Log.d("NoteAdapter", "setCheckList: ")
            val sb = StringBuilder(this.tvBody.text)
            if (checkList != null && !checkList.isEmpty()) {
                val j = JSONTokener(checkList)
                try {
                    val jsonArray = JSONArray(j)
                    for (i in 0..jsonArray.length() - 1) {
                        val o = jsonArray.getJSONObject(i)
                        Log.d("NoteJson", "setCheckList: " + o)
                        sb.append("[ ")
                        val checked = o.getBoolean("checked")
                        if (checked)
                            sb.append("x")
                        else
                            sb.append(" ")
                        sb.append(" ] - ")
                        sb.append(o.getString("text"))
                        sb.append("\n")
                    }

                    this.tvBody.text = sb.toString()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }

        fun setColor(color: String) {
            rlNoteCard.setBackgroundColor(ResourcesCompat.getColor(context.resources, getNoteColor(color), null))
        }
    }

    interface OnNoteClickListener {
        fun onNoteClicked(note: Note)
    }
}
