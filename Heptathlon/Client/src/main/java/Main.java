import com.formdev.flatlaf.FlatDarculaLaf;
import service.Checkout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.tree.DefaultMutableTreeNode;

public class Main extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;
    private int id_facture = -1;
    JTable table;
    JButton facturer = new JButton("Facturer"); //Done
    JButton ajouterStock = new JButton("Ajouter Stock");
    JTextField searchArticle = new JTextField("Rechercher un article");
    JButton search = new JButton("Rechercher");
    JButton editQuantity = new JButton("Modifier la quantité");
    JButton deleteArticle = new JButton("Supprimer l'article");
    JCheckBox bankingCard = new JCheckBox("Carte Bancaire");
    JCheckBox cash = new JCheckBox("Espèces");
    JButton payFacture = new JButton("Payer la facture");
    JButton cancelFacture = new JButton("Annuler la facture");
    JButton CA  = new JButton(new ImageIcon("src/main/resources/CAB.png"));
    JButton SendDataToSiege = new JButton(new ImageIcon("src/main/resources/BDD.png"));
    JButton UpdatePrices = new JButton(new ImageIcon("src/main/resources/price.png"));
    Checkout stub = (Checkout) Naming.lookup("rmi://localhost:1095/CK");

    //Constructor
    public Main() throws MalformedURLException, NotBoundException, RemoteException {
        //Window settings
        super("Heptathlon");
        this.setIconImage(new ImageIcon("src/main/resources/HeptathlonLogo.jpg").getImage());
        this.setTitle("Heptathlon - Caisse N° " + serialVersionUID);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(1350, 700);
        this.setLocationRelativeTo(null);

        //Header
        this.setJMenuBar( createHeader("Page des articles"));

        //Window content
        JPanel panel = (JPanel) this.getContentPane();

        //split bar between left and right body
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createBodyLeft(), createBodyRight());
        split.setDividerLocation(200);
        panel.add(split, BorderLayout.CENTER);
        //footer (Status bar)
        panel.add( createFooter("Aucune action a performer ..."), BorderLayout.SOUTH);

        //Event listeners
        //Facturer button
        facturer.addActionListener(new ActionListener() {
                                       @Override
                                       public void actionPerformed(ActionEvent e) {
                                           try {
                                               facturerClicked();
                                           } catch (InterruptedException | RemoteException ex) {
                                               throw new RuntimeException(ex);
                                           }
                                       }
                                   });
        //Event listener for each checkbox in the table to update status bar with : "Row Selected : Ref: ... Quantity: ... Stock: ... Price: ..."
        table.getModel().addTableModelListener(this::selectedNotSelectedStatus);


                //test OK (Can't use if the Footer is not created yet !)
                //updateStatus("Initialisation terminée ...");

        //AjouterStock
        ajouterStock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ajouterStock(split);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        //Rechercher un article
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    searchArticle();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        //Auto Return Default Text in searchArticle
        searchArticle.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                updateStatus("Recherche d'un article selon sa référence ...");
                searchArticle.setText("");
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if(searchArticle.getText().isEmpty()) {
                    searchArticle.setText("Rechercher un article");
                }
            }
        });

        //Pay Facture
        payFacture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    payFacture(split);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        //Cancel Facture
        cancelFacture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cancelFacture(split);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        //Edit Commanded Quantity
        editQuantity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    editQuantity();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        //Delete Article from Facture
        deleteArticle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    deleteArticle();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        //CA
        CA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    getCA();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        //Send Data To Siege
        SendDataToSiege.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendDataToSiege();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        //Update Prices
        UpdatePrices.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    updatePrices();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void updatePrices() throws RemoteException {
        if (stub.updatePrices()) {
            updateStatus("Prix des articles mis à jour avec succès");
            JOptionPane.showMessageDialog(this, "Prix des articles mis à jour avec succès");
        } else {
            updateStatus("Erreur lors de la mise à jour des prix des articles");
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour des prix des articles");
        }
        defaultProductsSetTable();
    }

    private void sendDataToSiege() throws RemoteException {
        if (stub.uploadDataToSiege()) {
            updateStatus("Données envoyées au siège avec succès");
            JOptionPane.showMessageDialog(this, "Données envoyées au siège avec succès");
        } else {
            updateStatus("Erreur lors de l'envoi des données au siège");
            JOptionPane.showMessageDialog(this, "Erreur lors de l'envoi des données au siège");
        }
    }

    private void getCA() throws RemoteException {
        //open option pane
        JFrame frame = new JFrame("Chiffre d'affaire");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(555, 150);
        frame.setLocationRelativeTo(null);
           frame.setVisible(true);
        //Create panel as this :
        // <Jlabel"Chiffre d'affaire entre le"> : <TextField>    "et"   <TextField>
        //                             <Jbutton : "Afficher">
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2,1));
        JPanel top = new JPanel();
        JPanel bottom = new JPanel();
        top.setLayout(new GridLayout(1, 4));
        bottom.setLayout(new FlowLayout());
        JLabel top_label_1 = new JLabel("Chiffre d'affaire entre le :");
        JTextField date1 = new JTextField("Début (YYYY-MM-DD)");
        date1.setPreferredSize(new Dimension(60, 10));
        JLabel top_label_2 = new JLabel("et");
        top_label_2.setHorizontalAlignment(SwingConstants.CENTER);
        JTextField date2 = new JTextField("Fin (YYYY-MM-DD)");
        date2.setPreferredSize(new Dimension(60, 10));
        JButton getCA = new JButton("Afficher");

        top.add(top_label_1);
        top.add(date1);
        top.add(top_label_2);
        top.add(date2);
        panel.add(top);
        bottom.add(getCA);
        panel.add(bottom);
        frame.add(panel);


        getCA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String date1 = ((JTextField) top.getComponent(1)).getText();
                    String date2 = ((JTextField) top.getComponent(3)).getText();
                    if (date1.isEmpty() || date2.isEmpty() || date1.equals("Début (YYYY-MM-DD)") || date2.equals("Fin (YYYY-MM-DD)")) {
                        JOptionPane.showMessageDialog(frame, "Veuillez entrer une date de début et une date de fin");
                        return;
                    }
                    if (date1.length() != 10 || date2.length() != 10) {
                        JOptionPane.showMessageDialog(frame, "Veuillez entrer une date valide (YYYY-MM-DD)");
                        return;
                    }
                    String CA = String.valueOf(stub.getCABetweenDates(date1, date2));
                    String localCA = String.valueOf(stub.getLocalCABetweenDates(date1, date2));
                    JOptionPane.showMessageDialog(frame, "Chiffre d'affaire Global entre le " + date1 + " et " + date2 + " est de " + CA + " EUR\n" +
                            "Chiffre d'affaire Local entre le " + date1 + " et " + date2 + " est de " + localCA + " EUR");

                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        //Focus listener for date1
        date1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                date1.setText("");
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if(date1.getText().isEmpty()) {
                    date1.setText("Début (YYYY-MM-DD)");
                }
            }
        });
        //Focus listener for date2
        date2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                date2.setText("");
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if(date2.getText().isEmpty()) {
                    date2.setText("Fin (YYYY-MM-DD)");
                }
            }
        });
    }

    private void deleteArticle() throws RemoteException {
        List<List<Integer>> SelectedProducts_Ref_Quantity = getSelectedProductsRefQuantity(false , false);
        if (SelectedProducts_Ref_Quantity == null ) {
            updateStatus("Erreur lors de la suppression de l'article.");
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de l'article. Veuillez ressayer ...");
        }
        stub.deleteFacturedMassProducts(SelectedProducts_Ref_Quantity, id_facture);
        List<String> products = stub.getFacturedProducts(id_facture);
        updateTableWithNewData(products);
        updateStatus("Article supprimé avec succès");
    }

    private void editQuantity() throws RemoteException {
        List<List<Integer>> SelectedProducts_Ref_Quantity = getSelectedProductsRefQuantity(true , false);
        if (SelectedProducts_Ref_Quantity == null ) {
            updateStatus("Erreur lors de la modification de la quantité commandé.");
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification de la quantité commandé. Veuillez ressayer ...");
        }
        stub.editFacturedMassProductsQuantity(SelectedProducts_Ref_Quantity, id_facture);
        List<String> products = stub.getFacturedProducts(id_facture);
        updateTableWithNewData(products);
        String TTC = String.valueOf(stub.getTTC(id_facture));
        updateHeader(id_facture, "Page de facturation N°" + id_facture + " - Caisse N°" + serialVersionUID , stub.getFactureDate(id_facture), TTC);
        updateStatus("Quantité modifiée avec succès");
    }

    private void cancelFacture(JSplitPane split) throws RemoteException {
        if (id_facture == -1) {
            JOptionPane.showMessageDialog(this, "Erreur à la récupération de la facture en cours");
            return;
        }
        stub.cancelFacture(id_facture);
        updateStatus("Facture d'ID " + id_facture + " annulée avec succès");
        resetToDefault(split);
        JOptionPane.showMessageDialog(this, "Facture D'ID " + id_facture + " annulée avec succès");
        this.id_facture = -1;
    }



    private void payFacture(JSplitPane split) throws RemoteException {
        if (id_facture == -1) {
            JOptionPane.showMessageDialog(this, "Aucune facture en cours de création");
            return;
        }
        if (!bankingCard.isSelected() && !cash.isSelected()) {
            JOptionPane.showMessageDialog(this, "Veuillez choisir un mode de paiement");
            return;
        }
        if (bankingCard.isSelected() && cash.isSelected()) {
            JOptionPane.showMessageDialog(this, "Veuillez choisir un seul mode de paiement");
            return;
        }
        Boolean isCard = bankingCard.isSelected();
        stub.payFacture(id_facture, isCard);
        updateStatus("Facture d'ID " + id_facture + " payée avec succès");
        resetToDefault(split);
        JOptionPane.showMessageDialog(this, "Facture D'ID " + id_facture + " payée avec succès");
        this.id_facture = -1;
    }

    private void resetToDefault(JSplitPane split) throws RemoteException {
        this.setJMenuBar( createHeader("Page des articles"));
        defaultProductsSetTable();
        this.setTitle("Heptathlon - Caisse N° " + serialVersionUID);
        split.setLeftComponent(createBodyLeft());
        split.setRightComponent(createBodyRight());
        split.setDividerLocation(200);
        searchArticle.setText("Rechercher un article");
    }



    private void searchArticle() throws RemoteException {
        String search = searchArticle.getText();
        if (search.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer une réference d'article à rechercher");
            defaultProductsSetTable();
            return;
        }
        int ref = -1;
        try {
            ref = Integer.parseInt(search);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La réference d'article doit être un nombre entier");
            defaultProductsSetTable();
            return;
        }
        List<String> product = stub.getProductInfo(ref);
        if (product == null || product.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun article trouvé avec la réference " + search);
            defaultProductsSetTable();
            return;
        }
        //Update table with new data
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        List<String> productInfo = product;
        System.out.println(productInfo);
        //Products print : [ref, price, name, stock]
        String Ref = productInfo.get(0);
        System.out.println("ref:"+Ref);
        String Name = productInfo.get(2);
        System.out.println("name:"+Name);
        String Price = productInfo.get(1) + " EUR";
        System.out.println("price:"+Price);
        String Stock = productInfo.get(3) + " UNIT";
        System.out.println("stock:"+Stock);
        model.addRow(new Object[]{Boolean.FALSE, Ref, Name, Price, Stock, ""});
        updateStatus("Article trouvé    =>   Réf: " + Ref + "    Catégorie: " + Name + "    Stock: " + Stock + "    Prix: " + Price);
    }

    private void ajouterStock(JSplitPane split) throws RemoteException {
        List<List<Integer>> SelectedProducts_Ref_Quantity = getSelectedProductsRefQuantity(false , false);
        if (SelectedProducts_Ref_Quantity == null ) {
            updateStatus("Erreur lors de l'ajout de stock.");
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de stock. Veuillez ressayer ...");
        }
        for (List<Integer> ref_quantity : SelectedProducts_Ref_Quantity) {
            int ref = ref_quantity.get(0);
            int quantity = ref_quantity.get(1);
            if (quantity == 0) {
                JOptionPane.showMessageDialog(this, "La quantité demandée pour l'article " + ref + " est nulle");
                return;
            }
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "La quantité demandée pour l'article " + ref + " est négative");
                return;
            }
        }
        stub.addMassProduct(SelectedProducts_Ref_Quantity);
        updateStatus("Stock ajouté avec succès");
        //defaultProductsSetTable();
        resetToDefault(split);
    }

    private void defaultProductsSetTable() throws RemoteException {
        List<String> products = stub.getProductsInfo();
        //Update table with new data
        System.out.println(products);
        //Products print : [ [ref, price, name, stock], [ref, price, name, stock], ...]
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (int i = 0; i < products.size(); i++) {
            String[] product = products.get(i).split(",");
            model.addRow(new Object[]{Boolean.FALSE, product[0].substring(1), product[2], product[1] + " EUR", product[3].substring(0, product[3].length() - 1) + " UNIT", ""});
            System.out.println(product.toString());
        }
    }

    private void selectedNotSelectedStatus(javax.swing.event.TableModelEvent e) {
        if (e.getColumn() == 0) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            DefaultTableModel model = (DefaultTableModel) e.getSource();
            Boolean selected = (Boolean) model.getValueAt(row, column);
            String actionType = selected ? "sélectionner" : "désélectionner";
            String ref = (String) model.getValueAt(row, 1);
            String name = (String) model.getValueAt(row, 2);
            String price = (String) model.getValueAt(row, 3);
            String stock = (String) model.getValueAt(row, 4);
            String quantity = (String) model.getValueAt(row, 5);
            if (Objects.equals(quantity, "")) {
                quantity = "0";
            }
            updateStatus("Article "+actionType+"    =>   Réf: " + ref + "    Catégorie: " + name + "    Quantité "+actionType+": " + quantity + "    Stock: " + stock + "    Prix: " + price);
        }
    }

    private List<List<Integer>> getSelectedProductsRefQuantity(Boolean isValide, Boolean stockCheck) throws RemoteException {
        List<List<Integer>> SelectedProducts_Ref_Quantity = new ArrayList<>();
        //Get selected rows
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean selected = (Boolean) model.getValueAt(i, 0);
            if (selected) {
                int ref = Integer.parseInt((String) model.getValueAt(i, 1));
                //remove UNIT from stock ex: 10 UNIT -> 10
                String strStock = (String) model.getValueAt(i, 4);
                int stock = Integer.parseInt(strStock.substring(1, strStock.length() - 5));
                String strQuantity = (String) model.getValueAt(i, 5);
                if (Objects.equals(strQuantity, "")) {
                    JOptionPane.showMessageDialog(this, "Veuillez entrer une quantité pour l'article " + ref);
                    return null;
                }
                int quantity = Integer.parseInt(strQuantity);
                if (isValide) {
                    if (quantity > stub.getStockOfProduct(ref)) {
                        JOptionPane.showMessageDialog(this, "La quantité demandée pour l'article " + ref + " est supérieure au stock disponible");
                        return null;
                    }
                    if (stockCheck) {
                        if (quantity == 0) {
                            JOptionPane.showMessageDialog(this, "La quantité demandée pour l'article " + ref + " est nulle");
                            return null;
                        }
                    }
                }
                List<Integer> ref_quantity = new ArrayList<>();
                ref_quantity.add(ref);
                ref_quantity.add(quantity);
                SelectedProducts_Ref_Quantity.add(ref_quantity);
            }
        }
        if (SelectedProducts_Ref_Quantity.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner au moins un article pour facturer");
            return null;
        }
        return SelectedProducts_Ref_Quantity;
    }


    private void facturerClicked() throws InterruptedException, RemoteException {
        System.out.println("Facturer clicked");
        updateStatus("Création de la facture en cours ...");
        List<List<Integer>> SelectedProducts_Ref_Quantity = getSelectedProductsRefQuantity(true, true);
        if (SelectedProducts_Ref_Quantity == null ) {
            updateStatus("Erreur lors de la creation de la facture.");
            JOptionPane.showMessageDialog(this, "Erreur lors de la creation de la facture. Veuillez ressayer ...");
            return;
        }

        //Create a new facture
        this.id_facture = updateTableWithNewData(stub.setNewFacture(SelectedProducts_Ref_Quantity, (int) serialVersionUID));
        System.out.println("Facture created with id: " + id_facture);
        updateStatus("Facture créée avec succès. ID: " + id_facture);

        String date_facturation = stub.getFactureDate(this.id_facture);
        String ttc = String.valueOf(stub.getTTC(this.id_facture));

        updateHeader(this.id_facture, "Page de facturation N°" + this.id_facture + " - Caisse N°" + serialVersionUID , date_facturation, ttc);

    }

    private void updateHeader(int id_facture, String title, String date_facture, String ttc) {
        this.setJMenuBar( createFactureHeader(id_facture, title, date_facture, ttc));
    }

    private JMenuBar createFactureHeader(int id_facture, String title, String date_facture, String ttc) {
        this.setTitle("Date de la facture: " + date_facture + "   TTC: " + ttc + " EUR");

        JMenuBar header = new JMenuBar();
        header.setLayout( new GridLayout(2, 1));
        header.setBackground(Color.DARK_GRAY);

        JPanel top = new JPanel();
        top.setLayout( new FlowLayout());
        top.setBackground(Color.DARK_GRAY);
        JLabel page_title = new JLabel(title);
        page_title.setFont(new Font("Arial", Font.BOLD, 20));
        page_title.setForeground(Color.GRAY);
        top.add( page_title);
        header.add(top);

        JPanel bottom = new JPanel();
        bottom.setLayout( new GridLayout(1, 3));
        bottom.setBackground(Color.DARK_GRAY);

        JPanel bottom_left = new JPanel();
        bottom_left.setLayout( new FlowLayout(FlowLayout.LEFT));
        bottom_left.setBackground(Color.DARK_GRAY);
        bottom_left.add(payFacture);
        payFacture.setToolTipText("Payer la facture en cours");
        bottom_left.add(cancelFacture);
        cancelFacture.setToolTipText("Annuler la facture en cours");
        bottom.add(bottom_left);

        JPanel bottom_middle = new JPanel();
        bottom_middle.setLayout( new FlowLayout(FlowLayout.CENTER));
        bottom_middle.setBackground(Color.DARK_GRAY);
        bottom_middle.add(bankingCard);
        bottom_middle.add(cash);
        bottom.add(bottom_middle);

        JPanel bottom_right = new JPanel();
        bottom_right.setLayout( new FlowLayout(FlowLayout.RIGHT));
        bottom_right.setBackground(Color.DARK_GRAY);
        bottom_right.add( editQuantity );
        editQuantity.setToolTipText("Modifier la quantité de l'article sélectionné");
        bottom_right.add( deleteArticle );
        deleteArticle.setToolTipText("Supprimer l'article sélectionné");
        bottom.add(bottom_right);

        header.add(bottom);
        return header;
    }


    private int updateTableWithNewData(List<String> products) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (int i = 0; i < products.size() - 1; i++) {
            String[] product = products.get(i).split(",");
            String Ref = product[0].substring(1);
            System.out.println("ref:"+Ref);
            String Name = product[2];
            System.out.println("name:"+Name);
            String Price = product[3].substring(0, product[3].length() - 1);
            System.out.println("price:"+Price);
            String Stock = product[1];
            System.out.println("stock:"+Stock);

            model.addRow(new Object[]{Boolean.FALSE, Ref, Name, Price + " EUR", Stock + " UNIT", ""});
            System.out.println(product.toString());
        }
        return Integer.parseInt(products.get(products.size() -1));
    }

    //Status bar
    private JPanel createFooter(String status) {
        JPanel footer = new JPanel();
        footer.setLayout( new GridLayout(1, 1));
        footer.add( new JLabel(status));
        return footer;
    }

    private void updateStatus(String status) {
        try {
            JPanel footer = (JPanel) this.getContentPane().getComponent(1);
            JLabel label = (JLabel) footer.getComponent(0);
            label.setText(status);
        } catch (Exception e) {
            System.err.println("Error: " + e.toString());
            e.printStackTrace();
        }
    }

    //List of articles
    private JPanel createBodyRight() throws RemoteException {
        JPanel body_right = new JPanel();
        body_right.setLayout( new GridLayout(1, 1));
        List<String> products = stub.getProductsInfo();
        //System.out.println(products);
        String[] columns = {" ","Référence", "Nom", "Prix", "Stock", "Quantité à selectionner"};


        Object[][] data = new Object[products.size()][6];
        for (int i = 0; i < products.size(); i++) {
            String[] product = products.get(i).split(",");
            data[i][0] = Boolean.FALSE; //Selection
            data[i][1] = product[0].substring(1);//Ref + remove "["
            data[i][2] = product[2]; //Name
            data[i][3] = product[1] + " EUR"; //Price
            data[i][4] = product[3].substring(0, product[3].length() - 1) + " UNIT"; //Stock + remove "]"
            data[i][5] = "";//Quantity
        }

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Boolean.class;
                    case 5:
                        return String.class;
                    default:
                        return String.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 5;
            }
        };

        this.table = new JTable(model);
        table.setRowHeight(30);
        //reduce width of first column
        table.getColumnModel().getColumn(0).setMaxWidth(20);


        table.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(new JTextField()));

        JScrollPane scroll = new JScrollPane(table);
        body_right.add(scroll);
        return body_right;
    }



    //Family Tree
    private JPanel createBodyLeft() throws RemoteException {
        JPanel body_left = new JPanel();
        body_left.setLayout( new GridLayout(1, 1));
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Catégories");
        //Jtree with Family->Articles
        List<List<String>> families = stub.getAllFamily();
        for (List<String> family : families) {
            System.out.println(family);
            DefaultMutableTreeNode familyNode = new DefaultMutableTreeNode(family.get(1));
            root.add(familyNode);
            List<String> articles = stub.getProductsByFamily(Integer.parseInt(family.get(0)));
            for (String article : articles) {
                String Ref = article.split(",")[0].substring(1); //remove "[" from ref
                String Price = article.split(",")[1];
                String Stock = article.split(",")[2].substring(0, article.split(",")[2].length() - 1);
                DefaultMutableTreeNode articleNode = new DefaultMutableTreeNode("Réf: " + Ref + " Prix: " + Price + " Stock: " + Stock);
                familyNode.add(articleNode);
            }
        }
        JTree tree = new JTree(root);
        JScrollPane scroll = new JScrollPane(tree);
        body_left.add(scroll);
        return body_left;
    }


    private JMenuBar createHeader(String title) {
        JMenuBar header = new JMenuBar();
        header.setLayout( new GridLayout(2, 1));
        header.setBackground(Color.DARK_GRAY);

        JPanel top = new JPanel();
        top.setLayout( new FlowLayout());
        top.setBackground(Color.DARK_GRAY);
        JLabel page_title = new JLabel(title);
        page_title.setFont(new Font("Arial", Font.BOLD, 20));
        page_title.setForeground(Color.GRAY);
        top.add( page_title);
        header.add(top);

        JPanel bottom = new JPanel();
        bottom.setLayout( new GridLayout(1, 2));
        bottom.setBackground(Color.DARK_GRAY);


        JPanel bottom_left = new JPanel();
        bottom_left.setLayout( new FlowLayout(FlowLayout.LEFT));
        bottom_left.setBackground(Color.DARK_GRAY);
        bottom_left.add( facturer );
        facturer.setToolTipText("Créer une facture contenant les articles sélectionnés");
        bottom_left.add( ajouterStock );
        ajouterStock.setToolTipText("Ajouter du stock aux articles sélectionnées");
        bottom.add(bottom_left);

        JPanel bottom_right = new JPanel();
        bottom_right.setLayout( new FlowLayout(FlowLayout.RIGHT));
        bottom_right.setBackground(Color.DARK_GRAY);
        bottom_right.add( searchArticle );
        searchArticle.setToolTipText("Rechercher un article par sa référence");
        bottom_right.add( search );
        search.setToolTipText("Appuyer pour rechercher l'article saisi");
        CA.setToolTipText("Chiffre d'affaire");
        bottom_right.add(CA);
        SendDataToSiege.setToolTipText("Envoyer les données à la base de données du siège");
        bottom_right.add(SendDataToSiege);
        UpdatePrices.setToolTipText("Demander la mise à jour des prix des articles au siège");
        bottom_right.add(UpdatePrices);
        bottom.add(bottom_right);

        header.add(bottom);
        return header;
    }

    public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel( new FlatDarculaLaf());
        Main main = new Main();
        main.setVisible(true);
//        try {
//            Checkout stub = (Checkout) Naming.lookup("rmi://localhost:1095/CK");
//            stub.test();
//        } catch (Exception e) {
//            System.err.println("Client exception: " + e.toString());
//            e.printStackTrace();
//        }
    }
}