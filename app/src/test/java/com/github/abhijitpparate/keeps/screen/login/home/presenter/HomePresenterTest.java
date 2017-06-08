package com.github.abhijitpparate.keeps.screen.login.home.presenter;

import com.github.abhijitpparate.keeps.R;
import com.github.abhijitpparate.keeps.data.auth.AuthInjector;
import com.github.abhijitpparate.keeps.data.auth.AuthSource;
import com.github.abhijitpparate.keeps.data.database.Note;
import com.github.abhijitpparate.keeps.screen.home.presenter.HomeContract;
import com.github.abhijitpparate.keeps.screen.home.presenter.HomePresenter;
import com.github.abhijitpparate.keeps.screen.login.presenter.login.LoginContract;
import com.github.abhijitpparate.keeps.screen.login.presenter.login.LoginPresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@SuppressWarnings("FieldCanBeLocal")
@RunWith(MockitoJUnitRunner.class)
public class HomePresenterTest {

    // Presenter needs view and auth source
    // and provide a fake one as follows
    @Mock
    private HomeContract.View view;

    private AuthSource authSource = AuthInjector.getAuthSource();

    // Testing the presenter
    private HomePresenter presenter;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        // Instantiate the presenter using the fake auth source and view
        presenter = new HomePresenter(view);
    }

    @Test
    public void onLogoutClick(){
        presenter.onLogoutClick();
        verify(view).showLoginScreen();
    }

    @Test
    public void onNewNoteClick(){
        authSource.setReturnFail(false);
        presenter.onNewNoteClick(HomeContract.NoteType.TEXT);
        verify(view).showNewNoteScreen(HomeContract.NoteType.TEXT);
    }

    @Test
    public void onNoteClick(){
        authSource.setReturnFail(true);

        Note note = new Note();
        presenter.onNoteClick(note);

        verify(view).showNoteScreen(note);
    }

}