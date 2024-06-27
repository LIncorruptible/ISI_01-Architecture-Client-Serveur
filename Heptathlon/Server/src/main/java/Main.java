import service.CheckoutImpl;
import siege.Siege;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;


public class Main extends CheckoutImpl {
    public Main() throws RemoteException {
    }
    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        try {
            LocateRegistry.createRegistry(1095);
            CheckoutImpl checkout = new CheckoutImpl();
            Naming.rebind("rmi://localhost:1095/CK", checkout);
            System.out.println("Server is ready.");
            //test
            //checkout.payFacture(4);
            //checkout.payFacture(5);
            //checkout.payFacture(17);
//            if (checkout.uploadDataToSiege()) {
//                System.out.println("Data uploaded to Siege");
//            } else {
//                System.out.println("Data not uploaded to Siege");
//            }
            //checkout.updatePrices();
        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }



//        try {
//            Siege stub = (Siege) Naming.lookup("rmi://localhost:1093/SG");
//            stub.testSiege();
//        } catch (Exception e) {
//            System.err.println("Client exception: " + e.toString());
//            e.printStackTrace();
//        }
    }
}