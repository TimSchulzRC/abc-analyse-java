package timschulz.abcanalysejava.adapter;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import timschulz.abcanalysejava.model.Rechnung;

import java.text.SimpleDateFormat;

public class RechnungAdapter {
    private static final ObservableList<RechnungAdapter> rechnungen = FXCollections.observableArrayList();

    private final StringProperty lieferantProperty;
    private final StringProperty rechnrProperty;
    private final StringProperty rechdatProperty;
    private final StringProperty nettoProperty;
    private final StringProperty materialProperty;

    public RechnungAdapter(Rechnung rechnung) {
        this.lieferantProperty = new SimpleStringProperty(rechnung.getLieferantName());
        this.rechnrProperty = new SimpleStringProperty(rechnung.getRechnr());
        String dateString = new SimpleDateFormat("dd.MM.yyyy").format(rechnung.getRechdat());
        this.rechdatProperty = new SimpleStringProperty(dateString);
        this.nettoProperty = new SimpleStringProperty(String.valueOf(rechnung.getNetto()));
        this.materialProperty = new SimpleStringProperty(rechnung.getMaterial());
        rechnungen.add(this);
    }

    public static ObservableList<RechnungAdapter> getRechnungen() {
        return rechnungen;
    }
    public StringProperty lieferantProperty() {
        return lieferantProperty;
    }
    public StringProperty rechnrProperty() {
        return rechnrProperty;
    }
    public StringProperty rechdatProperty() {
        return rechdatProperty;
    }
    public StringProperty nettoProperty() {
        return nettoProperty;
    }
    public StringProperty materialProperty() {
        return materialProperty;
    }

    public static void clear() {
        rechnungen.clear();
    }
    public static void addRechnung(Rechnung rechnung) {
        new RechnungAdapter(rechnung);
    }
}
