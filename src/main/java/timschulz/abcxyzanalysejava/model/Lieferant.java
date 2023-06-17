package timschulz.abcxyzanalysejava.model;

import java.util.ArrayList;

public class Lieferant {
    private static final ArrayList<Lieferant> lieferanten = new ArrayList<>();
    private String lief_id;
    private String name;

    public Lieferant() {
        lieferanten.add(this);
    }
    public Lieferant(String lief_id, String name) {
        this.lief_id = lief_id;
        this.name = name;
        lieferanten.add(this);
    }

    public static ArrayList<Lieferant> getLieferanten() {
        return lieferanten;
    }

    public String getLief_id() {
        return lief_id;
    }
    public void setLief_id(String lief_id) {
        this.lief_id = lief_id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
