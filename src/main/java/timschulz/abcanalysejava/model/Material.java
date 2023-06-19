package timschulz.abcanalysejava.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Material {
    public static ArrayList<Material> materials = new ArrayList<>();
    private String material;
    private char klasse;
    private int gesamtwertCt;
    private float anteil;
    private float kumAnteil;

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
            if(kumuliert <= ABC.getA()+3) {
                material.setKlasse('A');
            } else if(kumuliert <= ABC.getA() + ABC.getB()+3) {
                material.setKlasse('B');
            } else {
                material.setKlasse('C');
            }
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
    public static void clear() {
        materials.clear();
    }
}
