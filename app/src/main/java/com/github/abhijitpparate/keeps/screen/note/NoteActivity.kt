package com.github.abhijitpparate.keeps.screen.note

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.constraint.ConstraintLayout
import android.support.v4.app.NavUtils
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton

import com.github.abhijitpparate.checklistview.CheckListItem
import com.github.abhijitpparate.checklistview.CheckListView
import com.github.abhijitpparate.keeps.R
import com.github.abhijitpparate.keeps.data.database.Note
import com.github.abhijitpparate.keeps.screen.home.HomeActivity
import com.github.abhijitpparate.keeps.screen.home.presenter.HomeContract
import com.github.abhijitpparate.keeps.screen.note.presenter.NoteContract
import com.github.abhijitpparate.keeps.screen.note.presenter.NotePresenter

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

import com.github.abhijitpparate.keeps.data.database.Note.NoteColor.WHITE

class NoteActivity : AppCompatActivity(), NoteContract.View {

    @BindView(R.id.clNote)
    internal var clNote: ConstraintLayout? = null

    @BindView(R.id.rlNewNote)
    internal var rlNewNote: RelativeLayout? = null

    @BindView(R.id.edtTitle)
    internal var tvNoteTitle: TextView? = null

    @BindView(R.id.edtBody)
    internal var tvNoteBody: TextView? = null

    @BindView(R.id.checkListView)
    internal var checkListView: CheckListView? = null

    @BindView(R.id.newListNote)
    internal var btnListNote: ToggleButton? = null

    @BindView(R.id.newDrawingNote)
    internal var btnNewDrawing: ImageButton? = null

    @BindView(R.id.newAudioNote)
    internal var btnNewAudioNote: ImageButton? = null

    @BindView(R.id.newVideoNote)
    internal var btnNewVideoNote: ImageButton? = null

    @BindView(R.id.newImageNote)
    internal var btnNewImageNote: ImageButton? = null

    @BindView(R.id.btnOptions)
    internal var options: ToggleButton? = null

    @BindView(R.id.optionsPanel)
    internal var optionsPanel: View? = null

    @BindView(R.id.actionDelete)
    internal var actionDelete: View? = null

    @BindView(R.id.actionSend)
    internal var actionSend: View? = null

    @BindView(R.id.actionLabel)
    internal var actionLabel: View? = null

    @BindView(R.id.actionDuplicate)
    internal var actionDuplicate: View? = null

    @BindView(R.id.actionNoteColor)
    internal var actionNoteColor: RadioGroup? = null

    @BindView(R.id.progressBar)
    internal var progressBar: ProgressBar? = null

    internal var presenter: NoteContract.Presenter? = null

    internal var noteType: String = ""
    override var noteId: String = ""
        internal set

    private var currentNote = Note()

    internal var slideDown: Animation? = null

    internal var slideUp: Animation? = null

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

        if (presenter == null) {
            presenter = NotePresenter(this)
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        btnListNote!!.setOnCheckedChangeListener { buttonView, isChecked -> presenter!!.onChecklistClick(isChecked) }

        actionNoteColor!!.setOnCheckedChangeListener { group, checkedId ->
            val colorText = group.findViewById(checkedId).tag as String

            when (colorText) {
                "white" -> presenter!!.onColorSelected(NoteContract.NoteColor.WHITE)
                "red" -> presenter!!.onColorSelected(NoteContract.NoteColor.RED)
                "green" -> presenter!!.onColorSelected(NoteContract.NoteColor.GREEN)
                "yellow" -> presenter!!.onColorSelected(NoteContract.NoteColor.YELLOW)
                "blue" -> presenter!!.onColorSelected(NoteContract.NoteColor.BLUE)
                "orange" -> presenter!!.onColorSelected(NoteContract.NoteColor.ORANGE)
                else -> presenter!!.onColorSelected(NoteContract.NoteColor.DEFAULT)
            }
        }

        setOptionsAnimations()
    }

