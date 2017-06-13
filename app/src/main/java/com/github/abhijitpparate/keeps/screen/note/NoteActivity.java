package com.github.abhijitpparate.keeps.screen.note;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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

import static com.github.abhijitpparate.keeps.data.database.Note.NoteColor.WHITE;

public class NoteActivity extends AppCompatActivity implements NoteContract.View {

    public static final String TAG = "NoteActivity";

    @BindView(R.id.clNote)
    ConstraintLayout clNote;

    @BindView(R.id.rlNewNote)
    RelativeLayout rlNewNote;

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

    @BindView(R.id.actionDelete)
    View actionDelete;

    @BindView(R.id.actionSend)
    View actionSend;

    @BindView(R.id.actionLabel)
    View actionLabel;

    @BindView(R.id.actionDuplicate)
    View actionDuplicate;

    @BindView(R.id.actionNoteColor)
    RadioGroup actionNoteColor;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    NoteContract.Presenter presenter;

    String noteType;
    String noteId;

    private Note currentNote = new Note();

    Animation slideDown;

    Animation slideUp;

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

        actionNoteColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                String colorText = (String) group.findViewById(checkedId).getTag();

                switch (colorText){
                    case "white" :
                        presenter.onColorSelected(NoteContract.NoteColor.WHITE);
                        break;
                    case "red" :
                        presenter.onColorSelected(NoteContract.NoteColor.RED);
                        break;
                    case "green" :
                        presenter.onColorSelected(NoteContract.NoteColor.GREEN);
                        break;
                    case "yellow" :
                        presenter.onColorSelected(NoteContract.NoteColor.YELLOW);
                        break;
                    case "blue" :
                        presenter.onColorSelected(NoteContract.NoteColor.BLUE);
                        break;
                    case "orange" :
                        presenter.onColorSelected(NoteContract.NoteColor.ORANGE);
                        break;
                    default:
                        presenter.onColorSelected(NoteContract.NoteColor.DEFAULT);
                        break;
                }
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
            case android.R.id.home:
                finish();
                return true;
            case R.id.actionSave:
                presenter.onSaveClick();
                return true;
        }
        return false;
    }

    @Override
    public String getNoteId() {
        return noteId;
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
    public String getNoteColor() {
        return currentNote.getColor();
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
    public void setNoteColor(@ColorRes int colorInt, String colorString) {
        Log.d(TAG, "setNoteColor: ");
        int c = ResourcesCompat.getColor(getResources(), colorInt, null);

        clNote.setBackgroundColor(c);
        rlNewNote.setBackgroundColor(c);

        currentNote.setColor(colorString);
    }

    @Override
    public void showOptionsPanel() {
        optionsPanel.startAnimation(slideUp);
    }

    @Override
    public void hideOptionsPanel() {
        optionsPanel.startAnimation(slideDown);
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
    public void showProgressbar(boolean bool) {
        if (bool) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
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
        presenter.onDrawingClick();
    }

    @OnClick(R.id.newAudioNote)
    void onAudioClick(View view){
        presenter.onAudioClick();
    }

    @OnClick(R.id.newVideoNote)
    void onVideoClick(View view){
        presenter.onVideoClick();
    }

    @OnClick(R.id.newImageNote)
    void onImageClick(View view){
        presenter.onImageClick();
    }

    @OnClick(R.id.btnOptions)
    void onOptionClick(View view){
        presenter.onOptionsClick();
    }

    @OnClick(R.id.actionDelete)
    void onDeleteClick(View view){
        presenter.onDeleteClick();
    }

    @OnClick(R.id.actionSend)
    void onSendClick(View view){
        presenter.onSendClick();
    }

    @OnClick(R.id.actionLabel)
    void onLabelClick(View view){
        presenter.onLabelClick();
    }

    @OnClick(R.id.actionDuplicate)
    void onDuplicateClick(View view){
        presenter.onDuplicateClick();
    }
}
