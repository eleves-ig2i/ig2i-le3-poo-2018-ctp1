package metier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.*;
import modele.*;

/**
 * Requêtes sur la base de données.
 * 
 * Cette classe à instance unique gère la liaison à la base de données, et
 * fait l'équivalence entre le contenu de la base et les objets modèle.
 * 
 * @author jessy
 */
public class RequeteGestionStock {
    // Configuration base de données
    
    /**
     * URL de connexion à la base.
     */
    private final static String DATABASE_URL = "jdbc:derby://localhost:1527/database";
    
    /**
     * Driver JDBC utilisé.
     */
    private final static String DATABASE_DRIVER = "org.apache.derby.jdbc.ClientDriver";
    
    /**
     * Utilisateur de la base de données.
     */
    private final static String DATABASE_USER = "username";
    
    /**
     * Mot de passe de la base de données.
     * 
     * @note Stocker le mot de passe en clair n'est pas très sécurisé.
     */
    private final static String DATABASE_PASS = "password";
    
    // Connection
    
    /**
     * Connexion JDBC.
     */
    private Connection connection;
    
    /**
     * Établit la connexion à la BDD.
     * 
     * @throws SQLException Erreur de la base.
     * @throws ClassNotFoundException Driver JDBC non trouvé.
     */
    private void connect() throws SQLException, ClassNotFoundException {
        Class.forName(DATABASE_DRIVER);
        connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
    }
    
    /**
     * Constructeur par défaut.
     * 
     * @throws SQLException Erreur de la base.
     * @throws ClassNotFoundException Driver JDBC non trouvé.
     */
    private RequeteGestionStock() throws SQLException, ClassNotFoundException {
        connect();
    }
    
    // Singleton
    
    /**
     * Instance globale (singleton)
     */
    private static RequeteGestionStock requeteGestionStock = null;
    
    /**
     * Récupère l'instance unique de cette classe. Elle est créée si nécessaire.
     * 
     * @return L'instance unique.
     * @throws SQLException Erreur de la base.
     * @throws ClassNotFoundException Driver JDBC non trouvé.
     */
    public static RequeteGestionStock getInstance() throws SQLException, ClassNotFoundException {
        // S'il n'y a pas d'instance, on en crée une
        if( requeteGestionStock == null )
            requeteGestionStock = new RequeteGestionStock();
        
        // A ce point c'est garanti qu'on a une instance, on la retourne.
        return requeteGestionStock;
    }
    
    // Requêtes
    
    /**
     * Obtient la liste de tous les produits.
     * 
     * @return Une liste des produits.
     * @throws SQLException Erreur SQL.
     */
    public List<Produit> ensProduits() throws SQLException {
        List<Produit> result = new ArrayList();
        
        // On exécute la requête
        String sql = "SELECT * FROM Produit";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        
        // On collecte les résultats
        while(rs.next()){
            // On reconstitue le produit
            int id = rs.getInt("nProduit");
            String nom = rs.getString("nom");
            int stockMin = rs.getInt("stockMin");
            int stockMax = rs.getInt("stockMax");
            result.add(new Produit(id, nom, stockMin, stockMax));
        }
        
        // Et on n'oublie pas de fermer les ressources!
        rs.close();
        stmt.close();
        return result;
    }
    
    /**
     * Obtient la liste de tous les produits dont le nom commence par nom.
     * 
     * @param nom Le début du nom recherché, ou le nom entier. Insensible à la casse.
     * @return Une liste des produits.
     * @throws SQLException Erreur SQL.
     */
    public List<Produit> ensProduits(String nom) throws SQLException {
        List<Produit> result = new ArrayList();
        
        // On exécute la requête
        String sql = "SELECT * FROM Produit WHERE UPPER(Produit.nom) LIKE ?";
        String search = nom.toUpperCase() + '%';
        
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, search);
        ResultSet rs = stmt.executeQuery();
        
        // On collecte les résultats
        while(rs.next()){
            // On reconstitue le produit
            int id = rs.getInt("nProduit");
            String nomProduit = rs.getString("nom");
            int stockMin = rs.getInt("stockMin");
            int stockMax = rs.getInt("stockMax");
            result.add(new Produit(id, nomProduit, stockMin, stockMax));
        }
        
