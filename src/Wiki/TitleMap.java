package Wiki;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Classe qui permet de reccuperer dans une langue donnée à partir du titre d'un article, l'entitée correspondante.
 * Elle est construite par appels successifs à la fonction addURL utilisé lors du parsing de wikidata.
 * 
 * @author Shervin Le Du
 *
 */

public class TitleMap {

	//afin d'instancier des hashmaps de taille suffisantes, on multiplie par un certain facteur
	//private static int PRECAUTION_FACTOR = 2;
	
	public Map<String, Entity>[] 		map;			
	//public Map<BigInteger, Entity> 		entitiesMap; 	//sert au parsing de wikipedia
	
	@SuppressWarnings("unchecked")
	public TitleMap(){
		this.map   = (HashMap<String, Entity>[]) new HashMap[LANG.values().length];
		int i = 0;
		for(LANG lg : LANG.values()){
			map[i] = new HashMap<>(lg.numberOfLinks, 100);
			i++;
		}
	}
	
	public Entity get(LANG lg, String url){
		int 	index = lg.getIndex();
		Entity 	retour = this.map[index].get(url);
		if(retour == null && url.length()>=16) {
			int 	quart = (int)(url.length()/4);
			String  premierQuart = url.substring(0, quart);
			String  dernierQuart = url.substring(url.length()-quart);
			String 	concat 	= premierQuart+"#"+dernierQuart;
			retour = this.map[index].get(concat);	
		}
		return retour;
	}
	
	public Entity remove(LANG lg, String url){
		int 	index = lg.getIndex();
		Entity retour = null;
		if((retour = this.map[index].remove(url)) == null && url.length()>=16) {
			int 	quart = (int)(url.length()/4);
			String  premierQuart = url.substring(0, quart);
			String  dernierQuart = url.substring(url.length()-quart);
			String 	concat 	= premierQuart+"#"+dernierQuart;
			retour = this.map[index].remove(concat);	
		}
		return retour;
	}
	
	private boolean contains(LANG lg, String url){
		return this.get(lg, url) == null ? false : true;
	}
	
	public void remove(LANG lg){
		int 	index = lg.getIndex();
		this.map[index] = null;
		System.gc();
	}

	public Map<String, Entity> get(LANG lg){
		int 	index = lg.getIndex();
		return 	this.map[index];
	}
	
	public void set(LANG lg, Map<String, Entity> map){
		int 	index = lg.getIndex();
		this.map[index] = map;
	}
	
	private void put(LANG lg, String url, Entity entity){
		int 	index = lg.getIndex();
		String 	realEntry = "";
		if(url.length() >= 16) {
			int 	quart = (int)(url.length()/4);
			String  premierQuart = url.substring(0, quart);
			String  dernierQuart = url.substring(url.length()-quart);
			String 	concat 	= premierQuart+"#"+dernierQuart;
			if(!this.contains(lg, concat)){
				realEntry = concat;
			}
			else {
				realEntry = url;
			}
		}
		else {
			realEntry = url;
		}
		
		this.map[index].put(realEntry, entity);
	}
	
