package com.github.abhijitpparate.keeps.screen.home

import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.github.abhijitpparate.keeps.R
import com.github.abhijitpparate.keeps.data.database.Note
import com.github.abhijitpparate.keeps.data.database.Profile
import com.github.abhijitpparate.keeps.screen.home.presenter.HomeContract
import com.github.abhijitpparate.keeps.screen.home.presenter.HomePresenter
import com.github.abhijitpparate.keeps.screen.login.LoginActivity
import com.github.abhijitpparate.keeps.screen.note.NoteActivity
import java.util.*

class HomeActivity : AppCompatActivity(), HomeContract.View, NotesAdapter.OnNoteClickListener {

    @BindView(R.id.rootView)
    lateinit var coordinatorLayout: CoordinatorLayout

    @BindView(R.id.swipeRefreshLayout)
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    @BindView(R.id.rvNotes)
    lateinit var rvNotes: RecyclerView

    @BindView(R.id.newTextNote)
    lateinit var btnNewTextNote: Button

    @BindView(R.id.newImageNote)
    lateinit var btnNewImageNote: ImageButton

    @BindView(R.id.newVideoNote)
    lateinit var btnNewVideoNote: ImageButton

    @BindView(R.id.newAudioNote)
    lateinit var btnNewAudioNote: ImageButton

    @BindView(R.id.newDrawingNote)
    lateinit var btnNewDrawing: ImageButton

    @BindView(R.id.newListNote)
    lateinit var btnNewListNote: ImageButton

    @BindView(R.id.progressBar)
    lateinit var progressBar: ProgressBar

    lateinit var mPresenter: HomeContract.Presenter

    lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        ButterKnife.bind(this)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        mPresenter = HomePresenter(this)

        notesAdapter = NotesAdapter(this, ArrayList<Note>())
        notesAdapter.setNoteClickListener(this)

        rvNotes.adapter = notesAdapter
        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.itemAnimator = DefaultItemAnimator()

        swipeRefreshLayout.setOnRefreshListener {
            mPresenter.onRefresh()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onStart() {
        super.onStart()
        mPresenter.subscribe()
    }

    override fun onStop() {
        super.onStop()
        mPresenter.unsubscribe()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionLogout -> {
                mPresenter.onLogoutClick()
                return true
            }
            R.id.actionView -> return true
            R.id.actionSearch -> return true
        }

        return false
    }

    override fun setUserInfo(profile: Profile) {
        Snackbar.make(coordinatorLayout, "Logged in as " + profile.name, Snackbar.LENGTH_SHORT).show()
    }

    override fun setNotes(notes: List<Note>) {
        notesAdapter.updateDataset(notes)
    }

    override fun showLoginScreen() {
        val i = Intent(this, LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(i)
        finish()
    }

    override fun showNewNoteScreen(noteType: HomeContract.NoteType) {
        val i = NoteActivity.getIntent(this, noteType)
        //        startActivityForResult(i, ACTION_NEW_NOTE);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(i)
    }

    override fun showNoteScreen(note: Note) {
        val i = NoteActivity.getIntent(this, note.noteId.toString())
        startActivity(i)
    }

    override fun setPresenter(presenter: HomeContract.Presenter) {
        this.mPresenter = presenter
    }

    override fun showProgressBar(bool: Boolean) {
        if (bool) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    override fun makeToast(@StringRes strId: Int) {
        Toast.makeText(this, getString(strId), Toast.LENGTH_SHORT).show()
    }

    override fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @OnClick(R.id.newTextNote)
    fun onNewTextNoteClick(view: View) {
        makeToast(view.contentDescription.toString())
        mPresenter.onNewNoteClick(HomeContract.NoteType.TEXT)
    }

    @OnClick(R.id.newImageNote)
    fun onNewImageNoteClick(view: View) {
        makeToast(view.contentDescription.toString())
        mPresenter.onNewNoteClick(HomeContract.NoteType.IMAGE)
    }

    @OnClick(R.id.newVideoNote)
    fun onNewVideoNoteClick(view: View) {
        makeToast(view.contentDescription.toString())
        mPresenter.onNewNoteClick(HomeContract.NoteType.VIDEO)
    }

    @OnClick(R.id.newAudioNote)
    fun onNewAudioNoteClick(view: View) {
        makeToast(view.contentDescription.toString())
        mPresenter.onNewNoteClick(HomeContract.NoteType.AUDIO)
    }

    @OnClick(R.id.newDrawingNote)
    fun onNewDrawingNoteClick(view: View) {
        makeToast(view.contentDescription.toString())
        mPresenter.onNewNoteClick(HomeContract.NoteType.DRAWING)
    }

    @OnClick(R.id.newListNote)
    fun onNewListNoteClick(view: View) {
        makeToast(view.contentDescription.toString())
        mPresenter.onNewNoteClick(HomeContract.NoteType.LIST)
    }

    override fun onNoteClicked(note: Note) {
        Log.d(TAG, "onNoteClicked: ")
        mPresenter.onNoteClick(note)
    }

    companion object {

        val TAG = "HomeActivity"

        private val ACTION_NEW_NOTE = 568
    }
}
