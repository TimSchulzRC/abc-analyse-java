package timschulz.abcanalysejava;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import timschulz.abcanalysejava.adapter.MaterialAdapter;
import timschulz.abcanalysejava.adapter.RechnungAdapter;
import timschulz.abcanalysejava.database.Database;
import timschulz.abcanalysejava.model.ABCXYZ;

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
    private TableColumn<MaterialAdapter, String> varKColumn;
    @FXML
    private TableColumn<MaterialAdapter, String> categoryColumn;

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
    @FXML
    private Slider xSlider;
    @FXML
    private Slider ySlider;
    @FXML
    private Label xLabel;
    @FXML
    private Label yLabel;


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
        varKColumn.setCellValueFactory(cellData -> cellData.getValue().varKProperty());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        materialsTable.setItems(MaterialAdapter.getMaterials());
        MaterialAdapter.createMaterialAdapters();

        aSlider.setValue(ABCXYZ.getA());
        bSlider.setValue(ABCXYZ.getB());
        cSlider.setValue(ABCXYZ.getC());

        updateSliders();

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
        xSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != oldValue.intValue()) {
                handleSlider();
            }
        });
        ySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != oldValue.intValue()) {
                handleSlider();
            }
        });
    }

    private void updateSliders() {
        aSlider.setMin(ABCXYZ.getB());
        bSlider.setMax(ABCXYZ.getA());
        bSlider.setMin(ABCXYZ.getC());
        cSlider.setMax(ABCXYZ.getB());
        aLabel.setText(ABCXYZ.getA() + " %");
        bLabel.setText(ABCXYZ.getB() + " %");
        cLabel.setText(ABCXYZ.getC() + " %");

        xSlider.setMin(0);
        xSlider.setMax(ABCXYZ.getY()-1);
        ySlider.setMin(ABCXYZ.getX()+1);
        ySlider.setMax(100);
        xSlider.setValue(ABCXYZ.getX());
        ySlider.setValue(ABCXYZ.getY());
        xLabel.setText(ABCXYZ.getX() + " %");
        yLabel.setText(ABCXYZ.getY() + " %");
    }

    public void handleResetSliders() {
        ABCXYZ.setA(70);
        ABCXYZ.setB(20);
        ABCXYZ.setC(10);
        ABCXYZ.setX(25);
        ABCXYZ.setY(50);
        updateSliders();
        MaterialAdapter.createMaterialAdapters();
    }

    public void handleSlider() {
        ABCXYZ.setA((int) aSlider.getValue());
        ABCXYZ.setB((int) bSlider.getValue());
        ABCXYZ.setC((int) cSlider.getValue());
        ABCXYZ.setX((int) xSlider.getValue());
        ABCXYZ.setY((int) ySlider.getValue());

        updateSliders();
        MaterialAdapter.createMaterialAdapters();
    }

    public void handleDateSelect() {
        LocalDate fromLocalDate = fromDatePicker.getValue();
        LocalDate toLocalDate = toDatePicker.getValue();


        if (fromDatePicker.getValue() != null && toDatePicker.getValue() != null) {
            Database.setFrom(Date.from(fromLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            Database.setTo(Date.from(toLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            Date fromDate = Date.from(fromLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date toDate = Date.from(toLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Database.loadRechnungen(fromDate, toDate);
        }
        if (fromDatePicker.getValue() != null && toDatePicker.getValue() == null) {
            Database.setFrom(Date.from(fromLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            Date fromDate = Date.from(fromLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Database.loadRechnungenAfter(fromDate);
        }
        if (fromDatePicker.getValue() == null && toDatePicker.getValue() != null) {
            Database.setTo(Date.from(toLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
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