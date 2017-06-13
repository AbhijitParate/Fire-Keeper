package com.github.abhijitpparate.keeps.screen.home.presenter

import com.github.abhijitpparate.keeps.data.auth.AuthInjector
import com.github.abhijitpparate.keeps.data.auth.AuthSource
import com.github.abhijitpparate.keeps.data.database.Note

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
class HomePresenterTest {

    // Presenter needs view and auth source
    // and provide a fake one as follows
    @Mock
    private val view: HomeContract.View? = null

    private val authSource = AuthInjector.authSource

    // Testing the presenter
    private var presenter: HomePresenter? = null

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        // Instantiate the presenter using the fake auth source and view
        presenter = HomePresenter(view)
    }

    @Test
    fun onLogoutClick() {
        presenter!!.onLogoutClick()
        verify<View>(view).showLoginScreen()
    }

    @Test
    fun onNewNoteClick() {
        authSource.setReturnFail(false)
        presenter!!.onNewNoteClick(HomeContract.NoteType.TEXT)
        verify<View>(view).showNewNoteScreen(HomeContract.NoteType.TEXT)
    }

    @Test
    fun onNoteClick() {
        authSource.setReturnFail(true)

        val note = Note()
        presenter!!.onNoteClick(note)

        verify<View>(view).showNoteScreen(note)
    }

}