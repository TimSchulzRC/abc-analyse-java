package timschulz.abcanalysejava.model;

import timschulz.abcanalysejava.database.Database;

import java.util.ArrayList;
import java.util.HashMap;

public class Material {
    public static ArrayList<Material> materials = new ArrayList<>();
    private final ArrayList<Rechnung> rechnungen = new ArrayList<>();
    private String material;
    private char klasse;
    private int gesamtwertCt;
    private float anteil;
    private float kumAnteil;
    private float category;
    private double varK;

    public static void createMaterialsFromRechnungen(ArrayList<Rechnung> rechnungen) {
        HashMap<String, Material> materialsMap = new HashMap<>();
        for (Rechnung rechnung : rechnungen) {
            String materialKey = rechnung.getMaterial();
            if (materialsMap.containsKey(materialKey)) {
                Material material = materialsMap.get(materialKey);
                material.setGesamtwertCt(material.getGesamtwertCt() + (int) (rechnung.getNetto()*100));
            } else {
                Material material = new Material();
                material.setMaterial(materialKey);
                material.setGesamtwertCt((int) (rechnung.getNetto()*100));
                materialsMap.put(materialKey, material);
            }
            materialsMap.get(materialKey).addRechnung(rechnung);
        }

        materials.addAll(materialsMap.values());

        materials.sort((Material m1, Material m2)-> Float.compare(m2.getGesamtwertCt(), m1.getGesamtwertCt()));


        int materialValueSumCt = materials.stream().reduce(0, (sum, material) -> sum + material.getGesamtwertCt(), Integer::sum);
        float kumuliert = 0;
        for (Material material : materials) {
            float anteil = (float)((float) Math.round((((double) material.getGesamtwertCt() / materialValueSumCt) * 100) * 100.0) / 100.0);
            kumuliert += anteil;
            material.setAnteil(anteil);
            material.setKumAnteil(kumuliert);
            if(kumuliert <= ABCXYZ.getA()+3) {
                material.setKlasse('A');
            } else if(kumuliert <= ABCXYZ.getA() + ABCXYZ.getB()+3) {
                material.setKlasse('B');
            } else {
                material.setKlasse('C');
            }
            // create map with all 12 months as keys and value is the count of all rechnungen in that month. also differentiate months in different years
            HashMap<String, Float> monthMap = new HashMap<>();
            // add month in timespan from Database.getFrom() to Database.getTo()
            for (int i = Database.getFrom().getYear(); i <= Database.getTo().getYear(); i++) {
                for (int j = 0; j < 12; j++) {
                    String month = i + "-" + (j+1);
                    monthMap.put(month, 0f);
                }
            }


            for (Rechnung rechnung : material.getRechnungen()) {
                String month = rechnung.getRechdat().getYear() + "-" + (rechnung.getRechdat().getMonth()+1);
                if (monthMap.containsKey(month)) {
                    monthMap.put(month, monthMap.get(month) + rechnung.getNetto());
                } else {
                    monthMap.put(month, rechnung.getNetto());
                }
            }
//            System.out.println("monthMap: " + monthMap);
            double average = monthMap.values().stream().reduce(0f, Float::sum) / (double) monthMap.size();
//            System.out.println("average: " + average);
            double varianz = monthMap.values().stream().mapToDouble(value -> Math.pow((value - average), 2)).sum() / (monthMap.values().stream().reduce(0f, Float::sum));
//            System.out.println("varianz: " + varianz);
            double standardabweichung = Math.sqrt(varianz);
//            System.out.println("standardabweichung: " + standardabweichung);
            double varK = (standardabweichung / average)*100;
//            System.out.println("varK: " + varK);
            material.setVarK(varK);
        }
    }

    public static ArrayList<Material> getMaterials() {
        return materials;
    }
    public String getMaterial() {
        return material;
    }
    public void setMaterial(String material) {
        this.material = material;
    }
    public char getKlasse() {
        return klasse;
    }
    public void setKlasse(char klasse) {
        this.klasse = klasse;
    }
    public int getGesamtwertCt() {
        return gesamtwertCt;
    }
    public void setGesamtwertCt(int gesamtwertCt) {
        this.gesamtwertCt = gesamtwertCt;
    }
    public double getGesamtwert() {
        return gesamtwertCt /100d;
    }
    public float getAnteil() {
        return anteil;
    }
    public void setAnteil(float anteil) {
        this.anteil = anteil;
    }
    public float getKumAnteil() {
        return kumAnteil;
    }
    public void setKumAnteil(float kumAnteil) {
        this.kumAnteil = kumAnteil;
    }
    public void addRechnung(Rechnung rechnung) {
        rechnungen.add(rechnung);
    }
    public ArrayList<Rechnung> getRechnungen() {
        return rechnungen;
    }
    public void setVarK(double varK) {
        this.varK = varK;
    }
    public static void clear() {
        materials.clear();
    }

    public double getVarK() {
        return varK;
    }
}
