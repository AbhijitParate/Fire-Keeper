package com.github.abhijitpparate.keeper.screen.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.abhijitpparate.keeper.R;
import com.github.abhijitpparate.keeper.data.database.Note;
import com.github.abhijitpparate.keeper.data.database.Profile;
import com.github.abhijitpparate.keeper.screen.home.presenter.HomeContract;
import com.github.abhijitpparate.keeper.screen.home.presenter.HomePresenter;
import com.github.abhijitpparate.keeper.screen.login.LoginActivity;
import com.github.abhijitpparate.keeper.screen.note.NoteActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements HomeContract.View, NotesAdapter.OnNoteClickListener, OnStartDragListener, OnNotesListChangedListener {

    public static final String TAG = "HomeActivity";

    private static final int ACTION_NEW_NOTE = 568;

    @BindView(R.id.rootView)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.rvNotes)
    RecyclerView rvNotes;

    @BindView(R.id.newTextNote)
    Button btnNewTextNote;

    @BindView(R.id.newImageNote)
    ImageButton btnNewImageNote;

    @BindView(R.id.newVideoNote)
    ImageButton btnNewVideoNote;

    @BindView(R.id.newAudioNote)
    ImageButton btnNewAudioNote;

    @BindView(R.id.newLocationNote)
    ImageButton btnNewLocationNote;

    @BindView(R.id.newDrawingNote)
    ImageButton btnNewDrawing;

    @BindView(R.id.newListNote)
    ImageButton btnNewListNote;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    HomeContract.Presenter presenter;

    NotesAdapter notesAdapter;

    ItemTouchHelper.Callback callback;
    private ItemTouchHelper mItemTouchHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (presenter == null){
            presenter = new HomePresenter(this);
        }

        notesAdapter = new NotesAdapter(this , new ArrayList<Note>(), this, this);
        notesAdapter.setNoteClickListener(this);

        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        rvNotes.setItemAnimator(new DefaultItemAnimator());

        // TODO: 6/17/2017 Add long press and select to delete

        callback = new NotesTouchHelperCallback(notesAdapter);

        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvNotes);

        rvNotes.setAdapter(notesAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.onRefresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.subscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unsubscribe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.actionLogout:
                presenter.onLogoutClick();
                return true;
            case R.id.actionView:
                return true;
            case R.id.actionSearch:
                return true;
        }

        return false;
    }

    @Override
    public void setUserInfo(Profile profile) {
        Snackbar.make(coordinatorLayout, "Logged in as " + profile.getName(), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setNotes(List<Note> notes) {
        notesAdapter.updateDataset(notes);
    }

    @Override
    public void showLoginScreen() {
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
        finish();
    }

    @Override
    public void showNewNoteScreen(HomeContract.NoteType noteType) {
        Intent i = NoteActivity.getIntent(this, noteType);
//        startActivityForResult(i, ACTION_NEW_NOTE);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
    }

    @Override
    public void showNoteScreen(Note note) {
        Intent i = NoteActivity.getIntent(this, note.getNoteId());
        startActivity(i);
    }

    @Override
    public void setPresenter(HomeContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showProgressBar(boolean bool) {
        if (bool){
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

    @OnClick(R.id.newTextNote)
    public void onNewTextNoteClick(View view){
        presenter.onNewNoteClick(HomeContract.NoteType.TEXT);
    }

    @OnClick(R.id.newImageNote)
    public void onNewImageNoteClick(View view){
        presenter.onNewNoteClick(HomeContract.NoteType.IMAGE);
    }

    @OnClick(R.id.newVideoNote)
    public void onNewVideoNoteClick(View view){
        makeToast(view.getContentDescription().toString());
        presenter.onNewNoteClick(HomeContract.NoteType.VIDEO);
    }

    @OnClick(R.id.newAudioNote)
    public void onNewAudioNoteClick(View view){
        presenter.onNewNoteClick(HomeContract.NoteType.AUDIO);
    }

    @OnClick(R.id.newLocationNote)
    public void onNewLocationNoteClick(View view){
        makeToast(view.getContentDescription().toString());
        presenter.onNewNoteClick(HomeContract.NoteType.LOCATION);
    }

    @OnClick(R.id.newDrawingNote)
    public void onNewDrawingNoteClick(View view){
        presenter.onNewNoteClick(HomeContract.NoteType.DRAWING);
    }

    @OnClick(R.id.newListNote)
    public void onNewListNoteClick(View view){
        presenter.onNewNoteClick(HomeContract.NoteType.LIST);
    }

    @Override
    public void onNoteClicked(Note note) {
        presenter.onNoteClick(note);
    }

    @Override
    public void onNoteListChanged(List<Note> customers) {

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
