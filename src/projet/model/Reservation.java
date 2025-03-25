package projet.model;

import javafx.beans.property.*;

public class Reservation {
    private final StringProperty nomCours;
    private final StringProperty date;
    private final IntegerProperty coursId;

    public Reservation(String nomCours, String date, int coursId) {
        this.nomCours = new SimpleStringProperty(nomCours);
        this.date = new SimpleStringProperty(date);
        this.coursId = new SimpleIntegerProperty(coursId);
    }

    public StringProperty nomCoursProperty() { return nomCours; }
    public StringProperty dateProperty() { return date; }
    public int getCoursId() { return coursId.get(); }
}