	//BigInteger test = new BigInteger("515818");

	
	/*@Override
	public String toString(){
		return Arrays.toString(this.map);
	}*/
	static int count = 0;
	//On imprime les redirections s'il exitse un article dans la langue de requete
	/**
	 * Permet de lier les entitées à leures differentes réalisations dans les differentes langues de wikipedia. On suppose que la pertinence de l'uri (qu'il s'agit bien d'un article dans une des langue retenue, qu'il s'agit dans article wikipedia (et non celui d'un projet lié), qu'il s'agit bien d'un article (et non d'un Modèle ou d'un Template etc...)) est testé à un niveau superieur. 
	 * Essentiellement la fonction mets à jour le TitleMap afin de lier le titre de l'article dans cette langue donnée à l'entitée qui lui correspond (et qui est donnée en paramètre).
	 * Si l'entitée comporte un article dans la langue de requête, il sera egalement établit un lien de redirection (trivaialement entre le titre de l'article et l'entitée)
	 * 
	 * @param lang la langue extraite sur l'uri brute et qui permet de ranger l'entitée dans la hash
	 * @param uri l'adresse uri correspondant à un article (dans une certaine langue) correspondant à l'entitée donnée en paramètre 
	 * @param Entity l'entitée (construite à partir de l'URI) qu'on veut relier à son titre, via la TitleMap
	 * @param outRedirect flux d'écriture des items lexicaux
	 * @param outLexicon flux d'écriture des redirections lexicales
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 */
	public 	void addUrl(LANG lang, String uri,  Entity e, PrintWriter outRedirect, PrintWriter outLexicon) throws URISyntaxException, MalformedURLException{
		
		/*if(e.getID() == Parser.test){
			System.out.println("TitleMap.addUrl : "+e);
		}*/
		//System.out.println("now uri : "+uri);
		String uncryptedUrl = Crypter.urIToUrl(uri);
		/*if(e.getID().equals(test)){
			System.out.println("uncrypted : "+uncryptedUrl);
		}*/
		String articleName = uncryptedUrl;
		articleName = articleName.replaceAll(" ", "_");
		/*if(e.getID().equals(test)){
			System.out.println("firtsReplace : "+articleName);
		}*/
		articleName = Crypter.normalise(articleName);
		/*if(e.getID().equals(test)){
			System.out.println("normalised : "+articleName);
		}*/
		
		//le "poids" de l'entité est augementée
		//un titre est donnée l'entit� s'il existe une page dans la langue de requete
		
		//if(articleName.equals("Belgique")) System.out.println(count + " WikidataParse " +lang+ " : Belgique, at line :" +line +" \""+sCurrentLine+"\"");
		int ind = 0;
		for(LANG l : Parser.querryLang){
			if(l == lang){
				e.printRedirect(lang, Parser.lastGivenLexiconId, outRedirect);
				e.printLexicon( Parser.lastGivenLexiconId, articleName, outLexicon);
				Parser.lastGivenLexiconId = Parser.lastGivenLexiconId + 1;
				e.titles[ind] = articleName;
			}
			ind ++;
		}
		
		
		//if(print) logger.log(Level.INFO, "article \"Sean Connery\" : "+cryptedUrl);

		//la HashMap reciproque { url -> entité } doit-être mis à jour
		this.put(lang, articleName, e);
		count++;
	}
	
	
	//POUR LES REDIRECTS IL NE S'AGIT PAS D'URI MAIS DEJA DE "TITRES"
	public 	void addTitle(LANG lang, String articleName,  Entity e, PrintWriter outRedirect, PrintWriter outLexicon) throws URISyntaxException, MalformedURLException{
		
		articleName = articleName.replaceAll(" ", "_");
		/*if(e.getID().equals(test)){
			System.out.println("firtsReplace : "+articleName);
		}*/
		articleName = Crypter.normalise(articleName);
		/*if(e.getID().equals(test)){
			System.out.println("normalised : "+articleName);
		}*/
		
		//le "poids" de l'entité est augementée
		//un titre est donnée l'entit� s'il existe une page dans la langue de requete
		
		//if(articleName.equals("Belgique")) System.out.println(count + " WikidataParse " +lang+ " : Belgique, at line :" +line +" \""+sCurrentLine+"\"");
		int i = 0;
		for(LANG l : Parser.querryLang){
			if(i == 0 && l.toString().equals(lang.toString())){
				e.printRedirect(lang, Parser.lastGivenLexiconId, outRedirect);
				e.printLexicon( Parser.lastGivenLexiconId, articleName, outLexicon);
				Parser.lastGivenLexiconId = Parser.lastGivenLexiconId + 1;
			}
			i++;
		}
		
		//if(print) logger.log(Level.INFO, "article \"Sean Connery\" : "+cryptedUrl);

		//la HashMap reciproque { url -> entité } doit-être mis à jour
		this.put(lang, articleName, e);
		count++;
	}
	
	
}
