package projet.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UtilisateurReservationData {

    private final StringProperty utilisateur;
    private final StringProperty cours;
    private final StringProperty date;

    public UtilisateurReservationData(String utilisateur, String cours, String date) {
        this.utilisateur = new SimpleStringProperty(utilisateur);
        this.cours = new SimpleStringProperty(cours);
        this.date = new SimpleStringProperty(date);
    }

    public StringProperty utilisateurProperty() { return utilisateur; }
    public StringProperty coursProperty() { return cours; }
    public StringProperty dateProperty() { return date; }
}
