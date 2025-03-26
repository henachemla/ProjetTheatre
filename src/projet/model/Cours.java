package projet.model;

import javafx.beans.property.*;
import javafx.scene.control.CheckBox;

public class Cours {
    private final IntegerProperty coursId;
    private final StringProperty nom;
    private final StringProperty description;
    private final IntegerProperty placesDispo;
    private final StringProperty trancheAge;
    private final StringProperty prix;
    private CheckBox checkBox;

    public Cours(int coursId, String nom, String description, int placesDispo, String trancheAge, String prix) {
        this.coursId = new SimpleIntegerProperty(coursId);
        this.nom = new SimpleStringProperty(nom);
        this.description = new SimpleStringProperty(description);
        this.placesDispo = new SimpleIntegerProperty(placesDispo);
        this.trancheAge = new SimpleStringProperty(trancheAge);
        this.prix = new SimpleStringProperty(prix);
        this.checkBox = new CheckBox();
    }

    // Getters pour les propriétés
    public IntegerProperty coursIdProperty() { return coursId; }
    public StringProperty nomProperty() { return nom; }
    public StringProperty descriptionProperty() { return description; }
    public IntegerProperty placesDispoProperty() { return placesDispo; }
    public StringProperty trancheAgeProperty() { return trancheAge; }
    public StringProperty prixProperty() { return prix; }

    // Getters et setters standards
    public int getCoursId() { return coursId.get(); }
    public String getNom() { return nom.get(); }
    public String getDescription() { return description.get(); }
    public int getPlacesDispo() { return placesDispo.get(); }
    public String getTrancheAge() { return trancheAge.get(); }
    public String getPrix() { return prix.get(); }

    public CheckBox getCheckBox() { return checkBox; }
    public void setCheckBox(CheckBox checkBox) { this.checkBox = checkBox; }

    public void setCoursId(int coursId) {
        this.coursId.set(coursId);
    }
}