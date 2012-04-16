package carp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import seisco.probleme.Probleme;
import seisco.probleme.Solution;
import seisco.util.Condition;
import seisco.util.Propriete;
import seisco.util.graphe.*;

/**
 * <p>Contient le problème à traiter. Chaque agent de calcul en possède un.
 *
 * @author Kamel Belkhelladi
 * @author Frank Réveillère
 * @author Bruno Boi
 * @author Jérôme Scohy
 * @version 2012
 * @see Probleme
 */
public class ProblemeCARP extends Probleme {

    public static final String NOM_CONDITION_NB_SOMMETS = "NB_SOMMETS";
    public static final String NOM_CONDITION_NB_ARRETE_NOREQ = "NB_ARRETE_NOREQ";
    public static final String NOM_CONDITION_CAPACITE_CAMION = "CAPACITE";
    public static final String NOM_CONDITION_NUM_DEPOT = "NUM_DEPOT";
    private static GrapheCARP graphe;
    private float[][] distancier;
    private int depot = 1;
    private float l_cost = 0;
    private long timeBoucles = 0;
    private long timeCalculDist = 0;

    /**
     * <p>Instancie un nouveau {@link ProblemeCARP}
     *
     * @since 2012
     * @see Probleme
     */
    public ProblemeCARP() {
        super("CARP");
    }

    /**
     * <p>Retourne le {@link Graphe} représentant le {@link ProblemeCARP}
     *
     * @return le {@link GrapheCARP} représentant le {@link ProblemeCARP}
     * @since 2012
     * @see GrapheCARP
     * @see Graphe
     */
    public static GrapheCARP getGraphe() {
        return graphe;
    }

    /**
     * <p>Permet d'évaluer la {@link Solution}
     *
     * @param s Une {@link Solution} au problème
     * @return L'évaluation de <b>s</b> sous forme de {@link Float}.
     * @since 2012
     * @see ProblemeCARP#split(carp.SolutionCARP)
     * @see SolutionCARP
     * @see Solution
     */
    @Override
    public float fonctionObjectif(Solution s) {
        if (s != null && s instanceof SolutionCARP) {
            split((SolutionCARP) s);
            return l_cost;
        }

        return 0;
    }

    /**
     * <p>
     * Retourne <b>true</b> ou <b>false</b> selon que
     * la {@link Solution} est réalisable ou non.
     * </p>
     *
     * @param s une {@link Solution} au {@link ProblemeCARP}
     * @return
     *  <p><b>true</b> si la {@link Solution} est réalisable.
     *  <p><b>false</b> sinon.
     * @since 2008
     * @see SolutionCARP
     * @see Solution
     */
    @Override
    public boolean estRealisable(Solution s) {
        return true; // what?
    }

