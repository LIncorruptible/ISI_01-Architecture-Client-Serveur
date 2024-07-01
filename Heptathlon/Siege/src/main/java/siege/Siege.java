package siege;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.ResultSet;

public interface Siege extends Remote {
    //Test Server
    void  testSiege() throws RemoteException;
    //Other methods
    public void makeDirectory(String path) throws RemoteException;//serverSide only
    public float getCABetweenDates(String date1, String date2) throws RemoteException; //external usage
    public Boolean uploadFile(byte[] fileData, String fileName, String id_caisse, String id_facture, String date_facture, Float prix_totale_TTC) throws RemoteException; //external usage
    public Boolean registerFactureInDB(String id_caisse, String id_facture, String date_facture, Float prix_totale_TTC, String path) throws RemoteException; //serverSide only
    public Boolean isExistFacture(String id_facture, String id_caisse, Float prix_totale_TTC) throws RemoteException; //serverSide only
    public Boolean updatePrices() throws RemoteException; //external usage
}
