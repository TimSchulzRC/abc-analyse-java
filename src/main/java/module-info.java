module timschulz.abcxyzanalysejava {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.opencsv;
    requires org.xerial.sqlitejdbc;

    opens timschulz.abcxyzanalysejava to javafx.fxml;
    exports timschulz.abcxyzanalysejava;
}