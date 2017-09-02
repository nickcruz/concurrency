package model;

import io.reactivex.Single;

import java.util.List;

public interface NoteRepository {
    Single<List<Notebook>> getNotebooks();
}
