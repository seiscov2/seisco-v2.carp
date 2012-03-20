package carp;

import java.util.List;
import seisco.util.graphe.Graphe;
import seisco.util.graphe.Noeud;
import seisco.util.graphe.Arc;

/**
 * <p>
 * Représente un {@link Graphe} pour les problèmes de type CARP.
 * Contient un coût de demande pour chaque {@link Arc}.
 * </p>
 * 
 * @author Bruno Boi
 * @version 2012
 * @see Graphe
 */
public class GrapheCARP extends Graphe {
	public final static String NOM_PROPRIETE_DEMANDE = "demande";
	
	/**
	 * <p>Crée une instance de {@link GrapheCARP}.
	 * 
	 * @param noeuds
	 *		Une liste de {@link Noeud} initialisée.
	 * @param arcs
	 *		Une liste d'{@link Arc} initialisée.
	 * @since 2012
	 * @see Noeud
	 * @see Arc
	 */
	public GrapheCARP(List<Noeud> noeuds, List<Arc> arcs) {
		super("CARP", noeuds, arcs);
	}
	
}