    private fun setOptionsAnimations() {
        slideDown = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_down)
        slideUp = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up)

        slideDown?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                optionsPanel!!.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        slideUp?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                optionsPanel!!.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

    }

    override fun onResume() {
        super.onResume()
        presenter!!.subscribe()
    }

    override fun onPause() {
        super.onPause()
        presenter!!.unsubscribe()
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
                presenter!!.onSaveClick()
                return true
            }
        }
        return false
    }

    override var noteTitle: String
        get() = tvNoteTitle!!.text.toString()
        set(title) {
            this.tvNoteTitle!!.text = title
        }

    override var noteBody: String
        get() = tvNoteBody!!.text.toString()
        set(body) {
            this.tvNoteBody!!.text = body
        }

    override val checkList: String
        get() = checkListView!!.checkListAsString

    override val noteColor: String
        get() = currentNote.color.toString()

    override fun setNote(note: Note) {
        currentNote = note
    }

    override fun setNoteChecklist(checklist: List<CheckListItem>) {
        checkListView!!.setChecklist(checklist)
    }

    override fun setNoteColor(@ColorRes colorInt: Int, colorString: String) {
        Log.d(TAG, "setNoteColor: ")
        val c = ResourcesCompat.getColor(resources, colorInt, null)

        clNote!!.setBackgroundColor(c)
        rlNewNote!!.setBackgroundColor(c)

        currentNote.color = colorString
    }

    override fun showOptionsPanel() {
        optionsPanel!!.startAnimation(slideUp)
    }

    override fun hideOptionsPanel() {
        optionsPanel!!.startAnimation(slideDown)
    }

    override fun showHomeScreen() {
        val i = Intent(this, HomeActivity::class.java)
        startActivity(i)
        finish()
    }

    override fun loadNoteIfAvailable() {
        if (noteId != null) {
            presenter!!.loadNote(noteId)
        }
    }

    override fun setPresenter(presenter: NoteContract.Presenter) {
        this.presenter = presenter
    }

    override fun showProgressbar(bool: Boolean) {
        if (bool) {
            progressBar!!.visibility = View.VISIBLE
        } else {
            progressBar!!.visibility = View.GONE
        }
    }

    override fun makeToast(@StringRes strId: Int) {
        Toast.makeText(this, getString(strId), Toast.LENGTH_SHORT).show()
    }

    override fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun switchToChecklist() {
        tvNoteBody!!.visibility = View.GONE
        checkListView!!.visibility = View.VISIBLE
        btnListNote!!.isChecked = true
    }

    override fun switchToText() {
        tvNoteBody!!.visibility = View.VISIBLE
        checkListView!!.visibility = View.GONE
        btnListNote!!.isChecked = false
    }

    @OnClick(R.id.newDrawingNote)
    internal fun onDrawingClick(view: View) {
        presenter!!.onDrawingClick()
    }

    @OnClick(R.id.newAudioNote)
    internal fun onAudioClick(view: View) {
        presenter!!.onAudioClick()
    }

    @OnClick(R.id.newVideoNote)
    internal fun onVideoClick(view: View) {
        presenter!!.onVideoClick()
    }

    @OnClick(R.id.newImageNote)
    internal fun onImageClick(view: View) {
        presenter!!.onImageClick()
    }

    @OnClick(R.id.btnOptions)
    internal fun onOptionClick(view: View) {
        presenter!!.onOptionsClick()
    }

    @OnClick(R.id.actionDelete)
    internal fun onDeleteClick(view: View) {
        presenter!!.onDeleteClick()
    }

    @OnClick(R.id.actionSend)
    internal fun onSendClick(view: View) {
        presenter!!.onSendClick()
    }

    @OnClick(R.id.actionLabel)
    internal fun onLabelClick(view: View) {
        presenter!!.onLabelClick()
    }

    @OnClick(R.id.actionDuplicate)
    internal fun onDuplicateClick(view: View) {
        presenter!!.onDuplicateClick()
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
