package timschulz.abcanalysejava.model;

import java.util.ArrayList;
import java.util.Date;

public class Rechnung {
    private static final ArrayList<Rechnung> rechnungen = new ArrayList<>();
    private String lieferantName;
    private String rechnr;
    private Date rechdat;
    private float netto;
    private int ust = 19;
    private String material;

    public Rechnung() {
        rechnungen.add(this);
    }

    public static ArrayList<Rechnung> getRechnungen() {
        return rechnungen;
    }
    public void setLieferant(String lieferant) {
        this.lieferantName = lieferant;
    }
    public void setRechnr(String rechnr) {
        this.rechnr = rechnr;
    }
    public void setRechdat(Date rechdat) {
        this.rechdat = rechdat;
    }
    public void setNetto(float netto) {
        this.netto = netto;
    }
    public void setUst(int ust) {
        this.ust = ust;
    }
    public void setMaterial(String material) {
        this.material = material;
    }
    public String getLieferantName() {
        return lieferantName;
    }
    public String getRechnr() {
        return rechnr;
    }
    public Date getRechdat() {
        return rechdat;
    }
    public float getNetto() {
        return netto;
    }
    public int getUst() {
        return ust;
    }
    public String getMaterial() {
        return material;
    }
    public static void clear() {
        rechnungen.clear();
    }
}
