package timschulz.abcxyzanalysejava;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import timschulz.abcxyzanalysejava.adapter.MaterialAdapter;
import timschulz.abcxyzanalysejava.adapter.RechnungAdapter;
import timschulz.abcxyzanalysejava.database.Database;
import timschulz.abcxyzanalysejava.model.ABC;
import timschulz.abcxyzanalysejava.model.Lieferant;
import timschulz.abcxyzanalysejava.model.Material;
import timschulz.abcxyzanalysejava.model.Rechnung;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;


public class MainController {
    @FXML
    private TableView<RechnungAdapter> rechnungenTable;
    @FXML
    private TableColumn<RechnungAdapter, String> lieferantColumn;
    @FXML
    private TableColumn<RechnungAdapter, String> rechnrColumn;
    @FXML
    private TableColumn<RechnungAdapter, String> rechdatColumn;
    @FXML
    private TableColumn<RechnungAdapter, String> nettoColumn;
    @FXML
    private TableColumn<RechnungAdapter, String> materialColumn;
    @FXML
    private DatePicker fromDatePicker = new DatePicker();
    @FXML
    private DatePicker toDatePicker = new DatePicker();

    @FXML
    private TableView<MaterialAdapter> materialsTable;
    @FXML
    private TableColumn<MaterialAdapter, String> materialNameColumn;
    @FXML
    private TableColumn<MaterialAdapter, String> klasseColumn;
    @FXML
    private TableColumn<MaterialAdapter, String> gesamtwertColumn;
    @FXML
    private TableColumn<MaterialAdapter, String> anteilColumn;
    @FXML
    private TableColumn<MaterialAdapter, String> kumAnteilColumn;

    @FXML
    private Slider aSlider;
    @FXML
    private Slider bSlider;
    @FXML
    private Slider cSlider;
    @FXML
    private Label aLabel;
    @FXML
    private Label bLabel;
    @FXML
    private Label cLabel;