    /**
     * <p>Partitionne une tournée géante en un ensemble de tournées faisables.
     *
     * @param sol une {@link SolutionCARP} contenant une tournée géante.
     * @since 2008
     * @see SolutionCARP
     * @see ProblemeCARP#fonctionObjectif(seisco.probleme.Solution)
     */
    public void split(SolutionCARP sol) {

        if (conditionPresente(NOM_CONDITION_CAPACITE_CAMION)) {
            List<Arc> tachesSol = sol.getTaches();
            int nbTaches = tachesSol.size();

            /*
             * WHY+1 Pour comprendre pourquoi on ajoute une ligne et une colonne
             * dans la matrice tourneeViable (+1) je vous invite à lire le
             * commentaire contenant le mot-clé WHY+1 dans la classe FitnessCARP
             */
            float tourneeViable[][] = new float[nbTaches + 1][nbTaches + 1];

            Arc currentTask, nextTask;
            Noeud currentDepart, currentArrivee;
            Noeud nextDepart, nextArrivee;

            float longueur;
            float capacite;
            float capacitecamion = (Integer) getCondition(NOM_CONDITION_CAPACITE_CAMION).getValeur();

            //Initialisation de la matrice tourneeViable
            for (int i = 0; i <= nbTaches; i++)
                Arrays.fill(tourneeViable[i], Float.POSITIVE_INFINITY);

            /*
             * Création d'un graphe sous forme d'une matrice identifiant les
             * arcs existants. Les arcs existants représentent les taches
             * successives pouvant être collectées sans violer la contrainte de
             * capacité du camion
             */
            Date dateDebut = new Date();
            for (int i = 0; i < nbTaches; i++)
                for (int j = i + 1; j <= nbTaches; j++) {
                    // On boucle ici pour remplir la matrice d'une maniere triangulaire

                    // Recherche des tournees ne depassant pas la capacite des camions
                    capacite = 0;
                    for (int k = i; k < j; k++) {
                        Propriete demande = tachesSol.get(k).getPropriete(GrapheCARP.NOM_PROPRIETE_DEMANDE);
                        if (demande != null)
                            //capacite += demande.getValeur().floatValue();
                            capacite += demande.getValeur();
                    }

                    if (capacite <= capacitecamion) {

                        currentTask = tachesSol.get(i);
                        currentDepart = currentTask.getDepart();
                        currentArrivee = currentTask.getArrivee();

                        // Distance du dépôt au sommet de départ courant
                        longueur = distancier[depot - 1][currentDepart.getNumero() - 1];

//						System.out.println("———————————————————————————————————————————————————————————————————————————————————————————————");
//						System.out.println("Distance du dépôt au sommet de départ courant (" + currentDepart.getNumero() + ") : " + longueur);

                        // Distance pour parcourir la tache courante
                        if (currentTask.coutPresent(Arc.NOM_COUT_PARCOURS))
                            longueur += currentTask.getCout(Arc.NOM_COUT_PARCOURS).getValeur();

//						System.out.println("\tCout de parcours : " + currentTask.getCout(Arc.NOM_COUT_PARCOURS).getValeur());

                        // Distance pour atteindre la tache suivante + distance de la tache parcourue
                        for (int k = i + 1; k < j; k++) {

                            nextTask = tachesSol.get(k);
                            nextDepart = nextTask.getDepart();
                            nextArrivee = nextTask.getArrivee();

                            // Distance pour rejoindre la tache suivante
                            longueur += distancier[currentArrivee.getNumero() - 1][nextDepart.getNumero() - 1];

//							System.out.println("Distance de (" + currentArrivee.getNumero() + ") à (" + nextDepart.getNumero() + ") : " + distancier[currentArrivee.getNumero() - 1][nextDepart.getNumero() - 1]);

                            // Distance de la tache suivante parcourue
                            if (nextTask.coutPresent(Arc.NOM_COUT_PARCOURS))
                                longueur += nextTask.getCout(Arc.NOM_COUT_PARCOURS).getValeur();

//							System.out.println("\tCout de parcours : " + nextTask.getCout(Arc.NOM_COUT_PARCOURS).getValeur());

                            /*
                             * Le noeud d'arrivee de la tache suivante devient
                             * celui de la tache courante La tache suivante
                             * devient la courante
                             */
                            currentArrivee = nextArrivee;
                            //currentDepart = nextDepart;
                            //currentTask = nextTask;
                        }

                        // Distance pour rejoindre le depot
                        longueur += distancier[currentArrivee.getNumero() - 1][depot - 1];

//						System.out.println("Distance de (" + currentArrivee.getNumero() + ") jusqu'au depot : " + distancier[currentArrivee.getNumero() - 1][depot - 1]);
//						System.out.println("\t\tTOTAL=" + longueur);

                        tourneeViable[i][j] = longueur;
                    }
                }

            Date dateFin = new Date();
            timeBoucles += dateFin.getTime() - dateDebut.getTime();

            //BOI
			/*
             * Afficher tourneeViable
             */
            /*
            System.out.print(
                    "Tournee Viable\n"
                    + "——————————————\n"
                    + "\t");
            for (int i = 0; i < tourneeViable.length; i++)
                System.out.print(i + "\t");
            System.out.print("\n");
            for (int i = 0; i < tourneeViable.length; i++) {
                System.out.print(i + "\t");
                for (int j = 0; j < tourneeViable.length; j++) {
                    float t = tourneeViable[i][j];
                    if (t != Float.POSITIVE_INFINITY)
                        System.out.print(t + "\t");
                    else
                        System.out.print(".\t");
                }
                System.out.print("\n");
            }
            //*/

            // Calcul du chemin le plus court dans le graphe tourneeViable
            dateDebut = new Date();
            l_cost = FitnessCARP.evaluer(tourneeViable, sol);
            dateFin = new Date();
            timeCalculDist += dateFin.getTime() - dateDebut.getTime();

        } else
            System.out.println("Erreur - la capacité du camion est inaccessible");

    }

