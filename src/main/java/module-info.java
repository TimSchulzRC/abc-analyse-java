module timschulz.abcanalysejava {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.opencsv;
    requires org.xerial.sqlitejdbc;

    opens timschulz.abcanalysejava to javafx.fxml;
    exports timschulz.abcanalysejava;
}