package carp;

import java.util.Arrays;
import java.util.List;
import seisco.util.graphe.Arc;

/**
 * <p>Sert à évaluer une <b>SolutionCARP</b>.
 * 
 * @author Bruno Boi
 * @version 2012
 * @see SolutionCARP
 */
public class FitnessCARP {

	/**
	 * <p>
	 * Retourne la fitness d'une {@link SolutionCARP} en se basant sur la
	 * solution et les différentes tournées viables de celle-ci.
	 * </p>
	 *
	 * @param tourneeViable
	 *		Matrice contenant le coût des différentes tournées viables de sol
	 * @param sol
	 *		La {@link SolutionCARP} à évaluer
	 * @return le coût total minimum de la découpe en tournées de sol
	 * @since 2012
	 * @see ProblemeCARP#split(carp.SolutionCARP) 
	 */
	public static float evaluer(float[][] tourneeViable, SolutionCARP sol) {
		/*
		 * ————————————————————————————————————————————————————————————————————————
		 * WHY+1 ? (Auteur de ce commentaire: Bruno BOI) DATE: 01/03/2012 Ce
		 * commentaire prend origine dans ProblemeCARP ( cherchez WHY+1 ) . La
		 * matrice tourneeViable contient une colonne et une ligne vide
		 * (respectivement la première et la dernière). Pourquoi donc? Car c'est
		 * plus simple pour construire le résultat de cet algorithme ! C'est
		 * principalement une question de coordonnées, que l'on stocke dans le
		 * tableau marqueur. En réalité, seule la colonne vide est nécessaire, mais
		 * par un soucis de symétrie, je préfère les matrices carrées… Pour infos,
		 * cette méthode était à l'origine un code traduit du C vers ce langage de
		 * manière brute. J'ai gagné pas mal de cheveux gris à l'analyser et à la
		 * comprendre, c'était vraiment l'enfer ! (	Le code est commenté en pied de
		 * ce document, comme exemple à NE PAS suivre …	) J'ai tenté d'en reproduire
		 * une version plus simple à comprendre, ce qui est sans doute le cas. Si,
		 * par hasard, vous désirez la comprendre alors faites la tourner À LA MAIN
		 * ! Ça vous évitera du temps perdu. Il n'y a pas de gros efforts sans
		 * récompenses… J'ai néanmoins tenté du mieux que j'ai pu de commenter ce
		 * code, pour permettre aux successeurs (vous?) de mieux s'y retrouver.
		 * Faites de même pour les suivants, et bon courage !
		 * ————————————————————————————————————————————————————————————————————————
		 */
		List<Arc> taches = sol.getTaches();
		int nbTaches = taches.size();

		/*
		 * Pour construire le résultat, nous avons besoin de deux tableaux que
		 * nous appellerons "marques" : tabTemp Contiendra progressivement les
		 * couts minimums des tournees viables et parfois la somme de celles-ci
		 * si on peut les faire se succéder marqueur Contiendra les indices des
		 * taches à récupérer dans tourneeViable Ce tableau se construit
		 * parallèlement à tabTemp
		 *
		 * Pour récupérer le résultat dans tourneeViable, le tableau choix sera
		 * construit en combinant tabTemp et marqueur. choix contiendra les
		 * indices des tournées sélectionnées dans la matrice tourneeViable
		 */
		float[] tabTemp = new float[nbTaches + 1];
		int[] marqueur = new int[nbTaches + 1];
		int[] choix = new int[nbTaches + 1];

		/*
		 * Initialisation des marques En gros, copie de la première ligne de
		 * tournéeViable.
		 */
		Arrays.fill(marqueur, Integer.MIN_VALUE);
		for (int i = 0; i <= nbTaches; i++) {
			tabTemp[i] = tourneeViable[0][i];
			if (tabTemp[i] != Float.POSITIVE_INFINITY)
				marqueur[i] = 0;
		}

		/*
		 * Parcours de la matrice tourneeViable pour construire les tableaux
		 * tabTemp et marqueur
		 */
		int nbVus = 0; // nombre de tournees analysees (max nbTaches)
		while (nbVus < nbTaches) {
			int iMin = getIndexOfNextMin(tabTemp, nbVus);
			if (iMin == nbTaches)
				// On a tout analysé alors…
				nbVus = nbTaches;
			else {
				nbVus++;
				for (int i = nbVus + 1; i <= nbTaches; i++)
					// On recherche un coût minimal…
					if (tourneeViable[iMin][i] != Float.POSITIVE_INFINITY)
						if (tabTemp[i] > tabTemp[iMin] + tourneeViable[iMin][i])
						{
							tabTemp[i] = tabTemp[iMin] + tourneeViable[iMin][i];
							marqueur[i] = iMin;
						}
			}
		}

		/*
		 * Combinaison de tabTemp et marqueur pour construire le choix optimal
		 * des tournées (il sera trié par ordre décroissant)
		 */
		int x = nbTaches;
		Arrays.fill(choix, Integer.MIN_VALUE);
		choix[0] = x;
		for (int i = 1; i <= nbTaches; i++)
			if (x != Integer.MIN_VALUE) {
				choix[i] = marqueur[x];
				x = marqueur[x];
			}

		/*
		 * Récupération des tournées sélectionnées et calcul du coût total
		 */
		float dist = 0;
		int nbTrips = 0;
		ResultatCARP resultat = new ResultatCARP();
		for (int i = nbTaches; i > 0; i--)
			if (choix[i] != Integer.MIN_VALUE) {
				int a = choix[i]; // premier indice
				int b = choix[i - 1]; // second indice

				// cout total des tournees
				dist += tourneeViable[a][b];

				// remplissage du résultat
				resultat.nouvelleTournee();
				for (int j = 0; j < (b - a); j++)
					/*
					 * NB:	(b-a) = le nombre de taches à
					 * ———	effectuer pour la tournee courante
					 */
					resultat.getTournees().get(nbTrips).addTache(taches.get(a+j));

				// nombre de tournees
				nbTrips++;
			}
		
        resultat.setCoutTotal(dist);
		sol.setResultat(resultat);

		//System.out.println("DISTANCE: " + dist);
		//System.out.println("NBTRIPS: " + nbTrips);

		return dist;
	}

