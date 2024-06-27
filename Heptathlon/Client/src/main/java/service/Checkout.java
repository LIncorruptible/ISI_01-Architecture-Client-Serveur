package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.List;

public interface Checkout extends Remote {
    //Test Server
    void  test() throws RemoteException;
    //SQL QUERY:
    public ResultSet sqlQuery(String query) throws RemoteException; //Usable only in server side
    public void sqlUpdate(String query) throws RemoteException; //Usable only in server side
    //Other methods
    public List<String> getAllProductsRef() throws RemoteException;
    public List<List<String>> getAllFamily() throws RemoteException;
    public int getStockOfProduct(int ref) throws RemoteException;
    public void addProduct(int ref, int quantity) throws RemoteException;
    public void addMassProduct(List<List<Integer>> Ref_Quantity) throws RemoteException;
    public List<String> getProductInfo(int ref) throws RemoteException;
    public List<String> getProductsInfo() throws RemoteException;
    public List<String> getProductsByFamily(int id_famille) throws RemoteException;
    public List<String> setNewFacture(List<List<Integer>> Ref_Quantity, int id_caisse) throws RemoteException;
    public List<String> getFacturedProducts(int id_facture) throws RemoteException;
    public String getFamillyOfProduct(int ref) throws RemoteException;
    public float getPriceOfProduct(int ref) throws RemoteException;
    public void editFacturedProductQuantity(int id_facture, int ref, int quantity) throws RemoteException;
    public void editFacturedMassProductsQuantity(List<List<Integer>> Ref_Quantity, int id_facture) throws RemoteException;
    public void deleteFacturedProduct(int id_facture, int ref) throws RemoteException;
    public void deleteFacturedMassProducts(List<List<Integer>> Ref_Quantity, int id_facture) throws RemoteException;
    public void setModePaiement(int id_facture, String mode) throws RemoteException;
    public float getTTC(int id_facture) throws RemoteException;
    public void payFacture(int id_facture, Boolean isCard) throws RemoteException;
    public void cancelFacture(int id_facture) throws RemoteException;
    public String getFactureDate(int id_facture) throws RemoteException;
    public String getFactureCaisseInfo(int id_facture) throws RemoteException;
    public void makeDirectory(String path) throws RemoteException;
    public int createFacture(int id_caisse) throws RemoteException;
    public float getCABetweenDates(String date1, String date2) throws RemoteException;
    public Boolean uploadDataToSiege() throws RemoteException;
    public String changeSlashFormat(String path) throws RemoteException;
    public String returnSlashFormat(String path) throws RemoteException;
    public Boolean updatePrices() throws RemoteException;
    public float getLocalCABetweenDates(String date1, String date2) throws RemoteException;
}
