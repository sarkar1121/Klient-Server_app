/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ordersclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.time.LocalDate;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 * Main Client class which create main frame
 * @author hrusk
 */
public final class MainFrame extends javax.swing.JFrame {

    String hostName;
    int portNumber;


    public MainFrame(String hostName, int portNumber) {
        initComponents();
        this.hostName = hostName;
        this.portNumber = portNumber;
        try {
            loadItems();
            loadConsum();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        addListeners();
        set();
    }

    /**
     * Method creating thread for reload items table
     * @throws Exception 
     */
    private void loadItems() throws Exception {
        new Thread(() -> {
            realoadItemsTable();
        }).start();
    }

    /**
     * Method creating thread for reload consumptions table
     * @throws Exception 
     */
    private void loadConsum() throws Exception {
        new Thread(() -> {
            reloadConsumTable();
        }).start();
    }

    /**
     * Initial settings this frame
     */
    private void set() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("Správa objednávek");
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Method that clean table and add new data to the ItemTable from server
     * response
     *
     * @param serverResponse String response from the server that represents all
     * items
     * @param table the table we are editing
     */
    public void reloadTable(String serverResponse, JTable table) {
        ((DefaultTableModel) table.getModel()).setRowCount(0);
        String[] lines = serverResponse.split("/");
        for (String line : lines) {
            String[] items = line.split("\\|");
            ((DefaultTableModel) table.getModel()).addRow(items);
            table.repaint();
        }
    }

    /**
     * Method that restores the data in the ItemTable using data from the server
     */
    public void realoadItemsTable() {
        try {
            ClientCommunicationManagement ccm = new ClientCommunicationManagement(new Socket(hostName, portNumber), "allItems");
            ccm.sendRequest();
            String answer = ccm.getMessage();
            SwingUtilities.invokeLater(() -> {
                try {
                    reloadTable(answer, ItemTable);
                } catch (Exception e) {
                    e.printStackTrace(); // nebo jakkoliv jinak zpracujte vyjímku
                }
            });
        } catch (Exception e) {
            e.printStackTrace(); // nebo jakkoliv jinak zpracujte vyjímku
        }
    }

    /**
     * Method that restores the data in the ConsumtionTable using data from the
     * server
     */
    public void reloadConsumTable() {
        try {
            ClientCommunicationManagement ccm = new ClientCommunicationManagement(new Socket(hostName, portNumber), "loadConsYear|" + LocalDate.now().getYear());
            ccm.sendRequest();
            String answer = ccm.getMessage();
            SwingUtilities.invokeLater(() -> {
                try {
                    reloadTable(answer, ConsumtionTable);
                } catch (Exception e) {
                    System.out.println(e);
                }
            });
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * This method add listeners to all using components
     */
    private void addListeners() {
        ItemTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                int viewRow = ItemTable.getSelectedRow();
                if (viewRow >= 0) {
                    int modelRow = ItemTable.convertRowIndexToModel(viewRow);

                    String text = (String) ItemTable.getModel().getValueAt(modelRow, 0);
                    String category = (String) ItemTable.getModel().getValueAt(modelRow, 1);

                    name2.setText(text);
                    jComboBox1.setSelectedItem(category);
                }
            }
        });

        ConsumtionTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                int viewRow = ConsumtionTable.getSelectedRow();
                if (viewRow >= 0) {
                    int modelRow = ConsumtionTable.convertRowIndexToModel(viewRow);

                    String text = (String) ConsumtionTable.getModel().getValueAt(modelRow, 0);
                    String ordered = (String) ConsumtionTable.getModel().getValueAt(modelRow, 1);
                    String amount = (String) ConsumtionTable.getModel().getValueAt(modelRow, 2);
                    String consumed = (String) ConsumtionTable.getModel().getValueAt(modelRow, 3);

                    name3.setText(text);
                    orderedText.setText(ordered);
                    amountText.setText(amount);
                    DateText.setText(consumed);

                }
            }
        });
        
        addItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddItem(null, true, hostName, portNumber);
                realoadItemsTable();
            }
        });
        
        AddOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddOrder(null, true,hostName, portNumber);
                reloadConsumTable();
            }
        });
        Search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query;
                switch ((String) comboBoxForSearch.getSelectedItem()) {
                    case "Jméno_položky":
                        query = "loadConsItem|" + textForSearch.getText();
                        break;
                    case "Rok":
                        query = "loadConsYear|" + textForSearch.getText();
                        break;
                    case "Datum":
                        query = "loadConsDate|" + textForSearch.getText();
                        break;
                    default:
                        query = "";
                        break;
                }
                new Thread(() -> {
                    try {
                        ClientCommunicationManagement ccm = new ClientCommunicationManagement(new Socket(hostName, portNumber), query);
                        ccm.sendRequest();
                        SwingUtilities.invokeLater(() -> {
                            try {
                                reloadTable(ccm.getMessage(), ConsumtionTable);
                            } catch (Exception ex) {
                                System.out.println(ex);
                            }
                        });
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }).start();
            }
        });

        updateConsum.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    try {
                        ClientCommunicationManagement ccm = new ClientCommunicationManagement(new Socket(hostName, portNumber), "updCon|" + name3.getText() + "|" + orderedText.getText() + "|" + amountText.getText() + "|" + DateText.getText());
                        ccm.sendRequest();
                        SwingUtilities.invokeLater(() -> {
                            try {
                                reloadConsumTable();
                            } catch (Exception ex) {
                                System.out.println(ex);
                            }
                        });
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }).start();
                reloadConsumTable();
            }
        });

        
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    try {
                        ClientCommunicationManagement ccm = new ClientCommunicationManagement(new Socket(hostName, portNumber), "remCon|" + name3.getText() + "|" + orderedText.getText());
                        ccm.sendRequest();
                        SwingUtilities.invokeLater(() -> {
                            try {
                                reloadTable(ccm.getMessage(), ConsumtionTable);
                            } catch (Exception ex) {
                                System.out.println(ex);
                            }
                        });
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }).start();
                reloadConsumTable();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        addItem = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        itemLabel2 = new javax.swing.JLabel();
        nameLabel2 = new javax.swing.JLabel();
        categoryLabel2 = new javax.swing.JLabel();
        name2 = new javax.swing.JTextField();
        change2 = new javax.swing.JButton();
        photoPanel2 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        ItemTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        textForSearch = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        AddOrder = new javax.swing.JButton();
        comboBoxForSearch = new javax.swing.JComboBox<>();
        warningDateLabel = new javax.swing.JLabel();
        Search = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        itemLabel3 = new javax.swing.JLabel();
        nameLabel3 = new javax.swing.JLabel();
        DateText = new javax.swing.JTextField();
        categoryLabel3 = new javax.swing.JLabel();
        name3 = new javax.swing.JTextField();
        updateConsum = new javax.swing.JButton();
        remove = new javax.swing.JButton();
        orderedText = new javax.swing.JTextField();
        categoryLabel4 = new javax.swing.JLabel();
        categoryLabel5 = new javax.swing.JLabel();
        amountText = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        ConsumtionTable = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        addItem.setText("Přidej nový");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addItem)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(addItem)
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel8.setMaximumSize(new java.awt.Dimension(219, 608));
        jPanel8.setMinimumSize(new java.awt.Dimension(219, 608));

        itemLabel2.setText("-----------------Položka---------------");

        nameLabel2.setText("Název");

        categoryLabel2.setText("Kategorie");

        name2.setEditable(false);

        change2.setText("Uložit změny");
        change2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                change2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout photoPanel2Layout = new javax.swing.GroupLayout(photoPanel2);
        photoPanel2.setLayout(photoPanel2Layout);
        photoPanel2Layout.setHorizontalGroup(
            photoPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        photoPanel2Layout.setVerticalGroup(
            photoPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 177, Short.MAX_VALUE)
        );

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pomůcky", "Prostředky", "Ochranné pomůcky", "Náhrandí díly", "Spotřební materiál", "Větší položky", "     " }));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(photoPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(change2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(name2)
                    .addComponent(itemLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(categoryLabel2)
                            .addComponent(nameLabel2))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(itemLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(name2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(categoryLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63)
                .addComponent(photoPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 221, Short.MAX_VALUE)
                .addComponent(change2)
                .addContainerGap())
        );

        ItemTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Název", "Kategorie"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(ItemTable);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Položky", jPanel1);

        textForSearch.setText("id");

        jLabel1.setText("Najdi všechny záznamy pro");

        AddOrder.setText("Přidej nový");

        comboBoxForSearch.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Jméno_položky", "Rok", "Datum" }));
        comboBoxForSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxForSearchActionPerformed(evt);
            }
        });

        warningDateLabel.setText("! Datum je třeba zadat ve formátu YYYY-MM-DD");

        Search.setText("Vyhledat");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(AddOrder)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxForSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textForSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(Search)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warningDateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                .addGap(17, 17, 17))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textForSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(AddOrder)
                    .addComponent(comboBoxForSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(warningDateLabel)
                    .addComponent(Search)))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel10.setMaximumSize(new java.awt.Dimension(219, 608));
        jPanel10.setMinimumSize(new java.awt.Dimension(219, 608));

        itemLabel3.setText("---------------Záznam---------------");

        nameLabel3.setText("Položka");

        DateText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DateTextActionPerformed(evt);
            }
        });

        categoryLabel3.setText("Objednáno");

        name3.setEditable(false);

        updateConsum.setText("Ulož změny");
        updateConsum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateConsumActionPerformed(evt);
            }
        });

        remove.setText("Smazat položku");

        orderedText.setEditable(false);
        orderedText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderedTextActionPerformed(evt);
            }
        });

        categoryLabel4.setText("Počet ks");

        categoryLabel5.setText("Datum spotřebování");

        amountText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                amountTextActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(remove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(updateConsum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(name3)
                    .addComponent(itemLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                    .addComponent(DateText)
                    .addComponent(orderedText)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameLabel3)
                            .addComponent(categoryLabel3)
                            .addComponent(categoryLabel4)
                            .addComponent(categoryLabel5))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(amountText))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(itemLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(name3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(categoryLabel3)
                .addGap(4, 4, 4)
                .addComponent(orderedText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(categoryLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(amountText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(categoryLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DateText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(updateConsum)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(remove)
                .addContainerGap())
        );

        ConsumtionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Položka", "Objednáno", "Počet", "Vypotřebováno"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(ConsumtionTable);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE))
                .addGap(0, 12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Záznamy", jPanel3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void change2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_change2ActionPerformed
        new Thread(() -> {
            try {
                ClientCommunicationManagement ccm = new ClientCommunicationManagement(new Socket(hostName, portNumber), "updItem|" + name2.getText() + "|" + (String) jComboBox1.getSelectedItem());
                ccm.sendRequest();
                String answer = ccm.getMessage();
                SwingUtilities.invokeLater(() -> {
                    try {
                        realoadItemsTable();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                });
            } catch (Exception e) {
                System.out.println(e);
            }
        }).start();
    }//GEN-LAST:event_change2ActionPerformed

    private void comboBoxForSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxForSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboBoxForSearchActionPerformed

    private void amountTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_amountTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_amountTextActionPerformed

    private void orderedTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderedTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_orderedTextActionPerformed

    private void updateConsumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateConsumActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_updateConsumActionPerformed

    private void DateTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DateTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DateTextActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            System.out.println(ex);
        }

        String hostName = "127.0.0.1";
        int portNumber = 8000;

        try {
            new MainFrame(hostName, portNumber).setVisible(true);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddOrder;
    private javax.swing.JTable ConsumtionTable;
    private javax.swing.JTextField DateText;
    private javax.swing.JTable ItemTable;
    private javax.swing.JButton Search;
    private javax.swing.JButton addItem;
    private javax.swing.JTextField amountText;
    private javax.swing.JLabel categoryLabel2;
    private javax.swing.JLabel categoryLabel3;
    private javax.swing.JLabel categoryLabel4;
    private javax.swing.JLabel categoryLabel5;
    private javax.swing.JButton change2;
    private javax.swing.JComboBox<String> comboBoxForSearch;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel itemLabel2;
    private javax.swing.JLabel itemLabel3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField name2;
    private javax.swing.JTextField name3;
    private javax.swing.JLabel nameLabel2;
    private javax.swing.JLabel nameLabel3;
    private javax.swing.JTextField orderedText;
    private javax.swing.JPanel photoPanel2;
    private javax.swing.JButton remove;
    private javax.swing.JTextField textForSearch;
    private javax.swing.JButton updateConsum;
    private javax.swing.JLabel warningDateLabel;
    // End of variables declaration//GEN-END:variables
}