	/**
	 * <p>
	 * getIndexOfNextMin permet de choisir le minimum d'un tableau de
     * {@link Float} en évitant les toSkip premiers éléments.
	 * </p>
	 * 
	 * @param tab
	 *		tableau de {@link Float} initialisé
	 * @param toSkip
	 *		nombre des premiers éléments à ne pas prendre en compte dans tab
	 * @return
	 *		l'index de la valeur minimale de tab
	 *		dans l'intervalle [toSkip, tab.length[
	 * @since 2012
	 * @see FitnessCARP#evaluer(float[][], carp.SolutionCARP) 
	 */
	private static int getIndexOfNextMin(float[] tab, int toSkip) {
		/*
		 * iMin commence à toSkip+1 car les toSkip+1 premiers éléments ont déjà
		 * été examinés. Le +1 vient toujours du fait que la matrice
		 * tourneeViable est décalée.
		 */
		int iMin = toSkip + 1;

		/*
		 * Recherche de la valeur minimale dans le reste du tableau Extraction
		 * de son index
		 */
		if (iMin < tab.length - 1)
			for (int i = iMin + 1; i < tab.length; i++)
				if (tab[i] < tab[iMin])
					iMin = i;

		return iMin;
	}
}
//	/**
//	 * La mÃ©thode <code>choisir_min</code> permet de choisit le 
//	 * sommet non examinÃ© ayant la marque est la plus petite
//	 * @param nb_sommet
//	 * @param pi
//	 * @param est_examine
//	 * @return le somment non examinÃ© dont la marque est la plus petite
//	 */	
//	private int choisir_min(int nb_sommet,float pi [],int est_examine []){
//	    int min;
//	    int x=0;
//
//	    //Initialisation de min par la recherche d'un sommet non encore Ã©xaminÃ©
//	    while (est_examine[x]==1 && x<nb_sommet){
//	        x++;
//	    }
//
//	    //comparaison de min avec la marque de tous les autres sommets non examinÃ©s
//	    //s'il n'y a aucun sommet non examinÃ©, on arrÃªte les itÃ©ration dans dijkstra
//	    if (x==nb_sommet-1)
//	        return x;
//	    else{
//	    	min=x;
//	        for (int i=x+1 ;i<nb_sommet; i++)
//	            if(est_examine[i]==0 && pi[i]<pi[min])
//	                min=i;
//	        return min;
//	    }
//	}
//
//	/**
//	 * Cette fonction permet de calculer du chemin le plus court dans le graphe tourneeViable
//	 * @param probleme
//	 * @param tableau
//	 * @retur le cout du plus cour chemin 
//	 */
//	private float distancier_distanceTourneeViable(Ttableau tableau[][], Individu solution){
//	   
//		int x,y,cptex,tailleindividu=0,s=0,nb_sommet;
//		int source,cpt;
//		float dist;
//		int nb_trip;
//		int f;//variable de debug
//		
//		float pi[];
//		int est_examine[],marqueur[],le_chemin[];
//		
//		tailleindividu = (Integer)(getMesTaches().size());	
//				
//		pi=new float [tailleindividu+1];
//		est_examine=new int[tailleindividu+1];
//		marqueur=new int[tailleindividu+1];
//		le_chemin=new int[tailleindividu+1];
//		
//		nb_sommet=tailleindividu+1;
//	    
//		/*initialisation des tableau*/
//		for (x=0;x<nb_sommet;x++){
//			pi[x]=9999;
//			est_examine[x]=0;
//			marqueur[x]=-1;
//		}
//
//		/*initialisation des marques*/
//		pi[s]=0;
//		for (y=0;y<=nb_sommet-1;y++)
//			if ((tableau[s][y].getPresent())==true){
//		        pi[y]=tableau[s][y].getLongueur();
//		        marqueur[y]=s;
//		    }
//		    
//		est_examine[s]=1;
//		cptex=1;
//
//
//		/*
//		 * tant que le nombre de sommet non examinÃ© n'est pas Ã©gale au nombre
//		 * de sommets du graphe, on continue les itÃ©ration
//		 */
//		while (cptex<nb_sommet){
//			x=choisir_min(nb_sommet,pi,est_examine);
//			if (x==nb_sommet){
//				/*
//				 * si la fonction choisir_min renvoie nb_sommet+1 Ã§a veux dire qu'il n'existe
//				 * pas de sommet non examinÃ© et dans ce cas, on arrÃªte les itÃ©rations
//				 */
//				cptex=nb_sommet+1;
//			}
//			else{
//				cptex=cptex+1;
//				est_examine[x]=1;
//
//				for(y=0;y<nb_sommet;y++){
//					if (((tableau[x][y].getPresent())==true) && (est_examine[y]==0)){
//						if (pi[y]>pi[x]+tableau[x][y].getLongueur()){
//							pi[y]=pi[x]+tableau[x][y].getLongueur();
//							marqueur[y]=x; //on stocke dans marqueur le prÃ©decesseur de y
//						}
//					}
//				}
//			}
//		}
//
//		x=nb_sommet-1;
//			
//		//initialisation du tableau le_chemin
//		for (f=0;f<nb_sommet;f++){
//				le_chemin[f]=-1;
//		}
//       
//        //construction d'un tableau de la liste des predeceseurs de x pour le plus court chemin
//        source=x;
//        le_chemin[0]=x;
//        cpt=0;
//        
//        while ((source!=s)&&(cpt<nb_sommet)){
//            cpt=cpt+1;
//            le_chemin[cpt]=marqueur[source];
//            source=marqueur[source];
//        }
//        	
//        
//		//affichage du chemin dans l'ordre des sommets rencontrÃ©s entre s et x et calcul de la distance
//        //System.out.println("Le plus court chemin du sommet" + s +"->"+ x +" sur le graphe de la meilleure tournee geante\n");
//        dist=0;
//        nb_trip=0;
//        for (f=nb_sommet-1;f>0;f--){
//        	if (le_chemin[f]!=-1) {
//        		dist+= tableau[le_chemin[f]][le_chemin[f-1]].getLongueur();
//				//affichage de chaque tournÃ©e
//				//System.out.println("Tournee nÂ° " + (nb_trip + 1)+":");
//				/*	
//				for(int indice=le_chemin[f]; indice <= le_chemin[f-1]; indice++){
//					if(indice< solution.getMesTaches().size()){
//						System.out.println(solution.getMesTaches().elementAt(indice).getNoeudDepart().getNumero() + " ---> " + 
//								solution.getMesTaches().elementAt(indice).getNoeudArrivee().getNumero());
//					}
//					else
//						System.out.println(solution.getMesTaches().elementAt(indice-solution.getMesTaches().size()).getNoeudArrivee().getNumero() + " ---> " + 
//								solution.getMesTaches().elementAt(indice-solution.getMesTaches().size()).getNoeudDepart().getNumero());
//						
//				}
//				System.out.println("Le coÃ»t de parcours de cette tache est : " + dist);
//				*/
//				nb_trip++;
//        	}
//        }
//		  
//		 //System.out.println("La rÃ©solution de ce problÃ¨me a donnÃ© " + (nb_trip +1) +" avec un cout total =" + dist);
//
//		 return dist; 
//	}