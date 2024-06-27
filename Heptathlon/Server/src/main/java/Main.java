import service.CheckoutImpl;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;


public class Main extends CheckoutImpl {
    public Main() throws RemoteException {}

    public static void main(String[] args) throws RemoteException {
        try {
            LocateRegistry.createRegistry(1095);
            CheckoutImpl checkout = new CheckoutImpl();
            Naming.rebind("rmi://localhost:1095/CK", checkout);
            System.out.println("Server is ready.");
        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}