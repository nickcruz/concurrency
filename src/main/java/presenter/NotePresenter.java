package presenter;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import model.NoteRepository;
import model.NoteRepositoryImpl;
import model.Notebook;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotePresenter {
    @NonNull private final NoteRepository noteRepository;
    @NonNull private final Scheduler mainScheduler;
    @NonNull private final Scheduler backgroundScheduler;

    private final List<Notebook> notebooks;

    public NotePresenter(@NonNull NoteRepository noteRepository,
                         @NonNull Scheduler mainScheduler,
                         @NonNull Scheduler backgroundScheduler) {
        this.noteRepository = new NoteRepositoryImpl(backgroundScheduler, noteRepository);
        this.mainScheduler = mainScheduler;
        this.backgroundScheduler = backgroundScheduler;

        notebooks = new ArrayList<>();
    }

    public List<Notebook> getNotebooks() {
        return notebooks;
    }

    public void loadNotebooks() {
        noteRepository.getNotebooks()
                .delay(10, TimeUnit.SECONDS, backgroundScheduler)
                .subscribeOn(mainScheduler)
                .subscribe(result -> {
                    notebooks.clear();
                    notebooks.addAll(result);
                });
    }
}
