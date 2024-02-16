public class Kategori {
    String namn;
    int kategoriID;
    public Kategori(String namn, int kategoriID) {
        this.namn = namn;
        this.kategoriID = kategoriID;
    }

    public String getNamn() {
        return namn;
    }

    public int getKategoriID() {
        return kategoriID;
    }

}
