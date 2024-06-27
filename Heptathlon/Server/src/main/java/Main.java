import service.CheckoutImpl;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Main {
    private static final int rmiPort = 1095;
    private static final String rmiCheckoutUrl = "rmi://localhost:" + rmiPort + "/CK";


    public static void main(String[] args) throws RemoteException {

        try {
            LocateRegistry.createRegistry(rmiPort);
            CheckoutImpl checkout = new CheckoutImpl();

            Naming.rebind(rmiCheckoutUrl, checkout);

            System.out.println("Server is ready.");
        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}