    public void initialize() {
        Database.loadLieferanten();
        Database.loadRechnungen();
        lieferantColumn.setCellValueFactory(cellData -> cellData.getValue().lieferantProperty());
        rechnrColumn.setCellValueFactory(cellData -> cellData.getValue().rechnrProperty());
        rechdatColumn.setCellValueFactory(cellData -> cellData.getValue().rechdatProperty());
        nettoColumn.setCellValueFactory(cellData -> cellData.getValue().nettoProperty());
        materialColumn.setCellValueFactory(cellData -> cellData.getValue().materialProperty());
        rechnungenTable.setItems(RechnungAdapter.getRechnungen());
        materialNameColumn.setCellValueFactory(cellData -> cellData.getValue().materialProperty());
        klasseColumn.setCellValueFactory(cellData -> cellData.getValue().klasseProperty());
        gesamtwertColumn.setCellValueFactory(cellData -> cellData.getValue().gesamtwertProperty());
        anteilColumn.setCellValueFactory(cellData -> cellData.getValue().anteilProperty());
        kumAnteilColumn.setCellValueFactory(cellData -> cellData.getValue().kumAnteilProperty());
        materialsTable.setItems(MaterialAdapter.getMaterials());
        MaterialAdapter.createMaterialAdapters();
        aSlider.setMin(ABC.getB());
        bSlider.setMax(ABC.getA());
        cSlider.setMin(ABC.getC());
        aSlider.setValue(ABC.getA());
        bSlider.setValue(ABC.getB());
        cSlider.setValue(ABC.getC());
        aLabel.setText(ABC.getA() + " %");
        bLabel.setText(ABC.getB() + " %");
        cLabel.setText(ABC.getC() + " %");

        aSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != oldValue.intValue()) {
                handleSlider();
            }
        });
        bSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != oldValue.intValue()) {
                handleSlider();
            }
        });
        cSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != oldValue.intValue()) {
                handleSlider();
            }
        });
    }

    public void handleSlider() {
        ABC.setA((int) aSlider.getValue());
        ABC.setB((int) bSlider.getValue());
        ABC.setC((int) cSlider.getValue());
        aLabel.setText(ABC.getA() + " %");
        bLabel.setText(ABC.getB() + " %");
        cLabel.setText(ABC.getC() + " %");
        aSlider.setMin(ABC.getB());
        bSlider.setMax(ABC.getA());
        cSlider.setMax(ABC.getB());
        MaterialAdapter.createMaterialAdapters();
    }

    public void handleDateSelect() {
        LocalDate fromLocalDate = fromDatePicker.getValue();
        LocalDate toLocalDate = toDatePicker.getValue();

        if (fromDatePicker.getValue() != null && toDatePicker.getValue() != null) {
            Date fromDate = Date.from(fromLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date toDate = Date.from(toLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Database.loadRechnungen(fromDate, toDate);
        }
        if (fromDatePicker.getValue() != null && toDatePicker.getValue() == null) {
            Date fromDate = Date.from(fromLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Database.loadRechnungenAfter(fromDate);
        }
        if (fromDatePicker.getValue() == null && toDatePicker.getValue() != null) {
            Date toDate = Date.from(toLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Database.loadRechnungenBefore(toDate);
        }
        MaterialAdapter.createMaterialAdapters();

    }

    public void handleResetDates() {
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        RechnungAdapter.clear();
        Database.loadRechnungen();
        MaterialAdapter.createMaterialAdapters();
    }

    public void handleAddRechnung() {
        // open dialog with input fields for rechnung
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rechnung hinzufügen");
        dialog.setHeaderText("Rechnung hinzufügen");
        dialog.setContentText("Bitte geben Sie die Rechnungsnummer ein:");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));
        TextField rechnr = new TextField();
        rechnr.setPromptText("Rechnungsnummer");
        ComboBox<Lieferant> lieferant = new ComboBox<>();
        ArrayList<Lieferant> lieferanten = Lieferant.getLieferanten();
        ObservableList<Lieferant> lieferantenObservableList = FXCollections.observableArrayList(lieferanten);
        lieferantenObservableList.forEach(l -> System.out.println(l.getName()));
        lieferant.setItems(lieferantenObservableList);
        lieferant.setConverter(new StringConverter<>() {
            @Override
            public String toString(Lieferant lieferant) {
                if(lieferant != null){
                    return lieferant.getName();
                }
                return "";
            }
            @Override
            public Lieferant fromString(String string) {
                return null;
            }
        });
        ComboBox<Material> material = new ComboBox<>();
        ArrayList<Material> materialien = Material.getMaterials();
        ObservableList<Material> materialienObservableList = FXCollections.observableArrayList(materialien);
        material.setItems(materialienObservableList);
        material.setConverter(new StringConverter<>() {
            @Override
            public String toString(Material material) {
                if(material != null){
                    return material.getMaterial();
                }
                return "";
            }
            @Override
            public Material fromString(String string) {
                return null;
            }
        });
        DatePicker rechdat = new DatePicker();
        rechdat.setPromptText("Rechnungsdatum");
        TextField netto = new TextField();
        // verify that input is a number
        netto.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                netto.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        netto.setPromptText("Nettobetrag");
        grid.add(new Label("Rechnungsnummer:"), 0, 0);
        grid.add(rechnr, 1, 0);
        grid.add(new Label("Lieferant:"), 0, 1);
        grid.add(lieferant, 1, 1);
        grid.add(new Label("Material:"), 0, 2);
        grid.add(material, 1, 2);
        grid.add(new Label("Rechnungsdatum:"), 0, 3);
        grid.add(rechdat, 1, 3);
        grid.add(new Label("Nettobetrag (€):"), 0, 4);
        grid.add(netto, 1, 4);
        dialog.getDialogPane().setContent(grid);
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (rechnr.getText().isEmpty() || lieferant.getValue() == null || rechdat.getValue() == null || netto.getText().isEmpty() || material.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fehler");
                alert.setHeaderText("Fehler beim Hinzufügen der Rechnung");
                alert.setContentText("Bitte füllen Sie alle Felder aus.");
                alert.showAndWait();
            } else {
                Rechnung rechnung = new Rechnung(lieferant.getValue(), rechnr.getText(), java.sql.Date.valueOf(rechdat.getValue()), Integer.parseInt(netto.getText()), material.getValue().getMaterial());
                Database.addRechnung(rechnung);
                RechnungAdapter.addRechnung(rechnung);
                MaterialAdapter.createMaterialAdapters();
            }
        });
    }

}