        // Et on n'oublie pas de fermer les ressources!
        rs.close();
        stmt.close();
        return result;
    }
    
    /**
     * Ajoute les mouvements d'un produit.
     * 
     * Ajoute au produit donné tous ses mouvements.
     * 
     * @param produit Le produit dont on veut les mouvements.
     * @warning La liste n'est pas vidée auparavant.
     * @throws SQLException Erreur SQL.
     */
    public void addAllMouvementsToProduit(Produit produit) throws SQLException {
        // On exécute la requête
        String sql = "SELECT * FROM Mouvement WHERE nProduit = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, produit.getId());
        ResultSet rs = stmt.executeQuery();
        
        while(rs.next()){
            // On reconstruit le mouvement
            int id = rs.getInt("nMouvement");
            int quantite = rs.getInt("quantite");
            Date date= rs.getDate("dateMouvement");
            Mouvement m = new Mouvement(id, produit, date, quantite);
            
            // Sans oublier de l'ajouter au produit.
            produit.addMouvement(m);
        }
    }
    
    /**
     * Ajoute un mouvement à un produit.
     * Le mouvement est ajouté en BDD, puis à l'objet Produit donné.
     * 
     * @param p Le produit en question.
     * @param quantite La quantité de produit à déplacer.
     * @return true si l'ajout a réussi, false sinon.
     * @throws SQLException Erreur SQL.
     */
    public boolean ajouteMouvement(Produit p, int quantite) throws SQLException {
        // On prend la date maintenant, ne sachant pas combien de temps la requête
        // prendra, autant la prélever une seule fois.
        Date dateCourante = new Date();
        
        // On exécute la requête
        String sql = "INSERT INTO Mouvement(nProduit, quantite, dateMouvement) VALUES(?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, p.getId());
        stmt.setInt(2, quantite);
        stmt.setDate(3, new java.sql.Date(dateCourante.getTime()));
        
        // On récupère le nombre de lignes changées pour voir si l'ajout s'est bien fait
        int rowsChanged = stmt.executeUpdate();
        
        if( rowsChanged == 0 )
            return false;
        
        // On récupère les ID's
        ResultSet ids = stmt.getGeneratedKeys();
        
        if( ids == null || !ids.next() )
            return false;
        
        // Et enfin on recrée le mouvement
        int id = ids.getInt(1);
        Mouvement m = new Mouvement(id, p, dateCourante, quantite);
        
        // Puis on l'ajoute au produit.
        p.addMouvement(m);
        return true;
    }
    
    // Tests
    
    /**
     * Méthode principale pour tester les requêtes.
     * 
     * @param args Arguments de ligne de commande.
     * @throws Exception Toute exception qui se produit pendant les tests.
     */
    public static void main(String[] args) throws Exception {
        // Tests partie 1
        RequeteGestionStock rq = RequeteGestionStock.getInstance();
        System.out.println("Requete: " + rq);
        
        // Tests partie 3
        List<Produit> allProduits = rq.ensProduits();
        System.out.printf("ensProduits() = [%d] %s\n", allProduits.size(), allProduits);
        
        List<Produit> shirts = rq.ensProduits("T-SHIrT");
        System.out.printf("ensProduits(\"t-shirts\") = [%d] %s\n", shirts.size(), shirts);
        
        Produit shirt = shirts.get(0);
        rq.addAllMouvementsToProduit(shirt);
        System.out.printf("shirt a %d mouvements: %s\n", shirt.getMouvements().size(), shirt.getMouvements());
        
        boolean result = rq.ajouteMouvement(shirt, 420);
        System.out.printf("ajout d'un mouvement de shirt: %b\n", result);
        
        // Tests partie 6
        List<Produit> manteaux = rq.ensProduits("Manteau");
        Produit manteau = manteaux.get(0);
        rq.addAllMouvementsToProduit(manteau);
        System.out.printf("manteau: %s\n", manteau);
        System.out.printf("mouvements (%d): %s\n", manteau.getMouvements().size(), manteau.getMouvements());
        System.out.printf("\tConsommation: %f\n\tRupture dans %f jours\n",
                manteau.getConsommationJournalière(), manteau.joursAvantRupture()
        );
    }
}
