package com.github.abhijitpparate.keeps.screen.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.abhijitpparate.checklistview.CheckListItem;
import com.github.abhijitpparate.checklistview.CheckListView;
import com.github.abhijitpparate.keeps.R;
import com.github.abhijitpparate.keeps.data.database.Note;
import com.github.abhijitpparate.keeps.screen.home.HomeActivity;
import com.github.abhijitpparate.keeps.screen.home.presenter.HomeContract;
import com.github.abhijitpparate.keeps.screen.note.presenter.NoteContract;
import com.github.abhijitpparate.keeps.screen.note.presenter.NotePresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoteActivity extends AppCompatActivity implements NoteContract.View {

    public static final String TAG = "NoteActivity";

    @BindView(R.id.edtTitle)
    TextView tvNoteTitle;

    @BindView(R.id.edtBody)
    TextView tvNoteBody;

    @BindView(R.id.checkListView)
    CheckListView checkListView;

    @BindView(R.id.newListNote)
    ToggleButton btnListNote;

    @BindView(R.id.newDrawingNote)
    ImageButton btnNewDrawing;

    @BindView(R.id.newAudioNote)
    ImageButton btnNewAudioNote;

    @BindView(R.id.newVideoNote)
    ImageButton btnNewVideoNote;

    @BindView(R.id.newImageNote)
    ImageButton btnNewImageNote;

    @BindView(R.id.btnOptions)
    ToggleButton options;

    @BindView(R.id.optionsPanel)
    View optionsPanel;

    NoteContract.Presenter presenter;

    String noteType;
    String noteId;

    private Note currentNote;

    boolean isOptionsOpen = false;

    Animation slideDown;

    Animation slideUp;

    Animation.AnimationListener animationListener;

    public static Intent getIntent(HomeActivity homeActivity, HomeContract.NoteType noteType) {
        Intent intent = new Intent(homeActivity, NoteActivity.class);
        intent.putExtra("type", noteType);
        return intent;
    }

    public static Intent getIntent(HomeActivity homeActivity, String noteId) {
        Intent intent = new Intent(homeActivity, NoteActivity.class);
        intent.putExtra("noteId", noteId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        if(getIntent().hasExtra("type")){
            noteType = getIntent().getStringExtra("type");
        }

        if(getIntent().hasExtra("noteId")) {
            noteId = getIntent().getStringExtra("noteId");
        }

        ButterKnife.bind(this);

        if (presenter == null) {
            presenter = new NotePresenter(this);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnListNote.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                presenter.onChecklistClick(isChecked);
            }
        });

        setOptionsAnimations();
    }

    private void setOptionsAnimations() {
        slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                optionsPanel.setVisibility(View.GONE);
                isOptionsOpen = !isOptionsOpen;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        slideUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                optionsPanel.setVisibility(View.VISIBLE);
                isOptionsOpen = !isOptionsOpen;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.unsubscribe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.actionSave:
                presenter.onSaveClick();
                return true;
        }
        return false;
    }

    @Override
    public String getNoteTitle() {
        return tvNoteTitle.getText().toString();
    }

    @Override
    public String getNoteBody() {
        return tvNoteBody.getText().toString();
    }

    @Override
    public String getCheckList() {
        return checkListView.getCheckListAsString();
    }

    @Override
    public void setNote(Note note) {
        currentNote = note;
    }

    @Override
    public void setNoteTitle(String title) {
        this.tvNoteTitle.setText(title);
    }

    @Override
    public void setNoteBody(String body) {
        this.tvNoteBody.setText(body);
    }

    @Override
    public void setNoteChecklist(List<CheckListItem> checklist) {
        checkListView.setChecklist(checklist);
    }

    @Override
    public void showHomeScreen() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void loadNoteIfAvailable() {
        if (noteId != null){
            presenter.loadNote(noteId);
        }
    }

    @Override
    public void setPresenter(NoteContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void makeToast(@StringRes int strId) {
        Toast.makeText(this, getString(strId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void switchToChecklist() {
        tvNoteBody.setVisibility(View.GONE);
        checkListView.setVisibility(View.VISIBLE);
        btnListNote.setChecked(true);
    }

    @Override
    public void switchToText() {
        tvNoteBody.setVisibility(View.VISIBLE);
        checkListView.setVisibility(View.GONE);
        btnListNote.setChecked(false);
    }

    @OnClick(R.id.newDrawingNote)
    void onDrawingClick(View view){

    }

    @OnClick(R.id.newAudioNote)
    void onAudioClick(View view){

    }

    @OnClick(R.id.newVideoNote)
    void onVideoClick(View view){

    }

    @OnClick(R.id.newImageNote)
    void onImageClick(View view){

    }

    @OnClick(R.id.btnOptions)
    void onOptionClick(View view){
        Log.d(TAG, "onOptionClick: ");
        if (isOptionsOpen){
            optionsPanel.startAnimation(slideDown);
        } else {
            optionsPanel.startAnimation(slideUp);
        }
    }
}
