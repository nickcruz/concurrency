package model;

import java.util.List;

public class Notebook {
    private final int id;
    private final List<Integer> notes;

    public Notebook(int id, List<Integer> notes) {
        this.id = id;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public List<Integer> getNotes() {
        return notes;
    }
}
