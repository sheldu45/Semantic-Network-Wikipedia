package Wiki;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

import OLL.Linkable;
import OLL.LinkedElement;
import OLL.OrderedLinkedList;
import OLL.SemiOrderedHashMap;

/**
 * 
 * Abstraction correspondant aux "Entitées" wikidata.
 * 
 * Chaque entitée wikidata est l'ensemble des articles intertraductibles. La structure de Graphe implique des connexions entre les entitées. La pondération de ces connexions correspond au nombre de liens rencontrées entre ces deux entitées dans les differentes langues.
 * 
 * @author Shervin Le Du
 *
 */
public class Entity extends OLL.ID{

	static	int			AVERAGE_NUMBER_OF_LINKED_ENTITIES 	= 4;	//afin d'instancier une hashMap de taille raisonnable (provisoirement, arbitrairement 1)
	static	int			MAXIMUM_VALUE_ACCEPTED 	= 10;				//afin d'instancier une hashMap de taille realiste
	static 	Crypter 	crypt 			= new Crypter();
		
	private Integer							ID;					//for csv printing
	String[] 								titles;				//si l'entite possede une page dans le language de requete, l'url cryptée est le titre de l'entite
	public 	SemiOrderedHashMap<Entity>		linkedEntities;		//chaque lien est valué
	public	String							hyperonome;
	public  byte							completedLanguages; //nombre de langues dans lesquels les liens ont été comptabilisés (lorsqu'égal à weight, on print l'Entity et on supprime les informations spécifiques à cette entitée)
			byte 							weight;				//nombre de langues associées à l'entité
		
	//------------------------CONSTRUCTEUR-----------------------------
	/**
	 * Construit une entitée identifiée par un numéro (conventionnellement, celui attribué par wikidata)
	 * @param ID
	 */
	public 	Entity(Integer ID){
		this.weight 			= 0;
		this.ID 				= ID;
		titles 					= new String[Parser.querryLang.length];
		this.hyperonome			= "";
	}
	
	public void setWeigthedHashMap(byte weight, int estimatedNumberOfLinks) {
		int numberOfLangs = weight == 0 ? 1 : weight;
		this.weight = weight;
		int secondEstimatedNumberOfLinks = estimatedNumberOfLinks == 0 ? 1 : estimatedNumberOfLinks;
		//System.out.println("numberOfLangs : "+numberOfLangs);
		//System.out.println("estimatedNumberOfLinks : "+secondEstimatedNumberOfLinks);
		int optimalValue = (int) Math.ceil((numberOfLangs) * secondEstimatedNumberOfLinks);
		int value = optimalValue <= MAXIMUM_VALUE_ACCEPTED ? optimalValue : MAXIMUM_VALUE_ACCEPTED; //On peut avoir des entitées instanciées avec moins de 4 case pour celles qui on très peu de liens
		this.linkedEntities 	= new SemiOrderedHashMap<Entity>(value);
	}
	
	//---------------------ACCESSEURS & MUTATEURS----------------------
	
	/**
	 * 
	 * @return l'identifiant de l'entitée
	 */
	public 	Integer getID(){
		return this.ID;
	}
	
	/**
	 * Au cas où plusieurs langues de requêtes du graphe seraient utilisés, renvoie le titre de l'entitée dans la première langue de requête où cette entitée existe. Si ce n'est pas la 1ere langue de requete elle renvoie entre "<>" avec precision de la lange
	 * @return le titre de l'entitée dans la(/une des) langues de requête
	 */
	public 	String getTitle(){
		if(titles == null) {
			return null;
		}
		else {
			for(int i = 0; i < titles.length; i++){
				if(titles[i]!=null) {
					return titles[i];
				}
			}
			return null;
		}
	}
	
	
	static boolean doubleLink;
	
