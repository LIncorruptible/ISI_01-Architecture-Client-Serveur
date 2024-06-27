import siege.SiegeImpl;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Main {
    public Main() throws RemoteException {
    }
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1093);
            SiegeImpl siege = new SiegeImpl();
            Naming.rebind("rmi://localhost:1093/SG", siege);
            System.out.println("Siege Server is ready.");
        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}