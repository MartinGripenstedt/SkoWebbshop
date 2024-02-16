public class Produkt {
    int proudktID;
    String färg;
    String märke;
    int storlek;
    double pris;

    public Produkt(int proudktID, String märke, String färg, int storlek, double pris) {
        this.proudktID = proudktID;
        this.färg = färg;
        this.märke = märke;
        this.storlek = storlek;
        this.pris = pris;
    }

    public int getProudktID() {
        return proudktID;
    }

    public String getFärg() {
        return färg;
    }

    public String getMärke() {
        return märke;
    }

    public int getStorlek() {
        return storlek;
    }

}
