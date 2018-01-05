package Wiki;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * Classe contenant les variables Statistiques désirées, qui calculées lors du parcours des datas.
 * Il reste à les rendre imprimable et chargeable dans/depuis un fichier, afin que les paramètres statistiques puissent être utilisés pour optimiser le parsing (exemple utilisation de la mediane du nombre de liens par entitées pour l'instanciation des HashMap correspondant à ceux-ci avec les bonnes dimensions de départ)
 * 
 * @author Shervin Le Du
 *
 */

public class Statistics {
	
	static PrintWriter outputStats;
	
//----------------Wikidata-----------------------
	
	static Integer 				numberOfEntities;				//NUMER OF ENTITIES (EXCLUDING ANYONE WHICH HAS NO TRANSLATION IN GIVEN LANGUAGES)
	static BigInteger 				numberOfOctetsWikidata;			//WEIGHT OF WIKIDATA DUMP
	static Integer 				numberOfLinkedArticles;			//TOUTES LES PAGES WIKIPEDIA D'UNE DES LANGUES RETENUES
	static Integer[]				distributionOfLinkedArticles;	//QUANTITES D'ENTITES AYANT i ARTICLES ASSOCIEES DANS LES LANGUES RETENUES
	
//----------------Wikipedia----------------------
	
	static Map<LANG, Integer> 	numberOfPages;					//ALL PAGES (INCLUDING ARTICLES, REDIRECT & DISAMBIGUISATION)
	static Map<LANG, Integer> 	numberOfRedirect;				//REDIRECT PAGES
	// Map<LANG, Integer> numberOfDisambiguisation;				//DISAMBIGUISATION PAGES		(Comment les parser en toute généralité???)
	static Map<LANG, Integer>	numberOfLinks;					//NUMBER OF LINKS PER LANGUAGES
	static Map<LANG, BigInteger> 	numberOfOctets;					//WEIGHT OF THE CORRESPONDING WIKIPEDIA DUMP
	static Map<LANG, Integer> 	numberOfFirstParagLinks;		//SUM OF ALL LINKS IN ARTICLES' INTRO
	static Map<LANG, Integer[]>	distributionOfFirstParagLinks;	
	static Map<LANG, Integer>		medianNumberOfFirstParagLinks;
	
	static Integer 				totalNumberOfPages;
	static Integer 				totalNumberOfArticles;
	static Integer 				totalNumberOfRedirect;
	// Integer 			totalNumberOfDisambiguisation;
	static Integer 				totalNumberOfOctets;
	static Integer 				totalNumberOfArticlesFirstParagLink;
	static Integer 				totalNumberOfEntitiesLink;
	static Integer[]				totalDistributionfOfArticlesFirstParagLink;
	//static Integer[]				totalDistributionfOfEntitiesFirstParagLink;
	static Integer					totalMedianNumberfArticlesFirstParagLink;
	//static Integer					totalMedianNumberfEntitiesFirstParagLink;
	
	//entity level stats :
	static Integer[]				totalDistributionOfLinkedEntities;	//QUANTITES D'ENTITES AYANT i ENTITEES ASSOCIEES
	static Integer					totalMedianNumberOfLinkedEntities;
	static Integer[]				totalDistributionOfLinksLength;		//QUANTITES DE LIENS AYANT i LANGUES
	
//-----------------Facteurs----------------------
	
	/*
	* Une application des stats permet de savoir l'avancement du calcul
	* (en particulier les données sur la taille des documents)
	*/
	public static int 			step 	= 10000000;								//permet de determiner la fréquence des logs d'informations sur l'avancemment
	public static int 			factor 	= 10000000;								//permet de decider de la réduction de la fraction representant l'avancement
	public static Integer 	one 	= new Integer("1");					//permet d'incrementer un Integer
	
	// en moyenne 150 octets par lignes (approx?)
	static double countOctets(File file) throws IOException {
		return file.length();
	}
	
//-----------------------------------------------
	
	public static void init(PrintWriter outputStats){
		
		Statistics.outputStats = outputStats;
		
	//------------wikidata----------------
		Statistics.numberOfEntities 				= new Integer("0");
		Statistics.numberOfOctetsWikidata 		= new BigInteger("0");			
		Statistics.numberOfLinkedArticles 		= new Integer("0");
		System.out.println("lang : "+LANG.values().length);
		for(LANG lang : LANG.values()){
			System.out.print(lang + ",");
		}
		Statistics.distributionOfLinkedArticles	= new Integer[LANG.values().length+1];
		for(int i = 0; i < Statistics.distributionOfLinkedArticles.length; i++){
			Statistics.distributionOfLinkedArticles[i] = new Integer("0");
		}
	//------------wikipedia----------------
		Statistics.numberOfPages 					= new HashMap<>(LANG.values().length, 2);
		Statistics.numberOfRedirect					= new HashMap<>(LANG.values().length, 2);
		Statistics.numberOfLinks					= new HashMap<>(LANG.values().length, 2);
		Statistics.numberOfOctets					= new HashMap<>(LANG.values().length, 2);
		Statistics.numberOfFirstParagLinks			= new HashMap<>(LANG.values().length, 2);
		Statistics.distributionOfFirstParagLinks	= new HashMap<>(LANG.values().length, 2);
		Statistics.medianNumberOfFirstParagLinks	= new HashMap<>(LANG.values().length, 2);
		for(LANG lang : LANG.values()){
			Statistics.numberOfPages.put(lang, new Integer("0"));
			Statistics.numberOfRedirect.put(lang, new Integer("0"));
			Statistics.numberOfLinks.put(lang, new Integer("0"));
			Statistics.numberOfOctets.put(lang, new BigInteger("0"));
			Statistics.numberOfFirstParagLinks.put(lang, new Integer("0"));
			Statistics.medianNumberOfFirstParagLinks.put(lang, new Integer("0"));
		}
		
		Statistics.totalNumberOfPages							= new Integer("0");
		Statistics.totalNumberOfArticles						= new Integer("0");
		Statistics.totalNumberOfRedirect						= new Integer("0");
		//private Integer 				totalNumberOfDisambiguisation;
		Statistics.totalNumberOfOctets						= new Integer("0");
		Statistics.totalDistributionfOfArticlesFirstParagLink	= new Integer[LANG.values().length+1];
		for(int i = 0; i < Statistics.totalDistributionfOfArticlesFirstParagLink.length; i++){
			Statistics.totalDistributionfOfArticlesFirstParagLink[i] = new Integer("0");
		}
		Statistics.totalMedianNumberfArticlesFirstParagLink	= new Integer("0");
		
		Statistics.totalDistributionOfLinkedEntities	= new Integer[LANG.values().length+1];
		for(int i = 0; i < Statistics.totalDistributionOfLinkedEntities.length; i++){
			Statistics.totalDistributionOfLinkedEntities[i] = new Integer("0");
		}
		Statistics.totalMedianNumberOfLinkedEntities	= new Integer("0");	
		totalDistributionOfLinksLength					= new Integer[LANG.values().length+1];
		for(int i = 0; i < Statistics.totalDistributionOfLinkedEntities.length; i++){
			Statistics.totalDistributionOfLinkedEntities[i] = new Integer("0");
		}
	}
	
	public static void print(){
		PrintWriter pw = Statistics.outputStats;
		pw.println("totalMedianNumberfArticlesFirstParagLink " + totalMedianNumberfArticlesFirstParagLink);
		pw.println("numberOfOctetsWikidata "+numberOfOctetsWikidata);
		pw.println("numberOfLinkedArticles "+numberOfLinkedArticles);
		pw.println("totalNumberOfPages "+totalNumberOfPages);
		pw.println("totalNumberOfArticles "+totalNumberOfArticles);
		pw.println("totalNumberOfRedirect "+totalNumberOfRedirect);
		pw.println("totalNumberOfOctets "+totalNumberOfOctets);
		pw.println("totalNumberOfArticlesFirstParagLink "+totalNumberOfArticlesFirstParagLink);
		pw.println("totalNumberOfEntitiesLink "+totalNumberOfEntitiesLink);
		pw.println();
		
		pw.println("distributionOfLinkedArticlesWikidata ");
		int i = 0;
		for(Integer bigI :distributionOfLinkedArticles){
			pw.println("\tdistributionOfLinkedArticles["+i+"] "+bigI);
			i++;
		}
		pw.println("totalDistributionfOfArticlesFirstParagLink ");
		i = 0;
		for(Integer bigI :totalDistributionfOfArticlesFirstParagLink){
			pw.println("\ttotalDistributionfOfArticlesFirstParagLink["+i+"] "+bigI);
			i++;
		}
		pw.println();

		pw.println("numberOfPages (by lang) ");
		for(Entry<LANG, Integer> e : numberOfPages.entrySet()){
			pw.println("\t"+e.getKey()+" "+e.getValue());
		}
		pw.println();

		pw.println("numberOfRedirects (by lang) ");
		for(Entry<LANG, Integer> e : numberOfRedirect.entrySet()){
			pw.println("\t"+e.getKey()+" "+e.getValue());
		}
		pw.println();

		pw.println("numberOfLinks (by lang) ");
		for(Entry<LANG, Integer> e : numberOfLinks.entrySet()){
			pw.println("\t"+e.getKey()+" "+e.getValue());
		}
		pw.println();

		pw.println("numberOfOctets (by lang) ");
		for(Entry<LANG, BigInteger> e : numberOfOctets.entrySet()){
			pw.println("\t"+e.getKey()+" "+e.getValue());
		}
		pw.println();

		pw.println("numberOfFirstParagLinks (by lang) ");
		for(Entry<LANG, Integer> e : numberOfFirstParagLinks.entrySet()){
			pw.println("\t"+e.getKey()+" "+e.getValue());
		}
		pw.println();

		pw.println("distributionOfFirstParagLinks (by lang) ");
		for(Entry<LANG, Integer[]> e : distributionOfFirstParagLinks.entrySet()){
			pw.println("\t"+e.getKey()+" ");
			Integer[] distr = e.getValue();
			i = 0;
			for(Integer bigI :distr){
				pw.println("\t\t"+i+" "+bigI);
				i++;
			}
			pw.println();
		}
		pw.println();

		pw.println("medianNumberOfFirstParagLinks (by lang) ");
		for(Entry<LANG, Integer> e : medianNumberOfFirstParagLinks.entrySet()){
			pw.println("\t"+e.getKey()+" "+e.getValue());
		}
		pw.println();

	}
	
}
