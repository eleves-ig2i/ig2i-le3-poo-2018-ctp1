package vuecontrole;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import metier.RequeteGestionStock;
import modele.*;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Fenêtre de prévision des ruptures de stock.
 * 
 * Cette fenêtre affiche le tableau des ruptures de stock à venir.
 * 
 * @author jessy
 */
public class Prevision extends javax.swing.JFrame {
    // On n'a pas besoin d'avoir plusieurs fenêtres Prevision, donc on utilise
    // un Singleton pour garantir qu'il n'y en a qu'une d'ouverte à la fois.
    static private Prevision singleton = null;
    
    /**
     * Retourne l'unique instance de cette classe.
     * 
     * @throws SQLException Erreur de BDD.
     * @throws ClassNotFoundException Driver JDBC non trouvé.
     */
    public static Prevision getInstance() throws SQLException, ClassNotFoundException{
        // Si l'instance n'existe pas encore, la créer.
        if( singleton == null )
            singleton = new Prevision();
        
        // Maintenant qu'on sait que l'instance existe, on la retourne!
        return singleton;
    }
    
    // Requête
    private RequeteGestionStock requete = null;

    /**
     * Constructuer par défaut.
     * 
     * Initialise la fenêtre et affiche les données.
     * 
     * @throws SQLException Erreur de BDD.
     * @throws ClassNotFoundException Driver JDBC non trouvé.
     */
    private Prevision() throws SQLException, ClassNotFoundException {
        // On récupère la requête.
        requete = RequeteGestionStock.getInstance();
        
        // On laisse Swing initialiser les composants.
        initComponents();
        
        // Puis on charge les données dans le JTable.
        updatePrevisions();
    }
    
    /**
     * Récupère la liste des produits dans l'ordre.
     * 
     * Les produits sont triés par date de rupture de stock décroissante.
     * 
     * @return La liste des produits.
     * @throws SQLException Erreur de BDD.
     */
    private List<Produit> recupererProduits() throws SQLException {
        // On récupère la liste des produits.
        List<Produit> produits = requete.ensProduits();
        
        // Pour chaque produit, on récupère tous les mouvements
        for(Produit curr: produits)
            requete.addAllMouvementsToProduit(curr);
        
        // Puis on trie la liste par dates de rupture de stock décroissantes.
        produits.sort(new Comparator<Produit>() {
            @Override
            public int compare(Produit o1, Produit o2) {
                return o2.getDateDeRupture().compareTo(o1.getDateDeRupture());
            }
        });
        
        return produits;
    }
    
    /**
     * Récupère les données et met à jour le JTable.
     */
    private void updatePrevisions(){
        
        try {
            // On récupère l'ensemble des produits triés.
            List<Produit> produits = recupererProduits();
            
            // On crée un modèle avec nos colonnes.
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Produit");
            model.addColumn("Quantité");
            model.addColumn("Conso. Moyenne");
            model.addColumn("Date Rupture");
            
            // On utilise ce formatteur pour afficher les dates proprement.
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            // Puis on ajoute chaque produit comme une ligne dans le JTable.
            for(Produit curr: produits){
                // On récupère toutes les informations à afficher.
                String nom = curr.getNom();
                int quantite = curr.getQuantiteEnStock();
                float conso = curr.getConsommationJournalière();
                Date rupture = curr.getDateDeRupture();
                
                // Puis on les place dans un tableau qui servira de ligne dans le modèle.
                Object[] row = {
                    nom, 
                    quantite, 
                    String.format("%.2f", conso), 
                    rupture != null ? dateFormat.format(rupture) : ""
                };
                
                model.addRow(row);
            }
            
            // Et on n'oublie pas d'associer ce modèle à notre table.
            table.setModel(model);
        } catch(SQLException e){
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        setTitle("Gestion Ruptures");

        jScrollPane1.setBorder(null);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(table);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}