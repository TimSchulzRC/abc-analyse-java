package timschulz.abcanalysejava.database;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import timschulz.abcanalysejava.adapter.RechnungAdapter;
import timschulz.abcanalysejava.model.Lieferant;
import timschulz.abcanalysejava.model.Rechnung;

public class Database {
    private static Date from;
    private static Date to;
    private static Connection connection;
    private static  final String CONNECTION_STRING = "jdbc:sqlite:data/database.db";

    private static Connection getConnection() {
            try {
                connection = DriverManager.getConnection(CONNECTION_STRING);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        return connection;
    }

    private static void createTables() {

        String lieferantTableSQL = """
                CREATE TABLE IF NOT EXISTS lieferant (
                	lief_id text NOT NULL,
                	nr text NOT NULL,
                	firma text NOT NULL,
                	PRIMARY KEY (lief_id)
                );""";

        String lieferungTableSQL = """
                CREATE TABLE IF NOT EXISTS lieferung (
                    lief_id text NOT NULL,
                	liefnr text NOT NULL,
                	liefdat date NOT NULL,
                	PRIMARY KEY (liefnr),
                	FOREIGN KEY (lief_id) REFERENCES lieferant (lief_id)
                );""";

        String rechnungTableSQL = """
                CREATE TABLE IF NOT EXISTS rechnung (
                	liefnr text NOT NULL,
                	rechnr text NOT NULL,
                	rechdat date NOT NULL,
                	netto real NOT NULL,
                	ust integer NOT NULL,
                	material text NOT NULL,
                    PRIMARY KEY (rechnr),
                    FOREIGN KEY (liefnr) REFERENCES lieferung (liefnr)
                );""";




        try (Statement stmt = connection.createStatement()) {
            stmt.execute(lieferantTableSQL);
            stmt.execute(lieferungTableSQL);
            stmt.execute(rechnungTableSQL);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Table lieferant is empty: " + dataTableIsEmpty("lieferant"));
        if(dataTableIsEmpty("lieferant")) insertDataLieferant();
        if(dataTableIsEmpty("lieferung")) insertDataLieferung();
        if(dataTableIsEmpty("rechnung")) insertDataRechnung();

    }
    
    private static void insertDataLieferant(){
        String sql = "INSERT INTO lieferant(lief_id, nr, firma) VALUES(?,?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            CSVParser parser =
                    new CSVParserBuilder()
                            .withSeparator(';')
                            .build();
            CSVReader reader = new CSVReaderBuilder(new FileReader("data/lieferant.csv")).withCSVParser(parser).withSkipLines(1).build();
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine[0].equals("")) {
                    continue;
                }
                pstmt.setString(1, nextLine[0]);
                pstmt.setString(2, nextLine[1]);
                pstmt.setString(3, nextLine[2]);
                pstmt.executeUpdate();
            }
        } catch (SQLException | FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertDataLieferung(){
        String sql = "INSERT INTO lieferung(lief_id, liefnr, liefdat) VALUES(?,?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            CSVParser parser =
                    new CSVParserBuilder()
                            .withSeparator(';')
                            .build();
            CSVReader reader = new CSVReaderBuilder(new FileReader("data/lieferung.csv")).withCSVParser(parser).withSkipLines(1).build();
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine[0].equals("")) {
                    continue;
                }
                Date date = new SimpleDateFormat("dd.MM.yy").parse(nextLine[2]);
                SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                pstmt.setString(1, nextLine[0]);
                pstmt.setString(2, nextLine[1]);
                pstmt.setDate(3, java.sql.Date.valueOf(newDateFormat.format(date)));
                pstmt.executeUpdate();
            }
        } catch (SQLException | FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (CsvValidationException | IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertDataRechnung(){
        String sql = "INSERT INTO rechnung(liefnr, rechnr, rechdat, netto, ust, material) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            CSVParser parser =
                    new CSVParserBuilder()
                            .withSeparator(';')
                            .build();
            CSVReader reader = new CSVReaderBuilder(new FileReader("data/rechnung.csv")).withCSVParser(parser).withSkipLines(1).build();
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine[0].equals("")) {
                    continue;
                }
                Date date = new SimpleDateFormat("dd.MM.yy").parse(nextLine[2]);
                SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                pstmt.setString(1, nextLine[0]);
                pstmt.setString(2, nextLine[1]);
                pstmt.setDate(3, java.sql.Date.valueOf(newDateFormat.format(date)));
                pstmt.setFloat(4, Float.parseFloat(nextLine[3].replace(",", ".")));
                pstmt.setInt(5, Integer.parseInt(nextLine[4]));
                pstmt.setString(6, nextLine[5]);
                pstmt.executeUpdate();
            }
        } catch (SQLException | FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (CsvValidationException | IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean dataTableIsEmpty(String table){
        String sql = "SELECT COUNT(*) FROM " + table + ";";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.getInt(1) == 0) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static void initialize() {
        connection = getConnection();
        createTables();
    }
    
    public static void loadRechnungen () {
        String sql = """
                SELECT rechnung.rechnr, rechnung.rechdat, rechnung.netto, rechnung.ust, rechnung.material, lieferant.firma
                FROM rechnung
                INNER JOIN lieferung ON rechnung.liefnr = lieferung.liefnr
                INNER JOIN lieferant ON lieferung.lief_id = lieferant.lief_id
                ORDER BY rechnung.rechnr;
                """;
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Rechnung rechnung = new Rechnung();
                rechnung.setRechnr(rs.getString("rechnr"));
                rechnung.setRechdat(rs.getDate("rechdat"));
                rechnung.setNetto(rs.getFloat("netto"));
                rechnung.setUst(rs.getInt("ust"));
                rechnung.setMaterial(rs.getString("material"));
                rechnung.setLieferant(rs.getString("firma"));
                new RechnungAdapter(rechnung);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static void loadRechnungen(Date from, Date to) {
        RechnungAdapter.clear();
        String sql = """
                SELECT rechnung.rechnr, rechnung.rechdat, rechnung.netto, rechnung.ust, rechnung.material, lieferant.firma
                FROM rechnung
                INNER JOIN lieferung ON rechnung.liefnr = lieferung.liefnr
                INNER JOIN lieferant ON lieferung.lief_id = lieferant.lief_id
                WHERE rechnung.rechdat BETWEEN ? AND ?
                ORDER BY rechnung.rechnr;
                """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setDate(1, java.sql.Date.valueOf(newDateFormat.format(from)));
            pstmt.setDate(2, java.sql.Date.valueOf(newDateFormat.format(to)));
            createRechnungen(pstmt);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void loadRechnungenAfter(Date from) {
        RechnungAdapter.clear();
        String sql = """
                SELECT rechnung.rechnr, rechnung.rechdat, rechnung.netto, rechnung.ust, rechnung.material, lieferant.firma
                FROM rechnung
                INNER JOIN lieferung ON rechnung.liefnr = lieferung.liefnr
                INNER JOIN lieferant ON lieferung.lief_id = lieferant.lief_id
                WHERE rechnung.rechdat >= ?
                ORDER BY rechnung.rechnr;
                """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setDate(1, java.sql.Date.valueOf(newDateFormat.format(from)));
            createRechnungen(pstmt);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void loadRechnungenBefore(Date to) {
        RechnungAdapter.clear();
        String sql = """
                SELECT rechnung.rechnr, rechnung.rechdat, rechnung.netto, rechnung.ust, rechnung.material, lieferant.firma
                FROM rechnung
                INNER JOIN lieferung ON rechnung.liefnr = lieferung.liefnr
                INNER JOIN lieferant ON lieferung.lief_id = lieferant.lief_id
                WHERE rechnung.rechdat <= ?
                ORDER BY rechnung.rechnr;
                """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setDate(1, java.sql.Date.valueOf(newDateFormat.format(to)));
            createRechnungen(pstmt);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createRechnungen(PreparedStatement pstmt) throws SQLException {
        ResultSet rs = pstmt.executeQuery();
        Rechnung.clear();
        while (rs.next()) {
            Rechnung rechnung = new Rechnung();
            rechnung.setRechnr(rs.getString("rechnr"));
            rechnung.setRechdat(rs.getDate("rechdat"));
            rechnung.setNetto(rs.getFloat("netto"));
            rechnung.setUst(rs.getInt("ust"));
            rechnung.setMaterial(rs.getString("material"));
            new RechnungAdapter(rechnung);
        }
    }

    public static void loadLieferanten() {
        String sql = """
                SELECT lieferant.firma, lieferant.lief_id
                FROM lieferant;
                """;
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Lieferant lieferant = new Lieferant();
                lieferant.setLief_id(rs.getString("lief_id"));
                lieferant.setName(rs.getString("firma"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void addRechnung(Rechnung rechnung) {
        String sql = """
                INSERT INTO rechnung(rechnr, rechdat, netto, ust, material, liefnr)
                VALUES(?,?,?,?,?,?);
                """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, rechnung.getRechnr());
            pstmt.setDate(2, new java.sql.Date(rechnung.getRechdat().getTime()));
            pstmt.setFloat(3, rechnung.getNetto());
            pstmt.setInt(4, rechnung.getUst());
            pstmt.setString(5, rechnung.getMaterial());
            pstmt.setString(6, rechnung.getRechnr());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void setFromToDateofFirstRechnung() {
        String sql = """
                SELECT rechnung.rechdat
                FROM rechnung
                ORDER BY rechnung.rechdat ASC
                LIMIT 1;
                """;
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                setFrom(rs.getDate("rechdat"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void setToToDateofLastRechnung() {
        String sql = """
                SELECT rechnung.rechdat
                FROM rechnung
                ORDER BY rechnung.rechdat DESC
                LIMIT 1;
                """;
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                setTo(rs.getDate("rechdat"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Date getFrom() {
        return from;
    }

    public static void setFrom(Date from) {
        Database.from = from;
    }

    public static Date getTo() {
        return to;
    }

    public static void setTo(Date to) {
        Database.to = to;
    }
}
