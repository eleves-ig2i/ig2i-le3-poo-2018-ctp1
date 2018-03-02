package modele;

import java.util.*;

/**
 * @brief Un produit dans le stock.
 * 
 * Cette classe représente un type de produit présent dans le stock, avec ses
 * limites désirées de stock et l'historique des mouvements.
 * 
 * @author jessy
 */
public class Produit {
    /**
     * Identifiant en base de données.
     */
    private int id;
    
    /**
     * Nom du produit.
     */
    private String nom;
    
    /**
     * Quantité minimale de stock désirée.
     */
    private int stockMin;

    /**
     * Quantité maximale de stock désirée.
     */
    private int stockMax;
    
    /**
     * Historique des mouvements.
     */
    private List<Mouvement> mouvements;

    /**
     * 
     * @param id Identifiant en base de données.
     * @param nom Nom du produit.
     * @param stockMin Quantité minimale de stock désirée.
     * @param stockMax Quantité maximale de stock désirée.
     */
    public Produit(int id, String nom, int stockMin, int stockMax) {
        this.id = id;
        this.nom = nom;
        this.stockMin = stockMin;
        this.stockMax = stockMax;
        this.mouvements = new ArrayList();
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public int getStockMin() {
        return stockMin;
    }

    public int getStockMax() {
        return stockMax;
    }

    public List<Mouvement> getMouvements() {
        return mouvements;
    }
    
    /**
     * Ajoute un mouvement à l'historique.
     * 
     * @param m Le mouvement à ajouter.
     */
    public void addMouvement(Mouvement m){
        mouvements.add(m);
        
        // On re-trie les mouvements
        // TODO: Inefficace, voir pour utiliser un TreeSet qui sera déjà trié.
        //       Aussi, implémenter Comparable dans Mouvement
        Collections.sort(mouvements);
    }

    /**
     * Calcule la quantité totale du produit actuellement en stock.
     * 
     * Cette quantité est calculée à partir de chaque entrée de l'historique, en
     * partant d'un stock initial vide.
     * 
     * @return La quantité actuellement en stock.
     */
    public int getQuantiteEnStock(){
        int result = 0;
        
        for(Mouvement curr: mouvements)
            result += curr.getQuantite();
        
        return result;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%d)", nom, getQuantiteEnStock());
    }
    
    /**
     * Retrouve le mouvement d'entrée pour ce produit ayant la date la plus reculée.
     * @return Le mouvement trouvé, ou null si non trouvé.
     */
    private Mouvement premiereEntree(){
        // La liste est triée avec les mouvements les plus récents en premiers,
        // donc on la parcourt en sens inverse pour trouver plus vite.
        ListIterator<Mouvement> it = mouvements.listIterator(mouvements.size());
        
        while( it.hasPrevious() ){
            Mouvement curr = it.previous();
            
            // On se content du premier mouvement positif qu'on trouve.
            if( curr.getQuantite() > 0 )
                return curr;
        }
        
        return null;
    }
    
    /**
     * Calcule le nombre de jours passés depuis la première entrée du produit
     * en magasin.
     * 
     * @return La valeur trouvée, ou 0 si le produit n'au aucune entrée.
     */
    private int joursDepuisPremiereEntree(){
        // On retrouve la première entrée.
        Mouvement premierMouvement = premiereEntree();
        
        if( premierMouvement == null )
            return 0;
        
        // On calcule le temps passé depuis
        Date now = new Date();
        long span = now.getTime() - premierMouvement.getDate().getTime();
        
        // On ramène cette valeur en jours, en arrondissant au plus bas pour
        // ne pas être influencés si la journée actuelle n'est pas complète.
        double msPerDay = 1000 * 60 * 60 * 24;
        return (int) Math.floor( (double) span / msPerDay );
    }
    
    /**
     * Calcule la quantité totale du produit consommée par les clients.
     * @return La quantité calculée, ou 0 s'il n'y a aucune sortie dans l'historique.
     */
    private int quantiteConsommee(){
        int quantiteTotale = 0;
        
        // On accumule simplement les mouvements négatifs.
        for(Mouvement curr: mouvements){
            if( curr.getQuantite() < 0 )
                quantiteTotale += -curr.getQuantite();
        }
        
        return quantiteTotale;
    }
    
    /**
     * Calcule la consommation journalière du produit.
     * 
     * @return La consommation trouvée.
     */
    public float getConsommationJournalière(){
        int qConso = quantiteConsommee();
        int nbJours = joursDepuisPremiereEntree();
        
        // S'il n'y a aucune entrée, nbJours=0. Continuer causerait une division
        // par 0, donc on traite ce cas séparément.
        if( nbJours == 0 )
            return 0;
        
        return (float) qConso / (float) nbJours;
    }
    
    /**
     * Calcule le nombre de jours restants avant une rupture de stock.
     * @return Le nombre de jours, ou 0 s'il n'y a aucun stock.
     */
    public float joursAvantRupture(){
        return (float) getQuantiteEnStock() / (float) getConsommationJournalière();
    }
    
    /**
     * Calcule la date de rupture de stock.
     * 
     * @return La date calculée.
     */
    public Date getDateDeRupture(){
        if( getConsommationJournalière() == 0 )
            return null;
        
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, (int) joursAvantRupture());
        return calendar.getTime();
    }
}
