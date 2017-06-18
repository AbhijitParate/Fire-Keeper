package com.github.abhijitpparate.keeper.screen.note;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.github.abhijitpparate.checklistview.CheckListItem;
import com.github.abhijitpparate.checklistview.CheckListView;
import com.github.abhijitpparate.keeper.R;
import com.github.abhijitpparate.keeper.data.database.Note;
import com.github.abhijitpparate.keeper.data.storage.File;
import com.github.abhijitpparate.keeper.screen.home.HomeActivity;
import com.github.abhijitpparate.keeper.screen.home.presenter.HomeContract;
import com.github.abhijitpparate.keeper.screen.note.presenter.NoteContract;
import com.github.abhijitpparate.keeper.screen.note.presenter.NotePresenter;
import com.github.abhijitpparate.keeper.utils.FileUtils;
import com.github.abhijitpparate.keeper.utils.PermissionUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoteActivity extends AppCompatActivity implements NoteContract.View, RadioGroup.OnCheckedChangeListener {

    public static final String TAG = "NoteActivity";

    private static final int PLACE_PICKER_REQUEST = 123;
    private static final int FILE_PICKER_REQUEST = 527;
    private static final int PERMISSIONS_ACCESS_FINE_LOCATION = 978;
    public static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

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

    @BindView(R.id.ivImage)
    ImageView ivImage;

    @BindView(R.id.mvPlace)
    MapView mvPlace;

    @BindView(R.id.newListNote)
    ToggleButton btnListNote;

    @BindView(R.id.newDrawingNote)
    ImageButton btnNewDrawing;

    @BindView(R.id.newLocationNote)
    ImageButton btnNewLocation;

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

    HomeContract.NoteType noteType = HomeContract.NoteType.TEXT;
    String noteId;

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
            noteType = (HomeContract.NoteType) getIntent().getExtras().get("type");
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

        actionNoteColor.setOnCheckedChangeListener(this);

        setOptionsAnimations();

        initMap(savedInstanceState);

        startNewNote();
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

    private void initMap(Bundle savedInstanceState){
        MapsInitializer.initialize(this);
        mvPlace.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.subscribe();
        mvPlace.onResume();
    }

    private void startNewNote() {
        makeToast(String.valueOf(noteType));
        switch (noteType){
            case TEXT: break;
            case LIST: switchToChecklist(); break;
            case LOCATION: showPlacePicker(); break;
            case AUDIO: break;
            case IMAGE: break;
            case VIDEO: break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.unsubscribe();
        mvPlace.onPause();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_ACCESS_FINE_LOCATION:
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    showPlacePicker();
                }
        }
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_ACCESS_FINE_LOCATION
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case PLACE_PICKER_REQUEST:
                    Place selection = PlacePicker.getPlace(this, data);
                    presenter.onPlaceSelected(selection);
                    break;
                case FILE_PICKER_REQUEST:
                    Uri uri = data.getData();
                    if (uri != null){
                        File file = FileUtils.getFile(this, File.Type.IMAGE, uri);
                        Log.e(TAG, "onActivityResult: " + file.getName());
                        presenter.onFileSelected(file, uri);
                        setImage(uri);
                    }
                    break;
            }
        }
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

//    @Override
//    public String getNoteColor() {
//        return findViewById(actionNoteColor.getCheckedRadioButtonId()).getTag().toString();
//    }
//
//    @Override
//    public Note.Place getNotePlace() {
//        mvPlace.getMapAsync();
//        return currentNote.getPlace();
//    }
//
//    @Override
//    public File getFile() {
//        return currentNote.getFile();
//    }

//    @Override
//    public void setNote(Note note) {
//        currentNote = note;
//    }

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
        actionNoteColor.check(actionNoteColor.findViewWithTag(colorString.toLowerCase()).getId());
//        currentNote.setColor(colorString);
    }

    @Override
    public void setNotePlace(final Note.Place place) {
        makeToast(place.getName());
//        currentNote.setPlace(place);
        mvPlace.setVisibility(View.VISIBLE);
        mvPlace.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                LatLng latLng;
                if (place.getLocation() != null) {
                    latLng = new LatLng(place.getLocation().getLatitude(), place.getLocation().getLongitude());
                } else {
                    latLng = new LatLng(0f,0f);
                }
                googleMap.addMarker(new MarkerOptions().title(place.getName()).position(latLng));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15.0f).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
    }

    @Override
    public void setNoteFile(File file) {
        makeToast(file.getName() + " received");
        Glide.with(this)
                .load(file.getUrl())
                .into(ivImage);
        ivImage.setVisibility(View.VISIBLE);
    }

    public void setImage(Uri uri){
        Glide.with(this)
                .load(uri)
                .into(ivImage);
        ivImage.setVisibility(View.VISIBLE);
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
    public void showPlacePicker() {
        if (PermissionUtils.hasLocationPermission(this)){
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                makeToast(e.getMessage());
            }
        } else {
            requestLocationPermission();
        }
    }

    @Override
    public void showFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_PICKER_REQUEST);
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

    @OnClick(R.id.newLocationNote)
    void onLocationClick(View view){
        presenter.onLocationClick();
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
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
}
