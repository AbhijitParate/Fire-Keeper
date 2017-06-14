package com.github.abhijitpparate.keeps.screen.note

import android.content.Intent
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.constraint.ConstraintLayout
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.github.abhijitpparate.checklistview.CheckListItem
import com.github.abhijitpparate.checklistview.CheckListView
import com.github.abhijitpparate.keeps.R
import com.github.abhijitpparate.keeps.data.database.Note
import com.github.abhijitpparate.keeps.screen.home.HomeActivity
import com.github.abhijitpparate.keeps.screen.home.presenter.HomeContract
import com.github.abhijitpparate.keeps.screen.note.presenter.NoteContract
import com.github.abhijitpparate.keeps.screen.note.presenter.NotePresenter

class NoteActivity : AppCompatActivity(), NoteContract.View {

    @BindView(R.id.clNote)
    lateinit var clNote: ConstraintLayout

    @BindView(R.id.rlNote)
    lateinit var rlNote: RelativeLayout

    @BindView(R.id.rlNewNote)
    lateinit var rlNewNote: RelativeLayout

    @BindView(R.id.edtTitle)
    lateinit var tvNoteTitle: TextView

    @BindView(R.id.edtBody)
    lateinit var tvNoteBody: TextView

    @BindView(R.id.checkListView)
    lateinit var checkListView: CheckListView

    @BindView(R.id.newListNote)
    lateinit var btnListNote: ToggleButton

    @BindView(R.id.newDrawingNote)
    lateinit var btnNewDrawing: ImageButton

    @BindView(R.id.newAudioNote)
    lateinit var btnNewAudioNote: ImageButton

    @BindView(R.id.newVideoNote)
    lateinit var btnNewVideoNote: ImageButton

    @BindView(R.id.newImageNote)
    lateinit var btnNewImageNote: ImageButton

    @BindView(R.id.btnOptions)
    lateinit var btnOptions: ToggleButton

    @BindView(R.id.optionsPanel)
    lateinit var optionsPanel: View

    @BindView(R.id.actionDelete)
    lateinit var actionDelete: View

    @BindView(R.id.actionSend)
    lateinit var actionSend: View

    @BindView(R.id.actionLabel)
    lateinit var actionLabel: View

    @BindView(R.id.actionDuplicate)
    lateinit var actionDuplicate: View

    @BindView(R.id.actionNoteColor)
    lateinit var actionNoteColor: RadioGroup

    @BindView(R.id.progressBar)
    lateinit var progressBar: ProgressBar

    lateinit var mPresenter: NoteContract.Presenter

    internal var noteType: String? = null
    internal var noteId: String? = null

    private var currentNote = Note()

    lateinit var slideDown: Animation

    lateinit var slideUp: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        if (intent.hasExtra("type")) {
            noteType = intent.getStringExtra("type")
        }

        if (intent.hasExtra("noteId")) {
            noteId = intent.getStringExtra("noteId")
        }

        ButterKnife.bind(this)

        mPresenter = NotePresenter(this)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        btnListNote.setOnCheckedChangeListener { _, isChecked -> mPresenter.onChecklistClick(isChecked) }

        actionNoteColor.setOnCheckedChangeListener { group, checkedId ->
            val colorText = group.findViewById(checkedId).tag as String

            when (colorText) {
                "white" -> mPresenter.onColorSelected(NoteContract.NoteColor.WHITE)
                "red" -> mPresenter.onColorSelected(NoteContract.NoteColor.RED)
                "green" -> mPresenter.onColorSelected(NoteContract.NoteColor.GREEN)
                "yellow" -> mPresenter.onColorSelected(NoteContract.NoteColor.YELLOW)
                "blue" -> mPresenter.onColorSelected(NoteContract.NoteColor.BLUE)
                "orange" -> mPresenter.onColorSelected(NoteContract.NoteColor.ORANGE)
                else -> mPresenter.onColorSelected(NoteContract.NoteColor.DEFAULT)
            }
        }

        setOptionsAnimations()

