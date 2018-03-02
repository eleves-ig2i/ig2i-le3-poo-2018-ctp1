package modele;

import java.util.Date;

/**
 * @brief Un mouvement de produits.
 * 
 * Cette classe représente un mouvement d'un produit vers ou depuis l'entrepôt.
 * Un approvisionnement a une quantité positive, une sortie a une quantité
 * négative. Chaque mouvement enregistre la date de l'opération.
 * 
 * @author jessy
 */
public class Mouvement implements Comparable<Mouvement> {
    /**
     * Identifiant en base de données.
     */
    private int id;
    
    /**
     * Produit auquel le mouvement se réfère.
     */
    private Produit produit;
    
    /**
     * Date à laquelle l'opération s'est déroulée.
     */
    private Date date;
    
    /**
     * Quantité de produit déplacée.
     * Une quantité positive représente une entrée, et une quantité négative une sortie.
     */
    private int quantite;

    /**
     * Constructeur par données.
     * 
     * @param id Identifiant du mouvement en base de données.
     * @param produit Produit qui fait l'objet d'un mouvement.
     * @param date Date à laquelle le mouvement s'est produit.
     * @param quantite Quantité de produit déplacée. Une quantité positive représente une entrée, et une quantité négative une sortie.
     */
    public Mouvement(int id, Produit produit, Date date, int quantite) {
        this.id = id;
        this.produit = produit;
        this.date = date;
        this.quantite = quantite;
    }

    public Produit getProduit() {
        return produit;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public int getQuantite() {
        return quantite;
    }

    @Override
    public String toString() {
        return "Mouvement{" + "id=" + id + ", date=" + date + ", quantite=" + quantite + '}';
    }
    
    // Implémentation de l'interface Comparable pour le tri.
    @Override
    public int compareTo(Mouvement o) {
        return o.getDate().compareTo(date);
    }
}
