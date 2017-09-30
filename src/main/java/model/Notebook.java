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

    @Override
    public String toString() {
        return "Notebook{" +
                "id=" + id +
                ", notes=" + notes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Notebook notebook = (Notebook) o;

        if (id != notebook.id) return false;
        return notes != null ? notes.equals(notebook.notes) : notebook.notes == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        return result;
    }
}
