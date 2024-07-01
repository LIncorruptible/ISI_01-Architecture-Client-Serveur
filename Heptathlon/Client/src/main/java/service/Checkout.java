package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.List;

/**
 * Checkout interface is used to define the methods that can be called remotely.
 */
public interface Checkout extends Remote {
    /**
     * Get all products references.
     * @return list of all products references.
     * @throws RemoteException if an error occurs.
     */
    public List<String> getAllProductsRef() throws RemoteException;

    /**
     * Get all products by family.
     * @return list of all products by family.
     * @throws RemoteException if an error occurs.
     */
    public List<List<String>> getAllFamily() throws RemoteException;

    /**
     * Get the stock of a product.
     * @param ref product reference.
     * @return stock of the product.
     * @throws RemoteException if an error occurs.
     */
    public int getStockOfProduct(int ref) throws RemoteException;

    /**
     * Add a product to the cart.
     * @param ref product reference.
     * @param quantity product quantity.
     * @throws RemoteException if an error occurs.
     */
    public void addProduct(
            int ref,
            int quantity
    ) throws RemoteException;

    /**
     * Add a mass product to the cart.
     * @param Ref_Quantity list of product references and quantities.
     * @throws RemoteException if an error occurs.
     */
    public void addMassProduct(List<List<Integer>> Ref_Quantity) throws RemoteException;

    /**
     * Get the cart info.
     * @param ref product reference.
     * @return cart info.
     * @throws RemoteException if an error occurs.
     */
    public List<String> getProductInfo(int ref) throws RemoteException;

    /**
     * Get the products info.
     * @return list of products info.
     * @throws RemoteException if an error occurs.
     */
    public List<String> getProductsInfo() throws RemoteException;

    /**
     * Get the products by family.
     * @param id_famille family id.
     * @return list of products by family.
     * @throws RemoteException if an error occurs.
     */
    public List<String> getProductsByFamily(int id_famille) throws RemoteException;

    /**
     * Get the total price of the cart.
     * @return total price of the cart.
     * @throws RemoteException if an error occurs.
     */
    public List<String> setNewFacture(
            List<List<Integer>> Ref_Quantity,
            int id_caisse
    ) throws RemoteException;

    /**
     * Get the factured products.
     * @param id_facture facture id.
     * @return list of facture products.
     * @throws RemoteException if an error occurs.
     */
    public List<String> getFacturedProducts(int id_facture) throws RemoteException;

    /**
     * Get the familly of a product.
     * @param ref product reference.
     * @return product family.
     * @throws RemoteException if an error occurs.
     */
    public String getFamillyOfProduct(int ref) throws RemoteException;

    /**
     * Get the price of a product.
     * @param ref product reference.
     * @return product price.
     * @throws RemoteException if an error occurs.
     */
    public float getPriceOfProduct(int ref) throws RemoteException;

    /**
     * Edit the quantity of a factured product.
     * @param id_facture facture id.
     * @param ref product reference.
     * @param quantity product quantity.
     * @throws RemoteException if an error occurs.
     */
    public void editFacturedProductQuantity(
            int id_facture,
            int ref,
            int quantity
    ) throws RemoteException;

    /**
     * Edit the quantity of a mass factured product.
     * @param Ref_Quantity list of product references and quantities.
     * @param id_facture facture id.
     * @throws RemoteException if an error occurs.
     */
    public void editFacturedMassProductsQuantity(
            List<List<Integer>> Ref_Quantity,
            int id_facture
    ) throws RemoteException;

    /**
     * Delete a factured product.
     * @param id_facture facture id.
     * @param ref product reference.
     * @throws RemoteException if an error occurs.
     */
    public void deleteFacturedProduct(int id_facture, int ref) throws RemoteException;

    /**
     * Delete a mass factured product.
     * @param Ref_Quantity list of product references and quantities.
     * @param id_facture facture id.
     * @throws RemoteException if an error occurs.
     */
    public void deleteFacturedMassProducts(List<List<Integer>> Ref_Quantity, int id_facture) throws RemoteException;

    /**
     * Set the payment mode of a facture.
     * @param id_facture facture id.
     * @param mode mode of payment.
     * @throws RemoteException if an error occurs.
     */
    public void setModePaiement(int id_facture, String mode) throws RemoteException;

    /**
     * Get the total price of a facture.
     * @param id_facture facture id.
     * @return total price of the facture.
     * @throws RemoteException if an error occurs.
     */
    public float getTTC(int id_facture) throws RemoteException;

    /**
     * Pay a facture.
     * @param id_facture facture id.
     * @param isCard true if the payment is by card, false otherwise.
     * @throws RemoteException if an error occurs.
     */
    public void payFacture(int id_facture, Boolean isCard) throws RemoteException;

    /**
     * Cancel a facture.
     * @param id_facture facture id.
     * @throws RemoteException if an error occurs.
     */
    public void cancelFacture(int id_facture) throws RemoteException;

    /**
     * Get the facture date.
     * @param id_facture facture id.
     * @return facture date.
     * @throws RemoteException if an error occurs.
     */
    public String getFactureDate(int id_facture) throws RemoteException;

    /**
     * Get the facture caisse info.
     * @param id_facture facture id.
     * @return facture caisse info.
     * @throws RemoteException if an error occurs.
     */
    public String getFactureCaisseInfo(int id_facture) throws RemoteException;

    /**
     * Make a directory.
     * @param path path to the directory.
     * @throws RemoteException if an error occurs.
     */
    public void makeDirectory(String path) throws RemoteException;

    /**
     * Create a facture.
     * @param id_caisse caisse id.
     * @return facture id.
     * @throws RemoteException if an error occurs.
     */
    public int createFacture(int id_caisse) throws RemoteException;

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
     * Upload a data to the siege.
     * @return true if the data was uploaded successfully, false otherwise.
     * @throws RemoteException if an error occurs.
     */
    public Boolean uploadDataToSiege() throws RemoteException;

    /**
     * Change the slash format.
     * @param path path to change.
     * @return path with the new format.
     * @throws RemoteException if an error occurs.
     */
    public String changeSlashFormat(String path) throws RemoteException;

    /**
     * Return the slash format.
     * @param path path to return.
     * @return path with the slash format.
     * @throws RemoteException if an error occurs.
     */
    public String returnSlashFormat(String path) throws RemoteException;

    /**
     * Update the prices.
     * @return true if the prices were updated successfully, false otherwise.
     * @throws RemoteException if an error occurs.
     */
    public Boolean updatePrices() throws RemoteException;

    /**
     * Get the local CA between two dates.
     * @param date1 start date.
     * @param date2 end date.
     * @return local CA between the two dates.
     * @throws RemoteException if an error occurs.
     */
    public float getLocalCABetweenDates(String date1, String date2) throws RemoteException;
}
