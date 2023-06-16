package timschulz.abcxyzanalysejava;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import timschulz.abcxyzanalysejava.adapter.MaterialAdapter;
import timschulz.abcxyzanalysejava.adapter.RechnungAdapter;
import timschulz.abcxyzanalysejava.database.Database;
import timschulz.abcxyzanalysejava.model.ABC;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;


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


}