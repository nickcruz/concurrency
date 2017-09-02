package model;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;

import java.util.List;

public class NoteRepositoryImpl implements NoteRepository {

    @NonNull final Scheduler delayScheduler;
    @NonNull private final NoteRepository noteRepository;

    public NoteRepositoryImpl(@NonNull Scheduler delayScheduler,
                                 @NonNull NoteRepository noteRepository) {
        this.delayScheduler = delayScheduler;
        this.noteRepository = noteRepository;
    }

    @Override
    public Single<List<Notebook>> getNotebooks() {
        return noteRepository.getNotebooks();
    }
}
