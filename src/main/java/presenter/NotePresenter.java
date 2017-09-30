package presenter;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import model.DelayedRepository;
import model.Note;
import model.NoteRepository;
import model.Notebook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotePresenter {
    @NonNull private final NoteRepository noteRepository;
    @NonNull private final Scheduler mainScheduler;

    private final List<Notebook> notebooks;
    private final Map<Notebook, List<Note>> notebooksWithNotes;

    public NotePresenter(@NonNull NoteRepository noteRepository,
                         @NonNull Scheduler mainScheduler,
                         @NonNull Scheduler backgroundScheduler) {
        this.noteRepository = new DelayedRepository(backgroundScheduler, noteRepository);
        this.mainScheduler = mainScheduler;

        notebooks = new ArrayList<>();
        notebooksWithNotes = new HashMap<>();
    }

    public List<Notebook> getNotebooks() {
        return notebooks;
    }

    public Map<Notebook, List<Note>> getNotebooksWithNotes() {
        return notebooksWithNotes;
    }

    public void loadNotebooks() {
        noteRepository.getNotebooks()
                .subscribeOn(mainScheduler)
                .subscribe(result -> {
                    notebooks.clear();
                    notebooks.addAll(result);
                });
    }

    public void loadNotebooksWithNotes() {
        noteRepository.getNotebooks()
                .subscribeOn(mainScheduler)
                .flatMapObservable(Observable::fromIterable)
                .flatMap(notebook -> noteRepository.getNotesByNotebookId(notebook.getId())
                        .subscribeOn(mainScheduler)
                        .flatMapObservable(Observable::fromIterable)
                        .doOnNext(note -> {
                            if (!notebooksWithNotes.containsKey(notebook)) {
                                notebooksWithNotes.put(notebook, new ArrayList<>());
                            }
                            notebooksWithNotes.get(notebook).add(note);
                        }))
                .subscribe();
    }
}
