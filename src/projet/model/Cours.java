package projet.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class Cours {
    private final StringProperty nom;
    private final StringProperty description;
    private final StringProperty trancheAge;
    private final StringProperty prix;
    private CheckBox checkBox; // ✅ Ajout de la CheckBox


    public Cours(String nom, String description, String trancheAge, String prix) {
        this.nom = new SimpleStringProperty(nom);
        this.description = new SimpleStringProperty(description);
        this.trancheAge = new SimpleStringProperty(trancheAge);
        this.prix = new SimpleStringProperty(prix);
        this.checkBox = new CheckBox(); // ✅ Initialisation de la CheckBox

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