    /**
     * <p>Retourne le numéro du dépôt
     * 
     * @return le numéro du dépôt
     * @since 2008
     * @see #setDepot(int) 
     */
    public int getDepot() {
        return depot;
    }

    /**
     * <p>Remplace le numéro du dépôt
     * 
     * @param depot
     *      le nouveau numéro du dépôt
     * @since 2008
     * @see #getDepot() 
     */
    public void setDepot(int depot) {
        this.depot = depot;
    }

    /**
     * <p>
     * Retourne le temps passé à découper
     * les {@link Solution} en tournées viables.
     * </p>
     * 
     * @return
     *      le temps passé à découper les <b>Solution</b> en
     *      tournées viables, sous la forme d'un long.
     * @since 2008
     */
    public long getTimeBoucles() {
        return timeBoucles;
    }

    /**
     * <p>Retourne le temps passé à évaluer la qualité des {@link Solution}.
     * 
     * @return
     *      le temps passé à évaluer la qualité des
     *      {@link Solution}, sous la forme d'un long.
     * @since 2008
     */
    public long getTimeCalculDist() {
        return timeCalculDist;
    }

    /**
     * <p>
     * Retourne la représentation en {@link String} du {@link ProblemeCARP}.
     * Son nom, ses {@link Condition} et son {@link GrapheCARP} y compris.
     * </p>
     * 
     * @return la représentation en {@link String} du {@link ProblemeCARP}.
     * @since 2012
     * @see Probleme#toString() 
     * @see GrapheCARP#toString()
     */
    @Override
    public String toString() {
        String resultat = super.toString();

        resultat += "Graphe associé :\n";
        resultat += graphe.toString() + "\n";

        return resultat;
    }

    /**
     * <p>
     * Initialise la matrice de {@link Float} <code>distancier</code>
     * à partir du {@link GrapheCARP}. Ce dernier doit
     * être correctement initialisé.
     * </p>
     * 
     * @since 2012
     * @see Dijkstra#initialiserDistancier(seisco.util.graphe.Graphe) 
     */
    public void initialiserDistancier() {
//		dijkstra = new DijkstraOrientedOnly(graphe);
//		dijkstra.executer(graphe.getNoeuds().get(depot - 1));
//		distancier = dijkstra.getDistancier();

        Noeud dep = null;
        for (Noeud n : graphe.getNoeuds())
            if (n.getNumero() == depot) {
                dep = n;
                break;
            }

        distancier = Dijkstra.initialiserDistancier(graphe);

        //BOI
			/*
         * Afficher distancier
         */
        /*
        System.out.print(
                "Distancier Dijkstra\n"
                + "———————————————————\n"
                + "\t");
        for (int i = 0; i < distancier.length; i++)
            System.out.print(i + "\t");
        System.out.print("\n");
        for (int i = 0; i < distancier.length; i++) {
            System.out.print(i + "\t");
            for (int j = 0; j < distancier.length; j++) {
                float t = distancier[i][j];
                if (t != Float.MAX_VALUE)
                    System.out.print(t + "\t");
                else
                    System.out.print(".\t");
            }
            System.out.print("\n");
        }
        //*/

//		if(conditionPresente(NOM_CONDITION_NB_SOMMETS)) {
//			
//			Arc[] tachesTab = new Arc[taches.size()];
//				tachesTab = taches.toArray(tachesTab);
//				
//			OldDijkstra distancier = new OldDijkstra((Integer)getCondition(NOM_CONDITION_NB_SOMMETS).getValeur());
//			distancier.make(tachesTab);
//			distancier = distancier.get();
//			
//		}
    }

