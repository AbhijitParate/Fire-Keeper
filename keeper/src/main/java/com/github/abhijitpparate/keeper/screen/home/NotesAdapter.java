package com.github.abhijitpparate.keeper.screen.home;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.abhijitpparate.keeper.data.database.Note;
import com.github.abhijitpparate.keeper.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.github.abhijitpparate.keeper.utils.Utils.getNoteColor;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesHolder> implements NotesTouchHelper.Adapter {

    private Context context;
    private List<Note> noteList;

    private OnNoteClickListener noteClickListener;
    private OnStartDragListener mDragStartListener;
    private OnNotesListChangedListener mListChangedListener;

    public NotesAdapter(Context context, List<Note> noteList, OnStartDragListener startDragListener, OnNotesListChangedListener listChangedListener) {
        this.context = context;
        this.noteList = noteList;
        this.mDragStartListener = startDragListener;
        this.mListChangedListener = listChangedListener;
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
    public void onBindViewHolder(final NotesHolder holder, int position) {
        Note note = noteList.get(position);
        holder.setNote(note);
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
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

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(noteList, fromPosition, toPosition);
        mListChangedListener.onNoteListChanged(noteList);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {

    }

    class NotesHolder extends RecyclerView.ViewHolder implements NotesTouchHelper.ViewHolder {

        @BindView(R.id.cvNoteCard)
        CardView cvNoteCard;

        @BindView(R.id.tvTitle)
        TextView tvTitle;

        @BindView(R.id.tvCreated)
        TextView tvCreated;

        @BindView(R.id.tvBody)
        TextView tvBody;

        NotesHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            cvNoteCard.setOnClickListener(new View.OnClickListener() {
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
            cvNoteCard.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), getNoteColor(color), null));
        }

        @Override
        public void onItemSelected() {
            cvNoteCard.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            cvNoteCard.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.colorBackground, null));
        }
    }

    interface OnNoteClickListener {
        void onNoteClicked(Note note);
    }
}
