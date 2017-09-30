package presenter;

import io.reactivex.Single;
import io.reactivex.schedulers.TestScheduler;
import model.Note;
import model.NoteRepository;
import model.Notebook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class NotePresenterTest {

    @Mock
    private NoteRepository noteRepository;

    private TestScheduler testScheduler;

    private NotePresenter notePresenter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        testScheduler = new TestScheduler();

        notePresenter = new NotePresenter(noteRepository, testScheduler, testScheduler);
    }

    private void advanceTimeBy(long delayTime) {
        testScheduler.advanceTimeBy(delayTime, TimeUnit.SECONDS);
    }

    @Test
    public void loadNotebooks_FullTime() throws Exception {
        List<Notebook> notebooks = new ArrayList<>();
        notebooks.add(new Notebook(1, new ArrayList<>()));
        notebooks.add(new Notebook(2, new ArrayList<>()));
        notebooks.add(new Notebook(3, new ArrayList<>()));
        when(noteRepository.getNotebooks()).thenReturn(Single.just(notebooks));

        notePresenter.loadNotebooks();

        advanceTimeBy(10);

        assertEquals(notebooks, notePresenter.getNotebooks());
    }

    @Test
    public void loadNotebooksWithNotes_LoadConcurrently() throws Exception {
        List<Notebook> notebooks = listOf(
                new Notebook(1, listOf(11, 12)),
                new Notebook(2, listOf()),
                new Notebook(3, listOf(31, 32, 33))
        );
        when(noteRepository.getNotebooks()).thenReturn(Single.just(notebooks));

        List<Note> notebook1Notes = listOf(
                new Note(11, "note11", "yolo"),
                new Note(12, "note12", "swag")
        );
        when(noteRepository.getNotesByNotebookId(1)).thenReturn(Single.just(notebook1Notes));

        when(noteRepository.getNotesByNotebookId(2)).thenReturn(Single.just(listOf()));

        List<Note> notebook3Notes = listOf(
                new Note(31, "note31", "danielle"),
                new Note(32, "note32", "corbin"),
                new Note(33, "note33", "nick")
        );
        when(noteRepository.getNotesByNotebookId(3)).thenReturn(Single.just(notebook3Notes));

        notePresenter.loadNotebooksWithNotes();

        advanceTimeBy(19);
        advanceTimeBy(1); // Commenting out this line will fail the test.l

        Map<Notebook, List<Note>> notebooksWithNotes = notePresenter.getNotebooksWithNotes();
        System.out.println(notebooksWithNotes);

        for (Note note : notebook1Notes) {
            System.out.println(note);
            assertTrue(notebooksWithNotes.get(notebooks.get(0)).contains(note));
        }
        for (Note note : notebook3Notes) {
            System.out.println(note);
            assertTrue(notebooksWithNotes.get(notebooks.get(2)).contains(note));
        }
    }

    private <M> List<M> listOf(M... items) {
        List<M> list = new ArrayList<>();
        Collections.addAll(list, items);
        return list;
    }
}