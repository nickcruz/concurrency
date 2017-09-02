package presenter;

import io.reactivex.Single;
import io.reactivex.schedulers.TestScheduler;
import model.NoteRepository;
import model.Notebook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class NotePresenterTest {

    @Mock
    private NoteRepository noteRepository;

    private TestScheduler mainScheduler;
    private TestScheduler backgroundScheduler;

    private NotePresenter notePresenter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        mainScheduler = new TestScheduler();
        backgroundScheduler = new TestScheduler();

        notePresenter = new NotePresenter(noteRepository, mainScheduler, backgroundScheduler);
    }

    private void advanceTimeBy(long delayTime) {
        mainScheduler.advanceTimeBy(delayTime, TimeUnit.SECONDS);
        backgroundScheduler.advanceTimeBy(delayTime, TimeUnit.SECONDS);
    }

    @Test
    public void loadNotebooks_SanityCheckNoDelay() throws Exception {
        List<Notebook> notebooks = new ArrayList<>();
        notebooks.add(new Notebook(1, new ArrayList<>()));
        notebooks.add(new Notebook(2, new ArrayList<>()));
        notebooks.add(new Notebook(3, new ArrayList<>()));
        when(noteRepository.getNotebooks()).thenReturn(Single.just(notebooks));

        notePresenter.loadNotebooks();

        advanceTimeBy(50);

        System.out.println(notePresenter.getNotebooks());
    }
}