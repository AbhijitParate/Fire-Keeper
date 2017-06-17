package com.github.abhijitpparate.keeper.screen.note

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
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
import com.github.abhijitpparate.keeper.R
import com.github.abhijitpparate.keeper.data.database.Note
import com.github.abhijitpparate.keeper.screen.home.HomeActivity
import com.github.abhijitpparate.keeper.screen.home.presenter.HomeContract
import com.github.abhijitpparate.keeper.screen.note.presenter.NoteContract
import com.github.abhijitpparate.keeper.screen.note.presenter.NotePresenter
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class NoteActivity : AppCompatActivity(), NoteContract.View {

    internal val TAG:String = "NoteActivity"

    internal val PLACE_PICKER_REQUEST:Int = 123
    internal val PERMISSIONS_ACCESS_FINE_LOCATION:Int = 123
    internal val MAP_VIEW_BUNDLE_KEY:String = "MapViewBundleKey"

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

    @BindView(R.id.mvLocation)
    lateinit var mvLocation: MapView

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

    private var noteType: String? = null
    private var noteId: String? = null

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

        var mapViewBundle:Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }
        mvLocation.onCreate(mapViewBundle)
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
        mvLocation.onResume()
    }

    override fun onPause() {
        super.onPause()
        mPresenter.unsubscribe()
        mvLocation.onPause()
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

    override fun getNoteUuid(): String? {
        return noteId
    }

    override fun getNoteTitle(): String? {
        return tvNoteTitle.text.toString()
    }

    override fun getNoteBody(): String? {
        return tvNoteBody.text.toString()
    }

    override fun getNoteCheckList(): String? {
        return checkListView.checkListAsString
    }

    override fun getNoteColor(): String? {
        return currentNote.color
    }

    override fun getNotePlace(): Note.Place? {
        return currentNote.place
    }

    override fun setNote(note: Note) {
        currentNote = note
    }

    override fun setNoteTitle(title: String?) {
        tvNoteTitle.text = title
    }

    override fun setNoteBody(body: String?) {
        tvNoteBody.text = body
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

        actionNoteColor.check(actionNoteColor.findViewWithTag(colorString.toLowerCase()).id)

        currentNote.color = colorString
    }

    override fun setNoteLocation(place: Note.Place) {
        Log.d(TAG, "setNoteLocation: ")
        makeToast("Location added")
        currentNote.place = place
        mvLocation.visibility = View.VISIBLE
        mvLocation.getMapAsync { googleMap ->
            Log.d(TAG, "getMapAsync" )

            place.location?.let {

                val latlng: LatLng = LatLng(-122.0840,37.4220)
                googleMap.addMarker(MarkerOptions().position(latlng).title(place.name))
                googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                val cameraPosition: CameraPosition
                        = CameraPosition.Builder()
                        .target(latlng)
                        .zoom(17.0f)
                        .build()
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        }
    }

    override fun showOptionsPanel() {
        optionsPanel.startAnimation(slideUp)
    }

    override fun hideOptionsPanel() {
        optionsPanel.startAnimation(slideDown)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSIONS_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    showLocationPicker()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK){
            val selection:Place = PlacePicker.getPlace(this, data)
            mPresenter.onLocationSelected(selection)
        }
    }

    override fun showLocationPicker() {
        if ( com.github.abhijitpparate.keeper.utils.PermissionUtils.hasGpsPermission(this)) {
            val builder = PlacePicker.IntentBuilder()
            try {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)
            } catch (e: GooglePlayServicesRepairableException) {
                Toast.makeText(this, "GooglePlayServicesNotAvailableException", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } catch (e: GooglePlayServicesNotAvailableException) {
                Toast.makeText(this, "GooglePlayServicesNotAvailableException", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        } else {
            requestLocationPermission()
        }
    }

    fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, Array(1, init = {Manifest.permission.ACCESS_FINE_LOCATION}), PERMISSIONS_ACCESS_FINE_LOCATION)
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

    @OnClick(R.id.newLocation)
    internal fun onLocationClick(view: View) {
        mPresenter.onLocationClick()
    }

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
