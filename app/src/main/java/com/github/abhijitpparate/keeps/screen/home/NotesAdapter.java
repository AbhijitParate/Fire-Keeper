package com.github.abhijitpparate.keeps.screen.home;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DrawableUtils;
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
import com.github.abhijitpparate.keeps.utils.Utils;

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

import static com.github.abhijitpparate.keeps.utils.Utils.getNoteColor;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesHolder> {

    private Context context;
    private List<Note> noteList;

    private OnNoteClickListener noteClickListener;

    public NotesAdapter(Context context, List<Note> noteList) {
        this.context = context;
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
        holder.setNote(note);
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

        @BindView(R.id.rlNoteCard)
        View rlNoteCard;

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

        void setNote(Note note) {
            setTitle(note.getTitle());
            setBody(note.getBody());
            setCheckList(note.getChecklist());
            setCreated(note.getCreated());
            setColor(note.getColor());
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
            Log.d("NoteAdapter", "setCheckList: ");
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

        public void setColor(String color) {
            rlNoteCard.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), getNoteColor(color), null));
        }
    }

    interface OnNoteClickListener {
        void onNoteClicked(Note note);
    }
}
