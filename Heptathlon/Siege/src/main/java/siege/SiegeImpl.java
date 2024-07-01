package siege;

import helper.DBHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;

public class SiegeImpl extends UnicastRemoteObject implements Siege {

    private static final String pathFacture = System.getProperty("user.home")+"\\Desktop\\Siege\\factures\\";

    private DBHelper dbHelperSiege = new DBHelper("siege");
    private DBHelper dbHelperHepta = new DBHelper("heptathlon");

    public SiegeImpl() throws RemoteException, RemoteException {
        super();
    }

    @Override
    public void testSiege() {
        System.out.println("Siege is working");
    }

    @Override
    public void makeDirectory(String path) throws RemoteException {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            System.out.println("Directory setup at : " + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public float getCABetweenDates(String date1, String date2) throws RemoteException {
        try (
                ResultSet resultSet = dbHelperSiege.executeQuery(
                        "SELECT SUM(prix_totale_TTC) " +
                        "FROM facture " +
                        "WHERE date_facturation " +
                        "BETWEEN '" + date1 + "' AND '" + date2 + "'"
                )
        ) {
            resultSet.next();
            if (resultSet.wasNull()) {
                return -1;
            } else {
                return resultSet.getFloat(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public Boolean uploadFile(
            byte[] fileData,
            String fileName,
            String id_caisse,
            String id_facture,
            String date_facture,
            Float prix_totale_TTC) throws RemoteException
    {
        String path = pathFacture + "\\" + id_caisse;
        makeDirectory(path);
        path += "\\" + id_facture + ".txt";
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            fileOutputStream.write(fileData);
            fileOutputStream.close();
            System.out.println("File "+fileName+" uploaded successfully at : " + path);
            return registerFactureInDB(id_caisse, id_facture, date_facture, prix_totale_TTC, path);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean registerFactureInDB(
            String id_caisse,
            String id_facture,
            String date_facture,
            Float prix_totale_TTC,
            String path) throws RemoteException
    {
        if (isExistFacture(id_facture, id_caisse, prix_totale_TTC)) {
            System.out.println("Facture "+id_facture+" already exist in DB");
            return true;
        } else {
            int statementStatus = dbHelperSiege.executeUpdateQuery(
                    "INSERT INTO facture (" +
                            "id_caisse, " +
                            "id_facture, " +
                            "date_facturation, " +
                            "prix_totale_TTC, path" +
                    ") " +
                    "VALUES ('" +
                            id_caisse + "', '" +
                            id_facture + "', '" +
                            date_facture + "', '" +
                            prix_totale_TTC + "', '" +
                            path +
                    "')"
            );
            return statementStatus > 0;
        }
    }

    @Override
    public Boolean isExistFacture(String id_facture, String id_caisse, Float prix_totale_TTC) throws RemoteException {
        try (
                ResultSet resultSet = dbHelperSiege.executeQuery(
                        "SELECT * " +
                        "FROM facture " +
                        "WHERE id_facture = '" + id_facture + "' " +
                        "AND id_caisse = '" + id_caisse + "' " +
                        "AND prix_totale_TTC = '" + prix_totale_TTC + "'"
                )
        ) {
            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean updatePrices() throws RemoteException {
        String path = System.getProperty("user.home")+"\\Desktop\\Siege\\prices.txt";
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("File not found at : " + path);
            return false;
        }
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            String[] lines = content.split("\n");
            for (String line : lines) {
                String[] data = line.split(";");
                int statementStatus = dbHelperHepta.executeUpdateQuery(
                        "UPDATE article SET prix = '" + data[1] + "' " +
                        "WHERE reference = '" + data[0] + "'"
                );

                return statementStatus > 0;
            }
            System.out.println("Prices updated successfully");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
