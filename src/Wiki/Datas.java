package Wiki;

import java.io.File;

/**
 * 
 * Une classe statique contenant les fonctions necessaires à l'interrogation et l'écriture des données (essentiellement des fonctions renvoyant le chemin d'accès d'un fichier)
 * 
 * @author Shervin Le Du
 */
public class Datas {
	
	
	//---------------------------------------------------------
	
	public	static File	wikidata(){
		File file = new File(rootPath()+wikidataRelativePath());
		return file;
	}
	public	static File	wikipedia(LANG lang){
		File file = new File(rootPath()+wikipediaRelativePath(lang));
		return file;
	}
	
	//---------------------------------------------------------
	
	public static String 	rootPath() {
		return "/home/paprika/Documents/wikipedia/";//wikidata/wikidata-sitelinks.nt";
	}
	private static String 	wikidataRelativePath() {
		return "wikidata-sitelinks.nt";
	}
	private static String 	wikipediaRelativePath(LANG lang) {
		return lang.name()+"wiki-20170820-pages-articles.xml";
	}
	
	//-----------------------------------------------------------
	
	static String pathNodesCSV(){
		return "/home/paprika/Documents/workspace/WikipediaFinal/output/Graph/Nodes.csv";
	}
	static String pathRelsCSV(){
		return "/home/paprika/Documents/workspace/WikipediaFinal/output/Graph/Rel.csv";
	}
	static String pathLexicon(){
		return "/home/paprika/Documents/workspace/WikipediaFinal/output/Graph/Lexicon.csv";
	}
	static String pathRedirect(){
		return "/home/paprika/Documents/workspace/WikipediaFinal/output/Graph/Redirect.csv";
	}
	static String pathLinkException(){
		return "/home/paprika/Documents/workspace/WikipediaFinal/output/Graph/LinkException";
	}
	static String pathNoWikidataEntityException(){
		return "//home/paprika/Documents/workspace/WikipediaFinal/output/Graph/NoWikidataEntityException";
	}
	static String pathStats(){
		return "/home/paprika/Documents/workspace/WikipediaFinal/output/Graph/Stats";
	}
	
	static String pathArticles(){
		return "/home/paprika/Documents/workspace/WikipediaFinal/output/Speech/Articles.csv";
	}
	static String pathOeuvres(){
		return "/home/paprika/Documents/workspace/WikipediaFinal/output/Speech/Oeuvres.csv";
	}
	static String pathLivres(){
		return "/home/paprika/Documents/workspace/WikipediaFinal/output/Speech/Livres.csv";
	}
	static String pathEcrivains(){
		return "/home/paprika/Documents/workspace/WikipediaFinal/output/Speech/Ecrivains.csv";
	}

	
	
	
}
