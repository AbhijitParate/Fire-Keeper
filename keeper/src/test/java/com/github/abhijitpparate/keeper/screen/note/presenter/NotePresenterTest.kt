package com.github.abhijitpparate.keeper.screen.note.presenter

import com.github.abhijitpparate.keeper.Constants

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.runners.MockitoJUnitRunner

import org.mockito.Mockito.verify

/**
 * Example local unit test, which will execute on the development machine (host).

 * @see [Testing documentation](http://d.android.com/tools/testing)
 */

@RunWith(MockitoJUnitRunner::class)
class NotePresenterTest {

    // Presenter needs view and auth source
    // and provide a fake one as follows
    @Mock
    private val view: NoteContract.View? = null

    // Testing the presenter
    private var presenter: NotePresenter? = null

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        // Instantiate the presenter using the fake auth source and view
        presenter = view?.let { NotePresenter(it) }
        presenter!!.subscribe()
    }

    @Test
    fun onSaveClick() {
        presenter!!.onSaveClick()
        verify<NoteContract.View>(view).showHomeScreen()
    }

    @Test
    fun onLoadNote() {
        val note = Constants.FAKE_NOTES[0]
        presenter!!.loadNote(note.noteId!!)
        verify<NoteContract.View>(view).setNote(note)
        verify<NoteContract.View>(view).noteTitle = note.title!!
        verify<NoteContract.View>(view).noteBody = note.body!!
    }

}