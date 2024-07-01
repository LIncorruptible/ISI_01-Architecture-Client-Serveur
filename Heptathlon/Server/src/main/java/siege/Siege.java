package siege;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.ResultSet;

/**
 * Siege interface is used to define the methods that can be called remotely.
 */
public interface Siege extends Remote {
    /**
     * Create a directory at the specified path.
     * @param path path to the directory.
     * @throws RemoteException if an error occurs.
     */
    public void makeDirectory(String path) throws RemoteException;

    /**
     * Get the total CA between two dates.
     * @param date1 start date.
     * @param date2 end date.
     * @return total CA between the two dates.
     * @throws RemoteException if an error occurs.
     */
    public float getCABetweenDates(
            String date1,
            String date2
    ) throws RemoteException;

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
    public Boolean uploadFile(
            byte[] fileData,
            String fileName,
            String id_caisse,
            String id_facture,
            String date_facture,
            Float prix_totale_TTC
    ) throws RemoteException;

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
    public Boolean registerFactureInDB(
            String id_caisse,
            String id_facture,
            String date_facture,
            Float prix_totale_TTC,
            String path
    ) throws RemoteException;

    /**
     * Check if a facture exists in the database.
     * @param id_facture facture id.
     * @param id_caisse caisse id.
     * @param prix_totale_TTC ttc total price.
     * @return true if the facture exists, false otherwise.
     * @throws RemoteException if an error occurs.
     */
    public Boolean isExistFacture(
            String id_facture,
            String id_caisse,
            Float prix_totale_TTC
    ) throws RemoteException;

    /**
     * Update the articles prices in the database.
     * @return true if the prices were updated successfully, false otherwise.
     * @throws RemoteException if an error occurs.
     */
    public Boolean updatePrices() throws RemoteException;
}
