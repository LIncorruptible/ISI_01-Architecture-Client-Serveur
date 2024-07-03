import siege.SiegeImpl;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Main class to start the siege server.
 */
public class Main {
    /**
     * RMI port and URL for the siege server.
     */
    private static final int rmiPort = 1093;
    private static final String rmiSiegeUrl = "rmi://localhost:" + rmiPort + "/SG";

    /**
     * Main method to start the siege server.
     * It creates the registry and binds the SiegeImpl object on port 1093.
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(rmiPort);
            SiegeImpl siege = new SiegeImpl();
            Naming.rebind(rmiSiegeUrl, siege);
            System.out.println("Siege Server is ready.");
        } catch (RemoteException e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}