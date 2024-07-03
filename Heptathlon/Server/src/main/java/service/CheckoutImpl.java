package service;
import helper.DBHelper;
import siege.Siege;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CheckoutImpl class is used to define the methods that can be called remotely.
 */
public class CheckoutImpl extends UnicastRemoteObject implements Checkout {
    /**
     * Path to the factures directory.
     */
    private static final String pathFacture = System.getProperty("user.home") + "\\Desktop\\factures\\";

    /**
     * RMI port and URL.
     */
    private static final int rmiPort = 1093;
    private static final String rmiSiegeUrl = "rmi://localhost:" + rmiPort + "/SG";

    /**
     * Database helper object.
     */
    private DBHelper dbHelper = new DBHelper();

    /**
     * Siege stub object.
     */
    private static final Siege stub;

    static {
        try {
            stub = (Siege) Naming.lookup(rmiSiegeUrl);
        } catch (NotBoundException | RemoteException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructor to create a CheckoutImpl object.
     * @throws RemoteException if an error occurs.
     */
    public CheckoutImpl() throws RemoteException {
        super();
    }

    /**
     * Get all products references.
     * @return list of all products references.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public List<String> getAllProductsRef() throws RemoteException {
        List<String> refs = new ArrayList<>();
        try (
                ResultSet resultSet = dbHelper.executeQuery(
                        "SELECT reference FROM article"
                )
        ) {
            if (resultSet == null) {
                return null;
            }
            while (resultSet.next()) {
                refs.add(resultSet.getString("reference"));
            }
            return refs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the family of a product.
     * @param ref product reference.
     * @return family of the product.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public String getFamillyOfProduct(int ref) throws RemoteException {
        try (
                ResultSet resultSet = dbHelper.executeQuery(
                        "SELECT nom FROM famille, article " +
                        "WHERE famille.id_famille = article.id_famille " +
                        "AND reference = " + ref
                )
        ) {
            resultSet.next();
            return resultSet.getString("nom");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all families.
     * @return list of all families.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public List<List<String>> getAllFamily() throws RemoteException {
        List<List<String>> families = new ArrayList<>();
        try (
                ResultSet resultSet = dbHelper.executeQuery(
                        "SELECT id_famille, nom FROM famille"
                )
        ) {
            if (resultSet == null) {
                return null;
            }
            while (resultSet.next()) {
                List<String> family = new ArrayList<>();
                String id_famille = resultSet.getString("id_famille");
                family.add(id_famille);
                family.add(resultSet.getString("nom"));
                List<String> productsByFamily = getProductsByFamily(Integer.parseInt(id_famille));
                family.add(String.valueOf(productsByFamily));
                families.add(family);
            }
            return families;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the stock of a product.
     * @param ref product reference.
     * @return stock of the product.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public int getStockOfProduct(int ref) throws RemoteException {
        try (
                ResultSet resultSet = dbHelper.executeQuery(
                        "SELECT quantite FROM article, stock " +
                        "WHERE article.id_stock = stock.id_stock " +
                        "AND article.reference = " + ref
                )
        ) {
            resultSet.next();
            return resultSet.getInt("quantite");
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Add a product to the cart.
     * @param ref product reference.
     * @param quantity product quantity.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public void addProduct(int ref, int quantity) throws RemoteException {
        int stock = getStockOfProduct(ref);
        if (stock >= 0) {
            dbHelper.executeUpdateQuery(
                    "UPDATE stock SET quantite = " + (stock + quantity) +
                    " WHERE id_stock = (SELECT id_stock FROM article WHERE reference = " + ref + ")"
            );
        }
    }

    /**
     * Add a mass product to the cart.
     * @param Ref_Quantity list of product references and quantities.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public void addMassProduct(List<List<Integer>> Ref_Quantity) throws RemoteException {
        for (List<Integer> ref_quantity : Ref_Quantity) {
            addProduct(ref_quantity.get(0), ref_quantity.get(1));
        }
    }

    /**
     * Get the product info.
     * @param ref product reference.
     * @return product info.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public List<String> getProductInfo(int ref) throws RemoteException {
        try (
                ResultSet resultSet = dbHelper.executeQuery(
                        "SELECT a.reference, a.prix, f.nom, s.quantite " +
                        "FROM article a, famille f, stock s " +
                        "WHERE a.id_famille = f.id_famille " +
                        "AND a.id_stock = s.id_stock " +
                        "AND reference = " + ref
                )
        ) {
            resultSet.next();
            List<String> productInfo = new ArrayList<>();
            productInfo.add(resultSet.getString("reference"));
            productInfo.add(resultSet.getString("prix"));
            productInfo.add(resultSet.getString("nom"));
            productInfo.add(resultSet.getString("quantite"));
            return productInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the products info.
     * @return list of products info.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public List<String> getProductsInfo() throws RemoteException {
        List<String> refs = getAllProductsRef();
        List<String> productsInfo = new ArrayList<>();
        if (refs == null) {
            return null;
        } else {
            for (String ref : refs) {
                List<String> productInfo = getProductInfo(Integer.parseInt(ref));
                productsInfo.add(String.valueOf(productInfo));
            }
            return productsInfo;
        }
    }

    /**
     * Get the products by family.
     * @param id_famille family id.
     * @return list of products by family.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public List<String> getProductsByFamily(int id_famille) throws RemoteException {
        try (
                ResultSet resultSet = dbHelper.executeQuery(
                        "SELECT reference, prix, nom, s.quantite " +
                        "FROM article a " +
                        "JOIN famille f ON a.id_famille = f.id_famille " +
                        "JOIN stock s ON a.id_stock = s.id_stock " +
                        "WHERE f.id_famille = " + id_famille + " " +
                        "AND s.quantite > 0"
                )
        ) {
            List<String> productsByFamily = new ArrayList<>();
            while (resultSet.next()) {
                List<String> productInfo = new ArrayList<>();
                productInfo.add(resultSet.getString("reference"));
                productInfo.add(resultSet.getString("prix"));
                productInfo.add(resultSet.getString("quantite"));
                productsByFamily.add(String.valueOf(productInfo));
            }
            return productsByFamily;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Make a directory.
     * @param path directory path.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public void makeDirectory(String path) throws RemoteException {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public int createFacture(int id_caisse) throws RemoteException {
        int id_new_facture = -1;
        String path = "";
        dbHelper.executeUpdateQuery(
                "INSERT INTO facture (id_caisse, date_facturation) " +
                "VALUES (" + id_caisse + ", NOW())"
        );
        try (
                ResultSet resultSet = dbHelper.executeQuery(
                        "SELECT MAX(id_facture) " +
                        "FROM facture"
                )
        ) {
            resultSet.next();
            id_new_facture = resultSet.getInt(1);
            makeDirectory(pathFacture + id_caisse);
            path = pathFacture + id_caisse + "\\" + id_new_facture + ".txt";
            dbHelper.executeUpdateQuery(
                    "UPDATE facture SET chemin_fichier = '" + changeSlashFormat(path) + "' " +
                    "WHERE id_facture = " + id_new_facture
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_new_facture;
    }

    /**
     * Get the price of a product.
     * @param ref product reference.
     * @return price of the product.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public float getPriceOfProduct(int ref) throws RemoteException {
        try (
                ResultSet resultSet = dbHelper.executeQuery(
                        "SELECT prix FROM article " +
                        "WHERE reference = " + ref
                )
        ) {
            resultSet.next();
            return resultSet.getFloat("prix");
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Get the factured products.
     * @param id_facture facture id.
     * @return list of factured products.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public List<String> getFacturedProducts(int id_facture) throws RemoteException {
        try (
                ResultSet resultSet = dbHelper.executeQuery(
                        "SELECT reference, quantite " +
                        "FROM facturer " +
                        "WHERE id_facture = " + id_facture
                )
        ) {
            List<String> facturedProducts = new ArrayList<>();
            while (resultSet.next()) {
                List<String> productInfo = new ArrayList<>();
                productInfo.add(resultSet.getString("reference"));
                productInfo.add(resultSet.getString("quantite"));
                productInfo.add(getFamillyOfProduct(resultSet.getInt("reference")));
                productInfo.add(String.valueOf(getPriceOfProduct(resultSet.getInt("reference"))));
                facturedProducts.add(String.valueOf(productInfo));
            }
            facturedProducts.add(String.valueOf(id_facture));
            return facturedProducts;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Set a new facture.
     * @param Ref_Quantity list of product references and quantities.
     * @param id_caisse caisse id.
     * @return list of factured products.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public List<String> setNewFacture(List<List<Integer>> Ref_Quantity, int id_caisse) throws RemoteException {
        int id_facture = createFacture(id_caisse);
        if (id_facture == -1) {
            System.out.println("Error creating new facture");
            return null;
        }
        for (List<Integer> ref_quantity : Ref_Quantity) {
            int ref = ref_quantity.get(0);
            int quantity = ref_quantity.get(1);
            dbHelper.executeUpdateQuery(
                    "INSERT INTO facturer (id_facture, reference, quantite) " +
                    "VALUES (" + id_facture + ", " + ref + ", " + quantity + ")"
            );
        }
        if (getFacturedProducts(id_facture) == null) {
            System.out.println("Error getting factured products");
            return null;
        }
        return getFacturedProducts(id_facture);
    }

    /**
     * Edit the quantity of a factured product.
     * @param id_facture facture id.
     * @param ref product reference.
     * @param quantity product quantity.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public void editFacturedProductQuantity(
            int id_facture, int ref,
            int quantity
    ) throws RemoteException {
        dbHelper.executeUpdateQuery(
                "UPDATE facturer SET quantite = " + quantity + " " +
                "WHERE id_facture = " + id_facture + " " +
                "AND reference = " + ref
        );
    }

    /**
     * Edit the quantity of a mass factured product.
     * @param Ref_Quantity list of product references and quantities.
     * @param id_facture facture id.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public void editFacturedMassProductsQuantity(
            List<List<Integer>> Ref_Quantity,
            int id_facture
    ) throws RemoteException {
        for (List<Integer> ref_quantity : Ref_Quantity) {
            editFacturedProductQuantity(id_facture, ref_quantity.get(0), ref_quantity.get(1));
        }
    }

    /**
     * Delete a factured product.
     * @param id_facture facture id.
     * @param ref product reference.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public void deleteFacturedProduct(int id_facture, int ref) throws RemoteException {
        try (
                ResultSet resultSet = dbHelper.executeQuery(
                        "DELETE FROM facturer " +
                        "WHERE id_facture = " + id_facture + " " +
                        "AND reference = " + ref
                )
        ) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a mass factured product.
     * @param Ref_Quantity list of product references and quantities.
     * @param id_facture facture id.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public void deleteFacturedMassProducts(
            List<List<Integer>> Ref_Quantity,
            int id_facture
    ) throws RemoteException {
        for (List<Integer> ref_quantity : Ref_Quantity) {
            deleteFacturedProduct(id_facture, ref_quantity.getFirst());
        }
    }

    /**
     * Set the mode of payment.
     * @param id_facture facture id.
     * @param mode payment mode.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public void setModePaiement(int id_facture, String mode) throws RemoteException {
        dbHelper.executeUpdateQuery(
                "UPDATE facture SET mode_paiement = '" + mode +
                "' WHERE id_facture = " + id_facture
        );
    }

    /**
     * Get the TTC price of a facture.
     * @param id_facture facture id.
     * @return TTC price of the facture.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public float getTTC(int id_facture) throws RemoteException {
        float TTC = 0;
        try (
                ResultSet resultSet = dbHelper.executeQuery(
                        "SELECT reference, quantite " +
                        "FROM facturer " +
                        "WHERE id_facture = " + id_facture
                )
        ) {
            while (resultSet.next()) {
                int ref = resultSet.getInt("reference");
                int quantity = resultSet.getInt("quantite");
                TTC += getPriceOfProduct(ref) * quantity;
            }
            return TTC;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Get the date of a facture.
     * @param id_facture facture id.
     * @return date of the facture.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public String getFactureDate(int id_facture) throws RemoteException {
        try (
                ResultSet resultSet = dbHelper.executeQuery(
                        "SELECT date_facturation " +
                        "FROM facture " +
                        "WHERE id_facture = " + id_facture
                )
        ) {
            resultSet.next();
            return resultSet.getString("date_facturation");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the facture caisse info.
     * @param id_facture facture id.
     * @return caisse info of the facture.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public String getFactureCaisseInfo(int id_facture) throws RemoteException {
        try (
                ResultSet resultSet = dbHelper.executeQuery(
                        "SELECT facture.id_caisse, nom " +
                        "FROM facture, caisse " +
                        "WHERE facture.id_caisse = caisse.id_caisse AND id_facture = " + id_facture
                )
        ) {
            resultSet.next();
            return resultSet.getString("id_caisse") + " - " + resultSet.getString("nom");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Cancel a facture.
     * @param id_facture facture id.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public void cancelFacture(int id_facture) throws RemoteException {
        try (
                ResultSet resultSet = dbHelper.executeQuery(
                        "DELETE FROM facturer " +
                        "WHERE id_facture = " + id_facture
                )
        ) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Pay a facture, generate a file and update the stock.
     * @param id_facture facture id.
     * @param isCard true if the payment is by card, false otherwise.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public void payFacture(int id_facture, Boolean isCard) throws RemoteException {
        String mode = "";
        if (isCard) {
            setModePaiement(id_facture, "Carte Bancaire");
            mode = "Par Carte Bancaire";
        } else {
            setModePaiement(id_facture, "Espèces");
            mode = "En Espèces";
        }
        dbHelper.executeUpdateQuery(
                "UPDATE facture SET prix_totale_TTC = " + getTTC(id_facture) +
                " WHERE id_facture = " + id_facture
        );
        List<String> facturedProducts = getFacturedProducts(id_facture);
        String Date = getFactureDate(id_facture);
        String TTC = String.valueOf(getTTC(id_facture));
        String CaisseInfo = getFactureCaisseInfo(id_facture);
        String Caisse_id = CaisseInfo.split(" - ")[0];
        String path = pathFacture + Caisse_id + "\\" + id_facture + ".txt";
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        java.io.FileWriter fileWriter = null;
        try {
            fileWriter = new java.io.FileWriter(file);
            fileWriter.write("=====================================================\n\n");
            fileWriter.write("      Heptathlon    -    Caisse N°:" + CaisseInfo + "\n\n");
            fileWriter.write("        Facture N°" + id_facture + "   Date : " + Date + "\n\n");
            fileWriter.write("====================Produits=========================\n\n");
            fileWriter.write("Ref| Catégorie | Quantité | Prix Unitaire\n\n");
            for (String product : facturedProducts) {
                if (product.equals(String.valueOf(id_facture))) {
                    continue;
                }
                String productRef = product.split(",")[0].split("\\[")[1];
                String productQuantity = product.split(",")[1].substring(1);
                String productFamily = product.split(",")[2].substring(1);
                String productPrice = product.split(",")[3]
                        .substring(1, product.split(",")[3].length() - 1) + " EUR";
                String productFormatted =
                        productRef + " | " +
                        productFamily + " |    " +
                        productQuantity + "    | " +
                        productPrice;
                fileWriter.write(productFormatted + "\n");
                int ref = Integer.parseInt(productRef);
                int quantity = Integer.parseInt(productQuantity);
                addProduct(ref, -quantity);
            }
            fileWriter.write("\n=====================================================\n");
            fileWriter.write("Prix TTC : " + TTC + "EUR  -- Paiement " + mode + "\n");
            fileWriter.write("=====================================================\n\n");
            fileWriter.write("         Veuillez conserver votre facture\n");
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the CA between dates.
     * @param date1 start date.
     * @param date2 end date.
     * @return CA between the two dates.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public float getCABetweenDates(String date1, String date2) throws RemoteException {
        try (
                ResultSet resultSet = dbHelper.executeQuery(
                        "SELECT SUM(prix_totale_TTC) " +
                        "FROM facture " +
                        "WHERE date_facturation " +
                        "BETWEEN '" + date1 + "' " +
                        "AND '" + date2 + "'"
                )
        ) {
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

    /**
     * Get the local CA between dates.
     * @param date1 start date.
     * @param date2 end date.
     * @return local CA between the two dates.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public float getLocalCABetweenDates(String date1, String date2) throws RemoteException {
        try (
                ResultSet resultSet = dbHelper.executeQuery(
                        "SELECT SUM(prix_totale_TTC) " +
                        "FROM facture " +
                        "WHERE date_facturation " +
                        "BETWEEN '" + date1 + "' " +
                        "AND '" + date2 + "' " +
                        "AND id_caisse = 1"
                )
        ) {
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

    /**
     * Change the slash format.
     * @param path path to change.
     * @return path with the new format.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public String changeSlashFormat(String path) throws RemoteException {
        return path.replace("\\", "/");
    }

    /**
     * Return the slash format.
     * @param path path to return.
     * @return path with the slash format.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public String returnSlashFormat(String path) throws RemoteException {
        return path.replace("/", "\\");
    }

    /**
     * Upload data to the siege.
     * @return true if the data was uploaded successfully, false otherwise.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public Boolean uploadDataToSiege() throws RemoteException {

        try (ResultSet resultSet = dbHelper.executeQuery("SELECT * FROM facture")) {
            if (resultSet == null) {
                System.out.println("Error getting Data from Facture");
                return false;
            }
            while (resultSet.next()) {
                String id_caisse = resultSet.getString("id_caisse");
                String id_facture = resultSet.getString("id_facture");
                String date_facture = resultSet.getString("date_facturation");
                Float prix_totale_TTC = resultSet.getFloat("prix_totale_TTC");
                String cheminFichier = returnSlashFormat(resultSet.getString("chemin_fichier"));
                Path path = Paths.get(cheminFichier);
                byte[] data = Files.readAllBytes(path);
                if (!stub.uploadFile(data, cheminFichier, id_caisse, id_facture, date_facture, prix_totale_TTC)) {
                    System.out.println("Error uploading Data : [ id facture : " + id_facture + " ] to Siege");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Update the prices.
     * @return true if the prices were updated successfully, false otherwise.
     * @throws RemoteException if an error occurs.
     */
    @Override
    public Boolean updatePrices() throws RemoteException {
        return stub.updatePrices();
    }
}

