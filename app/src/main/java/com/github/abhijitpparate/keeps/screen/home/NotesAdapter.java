package com.github.abhijitpparate.keeps.screen.home;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.abhijitpparate.keeps.R;
import com.github.abhijitpparate.keeps.data.database.Note;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesHolder> {

    private List<Note> noteList;

    private OnNoteClickListener noteClickListener;

    public NotesAdapter(List<Note> noteList) {
        this.noteList = noteList;
    }

    public void setNoteClickListener(OnNoteClickListener noteClickListener) {
        this.noteClickListener = noteClickListener;
    }

    @Override
    public NotesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View noteView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout, parent, false);
        return new NotesHolder(noteView);
    }

    @Override
    public void onBindViewHolder(NotesHolder holder, int position) {
        Note note = noteList.get(position);
        holder.setTitle(note.getTitle());
        holder.setBody(note.getBody());
        holder.setCheckList(note.getChecklist());
        holder.setCreated(note.getCreated());
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public void updateDataset(List<Note> notes){
        if (notes != null && notes.size() != 0 ){
            noteList.clear();
            noteList.addAll(notes);
            notifyDataSetChanged();
        }
    }

    class NotesHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tvTitle)
        TextView tvTitle;

        @BindView(R.id.tvCreated)
        TextView tvCreated;

        @BindView(R.id.tvBody)
        TextView tvBody;

        NotesHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noteClickListener.onNoteClicked(noteList.get(getLayoutPosition()));
                }
            });
        }

        void setTitle(String title) {
            this.tvTitle.setText(title);
        }

        void setCreated(String created){
            //Tue Jun 06 22:08:23 PDT 2017
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.getDefault());
            Date date;
            try {
                date = sdf.parse(created);
            } catch (ParseException e) {
                e.printStackTrace();
                date = Calendar.getInstance().getTime();
            }
            this.tvCreated.setText(DateUtils.getRelativeTimeSpanString(date.getTime()));
        }

        void setBody(String body) {
            this.tvBody.setText(body);
        }

        void setCheckList(String checkList){
            Log.d("NoteAdapter", "setCheckList: " + checkList);
            StringBuilder sb = new StringBuilder(this.tvBody.getText());
            if (checkList != null && !checkList.isEmpty()) {
                JSONTokener j = new JSONTokener(checkList);
                try {
                    JSONArray jsonArray = new JSONArray(j);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        Log.d("NoteJson", "setCheckList: " + o);
                        sb.append("[ " );
                        boolean checked = o.getBoolean("checked");
                        if (checked) sb.append("x");
                        else sb.append(" " );
                        sb.append(" ] - " );
                        sb.append(o.getString("text"));
                        sb.append("\n");
                    }

                    this.tvBody.setText(sb.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    interface OnNoteClickListener {
        void onNoteClicked(Note note);
    }
}
