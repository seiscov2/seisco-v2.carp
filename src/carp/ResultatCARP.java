package carp;

import jade.content.Concept;
import java.util.ArrayList;
import java.util.List;
import seisco.probleme.Resultat;
import seisco.util.graphe.Arc;

/**
 * <p>Contient le résultat du problème. Peut servir à l'afficher en console.
 * 
 * @author Kamel Belkhelladi
 * @author Frank Réveillère
 * @author Bruno Boi
 * @author Jérôme Scohy
 * @version 2012
 */
public class ResultatCARP extends Resultat {

	private float coutTotal = 0;
	private List<Tournee> tournees;

    /**
     * <p>Instancie un nouveau {@link ResultatCARP}.
     * 
     * @since 2012
     * @see Resultat#Resultat() 
     */
	public ResultatCARP() {
		super();
        this.tournees = new ArrayList<Tournee>();
	}

    /**
     * <p>
     * Retourne le coût total de parcours pour
     * les {@link Tournee} que contient ce résultat.
     * </p>
     * 
     * @return
     *  le coût total de parcours pour les {@link Tournee} contenues dans ce résultat.
     * @since 2012
     * @see #setCoutTotal(float) 
     */
	public float getCoutTotal() {
		return coutTotal;
	}

    /**
     * <p>
     * Remplace le coût total de parcours pour
     * les {@link Tournee} que contient ce résultat.
     * </p>
     * 
     * @param coutTotal
     *  le coût total de parcours pour les {@link Tournee} contenues dans ce résultat.
     * @since 2012
     * @see #getCoutTotal() 
     */
	public void setCoutTotal(float coutTotal) {
		this.coutTotal = coutTotal;
	}

    /**
     * <p>Retourne les {@link Tournee} du résultat
     * 
     * @return les {@link Tournee} du résultat sous forme de {@link List}
     * @since 2012
     * @see Tournee
     * @see #setTournees(java.util.List) 
     */
	public List<Tournee> getTournees() {
		return tournees;
	}

    /**
     * <p>Remplace les {@link Tournee} du résultat
     * 
     * @param tournees
     *      les nouvelles {@link Tournee} du résultat
     * @since 2012
     * @see #getTournees() 
     */
	public void setTournees(List<Tournee> tournees) {
		this.tournees = tournees;
	}

    /**
     * <p>Ajoute une nouvelle {@link Tournee} vide à la {@link List} des {@link Tournee} du résultat.
     * @return
     *      <p><b>true</b> si l'ajout s'est bien déroulé.
     *      <p><b>false</b> dans le cas contraire.
     * @since 2012
     */
	public boolean nouvelleTournee() {
		return tournees.add(new Tournee());
	}

    /**
     * <p>Retourne sous forme de {@link String} le résultat.
     * 
     * @return sous forme de {@link String} le résultat.
     *      Y compris ses {@link Tournee}, leur nombre de tâches et leurs {@link Arc}.
     * @since 2012
     * @see Arc#toString() 
     */
	@Override
	public String toString() {
		String res = new String();

		for (int i = 1; i <= tournees.size(); i++) {
			List<Arc> taches = this.tournees.get(i - 1).getTaches();
			res += "\nTournée(" + i + ")\tNombre de tâches: " + taches.size() + "\n";
			res += "————————————————————————————————————————————————————————\n";
			for (Arc arc : taches)
				res += arc.toString() + "\n";
		}

		return res;
	}
}

/**
 * <p>
 * Représente la tournée d'un camion, avec la
 * liste des tâches à effectuer dans cet ordre.
 * </p>
 * 
 * @author Kamel Belkhelladi
 * @author Frank Réveillère
 * @author Bruno Boi
 * @author Jérôme Scohy
 * @version 2012
 */
class Tournee implements Concept {

	private float coutTournee = 0;
	private List<Arc> taches;

    /**
     * <p>Instancie une nouvelle {@link Tournee}
     * 
     * @since 2012
     */
    public Tournee() {
        super();
        this.taches = new ArrayList<Arc>();
    }
 
    /**
     * <p>Retourne le coût de parcours total de la {@link Tournee}.
     * 
     * @return le coût total de la {@link Tournee}, sous forme de {@link Float}.
     * @since 201
     * @see #setCoutTournee(float)
     */
	public float getCoutTournee() {
		return coutTournee;
	}

    /**
     * <p>Remplace le coût de parcours total de la {@link Tournee}.
     * 
     * @param coutTournee 
     *      le nouveau coût total de la {@link Tournee}.
     * @since 2012
     * @see #getCoutTournee() 
     */
	public void setCoutTournee(float coutTournee) {
		this.coutTournee = coutTournee;
	}

    /**
     * <p>Retourne la {@link List} des tâches ({@link Arc}) à effectuer dans cette {@link Tournee}.
     * 
     * @return la {@link List} des tâches ({@link Arc}) à effectuer dans cette {@link Tournee}.
     * @since 2012
     * @see #setTaches(java.util.List) 
     */
	public List<Arc> getTaches() {
		return taches;
	}

    /**
     * <p>Remplace la {@link List} des tâches ({@link Arc}) à effectuer dans cette {@link Tournee}.
     * 
     * @param taches 
     *      la nouvelle {@link List} des tâches ({@link Arc}) à effectuer dans cette {@link Tournee}.
     * @since 2012
     * @see #getTaches() 
     */
	public void setTaches(List<Arc> taches) {
		this.taches = taches;
	}

    /**
     * <p>Ajoute une nouvelle tâche ({@link Arc}) à la suite de la {@link Tournee}
     * 
     * @param t
     *      l'{@link Arc} de la tâche à ajouter
     * @return 
     *      <p><b>true</b> si la tâche ({@link Arc}) a correctement été ajoutée.
     *      <p><b>false</b> dans le cas contraire.
     * @since 2012
     */
	public boolean addTache(Arc t) {
		return taches.add(t);
	}
}