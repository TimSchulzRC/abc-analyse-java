package timschulz.abcanalysejava.adapter;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import timschulz.abcanalysejava.model.Material;
import timschulz.abcanalysejava.model.Rechnung;

public class MaterialAdapter {
    private static final ObservableList<MaterialAdapter> materials = FXCollections.observableArrayList();
    private final StringProperty materialProperty;
    private final StringProperty klasseProperty;
    private final StringProperty gesamtwertProperty;
    private final StringProperty anteilProperty;
    private final StringProperty kumAnteilProperty;

    public MaterialAdapter(String material, String klasse, String gesamtwert, String anteil, String kumAnteil) {
        this.materialProperty = new javafx.beans.property.SimpleStringProperty(material);
        this.klasseProperty = new javafx.beans.property.SimpleStringProperty(klasse);
        this.gesamtwertProperty = new javafx.beans.property.SimpleStringProperty(gesamtwert);
        this.anteilProperty = new javafx.beans.property.SimpleStringProperty(anteil);
        this.kumAnteilProperty = new javafx.beans.property.SimpleStringProperty(kumAnteil);
        materials.add(this);
    }
    public static ObservableList<MaterialAdapter> getMaterials() {
        return materials;
    }
    public StringProperty materialProperty() {
        return materialProperty;
    }
    public StringProperty klasseProperty() {
        return klasseProperty;
    }
    public StringProperty gesamtwertProperty() {
        return gesamtwertProperty;
    }
    public StringProperty anteilProperty() {
        return anteilProperty;
    }
    public StringProperty kumAnteilProperty() {
        return kumAnteilProperty;
    }
    public static void clear() {
        materials.clear();
    }
    public static void createMaterialAdapters(){
        MaterialAdapter.clear();
        Material.clear();
        Material.createMaterialsFromRechnungen(Rechnung.getRechnungen());
        Material.getMaterials().forEach(material -> new MaterialAdapter(material.getMaterial(), String.valueOf(material.getKlasse()), String.valueOf(material.getGesamtwert()), String.valueOf(material.getAnteil()), String.valueOf(material.getKumAnteil())));
    }

}
