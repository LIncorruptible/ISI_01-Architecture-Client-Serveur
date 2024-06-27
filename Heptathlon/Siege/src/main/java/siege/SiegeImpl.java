package siege;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;

public class SiegeImpl extends UnicastRemoteObject implements Siege {

    private static final String dbUrl = "jdbc:mysql://127.0.0.1:3306/siege";
    private static final  String dbUser = "root";
    private static final String dbPassword = "cariva";
    private static final String pathFacture = System.getProperty("user.home")+"\\Desktop\\Siege\\factures\\";

    public SiegeImpl() throws RemoteException, RemoteException {
        super();
    }

    @Override
    public void testSiege() {
        System.out.println("Siege is working");
    }

    @Override
    public ResultSet sqlQuery(String query) throws RemoteException {
        try {
            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void sqlUpdate(String query) throws RemoteException {
        try {
            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();

        }
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
        try {
            ResultSet resultSet = sqlQuery("SELECT SUM(prix_totale_TTC) FROM facture WHERE date_facturation BETWEEN '" + date1 + "' AND '" + date2 + "'");
            resultSet.next();
            if (resultSet.wasNull()) {
                return -1;
            }
            return resultSet.getFloat(1);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public Boolean uploadFile(byte[] fileData, String fileName, String id_caisse, String id_facture, String date_facture, Float prix_totale_TTC) throws RemoteException {
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
    public Boolean registerFactureInDB(String id_caisse, String id_facture, String date_facture, Float prix_totale_TTC, String path) throws RemoteException {
        try {
            if (isExistFacture(id_facture, id_caisse, prix_totale_TTC)) {
                System.out.println("Facture "+id_facture+" already exist in DB");
                return true;
            }
            sqlUpdate("INSERT INTO facture (id_caisse, id_facture, date_facturation, prix_totale_TTC, path) VALUES ('" + id_caisse + "', '" + id_facture + "', '" + date_facture + "', '" + prix_totale_TTC + "', '" + path + "')");
            System.out.println("Facture "+id_facture+" registered in DB");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean isExistFacture(String id_facture, String id_caisse, Float prix_totale_TTC) throws RemoteException {
        try {
            ResultSet resultSet = sqlQuery("SELECT * FROM facture WHERE id_facture = '" + id_facture + "' AND id_caisse = '" + id_caisse + "' AND prix_totale_TTC = '" + prix_totale_TTC + "'");
            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ResultSet sqlQueryLocal(String query) throws RemoteException {
        try {
            String dbUrlLocal = "jdbc:mysql://127.0.0.1:3306/heptathlon";
            String dbUserLocal = "root";
            String dbPasswordLocal = "cariva";
            Connection connection = DriverManager.getConnection(dbUrlLocal, dbUserLocal, dbPasswordLocal);
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void sqlUpdateLocal(String query) throws RemoteException {
        try {
            String dbUrlLocal = "jdbc:mysql://127.0.0.1:3306/heptathlon";
            String dbUserLocal = "root";
            String dbPasswordLocal = "cariva";
            Connection connection = DriverManager.getConnection(dbUrlLocal, dbUserLocal, dbPasswordLocal);
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Boolean updatePrices() throws RemoteException {
        //Read File of prices contains: reference;price\n...
        try {
            String path = System.getProperty("user.home")+"\\Desktop\\Siege\\prices.txt";
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("File not found at : " + path);
                return false;
            }
            String content = new String(Files.readAllBytes(file.toPath()));
            String[] lines = content.split("\n");
            for (String line : lines) {
                String[] data = line.split(";");
                sqlUpdateLocal("UPDATE article SET prix = '" + data[1] + "' WHERE reference = '" + data[0] + "'");
            }
            System.out.println("Prices updated successfully");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