        btnOptions.setOnCheckedChangeListener({ _, _ -> mPresenter.onOptionsClick() })
    }

    private fun setOptionsAnimations() {
        slideDown = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_down)
        slideUp = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up)

        slideDown.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                optionsPanel.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        slideUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                optionsPanel.visibility = View.VISIBLE
                optionsPanel.requestFocus()
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

    }

    override fun onResume() {
        super.onResume()
        mPresenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        mPresenter.unsubscribe()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_note, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.actionSave -> {
                mPresenter.onSaveClick()
                return true
            }
        }
        return false
    }

    override var noteUuid: String
        get() = noteId!!
        set(noteId) {
            this.noteId = noteId
        }

    override var noteTitle: String
        get() = tvNoteTitle.text.toString()
        set(title) {
            this.tvNoteTitle.text = title
        }

    override var noteBody: String
        get() = tvNoteBody.text.toString()
        set(body) {
            this.tvNoteBody.text = body
        }

    override val checkList: String
        get() = checkListView.checkListAsString

    override val noteColor: String
        get() = currentNote.color.toString()

    override fun setNote(note: Note) {
        currentNote = note
    }

    override fun setNoteChecklist(checklist: List<CheckListItem>) {
        checkListView.setChecklist(checklist)
    }

    override fun setNoteColor(@ColorRes colorInt: Int, colorString: String) {
        Log.d(TAG, "setNoteColor: ")
        val c = ResourcesCompat.getColor(resources, colorInt, null)

        clNote.setBackgroundColor(c)
        optionsPanel.setBackgroundColor(c)
        rlNewNote.setBackgroundColor(c)

        currentNote.color = colorString
    }

    override fun showOptionsPanel() {
        optionsPanel.startAnimation(slideUp)
    }

    override fun hideOptionsPanel() {
        optionsPanel.startAnimation(slideDown)
    }

    override fun showHomeScreen() {
        val i = Intent(this, HomeActivity::class.java)
        startActivity(i)
        finish()
    }

    override fun loadNoteIfAvailable() {
        if (noteId != null) {
            mPresenter.loadNote(noteId!!)
        }
    }

    override fun setPresenter(presenter: NoteContract.Presenter) {
        this.mPresenter = presenter
    }

    override fun showProgressbar(bool: Boolean) {
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

    override fun switchToChecklist() {
        tvNoteBody.visibility = View.GONE
        checkListView.visibility = View.VISIBLE
        btnListNote.isChecked = true
    }

    override fun switchToText() {
        tvNoteBody.visibility = View.VISIBLE
        checkListView.visibility = View.GONE
        btnListNote.isChecked = false
    }

    @OnClick(R.id.rlNote)
    internal fun onNoteClick(view: View) {
        btnOptions.isChecked = false
    }

    @OnClick(R.id.newDrawingNote)
    internal fun onDrawingClick(view: View) {
        mPresenter.onDrawingClick()
    }

    @OnClick(R.id.newAudioNote)
    internal fun onAudioClick(view: View) {
        mPresenter.onAudioClick()
    }

    @OnClick(R.id.newVideoNote)
    internal fun onVideoClick(view: View) {
        mPresenter.onVideoClick()
    }

    @OnClick(R.id.newImageNote)
    internal fun onImageClick(view: View) {
        mPresenter.onImageClick()
    }

//    @OnClick(R.id.btnOptions)
//    internal fun onOptionClick(view: View) {
//        mPresenter.onOptionsClick()
//    }

    @OnClick(R.id.actionDelete)
    internal fun onDeleteClick(view: View) {
        mPresenter.onDeleteClick()
    }

    @OnClick(R.id.actionSend)
    internal fun onSendClick(view: View) {
        mPresenter.onSendClick()
    }

    @OnClick(R.id.actionLabel)
    internal fun onLabelClick(view: View) {
        mPresenter.onLabelClick()
    }

    @OnClick(R.id.actionDuplicate)
    internal fun onDuplicateClick(view: View) {
        mPresenter.onDuplicateClick()
    }

    companion object {

        val TAG = "NoteActivity"

        fun getIntent(homeActivity: HomeActivity, noteType: HomeContract.NoteType): Intent {
            val intent = Intent(homeActivity, NoteActivity::class.java)
            intent.putExtra("type", noteType)
            return intent
        }

        fun getIntent(homeActivity: HomeActivity, noteId: String): Intent {
            val intent = Intent(homeActivity, NoteActivity::class.java)
            intent.putExtra("noteId", noteId)
            return intent
        }
    }
}
