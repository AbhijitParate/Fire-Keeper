package com.github.abhijitpparate.keeper.screen.note.presenter;

import com.github.abhijitpparate.keeper.Constants;
import com.github.abhijitpparate.keeper.data.database.Note;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@SuppressWarnings("FieldCanBeLocal")
@RunWith(MockitoJUnitRunner.class)
public class NotePresenterTest {

    // Presenter needs view and auth source
    // and provide a fake one as follows
    @Mock
    private NoteContract.View view;

    // Testing the presenter
    private NotePresenter presenter;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        // Instantiate the presenter using the fake auth source and view
        presenter = new NotePresenter(view);
        presenter.subscribe();
    }

    @Test
    public void onSaveClick(){
        presenter.onSaveClick();
        verify(view).showHomeScreen();
    }

    @Test
    public void onLoadNote(){
        Note note = Constants.FAKE_NOTES.get(0);
        presenter.loadNote(note.getNoteId());
        verify(view).setNote(note);
        verify(view).setNoteTitle(note.getTitle());
        verify(view).setNoteBody(note.getBody());
    }

    @Test
    public void onChecklistClick(){
        presenter.onChecklistClick(true);
        verify(view).switchToChecklist();
    }

    @Test
    public void onLocationClick(){
        presenter.onLocationClick();
        verify(view).showPlacePicker();
    }



}