	/**
	 * ON PART DU PRINCIPE QUE LA NON-NULLITE DE ELEM EST TESTE A UN NIVEAU SUPERIEUR
	 * @param lg
	 * @param elem
	 
	public 	void linkTo(LANG lg, Entity elem, double increment){
		linkTo(lg, this, elem, increment);
		if (doubleLink) linkTo(lg, elem, this, increment);// avec : 12.20.02
	}*/
	
	
	//ON INTERDIT DEUX LIENS RELIANT 2 FOIS LES MEMES ENTITEES DANS LA MEME LANGUE
	/**
	 * 
	 * @param lg
	 * @param elem1
	 * @param elem2
	 * @param increment
	 */
	void linkTo(LANG lg, Entity cible, double increment){
		if(!cible.equals(this)) {	//on ne veut pas d'entité pointant vers elle-mêmes
			Link eventuelNouveauLink 				= new Link((byte) 1, cible);
			LinkedElement<Entity> linkToEntity 		= this.linkedEntities.getOrAdd(cible, eventuelNouveauLink);
			boolean allreadyLinked					= linkToEntity != null;
			if(!allreadyLinked) { 	//la liaison n'existait pas encore
				eventuelNouveauLink.langages.add(lg);
			}
			else { 					//déjà lié : on incremente la ponderation en verifaint qu'il n'existe pas deja dans cette langues
				Link lk = (Link) linkToEntity;
				boolean allreadyLinkedInSameLang 	= lk.langages.contains(lg);
				if(!allreadyLinkedInSameLang) { //n'avait jamais été rencontré dans cette langue
					lk.value++;
					lk.langages.add(lg);
				}
			}
		}
		/*
		boolean allreadyLinkedInSameLang = false;
		if(allreadyLinked && elem1.linkedEntities.get(elem2).langages.contains(lg)){
			allreadyLinkedInSameLang = true;
		}
		if(!allreadyLinkedInSameLang){
			Link lp ;
			if(allreadyLinked){
				lp 	= elem1.linkedEntities.remove(elem2);
			}
			else{
				lp = new Link((byte) 0, elem2);
			}
			lp.langages.add(lg);
			lp.value+=increment;
			elem1.linkedEntities.put(elem2, lp);
		}*/
	}
	
	public 	int getWeight(){
		return this.weight;
	}
	/*
	public 	void setWeight(byte weight){
		this.weight = weight;
	}*/
	
	//----------------------PRINT-----------------------

	@Override
	public 	String toString(){
		return this.getID().toString();
	}
	
	static int i = 0;

	/**
	 * JE CONSTATE QU'IL EXISTE DES EXCEPTIONS JE FAIS LE CHOIX DE LES IGNORER (en les levant et en les sautant pour le moment)
	 *
	 */
	public void printRelationFile(PrintWriter out){
		Integer numberOfException = 0;
		i++;
		//System.out.println("supression numero : "+i+"\tID : "+ID+"\tTitleUnderWichIAmBeingDelated : "+title+"\n\r");
		for(OrderedLinkedList<Entity> list : this.linkedEntities) {
			Linkable lp = list.head;
			while(lp != null) {
				Link lk = (Link) lp; 
				try {
					String langTab = "[";
					int i = 0; //afin qu'il y'ait le bon nombre de virgules
					for (LANG lg : lk.langages) {
						if (i != 0)
							langTab += ",";
						langTab += lg.toString();
						i++;
					}
					langTab += "]";
					out.print(this.getID().toString() + " " + lk.value + " " + langTab + " " + lk.elem.getID().toString() +"\r\n");
					lk.langages = null;
					lk = null;
					Linkable tempLp = lp.next();
					lp = null;
					lp = tempLp;
				}
				catch(java.lang.NullPointerException exc){
					try {
						//l("exception while printing entity n°: "+lk.elem.ID + " entitled : "+lk.elem.getTitle());
						lk.langages = null;
						lk = null;
						Linkable tempLp = lp.next();
						lp = null;
						lp = tempLp;
					}
					catch(Exception k ) {
						Linkable tempLp = lp.next();
						lp = null;
						lp = tempLp;
					}
					numberOfException++;
					continue;
				}
			}
			list = null;
		}
		//System.out.println("numberOfException while printing entity : " +numberOfException);
	}
	
	public void printNodeFile(Parser p, PrintWriter out) {
		String title = this.getTitle(); //retrieves top-title for printing node in file, than deletes all titles(?)
		//this.titles = null;
		if (title == null) {
			title = "<untitled>";
		}
		String print = "";
		print = this.getID().toString() + " " + title + " " + this.getWeight() + " "+this.hyperonome;
		print = print.replaceAll("\"", "\\\\\"");
		out.print(print + "\r\n");
		p.toBePrintedNodes.remove(this.getID());
	}
	
	public void printRedirect(LANG lang, Integer lexiconId, PrintWriter out){
		out.println(lexiconId+" "+lang.toString() +" "+this.getID());
	}
	
	public void printLexicon(Integer lexiconId, String s, PrintWriter out){
		out.println(lexiconId +" "+s);
	}

	public int compareTo(Entity o) {
		return this.getID().compareTo(o.getID());
	}
	
}
	
