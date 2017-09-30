package model;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DelayedRepository implements NoteRepository {

    @NonNull final Scheduler delayScheduler;
    @NonNull private final NoteRepository noteRepository;

    public DelayedRepository(@NonNull Scheduler delayScheduler,
                             @NonNull NoteRepository noteRepository) {
        this.delayScheduler = delayScheduler;
        this.noteRepository = noteRepository;
    }

    @Override
    public Single<List<Notebook>> getNotebooks() {
        return noteRepository.getNotebooks()
                .delay(10, TimeUnit.SECONDS, delayScheduler);
    }

    @Override
    public Single<List<Note>> getNotesByNotebookId(int id) {
        return noteRepository.getNotesByNotebookId(id)
                .delay(10, TimeUnit.SECONDS, delayScheduler);
    }
}
