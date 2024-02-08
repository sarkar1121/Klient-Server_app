/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordersclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Class JDialog for create text representation of consumption and send it to
 * server
 *
 * @author hrusk
 */
public class AddOrder extends javax.swing.JDialog {

    String hostName;
    int portNumber;
    DefaultTableModel model;

    /**
     *
     * @param parent
     * @param modal
     * @param hostName hostname for creating Socket
     * @param portNumber portnumber for creating Socket
     */
    public AddOrder(java.awt.Frame parent, boolean modal, String hostName, int portNumber) {
        super(parent, "Přidej objednávku", modal);
        initComponents();
        this.hostName=hostName;
        this.portNumber=portNumber;
        model = (DefaultTableModel) jTable2.getModel();
        add.setEnabled(false);
        saveButton.setEnabled(false);
        ClientCommunicationManagement ccm;
        try {
            ccm = new ClientCommunicationManagement(new Socket(hostName, portNumber), "allItems");
            ccm.sendRequest();
            addToCB(ccm.getMessage());
        } catch (Exception ex) {
            System.out.println(ex);
        }
        buttonUse();
        buttonAdd();
        buttonListener();
        set();
    }

    public void addToCB(String serverResponse) {
        String[] lines = serverResponse.split("/");
        for (String line : lines) {
            String[] items = line.split("\\|");
            cbItem.addItem(items[0]);
        }
    }

    private void close() {
        JOptionPane.showMessageDialog(null, "Obsah objednávky přidán", "Objednávka úspěšně přidána", JOptionPane.INFORMATION_MESSAGE);
        this.dispose();
    }

    private void set() {
        saveButton.setEnabled(false);
        add.setEnabled(false);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationByPlatform(false);
    }

    private void buttonUse() {
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jButton1.setEnabled(false);
                jTextField1.setEditable(false);
                add.setEnabled(true);
            }
        });
    }

    private void buttonAdd() {
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveButton.setEnabled(true);
                String selectedItem = (String) cbItem.getSelectedItem();  // Get selected item from JComboBox
                String val = value.getText();  // Get entered value from JTextField
                model.addRow(new Object[]{selectedItem, val});  // Add row to the table               
            }
        });
    }

    private void buttonListener() {
    saveButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ClientCommunicationManagement ccm;
                    String ordered = jTextField1.getText();
                    try {
                        int rowCount = model.getRowCount();
                        for (int i = 0; i < rowCount; i++) {
                            String itemName = (String) model.getValueAt(i, 0);
                            String val = (String) model.getValueAt(i, 1);
                            System.out.println("addCon|" + itemName+"|"+ordered + "|" + val+"|"+null);
                            ccm = new ClientCommunicationManagement(new Socket(hostName, portNumber), "addCon|" + itemName+"|"+ordered + "|" + val+"|"+null);
                            ccm.sendRequest();
                            String serverResponse = ccm.getMessage();
                            if (!serverResponse.equals("itemAdded")) {  // replace "itemAdded" with the actual success message from your server
                                throw new Exception("Failed to add item " + itemName);
                            }
                        }
                        JOptionPane.showMessageDialog(null, "Všechny položky byly úspěšně přidány.", "Úspěch", JOptionPane.INFORMATION_MESSAGE);
                        close();
                    } catch (Exception ex) {
                        System.out.println(ex);
                        JOptionPane.showMessageDialog(null, "Přidání položky se nezdařilo.", "Chyba", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }).start();
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

        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cbItem = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        value = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        add = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        saveButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocation(new java.awt.Point(0, 0));
        setMaximumSize(new java.awt.Dimension(611, 388));
        setMinimumSize(new java.awt.Dimension(611, 388));
        setResizable(false);
        setSize(new java.awt.Dimension(611, 391));

        jLabel1.setText("---------------------------------------------------------Nákup-------------------------------------------------------");

        jLabel4.setText("--------------------------------------------------------Položky-------------------------------------------------------");

        jLabel5.setText("Položka");

        jLabel6.setText("Počet");

        jLabel7.setText("ks");

        jLabel8.setText("Nakoupeno");

        add.setText("Přidej");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "název", "počet ks"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jTextField1.setText("YYYY-MM-DD");

        saveButton.setText("Uložit");

        jButton1.setText("Použít");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(saveButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbItem, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(value, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(add, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 509, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cbItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(value, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(add)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add;
    private javax.swing.JComboBox<String> cbItem;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField value;
    // End of variables declaration//GEN-END:variables
}
