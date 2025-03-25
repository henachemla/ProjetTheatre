package projet.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class Cours {
    private final StringProperty nom;
    private final StringProperty description;
    private final StringProperty trancheAge;
    private final StringProperty prix;
    private int placesdispo;
    private CheckBox checkBox; // ✅ Ajout de la CheckBox
    private int coursId;


    public Cours(int coursId, String nom, String description, int placesdispo, String trancheAge, String prix) {
    	this.coursId = coursId;
        this.nom = new SimpleStringProperty(nom);
        this.description = new SimpleStringProperty(description);
        this.trancheAge = new SimpleStringProperty(trancheAge);
        this.prix = new SimpleStringProperty(prix);
        this.checkBox = new CheckBox(); // ✅ Initialisation de la CheckBox
        this.placesdispo= placesdispo;

    }
    
    public int getCoursId() {
        return coursId;
    }
    
    public int getPlacesDispo() {
        return placesdispo;
    }

    public void setPlacesDispo(int placesdispo) {
        this.placesdispo = placesdispo;
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty trancheAgeProperty() {
        return trancheAge;
    }

    public StringProperty prixProperty() {
        return prix;
    }
    
    public CheckBox getCheckBox() { return checkBox; } // ✅ Getter pour la CheckBox

    
    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }
    
}