    /**
     * <p>Charge le {@link ProblemeCARP} à partir d'un fichier
     * 
     * <p><b>1. THE FILE FORMAT</b>
     * <p>
     * <p>Basically, each file consists of two parts: a specification part and a 
     * data part. The specification part contains information on the file format 
     * and on its contents. The data part contains explicit data.
     * <p>
     * <p><b>1.1. The specification part</b>
     * <p>
     * <p>All entries in this section consist of lines of the form
     * <p>
     * <p>-----------------------------------------------
     * <p>{keyword} : {value}
     * <p>-----------------------------------------------
     * <p>
     * <p>where {keyword} denotes an alphanumerical keyword and {value} denotes 
     * alphanumerical or numerical data. The terms {string}, {integer} and {real}
     * denote character string, integer or real data, respectively. Integer and
     * real numbers are given in free format. All the keywords are in french language.
     * <p>Below we give a list of all available keywords.
     * <p>
     * <p>-----------------------------------------------
     * <p>PROBLEME : {string}
     * <p>-----------------------------------------------
     * <p>
     * <p>Used as an identification of the data file (name of the instance).
     * <p>
     * <p>---------------------------------------------------
     * <p>NB_COND : {integer}
     * <p>---------------------------------------------------
     * <p>
     * <p>The number of conditions.
     * <p>
     * <p>-----------------------------------------------
     * <p>NB_CAMIONS : {integer}
     * <p>-----------------------------------------------
     * <p>
     * <p>Specifies the number of vehicles available.
     * <p>
     * <p>-----------------------------------------------
     * <p>CAPACITE : {integer}
     * <p>-----------------------------------------------
     * <p>
     * <p>Specifies the capacity of a truck.
     * <p>
     * <p>-----------------------------------------------
     * <p>NB_ARRETE_NOREQ : {integer}
     * <p>-----------------------------------------------
     * <p>
     * <p>Specifies the number of edges with zero demand (non required edges).
     * <p>
     * <p>-----------------------------------------------
     * <p>NUM_DEPOT : {integer}
     * <p>-----------------------------------------------
     * <p>
     * <p>Specifies the number of the starting node.
     * <p>
     * <p>-----------------------------------------------
     * <p>NB_SOMMETS : {integer}
     * <p>-----------------------------------------------
     * <p>
     * <p>Specifies the number of nodes.
     * <p>
     * <p>-----------------------------------------------
     * <p>NB_TACHES : {integer}
     * <p>-----------------------------------------------
     * <p>
     * <p>Specifies the number of edges with positive demand (required edges).
     * <p>
     * <p><b>1.2. The data part</b>
     * <p>
     * <p>The data are given in the corresponding
     * data sections which follow the specification part. Each data section is
     * started with a corresponding keyword. The length of the section is either 
     * implicitly known from the format specification or the section is 
     * terminated by special end-of-section terminators.
     * <p>-----------------------------------------------
     * <p>DEPART   ARRIVEE     COUT    DEMANDE 
     * <p>-----------------------------------------------
     * <p>
     * <p>Edges with positive demand are given in this section.
     * <p>Each line is of the form:
     * <p>
     * <p>{integer}     {integer}       {integer}       {integer}
     * <p>
     * <p>The first pair of integers give the node indices of the endpoints
     * of the edge. The other two integers specify the
     * traversing cost and the demand, respectively.
     * 
     * @param nomFichier
     *      le fichier de configuration du problème
     * @return
     *      une instance de {@link ProblemeCARP} correctement initialisée.
     * @throws IOException quand le nom de fichier n'est pas valide
     * @since 2012
     */
    public static ProblemeCARP loadFromFile(String nomFichier) throws IOException {
        if (nomFichier.isEmpty())
            throw new IOException("Chemin du fichier non specifie");

        BufferedReader lecteur;
        StringTokenizer st;
        String ligne, token, token2;
        ProblemeCARP prob = new ProblemeCARP();

        int nbConditions = 0, nbTaches = 0, nbNoeuds = 0;
        List<Noeud> noeuds = new ArrayList<Noeud>();
        List<Arc> taches = new ArrayList<Arc>();

        // Remise à 1 du compteur des noeuds
        Noeud.init();
        
        lecteur = new BufferedReader(new FileReader(nomFichier));

        // Premiere ligne = nom
        ligne = lecteur.readLine();
        if (ligne != null) {
            st = new StringTokenizer(ligne);
            st.nextToken(" :");
            token = st.nextToken(" :");
            prob.setNom(token);
        }

        // Nombre de conditions
        ligne = lecteur.readLine();
        if (ligne != null) {
            st = new StringTokenizer(ligne);
            st.nextToken(":");
            token = st.nextToken(" :");

            nbConditions = Integer.decode(token);
        }

        // Conditions
        for (int i = 0; i < nbConditions; i++) {
            ligne = lecteur.readLine();
            if (ligne != null) {
                st = new StringTokenizer(ligne);
                token = st.nextToken(" :");
                token2 = st.nextToken(" :");
                prob.getConditions().add(new Condition<Integer>(token, Integer.decode(token2)));
            }
        }
        
        // Nombre de noeuds
        ligne = lecteur.readLine();
        if (ligne != null) {
            st = new StringTokenizer(ligne);
            st.nextToken(":");
            token = st.nextToken(" :");
            nbNoeuds = Integer.decode(token);
            prob.getConditions().add(new Condition<Integer>(ProblemeCARP.NOM_CONDITION_NB_SOMMETS, nbNoeuds));
        }

        // Noeuds
        for (int i = 0; i < nbNoeuds; i++)
            noeuds.add(new Noeud());

        // Liste des taches et nombre de taches
        ligne = lecteur.readLine();
        if (ligne != null) {
            st = new StringTokenizer(ligne);
            st.nextToken(":");
            token = st.nextToken(" :");

            nbTaches = Integer.decode(token);
        }

        // Labels (Noeud départ, Noeau arrivée, ...)
        lecteur.readLine();

        // Taches
        for (int i = 0; i < nbTaches; i++) {
            ligne = lecteur.readLine();
            if (ligne != null) {
                st = new StringTokenizer(ligne);
                // Noeud de départ
                token = st.nextToken();
                int noeudDepart = Integer.decode(token);
                // Noeud d'arrivée
                token = st.nextToken(" ");
                int noeudArrive = Integer.decode(token);
                // Cout
                token = st.nextToken(" ");
                float cout = Float.parseFloat(token);
                // Demande
                token = st.nextToken(" ");
                float demande = Float.parseFloat(token);
                if ((noeudDepart <= nbNoeuds) && (noeudArrive <= nbNoeuds)) {
                    Noeud n1 = noeuds.get(noeudDepart - 1);
                    Noeud n2 = noeuds.get(noeudArrive - 1);
                    
                    //TEST BBOI: INVERSE ----- DEBUT MODIF
                    /*
                    Arc t = new Arc(n1, n2);
                    t.ajouterCout(new Cout(Arc.NOM_COUT_PARCOURS, cout));
                    t.ajouterPropriete(new Propriete(GrapheCARP.NOM_PROPRIETE_DEMANDE, demande));
                    taches.add(t);

                    Arc t2 = new Arc(n2, n1, false);
                    t2.ajouterCout(new Cout(Arc.NOM_COUT_PARCOURS, cout));
                    //t2.ajouterPropriete(new Propriete(GrapheCARP.NOM_PROPRIETE_DEMANDE, demande));

                    // Ajout d'adjacence au noeud
                    noeuds.get(noeudDepart - 1).addAdjacent(t);
                    noeuds.get(noeudArrive - 1).addAdjacent(t2);
                    //*/
                    //*
                    Arc t = new Arc(n2, n1);
                    t.ajouterCout(new Cout(Arc.NOM_COUT_PARCOURS, cout));
                    t.ajouterPropriete(new Propriete(GrapheCARP.NOM_PROPRIETE_DEMANDE, demande));
                    taches.add(t);
                    
                    // Ajout d'adjacence au noeud
                    noeuds.get(noeudArrive - 1).addAdjacent(t);
                    noeuds.get(noeudDepart - 1).addAdjacent(t.clone().swap());
                    //*/
                    //TEST BBOI: INVERSE ----- FIN MMODIF
                }
            }
        }

        // Fermeture du fichier
        lecteur.close();

        // Initialisation
        graphe = new GrapheCARP(noeuds, taches);
        prob.initialiserDistancier();

        return prob;
    }
}
