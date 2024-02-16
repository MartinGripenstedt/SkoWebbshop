import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Properties p = new Properties();

        try {
            p.load(new FileInputStream("src/settings.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Scanner scanner = new Scanner(System.in);

             Connection connection = DriverManager.getConnection(p.getProperty("url"), p.getProperty("user"), p.getProperty("pw"))) {

            boolean inloggad = false;
            Kund kund = new Kund(0);

            while (!inloggad) {

                System.out.println("Ange ditt namn:");
                String kundNamn = scanner.nextLine();

                System.out.println("Ange ditt lösenord:");
                String lösenord = scanner.nextLine();

                String queryKund = "SELECT KundID FROM Kund WHERE Namn = ? AND Lösenord = ?";
                try (PreparedStatement psKund = connection.prepareStatement(queryKund)) {
                    psKund.setString(1, kundNamn);
                    psKund.setString(2, lösenord);

                    ResultSet rsKund = psKund.executeQuery();

                    if (rsKund.next()) {
                        int kundID = rsKund.getInt("KundID");
                        inloggad = true;
                        kund.setKundId(kundID);
                        System.out.println(kund.getKundId());
                    } else {
                        System.out.println("Ingen användare hittades med det namnet och lösenordet.");
                    }
                }
            }
            List<Integer> beställningsIDs = new ArrayList<>();
            String queryBästllningsid = "SELECT BeställningID FROM Beställning WHERE KundID = ?";

            try (PreparedStatement psBeställningsid = connection.prepareStatement(queryBästllningsid)) {

                psBeställningsid.setInt(1, kund.getKundId());
                ResultSet rs = psBeställningsid.executeQuery();

                while (rs.next()) {
                    beställningsIDs.add(rs.getInt("BeställningID"));
                }
            }
            System.out.println("BeställningsID nedanför");

            System.out.println(beställningsIDs);


            List<Kategori> kategorier = new ArrayList<>();
            try (Statement statement = connection.createStatement();
                 ResultSet rs = statement.executeQuery("select Namn, KategoriID from Kategori")) {
                while (rs.next()) {
                    kategorier.add(new Kategori(rs.getString("namn"), rs.getInt("KategoriID")));
                }
            }

            kategorier.forEach(kategori -> System.out.println(kategori.getNamn()));

            System.out.println("Välj en kategori:");
            String kategoriNamn = scanner.nextLine();

            Optional<Kategori> valdKategoriNamn = kategorier.stream()
                    .filter(kategori -> kategori.getNamn().equalsIgnoreCase(kategoriNamn))
                    .findFirst();
            int kategoriID = valdKategoriNamn.get().getKategoriID();

            System.out.println(kategoriID + "Är det här rätt kategoriID????");

            List<Produkt> produkter = new ArrayList<>();
            String query = "select Produkt.produktID, Produkt.märke, Produkt.färg, Produkt.pris, Produkt.storlek " +
                    "from Produkt " +
                    "inner join kategoriinnehåller on Produkt.ProduktID = kategoriinnehåller.ProduktID " +
                    "where KategoriID = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, kategoriID);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int produktID = rs.getInt("produktID");
                        String färg = rs.getString("färg");
                        String märke = rs.getString("märke");
                        double pris = rs.getDouble("pris");
                        int storlek = rs.getInt("storlek");
                        produkter.add(new Produkt(produktID, märke, färg, storlek, pris));
                    }
                }
            }

            produkter.stream().map(Produkt::getMärke).distinct().forEach(System.out::println);

            boolean hittad = false;
            String valdMärke = "";

            while (!hittad) {


                System.out.println("Skriv märket på produkten du vill lägga till:");
                String tempValdMärke = scanner.nextLine();
                hittad = produkter.stream()
                        .map(Produkt::getMärke)
                        .anyMatch(märke -> märke.equalsIgnoreCase(tempValdMärke));
                valdMärke = tempValdMärke;

                if (!hittad) {
                    System.out.println("Det märket finns inte. Försök igen.");
                }
            }

            String finalValdMärke = valdMärke;

            produkter.stream().filter(Produkt -> Produkt.getMärke().equals(finalValdMärke))
                    .map(Produkt::getFärg).distinct().forEach(System.out::println);

            hittad = false;
            String valdFärg = "";

            while (!hittad) {


                System.out.println("Skriv färg på produkten du vill lägga till:");
                String tempValdFärg = scanner.nextLine();
                hittad = produkter.stream().filter(Produkt -> Produkt.getMärke().equals(finalValdMärke))
                        .map(Produkt::getFärg).anyMatch(färg -> färg.equalsIgnoreCase(tempValdFärg));
                valdFärg = tempValdFärg;

                if (!hittad) {
                    System.out.println("Den färgen finns inte. Försök igen.");
                }
            }
            String finalValdFärg = valdFärg;

            System.out.println("Här är alla storlekar som finns:");
            produkter.stream().filter(Produkt -> Produkt.getMärke().equals(finalValdMärke))
                    .filter(Produkt -> Produkt.getFärg().equals(finalValdFärg))
                    .map(Produkt::getStorlek).distinct().forEach(System.out::println);

            hittad = false;
            int valdStorlek = 0;

            while (!hittad) {

                System.out.println("Skriv storlek på produkten du vill lägga till:");
                String tempValdStorlek = scanner.nextLine();

                try {
                    int temp2ValdStorlek = Integer.parseInt(tempValdStorlek);
                    hittad = produkter.stream()
                            .filter(produkt -> produkt.getMärke().equalsIgnoreCase(finalValdMärke))
                            .filter(produkt -> produkt.getFärg().equalsIgnoreCase(finalValdFärg))
                            .anyMatch(produkt -> produkt.getStorlek() == temp2ValdStorlek);
                    valdStorlek = temp2ValdStorlek;


                    if (!hittad) {
                        System.out.println("Den storleken finns inte. Försök igen.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Var god ange en giltig siffra för storleken.");
                }
            }
            int finalValdStorlek = valdStorlek;

            Produkt valdProdukt = produkter.stream()
                    .filter(produkt -> produkt.getMärke().equals(finalValdMärke))
                    .filter(produkt -> produkt.getFärg().equals(finalValdFärg))
                    .filter(produkt -> produkt.getStorlek() == finalValdStorlek)
                    .findAny()
                    .orElse(null);

            System.out.println(valdProdukt.getMärke() + valdProdukt.getFärg() + valdProdukt.proudktID);


            try (CallableStatement statement = connection.prepareCall("{CALL AddToCart2(?, ?, ?)}")) {
                statement.setInt(1, kund.getKundId());
                statement.setObject(2, beställningsIDs.get(0), Types.INTEGER);
                statement.setInt(3, valdProdukt.proudktID);

                statement.execute();
                System.out.println("Produkt tillagd till beställningen.");
                System.out.println();

                System.out.println("Kund ID:  "+kund.getKundId());
                System.out.println("BeställningsID : "+ beställningsIDs);
                System.out.println("Produkt ID : " + valdProdukt.getProudktID());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

