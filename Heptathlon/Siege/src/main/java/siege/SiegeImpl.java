package siege;

import helper.DBHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;

/**
 * This class implements the Siege interface.
 * It provides the implementation of the methods declared in the Siege interface.
 * It also provides the implementation of the methods that are specific to the Siege module.
 */
public class SiegeImpl extends UnicastRemoteObject implements Siege {

    /**
     * The path where the factures will be stored.
     */
    private static final String pathFacture = System.getProperty("user.home")+"\\Desktop\\Siege\\factures\\";

    /**
     * The DBHelper object that will be used to interact with the databases.
     * The first DBHelper object is used to interact with the siege database.
     * The second DBHelper object is used to interact with the heptathlon database.
     */
    private DBHelper dbHelperSiege = new DBHelper("siege");
    private DBHelper dbHelperHepta = new DBHelper("heptathlon");

    /**
     * The constructor of the SiegeImpl class.
     * @throws RemoteException RemoteException is thrown when a call is made to a method in a remote object.
     * @throws RemoteException RemoteException is thrown when a call is made to a method in a remote object.
     */
    public SiegeImpl() throws RemoteException, RemoteException {
        super();
    }

    /**
     * Create a directory at the specified path.
     * @param path path to the directory.
     * @throws RemoteException if an error occurs.
     */
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

    /**
     * Get the total CA between two dates.
     * @param date1 start date.
     * @param date2 end date.
     * @return total CA between the two dates.
     * @throws RemoteException if an error occurs.
     */
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

    /**
     * Upload a file to the server.
     * @param fileData file data.
     * @param fileName file name.
     * @param id_caisse caisse id.
     * @param id_facture facture id.
     * @param date_facture facture date.
     * @param prix_totale_TTC ttc total price.
     * @return true if the file was uploaded successfully, false otherwise.
     * @throws RemoteException if an error occurs.
     */
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

    /**
     * Register a facture in the database.
     * @param id_caisse caisse id.
     * @param id_facture facture id.
     * @param date_facture facture date.
     * @param prix_totale_TTC ttc total price.
     * @param path path to the facture file.
     * @return true if the facture was registered successfully, false otherwise.
     * @throws RemoteException if an error occurs.
     */
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

    /**
     * Check if a facture exists in the database.
     * @param id_facture facture id.
     * @param id_caisse caisse id.
     * @param prix_totale_TTC ttc toal price.
     * @return true if the facture exists, false otherwise.
     * @throws RemoteException if an error occurs.
     */
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

    /**
     * Update the prices of the articles in the database.
     * @return true if the prices were updated successfully, false otherwise.
     * @throws RemoteException if an error occurs.
     */
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

//                return statementStatus > 0;
            }
            System.out.println("Prices updated successfully");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
