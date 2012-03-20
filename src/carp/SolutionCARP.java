package carp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import seisco.probleme.Solution;
import seisco.util.graphe.Arc;

/**
 * <p>
 * Contient une solution au problème CARP,
 * qui n'est pas forcément la meilleure.
 * </p>
 * 
 * @author Kamel Belkhelladi
 * @author Frank Réveillère
 * @author Bruno Boi
 * @author Jérôme Scohy
 * @version 2012
 */
public abstract class SolutionCARP extends Solution implements Cloneable {

	protected List<Arc> taches;
	private ResultatCARP resultat;

    /**
     * <p>Instancie une {@link SolutionCARP}.
     * 
     * @since 2012
     * @see Solution#Solution() 
     */
	public SolutionCARP() {
		super();
		taches = new ArrayList<Arc>();
		resultat = new ResultatCARP();
	}

    /**
     * <p>Instancie une {@link SolutionCARP}.
     * 
     * @param nbTaches
     *      le nombre de tâches ({@link Arc}) que contiendra la solution
     * @since 2012
     * @see Solution#Solution()
     */
	public SolutionCARP(int nbTaches) {
		super();

		taches = new ArrayList<Arc>(nbTaches);
		setNbTaches(nbTaches);
        resultat = new ResultatCARP();
	}

    /**
     * <p>Instancie une {@link SolutionCARP}.
     * 
     * @param taches
     *      la liste des tâches que contiendra la solution
     * @since 2012
     * @see Solution#Solution()
     */
	public SolutionCARP(ArrayList<Arc> taches) {
		super();
        resultat = new ResultatCARP();
		this.taches = taches;
	}

    /**
     * <p>Retourne le résultat de la solution
     * 
     * @return
     *      le résultat de la solution sous la forme d'un {@link ResultatCARP}.
     * @since 2012
     * @see ResultatCARP
     * @see #setResultat(carp.ResultatCARP) 
     */
	public ResultatCARP getResultat() {
		return resultat;
	}

    /**
     * <p>Remplace le résultat de la solution
     * 
     * @param resultat
     *      le résultat de la solution sous la forme d'un {@link ResultatCARP}.
     * @since 2012
     * @see ResultatCARP
     * @see #getResultat() 
     */
	public void setResultat(ResultatCARP resultat) {
		this.resultat = resultat;
	}

    /**
     * <p>Retourne la liste des tâches de la solution
     * 
     * @return
     *  la {@link List} des {@link Arc} de la solution.
     * @since 2012
     * @see Arc
     * @see #setTaches(java.util.ArrayList) 
     */
	public List<Arc> getTaches() {
		return taches;
	}

    /**
     * <p>Remplace la liste des tâches de la solution.
     * 
     * @param taches
     *  la nouvelle {@link ArrayList} des {@link Arc} de la solution.
     * @since 2012
     * @see Arc
     * @see #getTaches() 
     */
	public void setTaches(ArrayList<Arc> taches) {
		this.taches = taches;
	}

    /**
     * <p>
     * Ajoute une nouvelle tâche à la suite
     * de celles déjà présentes dans la solution.
     * </p>
     * 
     * @param nouvelleTache
     *  la nouvelle tâche ({@link Arc}) à ajouter
     * @return 
     *  <p><b>true</b> si la tâche a correctement été ajoutée.
     *  <p><b>false</b> dans le cas contraire.
     * @since 2012
     * @see Arc
     * @see #retirerTache(seisco.util.graphe.Arc) 
     * @see #retirerTache(int) 
     */
	public boolean ajouterTache(Arc nouvelleTache) {
		if (!taches.contains(nouvelleTache))
			return taches.add(nouvelleTache);

		return false;
	}

    /**
     * <p>
     * Enlève la tâche passée en paramètre
     * de la liste des tâches de la solution.
     * </p>
     * 
     * @param t
     *  la tâche ({@link Arc}) à supprimer
     * @return 
     *  <p><b>true</b> si la tâche a correctement été enlevée.
     *  <p><b>false</b> dans le cas contraire.
     * @since 2012
     * @see Arc
     * @see #ajouterTache(seisco.util.graphe.Arc) 
     */
	public boolean retirerTache(Arc t) {
		return taches.remove(t);
	}

    /**
     * <p>
     * Enlève la tâche qui possède le numéro passé en
     * paramètre de la liste des tâches de la solution.
     * </p>
     * 
     * @param numeroTache 
     *  la numéro de la tâche ({@link Arc}) à supprimer
     * @return 
     *  <p><b>true</b> si la tâche a correctement été enlevée.
     *  <p><b>false</b> dans le cas contraire.
     * @since 2012
     * @see Arc
     * @see #ajouterTache(seisco.util.graphe.Arc) 
     */
	public boolean retirerTache(int numeroTache) {
		for (Arc t : taches)
			if (t.getNumero() == numeroTache)
				return taches.remove(t);

		return false;
	}

    /**
     * <p>
     * Ajoute des tâches nulles si nb est plus grand
     * que la taille de la liste de tâches présente.
     * Aucun changement donc si la taille passée est
     * plus petite que la taille actuelle.
     * </p>
     * 
     * @param nb 
     *  la taille qui doit être donnée à la liste des tâches.
     * @since 2012
     */
	public final void setNbTaches(int nb) {
		if (nb > taches.size())
			for (int i = taches.size() + 1; i <= nb; i++)
				taches.add(null);
	}

    /**
     * <p>Vérifie l'égalité entre deux {@link SolutionCARP}.
     * 
     * @param obj
     *  l'{@link Object} à comparer avec l'instance courante
     * @return
     *  <p><b>true</b> si l'égalité est vérifiée.
     *  <p><b>false</b> dans le cas contraire.
     * @since 2012
     * @see Solution#equals(java.lang.Object) 
     */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof SolutionCARP) {
			SolutionCARP s = (SolutionCARP) obj;

			if (taches.size() != s.taches.size())
				return false;

			for (int k = 0; k < taches.size(); k++)
				if (!taches.get(k).equals(s.taches.get(k)))
					return false;
                        
                        return true;
		} else 
                    return false;
	}

    /**
     * <p>
     * Retourne la représentation de la solution
     * sous la forme d'un {@link String}.
     * </p>
     * 
     * @return
     *  la liste des tâches de la solution sous la forme d'un {@link String}.
     * @since 2012
     * @see Arc#toString() 
     */
	@Override
	public String toString() {
		String resultat = "Liste des tâches :\n";
		for (Iterator<Arc> it = taches.iterator(); it.hasNext();) {
			Arc t = it.next();
			resultat += t.toString();
		}

		return resultat;
	}

    /**
     * <p>Retourne une copie neuve de la solution courante.
     * 
     * @return
     *  une copie neuve de la solution courante
     *  sous la forme d'une {@link SolutionCARP}.
     * @throws CloneNotSupportedException quand la copie ne peut avoir lieu
     * @since 2012
     * @see Collections#copy(java.util.List, java.util.List) 
     */
	public SolutionCARP copy() throws CloneNotSupportedException {
		SolutionCARP c = (SolutionCARP) super.clone();
		c.setNbTaches(this.taches.size());
		Collections.copy(c.taches, this.taches);

		return c;
	}
	
    /**
     * <p>Affiche en console le meilleur {@link ResultatCARP} de la solution.
     * 
     * @see ResultatCARP#toString() 
     */
	@Override
	public void afficher() {
		System.out.println(this.resultat.toString());
	}
}
