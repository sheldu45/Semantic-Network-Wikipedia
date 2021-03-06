package Wiki;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Classe principale qui parcourt les dumps wikidata/wikipedia, afin de mettre à jour le Graphe et imprimer celui-ci dans un fichier au format .csv (interprétable par Neo4j).
 * Lors du parcours, quelques données statistiques sont également calculées et enregistrées dans les variables de la classe statique Statistics
 * 
 * Le parsing se fait par reconnaissance d'expressions régulières ligne après ligne.
 * 
 * POUR le moment aucun traitement specifique n'est fait aux pages de désambiguisation. Si le titre de la désambiguisation est le même qu'une de ses définitions, elles seront confondus. Sinon (typiquement les titres style : "...(homonymie) elles seront distinguées")
 * Un traitement spécifique pourrait être fait pour les désambiguisations, mais il faudrait alors pour les langues où on les étudierait reconnaitre les differentes formes prise dans l'Infox (homonymie, Homonymie, Patronymes etc...)
 * 
 * @author Shervin Le Du
 *
 */

public class Parser{
	
	//----------------------VARIABLES publiques-----------------------
	
	public static 				LANG[] querryLang 									= {LANG.FR}; //langage de requete
	TitleMap 					titleMap;
	TitleMap 					redirectMap;
	HashMap<Integer, Entity> 	entitySet; 											//ancre les entitées dans la durée (d'un fichier à l'autre)

	//----------------------VARIABLES privées-----------------------
	
	private static 	Logger logger 						= Logger.getLogger(Parser.class.getName());
	//pour le conteur
	static int step = 10000000;
	static int factor = 10000000;
	long start; 
	public static Integer one = new Integer("1");
		
	public Parser(boolean doubleLink){
		this.titleMap = new TitleMap();
		this.redirectMap = new TitleMap(); 
		entitySet = new HashMap<Integer, Entity>(2000000, 100);
		Entity.doubleLink = doubleLink;
	}
	
	static boolean isQuerryLangage(LANG lg){
		for(LANG l : querryLang){
			if(lg.toString().equals(l.toString())) return true;
		}
		return false;
	}
	static int indexof(LANG lg){
		int i = 0;
		for(LANG l : querryLang){
			if(lg.toString().equals(l.toString())) {
				return i;
			}
			i++;
		}
		return -1;
	}

	
	public static Integer lastGivenLexiconId = new Integer("0");
	
	public void parse(){
	
	//Writing Nodes
		BufferedWriter bwNodes = null;
		PrintWriter outNodes = null;
		
	//Writing Relations
		BufferedWriter bwRels = null;
		PrintWriter outRels = null;
		
	//Writing Lexicon
		BufferedWriter bwLexicon = null;
		PrintWriter outLexicon = null;
		
	//Writing Redirect
		BufferedWriter bwRedirect = null;
		PrintWriter outRedirect = null;
		
	//Writing Exception
		BufferedWriter bwLinkException = null;
		PrintWriter outLinkException = null;
	
	//Writing NoWikidataEntityException (titre non reconnu dans wikidata)
		BufferedWriter bwNoWikidataEntityException = null;
		PrintWriter outNoWikidataEntityException = null;
		
	//Writing Stats
		BufferedWriter bwStats = null;
		PrintWriter outStats = null;
		
	//Writing Articles
		BufferedWriter bwArticles = null;
		PrintWriter outArticles = null;
				
	//Writing Oeuvres
		BufferedWriter bwOeuvres = null;
		PrintWriter outOeuvres = null;
		
	//Writing Livres
		BufferedWriter bwLivres = null;
		PrintWriter outLivres = null;
			
	//Writing Ecrivains
		BufferedWriter bwEcrivains = null;
		PrintWriter outEcrivains = null;
		
		try {
			
			bwNodes = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Datas.pathNodesCSV()), StandardCharsets.UTF_8));
			outNodes = new PrintWriter(bwNodes);
			
			bwRels = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Datas.pathRelsCSV()), StandardCharsets.UTF_8));
			outRels = new PrintWriter(bwRels);
			
			bwRedirect = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Datas.pathRedirect()), StandardCharsets.UTF_8));
			outRedirect = new PrintWriter(bwRedirect);
			
			bwLexicon = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Datas.pathLexicon()), StandardCharsets.UTF_8));
			outLexicon = new PrintWriter(bwLexicon);
			
			bwLinkException = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Datas.pathLinkException()), StandardCharsets.UTF_8));
			outLinkException = new PrintWriter(bwLinkException);
			
			bwStats =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Datas.pathStats()), StandardCharsets.UTF_8));
			outStats = new PrintWriter(bwStats);
			
			bwArticles = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Datas.pathArticles()), StandardCharsets.UTF_8));;
			outArticles = new PrintWriter(bwArticles);
					
			bwOeuvres = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Datas.pathOeuvres()), StandardCharsets.UTF_8));;
			outOeuvres = new PrintWriter(bwOeuvres);
			
			bwLivres = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Datas.pathLivres()), StandardCharsets.UTF_8));;
			outLivres = new PrintWriter(bwLivres);
				
			bwEcrivains = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Datas.pathEcrivains()), StandardCharsets.UTF_8));;
			outEcrivains = new PrintWriter(bwEcrivains);
			
			bwNoWikidataEntityException = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Datas.pathNoWikidataEntityException()), StandardCharsets.UTF_8));;
			outNoWikidataEntityException = new PrintWriter(bwNoWikidataEntityException);
			
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("Init File : "+Datas.pathRelsCSV());
		this.initRelationFile(outRels);
		this.initNodeFile(outNodes);
		this.initRedirectFile(outRedirect);
		this.initLexiconFile(outLexicon);
		Statistics.init(outStats);

		//Crypter c = new Crypter();
		
		try{
			parseWikipedia(outRels, outRedirect, outLexicon, outLinkException, outNodes, outArticles, outOeuvres, outLivres, outEcrivains, outNoWikidataEntityException);
		}
		catch(Throwable e){
			System.out.println("probleme with wikipedia parsing");
			e.printStackTrace();
		}
		finally{
			printUnPrintedRelationships(outRels);
			//printAllUnprintedNodes(outNodes);
		}
		
		Statistics.print();
		
	//Closing readers and writers
		try {
			if (bwNodes != null)
				bwNodes.close();
			if (outNodes != null)
				outNodes.close();
			if (bwRels != null)
				bwRels.close();
			if (outRels != null)
				outRels.close();
			if (bwRedirect != null)
				bwRedirect.close();
			if (outRedirect != null)
				outRedirect.close();
			if (bwLexicon != null)
				bwLexicon.close();
			if (outLexicon != null)
				outLexicon.close();
			if(bwLinkException != null)
				bwLinkException.close();
			if(outLinkException != null)
				outLinkException.close();
			if(bwStats != null)
				bwStats.close();
			if(outStats != null)
				outStats.close();
			
			if(bwArticles != null)
				bwArticles.close();
			if(outArticles != null)
				outArticles.close();
			if(bwOeuvres != null)
				bwOeuvres.close();
			if(outOeuvres != null)
				outOeuvres.close();
			if(bwLivres != null)
				bwLivres.close();
			if(outLivres != null)
				outLivres.close();
			if(bwEcrivains != null)
				bwEcrivains.close();
			if(outEcrivains != null)
				outEcrivains.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//static Integer test = 12885989;
	
	//IL FAUT PENSER BIENS SUR A REOUVRIR WIKIDATA AVANT LE PARSE DE WIKIDATA ET A LE REFERMER APRES
	ArrayList<LANG> finishedLang = new ArrayList<LANG>();
	public void parseWikidata(PrintWriter outRedirect, PrintWriter outLexicon, LANG lg) {
		System.out.println("\nSTART WIKIDATA PARSE FOR "+lg);
		start = System.currentTimeMillis();
		try {
			//reading wikidata
			BufferedReader brWikidata = null;
			//Reading wikidata
			File WikidataFile = Datas.wikidata();
			brWikidata = new BufferedReader(new InputStreamReader(new FileInputStream(WikidataFile), StandardCharsets.UTF_8));
			
			
			toBePrintedNodes = new HashMap<Integer, Entity>(lg.numberOfLinks);
			double numberOfBytesInWikiData = Statistics.countOctets(Datas.wikidata());
			Statistics.numberOfOctetsWikidata = new BigDecimal(numberOfBytesInWikiData).toBigInteger();
			String sCurrentLine;
			double i = 1;
			double oct = 0;
			Entity entity = null;
			int count = 0;
			String ID = null;
			String 	toBeAddedURL = null;
			HashSet<LANG> languagesInWhichExists = new HashSet<LANG>(LANG.values().length);
			while ((sCurrentLine = brWikidata.readLine()) != null) {
				
				// Check encoded sizes
				final byte[] utf8Bytes = sCurrentLine.getBytes(StandardCharsets.UTF_8);
				oct += utf8Bytes.length;
				if (i % step == 0) {
					logger.log(Level.INFO, lg + " - " +(int) (oct / factor) + "/" + (int) (numberOfBytesInWikiData / factor));
				}

				String[] ligne = sCurrentLine.split(" ");
				String premiereBalise = ligne[0];
				String dernierBalise = ligne[ligne.length - 2];

				if (dernierBalise.indexOf("#") != -1 && dernierBalise.split("#")[1].equals("Item>")) {
					if(ID != null && toBeAddedURL != null) { //instancier des hashmaps proportionnelles au nombres de langues && SSI la langue interressée est contenue
						this.entitySet.put(entity.getID(), entity);
						byte numberOfLangs = (byte) languagesInWhichExists.size();
						entity.weight = numberOfLangs;
						titleMap.addUrl(lg, toBeAddedURL, entity, outRedirect, outLexicon);
						/*if(entity.getID() == test){
							System.out.println("WikiData (334), added "+toBeAddedURL+" under title : "+entity.getTitle());
						}*/
						Statistics.numberOfLinkedArticles = Statistics.numberOfLinkedArticles += 1;
						toBeAddedURL = null;
						/*if(entity.getID() == test){
							System.out.println("WikiData (334), finished "+ finishedLang.size()+" languages which are : "+finishedLang+", languagesInWhichExists are "+languagesInWhichExists.size()+" which are :"+languagesInWhichExists);
						}*/
						boolean entityNeverMet = true;
						for(LANG langsOfEntity : languagesInWhichExists) {
							for(LANG doneLanguage : finishedLang) {
								if(langsOfEntity == doneLanguage) {
									entityNeverMet = false;
								}
							}
						}
						/*if(entity.getID() == (test)){
							System.out.println("WikiData (334), entityNeverMet "+entityNeverMet);
						}*/
						if(entityNeverMet){
							toBePrintedNodes.put(entity.getID(), entity);
							//entity.printNodeFile(this, outNodes);
						}
					}
					ID = premiereBalise.substring(premiereBalise.lastIndexOf("/") + 2, premiereBalise.length() - 1);
					entity = new Entity(new Integer(ID));
					languagesInWhichExists = new HashSet<LANG>(LANG.values().length);
					Entity RealEntity = this.entitySet.get(entity.getID());
					if(RealEntity != null) {
						entity = RealEntity;
					}
					if (entity != null && entity.getWeight() > 0) { //on imprime l'entité precédente
						Statistics.numberOfEntities = Statistics.numberOfEntities += 1;
						Statistics.distributionOfLinkedArticles[entity.getWeight()] = Statistics.distributionOfLinkedArticles[entity.getWeight()] += 1;
					} else if (entity != null && entity.getWeight() == 0) {
						Statistics.distributionOfLinkedArticles[0] = Statistics.distributionOfLinkedArticles[0] += 1;
					}
					count = 0;
				} else {
					count++;
					if (count == 3) {
						//contient la veritable langue
						String langExtraite = premiereBalise.substring(premiereBalise.indexOf("//")+2, premiereBalise.indexOf('.'));
						if (LANG.isLang(langExtraite)) {
							boolean isArticle = true;
							String title = "";
							String uri = premiereBalise.substring(1, premiereBalise.length() - 1);
							LANG lang = LANG.extractLangFromUrl(uri);
							//On élimine les articles liés à d'autre projets que wikipedia (wiktionnaire, wikiquote etc...)
							String subwikiDomain = uri.substring(uri.indexOf('.') + 1);
							subwikiDomain = subwikiDomain.substring(0, subwikiDomain.indexOf('.'));
							//On élimine les non-articles (Modèles, Catégories etc...)
							title = uri;
							String categoryAndTitle = title;
							String category = "";
							//System.out.println("brut uri  : "+uri);
							for (int k = 0; k < 3; k++) {
								//System.out.println(k+" : "+langWikiCategory);
								categoryAndTitle = categoryAndTitle.substring(categoryAndTitle.indexOf('/') + 1);
							}
							category = categoryAndTitle.substring(0, categoryAndTitle.indexOf('/'));
							title = categoryAndTitle.substring(categoryAndTitle.indexOf('/') + 1);
							if (title.indexOf('/') != -1 && title.indexOf(':') != -1) { //arbitrairement on decide que c'est une condition suffisant pour dire que ce n'est pas un aticle valide : what should be the title is in fact still a category and a title
								isArticle = false;
								int index = title.indexOf('/');
								try {
									title = title.substring(index + 1);
								} catch (java.lang.StringIndexOutOfBoundsException bugexc) {
										//System.out.println("not an article : "+premiereBalise);
								}
							}
							if(!category.equals("wiki")) isArticle = false;
							if ("wikipedia".equals(subwikiDomain) && isArticle){
								if(lang == lg) {
									toBeAddedURL = title;
								}
								/*if(entity.getID() == test){
									System.out.println("WikiData (330), about to add "+lang+" as existing lang for entity ");
								}*/
								languagesInWhichExists.add(lang);
							}
						}
						count = 0;
					}
				}
				i++;
			}
			if(ID != null && entity.linkedEntities == null &&  toBeAddedURL != null) { //instancier des hashmaps proportionnelles au nombres de langues && afin detre sûr que la derniere entitée sera instanciée correctement && SSI la langue interressée est contenue 
				//numberOfLangs = numberOfLangs == 0 ? 1 : numberOfLangs; //au cas où numberOfLangs vaudrait 0
				byte numberOfLangs = (byte) languagesInWhichExists.size();
				Entity RealEntity = this.entitySet.get(entity.getID());
				if(RealEntity != null) {
					entity = RealEntity;
				}
				else {
					this.entitySet.put(entity.getID(), entity);
				}
				entity.weight = numberOfLangs;
				titleMap.addUrl(lg, toBeAddedURL, entity, outRedirect, outLexicon);
				/*if(entity.getID().equals(test)){
					System.out.println("WikiData (334), added "+uri+" under title : "+entity.getTitle());
				}*/
				Statistics.numberOfLinkedArticles = Statistics.numberOfLinkedArticles += 1;
				toBeAddedURL = null;
				boolean entityNeverMet = true;
				for(LANG langsOfEntity : languagesInWhichExists) {
					for(LANG doneLanguage : finishedLang) {
						if(langsOfEntity == doneLanguage) {
							entityNeverMet = false;
						}
					}
				}
				if(entityNeverMet){
					toBePrintedNodes.put(entity.getID(), entity);
					//entity.printNodeFile(this, outNodes);
				}
				languagesInWhichExists = new HashSet<LANG>(LANG.values().length);

			}
			if(brWikidata != null) {
				brWikidata.close();
			}
		} catch (IOException | URISyntaxException e) {
			System.out.println("fatal error on wikidata");
			e.printStackTrace();
		}
		int i = 0;
		for(Integer bigI : Statistics.distributionOfLinkedArticles){
			System.out.println("Number of entities with "+i+" languages : "+bigI);
			i++;
		}
		System.out.println("END WIKIDATA PARSE FOR "+lg+"\n");
	}

	private void parseWikipedia(PrintWriter outRels, PrintWriter outRedirect,  PrintWriter outLexicon, PrintWriter outLinkException, PrintWriter outNodes,  PrintWriter articlesOut, PrintWriter oeuvresOut, PrintWriter livresOut, PrintWriter ecrivainsOut, PrintWriter noWikidataEntityExceptionOutput) throws IOException {
		//Reading wikipedia
		BufferedReader brWikpedia = null;
		try {
			assert Parser.querryLang.length == 1; //[POUR LE MOEMENT FONCTIONNEL SIL Y'A UNE SEULE LANGUE DE REQUETE]
			for(LANG lg : Parser.querryLang){ //On parcourt tous les langages de requete afin de pouvoir ensuite imprimer les entitées (une fois leur Label mis à jour)
				parseWikidata(outRedirect, outLexicon, lg);
				
				String filename = lg.wikipediaFileName();
				double numberOfBytesInWikipedia = -1;
				File fileDir = new File(filename);
				try {
					//reading
					numberOfBytesInWikipedia = Statistics.countOctets(fileDir);	
					
				} catch (Exception e) {
					System.out.println("wrong file");
				}
				brWikpedia = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
				parseRedirect(true, lg, numberOfBytesInWikipedia, brWikpedia, outRedirect, outLexicon);
				//on réinitialise la lecture
				brWikpedia = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
				parseWikipedia(lg, numberOfBytesInWikipedia, brWikpedia, outNodes, outRels, outLinkException, noWikidataEntityExceptionOutput);
				/*
				 * TODO
				 * POUR LE CHATBOT, IL FAUDRA RECONSTRUIRE CETTE PARTIE AVEC L'IDEE SUIVANTE : la gestion des synonymies est parallèle à la gestion des redirections 
				 * Map<Entity, LinkedList<String>> synonymsTable = constructSynonymsTable(redirect);
				 * printAllSynonyms(synonymsTable, articlesOut, oeuvresOut, livresOut, ecrivainsOut);	
				 */
				printAllUnprintedNodes(outNodes);
				removeFinishedNodes(outNodes, lg); //On imprime les noeuds ici afin de pouvoir imprimer l'hyperonomie avec
			}		
			
			//il faudrait faire la meme chose ici pour la gestion des lexicons et redirections multilingues
			
			for(LANG lg : LANG.values()){
				if(!Parser.isQuerryLangage(lg)){
					parseWikidata(outRedirect, outLexicon, lg);
					String filename = lg.wikipediaFileName();
					double numberOfBytesInWikipedia = -1;
					File fileDir = new File(filename);
					try {
						//reading
						numberOfBytesInWikipedia = Statistics.countOctets(fileDir);	
						
					} catch (Exception e) {
						System.out.println("wrong file");
					}
					brWikpedia = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
					parseRedirect(false, lg, numberOfBytesInWikipedia, brWikpedia, outRedirect, outLexicon);
					//on réinitialise la lecture
					brWikpedia = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
					parseWikipedia(lg, numberOfBytesInWikipedia, brWikpedia, outNodes, outRels, outLinkException, noWikidataEntityExceptionOutput);
					printAllUnprintedNodes(outNodes);
					removeFinishedNodes(outNodes, lg); //On imprime les nouveaux noeuds (et on libere de la memoire)
				}
			}
		} catch (IOException  e) {
			System.out.println("fatal error on wikipedia");
			e.printStackTrace();
		}
		if(brWikpedia != null)
			brWikpedia.close();
	}
	
	//Integer test = new Integer("515818");
	private static int numberOfFatalErrorsSkipped = 0;	
	private void parseWikipedia(LANG lang, double numberOfBytesInWikipedia, BufferedReader br, PrintWriter outNodes, PrintWriter relationOutput, PrintWriter linkExceptionOutput, PrintWriter noWikidataEntityException) throws IOException {
		System.out.println("begin wikipedia " + lang);
		linkExceptionOutput.println(lang);
		Integer unParsedPages = new Integer("0");
		Integer unParsedLinks = new Integer("0");
		Integer countLinks = new Integer("0");
		Integer numberOfPages = new Integer("0");

		//Stats
		Map<Integer, Integer> distribution = new HashMap<>(lang.numberOfLinks);
		Integer max = 0;
		Integer numberOfLinks = new Integer("0");

		start = System.currentTimeMillis();
		
		Pattern balisePage = Pattern.compile("[ ]*<page>[ ]*");
		Pattern openInfo = Pattern.compile("\\{\\{Infobox");
		Pattern italique = Pattern.compile("<text xml:space=\"preserve\">\\{\\{titre en italique\\}\\}");
		Pattern subject = Pattern.compile("[ ]*([ÉéEe]crivain|[Ll]ivre)[ ]*");
		Pattern baliseRedirect = Pattern.compile("[ ]*<redirect[ ]*");
		Pattern baliseTitle = Pattern.compile("[ ]*<title>[ ]*");
		Pattern baliseOpenText = Pattern.compile("[ ]*<text>[ ]*");
		Pattern baliseCloseText = Pattern.compile("[ ]*</text>[ ]*");
		Pattern metadata = Pattern.compile("^[ ]*[<|{=}]");
		Pattern separation = Pattern.compile("(^[ ]*==|[ ]*</page>[ ]*)");
		Pattern link = Pattern.compile("\\[\\[[^#%:\\.\\]]+\\]\\]");
		Pattern sentenceSeparator = Pattern.compile("[\\.\\?\\!]");
		
		String sCurrentLine;

		boolean querryLang = false;
		for(LANG lg :  Parser.querryLang){
			if(lang.toString().equals(lg.toString())){
				querryLang = true;
			}
		}
	
		double j = 0;
		double oct = 0;
		Entity mainEntity = null;
		boolean open = false;
		boolean isRedirect = false;
		boolean hasPrintedHypernomia = false;
		boolean hasPrintedItalic = false;
		Integer numLine = new Integer("0");
		String title = "";
		HashSet<Entity> completedEntity = new HashSet<Entity>(); //permettra de garder le compte des entitées complétées dans cette langue. Ainsi si pour une langue donnée deux articles ont le même nom (article et desambiguisations), l'incrémentations n'aura lieux qu'une seule fois quand même 
		LinkedList<Entity> toLinkEntity = null;
		while ((sCurrentLine = br.readLine()) != null) {
			numLine += 1;
			// Check encoded sizes
			final byte[] utf8Bytes = sCurrentLine.getBytes("UTF-8");
			oct += utf8Bytes.length;
			if (j % step == 0) {
				long elapsedTimeMillis = System.currentTimeMillis() - start;
				// Get elapsed time in seconds
				float elapsedTimeSec = elapsedTimeMillis / 1000F;
				double remainingTime = (elapsedTimeSec * (numberOfBytesInWikipedia / factor) / (oct / factor)) - elapsedTimeSec;
				String linkNature = Entity.doubleLink ? "Double linked" : "OrientedLink";
				logger.log(Level.INFO, "Parser.parseWikipedia : \nLang : " + lang + "\n"
						+ ((int) (oct / factor) + "/" + (int) (numberOfBytesInWikipedia / factor)) + "\nspent time : "
						+ elapsedTimeSec + " seconds\nremaining time : " + remainingTime + " secondes" + "\n" + linkNature);
				//printRelations(out);
			}
			if (!open) {																				//page fermée
				Matcher matcherBalisePage = balisePage.matcher(sCurrentLine);
				if (matcherBalisePage.find()) {			//nouvelle page
					open = true;
				}
			} else {																					//page ouverte
				Matcher matcherMetadata = metadata.matcher(sCurrentLine);
				if (matcherMetadata.find()) { 												//metadonnées de la pages (titre, redirection)
					Matcher matcherRedirect = baliseRedirect.matcher(sCurrentLine);
					if (matcherRedirect.find()) {
						isRedirect = true;
						mainEntity = null;
					}
					Matcher matcherTitle = baliseTitle.matcher(sCurrentLine);
					if (matcherTitle.find()) {
						String brutTitle = sCurrentLine;
						brutTitle = brutTitle.substring(brutTitle.indexOf(">") + 1);
						brutTitle = brutTitle.substring(0, brutTitle.indexOf("<"));
						title = brutTitle;
						brutTitle = Crypter.normalise(brutTitle);
						//if(title.equals("Belgique")) System.out.println("WikipediaParse : Belgique (title)");
						mainEntity = this.titleMap.get(lang, brutTitle);
						if (isRedirect) {
							mainEntity = null;
						}
						numberOfPages = numberOfPages += 1;
						toLinkEntity = new LinkedList<Entity>();
						//Integer nbLiens = 0;
					}
					Matcher matcherSepartaion = separation.matcher(sCurrentLine);
					if (matcherSepartaion.find()) { // FIN DU PREMIER PARAGRAPHE
						if (mainEntity == null && !isRedirect) {
							unParsedPages = unParsedPages += 1;
							noWikidataEntityException.println(lang + "wiki : unable to get any entity under title : "+title);
						}
						else {
							//incremente le conteur de l'entitée qui permettra de l'imprimer si elle est complete à la fin du parsing de cette langue
							if (!isRedirect && !completedEntity.contains(mainEntity)) {
								//System.out.println(mainEntity.completedLanguages);
								completedEntity.add(mainEntity);
								if(this.toBePrintedNodes.containsKey(mainEntity.getID())){
									mainEntity.printNodeFile(this, outNodes);
									/*if(mainEntity.getID() == test){
										System.out.println("WikiData (334), printed "+mainEntity+" in wikipedia parse ");
									}*/
								}
								//System.out.println("toLink.size() : " +toLinkEntity.size());
								if(mainEntity.completedLanguages == 0) {
									mainEntity.setWeigthedHashMap(mainEntity.weight, toLinkEntity.size());
								}
								mainEntity.completedLanguages++;
								for(Entity e : toLinkEntity) {
									mainEntity.linkTo(lang, e, 1.0);
								}
								if(mainEntity.completedLanguages >= mainEntity.weight){ //on imprime les relations (=> suppression du noeud de entitySet et destruction des liens sortants)
									try {
										mainEntity.printRelationFile(relationOutput);
										printedRelationships = printedRelationships += 1;
									} catch (Exception e1) {
										System.out.println("error in printAllRelationsOfFinishedEntities");
										remove(this, mainEntity);
										continue;
									}
									remove(this, mainEntity);
								}
								mainEntity = null;
							}
						}
						isRedirect = false;
						open = false;
						hasPrintedHypernomia = false;
						hasPrintedItalic = false;
						title = "";
					} 
					Matcher info = openInfo.matcher(sCurrentLine);
					if (info.find() && querryLang && mainEntity != null) {
						int index = sCurrentLine.indexOf("Infobox") + "Infobox".length();
						if (index > -1) {
							String r = "";
							try {
								r = sCurrentLine.substring(index + 1);
								LinkedList<Integer> list = new LinkedList<>();
								int l = r.indexOf('/');
								int m = r.indexOf("}}");
								int n = r.indexOf('|');
								int o = r.indexOf("&lt");
								if (l != -1)
									list.add(l);
								if (m != -1)
									list.add(m);
								if (n != -1)
									list.add(n);
								if (o != -1)
									list.add(o);
								int min = list.isEmpty() ? -1 : list.get(0);
								for (int i = 0; i < list.size(); i++) {
									min = Math.min(min, list.get(i));
								}
								int endIndex = min;
								if (endIndex != -1) {
									r = r.substring(0, endIndex);
								}
								if (r.charAt(r.length() - 1) == ' ')
									r = r.substring(0, r.length() - 1);

							
							} catch (java.lang.StringIndexOutOfBoundsException e) {
								//System.out.println("exception found : "+sCurrentLine);
								continue;
							}
							Matcher sujetsRetenus = subject.matcher(r);
							if(sujetsRetenus.matches() && !hasPrintedHypernomia){
								if(r.substring(1).equals("crivain")){
									mainEntity.hyperonome += "Ecrivain;";
								}
								if(r.substring(1).equals("ivre")){
									mainEntity.hyperonome += "Livre;";
								}
								hasPrintedHypernomia = true;
							}
							else if (r != null && r.length()>1){
								String hyper = (r.substring(0,1).toUpperCase() + r.substring(1)).replaceAll(" ", "_");
								while(hyper.charAt(hyper.length()-1) == '_' && hyper.length()>1) { //on supprime les '_' initiaux
									hyper = hyper.substring(1);
								}
								while(hyper.charAt(hyper.length()-1) == '_' && hyper.length()>1) { //on supprime les '_' finaux
									hyper = hyper.substring(0, hyper.lastIndexOf('_'));
								}
								if(hyper.length()>1)
									mainEntity.hyperonome += hyper+";";
							}
						}
					}
					Matcher ital = italique.matcher(sCurrentLine);
					if (ital.find() && querryLang && mainEntity != null && !hasPrintedItalic) {
						mainEntity.hyperonome += "Oeuvre;";
						hasPrintedItalic = true;
					}
				} else if (mainEntity != null) { 											//contenu
					Matcher matcherLink = link.matcher(sCurrentLine);
					int i = 1;
					while (matcherLink.find()) {
						//nbLiens += 1;
						countLinks = countLinks += 1;
						String brutLink = matcherLink.group();
						Entity linkedEntity = null;
						String articleName = brutLink.substring(2, brutLink.length() - 2);
						int indexSep = articleName.indexOf("|");
						if (indexSep != -1)
							articleName = articleName.substring(0, indexSep);
						if (articleName.length() > 0) {
							articleName = Crypter.normalise(articleName);
							linkedEntity = this.titleMap.get(lang, articleName);
							if (linkedEntity == null) {
								linkedEntity = this.redirectMap.get(lang, articleName);
								//System.out.println("redirect : "+articleName + "--->"+linkedEntity);
							}
							if (linkedEntity == null) {
								unParsedLinks = unParsedLinks += 1;
								linkExceptionOutput.println("\t"+articleName);
							}
							if (linkedEntity != null  && mainEntity!=linkedEntity) {
	
								/*if(mainEntity.getID() == test){
									System.out.println("at line "+brutLink+" found entity under title : "+linkedEntity.getTitle());
								}*/
								numberOfLinks = numberOfLinks += 1;
								try{
									toLinkEntity.add(linkedEntity);
								}
								catch(java.lang.NullPointerException lastException){//il semblerait que la toute dernière entitée provoque une exception
									/*if(br!=null){
										br.close();
									}*/
									numberOfFatalErrorsSkipped++;
									StringBuilder sb = new StringBuilder(40);
									sb.append("error at line : "+numLine+" of wikipedia ");
									if(lang!=null) {
										sb.append("lang : "+lang+"\r\n");
									}
									else  {
										sb.append("lang : null\r\n");
									}
									sb.append("mainEntity : "+mainEntity+" -> ");
									sb.append("linkedEntity : "+linkedEntity+"\n");
									if(mainEntity.linkedEntities!=null) {
										sb.append("mainEntity.linkedEntities : "+mainEntity.linkedEntities);
									}
									else {
										sb.append("mainEntity.linkedEntities : null");
									}
									logger.log(Level.INFO, sb.toString());
									//logger.log(Level.INFO, "reading line : "+numLine);
									continue;
								}
							}
						}
						i++;
						if(max<i){
							max = i;
						}
						Integer count = distribution.remove(i);
						if(count != null){
							count = count += 1;
							distribution.put(i, count);
							//System.out.println(i + " : " +count);
						}
						else{
							distribution.put(i, 1);
							//System.out.println(i + " : " +1);
						}
					}
				}
			}
			j++;
		}
		Integer[] distr = new Integer[max+1];
		Integer sum = new Integer("0");

		System.out.println("max : "+max);
		for(int i = 0; i < max; i++){
			distr[i] = new Integer("0");
		}
		for(Entry<Integer, Integer> e : distribution.entrySet()){
			distr[e.getKey()] = e.getValue();
			sum = sum += (e.getValue());
		}
		Integer k = new Integer("0");
		Integer demiSum = sum / (new Integer("2"));
		for(int i = 0; i < max; i++){
			System.out.println(i + " : " +distr[i]);
			k= k += (distr[i]);
			if(k.compareTo(demiSum)>0){
				System.out.println("mediane : "+i);
				break;
			}
		}
		printAllRelationsOfFinishedEntities(completedEntity, relationOutput);
		Statistics.numberOfPages.put(lang, numberOfPages);
		Statistics.numberOfOctets.put(lang, new BigDecimal(numberOfBytesInWikipedia).toBigInteger());
		Statistics.numberOfLinks.put(lang, numberOfLinks);
		Statistics.distributionOfFirstParagLinks.put(lang, distr);
		System.out.println("end wikipedia " + lang +", "+unParsedPages.toString()+" articles (redirect excluded) could not be matched to any wikidata Entity out of "+ (Statistics.numberOfPages.get(lang) - (Statistics.numberOfRedirect.get(lang))) );
		System.out.print(unParsedLinks.toString()+" links could not be matched ");
		System.out.println("out of : "+ countLinks +" links encountered. ");
	}

	
	private Map<String, Entity> parseRedirect(boolean print, LANG lang, double numberOfBytesInWikipedia, BufferedReader br, PrintWriter outRedirect, PrintWriter outLexicon) throws IOException {
		//Map<String, Entity> retour = new HashMap<>(lang.numberOfLinks);
		start = System.currentTimeMillis();
		Integer numberOfRedirect = new Integer("0");
		Integer numberOfUnParsedRedirect = new Integer("0");
		
		Pattern balisePage = Pattern.compile("[ ]*<page>[ ]*");
		Pattern baliseTitle = Pattern.compile("[ ]*<title>[ ]*");
		Pattern baliseRedirect = Pattern.compile("[ ]*<redirect[ ]*");
		Pattern metadata = Pattern.compile("^[ ]*[<|{=}]");
		Pattern separation = Pattern.compile("(^[ ]*==|[ ]*</page>[ ]*)");
		Pattern link = Pattern.compile("\\[\\[[^#%:\\.\\]]+\\]\\]");
		Pattern sentenceSeparator = Pattern.compile("[\\.\\?\\!]");

		String sCurrentLine;

		double j = 1;
		double oct = 0;
		boolean open = false;
		//boolean print = false;
		//String uri = null;
		String titleRedirect = "";

		while ((sCurrentLine = br.readLine()) != null) {
			
			// Check encoded sizes
			final byte[] utf8Bytes = sCurrentLine.getBytes("UTF-8");
			oct += utf8Bytes.length;
			if (j % step == 0) {
				long elapsedTimeMillis = System.currentTimeMillis() - start;
				// Get elapsed time in seconds
				float elapsedTimeSec = elapsedTimeMillis / 1000F;
				double remainingTime = (elapsedTimeSec * (numberOfBytesInWikipedia / factor) / (oct / factor)) - elapsedTimeSec;
				String linkNature = Entity.doubleLink ? "Double linked" : "OrientedLink";
				logger.log(Level.INFO, "Parser.redirect : \nLang  : " + lang + "\n" + ((int) (oct / factor) + "/" + (int) (numberOfBytesInWikipedia / factor)) + "\nspent time : "
						+ elapsedTimeSec + " seconds\nremaining time : " + remainingTime + " secondes" + "\n" + linkNature);
			}
			if (!open) {
				Matcher matcherBalisePage = balisePage.matcher(sCurrentLine);
				if (matcherBalisePage.find()) {
					open = true;
				}
			} else {
				Matcher matcherMetadata = metadata.matcher(sCurrentLine);
				if (matcherMetadata.find()) {
					Matcher matcherTitle = baliseTitle.matcher(sCurrentLine);
					if (matcherTitle.find()) {
						String brutTitle = sCurrentLine;
						brutTitle = brutTitle.substring(brutTitle.indexOf(">") + 1);
						brutTitle = brutTitle.substring(0, brutTitle.indexOf("<"));
						brutTitle = Crypter.normalise(brutTitle);
						titleRedirect = brutTitle;
					}
					Matcher matcherSepartaion = separation.matcher(sCurrentLine);
					if (matcherSepartaion.find()) {
						open = false;
					}
					Matcher matchRedirect = baliseRedirect.matcher(sCurrentLine);
					if (matchRedirect.find()) {
						numberOfRedirect = numberOfRedirect += 1;
						//System.out.println(sCurrentLine);
						try{
							String redirect = sCurrentLine.substring(sCurrentLine.indexOf('='), sCurrentLine.indexOf('>'));
							redirect = redirect.substring(2);
							redirect = redirect.substring(0, redirect.length()-3);
							redirect = redirect.replaceAll(" ", "_");
							redirect = Crypter.normalise(redirect);
							//System.out.println(redirect);
							Entity e = this.titleMap.get(lang, redirect);
							if (e != null) {
								this.redirectMap.addTitle(lang, titleRedirect, e, outRedirect, outLexicon);
								if (print) {
									e.printRedirect(lang, Parser.lastGivenLexiconId, outRedirect);
									e.printLexicon(Parser.lastGivenLexiconId, titleRedirect, outLexicon);
									Parser.lastGivenLexiconId = Parser.lastGivenLexiconId + (Parser.one);
								}

							} else {
								numberOfUnParsedRedirect = numberOfUnParsedRedirect += 1;
								/*StringBuilder sb = new StringBuilder(40);
								if (lang != null)
									sb.append("lang : " + lang + "\r\n");
								else
									sb.append("lang : null\r\n");
								if (titleRedirect != null)
									sb.append("titleRedirect : " + titleRedirect + "\r\n");
								else
									sb.append("titleRedirect : null\r\n");
								if (titleRedirect != null)
									sb.append("redirect : " + redirect + "\r\n");
								else
									sb.append("redirect : +null\r\n");
								if (lang != null)
									sb.append("e : " + e + "\r\n");
								else
									sb.append("e : null\r\n");
								logger.log(Level.INFO, sb.toString());*/
								continue;
							}

						}
						catch(java.lang.StringIndexOutOfBoundsException | URISyntaxException  e){
							System.out.println("Error while redirecting at the line : "+sCurrentLine);
							continue;
						}
					}
				}
			}
			j++;
		}
		System.out.println(numberOfUnParsedRedirect+" out of "+numberOfRedirect+" redirects were not correctly matched to en entity");
		Statistics.numberOfRedirect.put(lang, numberOfRedirect);
		return this.redirectMap.get(lang);
	}

	
	//----------------------PRINT-----------------------
	
	
	public String toString(){
		return this.titleMap.toString();
	}

	public static void initNodeFile(PrintWriter out) {
		out.print("nodeId:ID(Node-ID) title weight :Label\r\n");
	}

	public static void initRelationFile(PrintWriter out) {
		out.print(":START_ID(Node-ID) length lang :END_ID(Node-ID)\r\n");
	}

	public static void initLexiconFile(PrintWriter out) {
		out.print("lexiconId:ID(Lexicon-ID) title\r\n");
	}

	public static void initRedirectFile(PrintWriter out) {
		out.print(":START_ID(Lexicon-ID) lang :END_ID(Node-ID)\r\n");
	}
	
	public HashMap<Integer, Entity> toBePrintedNodes;
	private void removeFinishedNodes(PrintWriter nodesOutput, LANG lang) {
		//int nodesPrinted = toBePrintedNodes.size();
		LinkedList<Entity> finished = new LinkedList<Entity>();
		for(Entity e : this.entitySet.values()){
			//if(e.getID().equals(test)) System.out.println("printAllNodes : printed test node");
			if(e.completedLanguages >= e.weight){
				finished.add(e);
			}
		}
		for(Entity e : finished) {
			remove(this, e);
		}
		//System.out.println(nodesPrinted+" nodes where printed in "+lang);
		finishedLang.add(lang);
		this.titleMap.remove(lang);
		this.redirectMap.remove(lang);
		System.gc();
	}
	
	
	private void printAllUnprintedNodes(PrintWriter nodesOutput) {
		System.out.println("START printAllUnprintedNodes");
		ArrayList<Entity> toPrint = new ArrayList<Entity>();
		for(Entity e : this.toBePrintedNodes.values()){
			/*if(e.getID().equals(test)){
				System.out.println("WikiData (334), printed "+e+" at printAllUnprintedNodes");
			}*/
			toPrint.add(e);
		}
		for(Entity e : toPrint) {
			e.printNodeFile(this, nodesOutput);
			e.hyperonome = null;
		}
		System.out.println("END printAllUnprintedNodes");
	}
	
	
	/*
	 * TODO
	 * CF LE COMMENTAIRE A L'APPEL DE LA FONCTION
	 */
	/*
	//a utiliser avant printNodes (qui nullifie les hyperonomes après impression)
	private void printAllSynonyms(Map<Entity, LinkedList<String>> synonymsTable, PrintWriter articlesOut, PrintWriter oeuvresOut, PrintWriter livresOut, PrintWriter ecrivainsOut) {
		for(Entry<Entity, LinkedList<String>> entry : synonymsTable.entrySet()){
			Entity e = entry.getKey();
			LinkedList<String> synonyms = entry.getValue();
			String hyper = e==null?null:e.hyperonome;
			if(hyper != null){
				PrintWriter out = articlesOut;
				String mainTitle = '"'+e.getTitle()+'"';
				out.print(mainTitle);
				out.print(", "+mainTitle.replaceAll("_", " "));
				for(String str : synonyms){
					if(!str.equals(mainTitle)){
						out.print(", \""+str.replaceAll("_", " ")+"\"");
					}
				}
				out.println("");
			}
			if(hyper != null && hyper.contains("Oeuvre")){
				PrintWriter out = oeuvresOut;
				String mainTitle = '"'+e.getTitle()+'"';
				out.print(mainTitle);
				out.print(", "+mainTitle.replaceAll("_", " "));
				for(String str : synonyms){
					if(!str.equals(mainTitle)){
						out.print(", \""+str.replaceAll("_", " ")+"\"");
					}
				}
				out.println("");
			}
			if(hyper != null && hyper.contains("Livre")){
				PrintWriter out = livresOut;
				String mainTitle = '"'+e.getTitle()+'"';
				out.print(mainTitle);
				out.print(", "+mainTitle.replaceAll("_", " "));
				for(String str : synonyms){
					if(!str.equals(mainTitle)){
						out.print(", \""+str.replaceAll("_", " ")+"\"");
					}
				}
				out.println("");
			}
			if(hyper != null && hyper.contains("Ecrivain")){
				PrintWriter out = ecrivainsOut;
				String mainTitle = '"'+e.getTitle()+'"';
				out.print(mainTitle);
				out.print(", "+mainTitle.replaceAll("_", " "));
				for(String str : synonyms){
					if(!str.equals(mainTitle)){
						out.print(", \""+str.replaceAll("_", " ")+"\"");
					}
				}
				out.println("");
			}
		}
	}
	
	
	private Map<Entity, LinkedList<String>> constructSynonymsTable(){
		HashMap<Entity, LinkedList<String>> retour = new HashMap<>();
		for(Entry<String, Entity> entry : redirect.entrySet()){
			Entity e = entry.getValue();
			if(retour.containsKey(e)){
				retour.get(e).add(entry.getKey());
			}
			else{
				retour.put(e, new LinkedList<String>());
			}
		}
		return retour;
	}*/
	
	private static Integer printedRelationships = new Integer("0");
	private void printAllRelationsOfFinishedEntities(HashSet<Entity> finishedEntity, PrintWriter relsOutput) {
		for (Entity e : finishedEntity) { //pour chaque entitée
			if (e.completedLanguages >= e.getWeight() && e.linkedEntities != null) {
				try {
					e.printRelationFile(relsOutput);
					printedRelationships = printedRelationships += 1;
				} catch (Exception e1) {
					System.out.println("error in printAllRelationsOfFinishedEntities");
					remove(this, e);
					continue;
				}
				remove(this, e);	
			}
		}
		System.out.println(printedRelationships + " entities had their relationships correctly printed at the end of printAllRelationsOfFinishedEntities");
		printedRelationships = new Integer("0");
		System.gc();
	}
	
	private static Integer unPrintedRelationships = new Integer("0");
	private void printUnPrintedRelationships(PrintWriter outRels) {
		System.out.println("START printUnPrintedRelationships");
		LinkedList<Entity> toRemove = new LinkedList<>();
		System.out.println("\tpart stacking toRemove list");
		for(Entity e : this.entitySet.values()){ //pour chaque entitée
			if(e.linkedEntities != null){
				try {
					e.printRelationFile(outRels);
					toRemove.add(e);
				} catch (Exception e1) {
					System.out.println("error in printUnPrintedRelationships");
					toRemove.add(e);
					continue;
				}
				unPrintedRelationships = unPrintedRelationships += 1;
			}
			else{
				toRemove.add(e);
			}
		}
		System.out.println("\tremoving elements");
		while(!toRemove.isEmpty()) {
			toRemove.remove();
		}
		System.out.println(unPrintedRelationships + " entities had their relationships found unprinted at the end of unPrintedRelationships");
		System.out.println("END printUnPrintedRelationships");
	}

	static void remove(Parser p, Entity e) {
		p.entitySet.remove(e.getID());
		e.titles = null;
		e.hyperonome = null;
		e.linkedEntities = null;
		e = null;
	}
	
	//----------------------MAIN-----------------------
	
	public static void main(String[] args) throws MalformedURLException, URISyntaxException  {
	//System.out.println((int)Math.ceil(1*(4*0.75*1)));
		
			long startingTime = System.currentTimeMillis();
			Parser parser = new Parser(false);
			parser.parse();
			System.out.println("NumberOfFatalNullPointerExceptionsSkipped : "+numberOfFatalErrorsSkipped);
			
		/*
		boolean isArticle = true;
		String premiereBalise = "<https://en.wikipedia.org/wiki/Lola_T97/30>"; //<https://en.wikibooks.org/wiki/Wikijunior:Countries_A-Z/Israel> <https://fr.wikipedia.org/wiki/Aston_Martin_DB2/4> <https://en.wikipedia.org/wiki/Universe>
		String title = "";
		String uri = premiereBalise.substring(1, premiereBalise.length() - 1);
		LANG lang = LANG.extractLangFromUrl(uri);
		//On élimine les articles liés à d'autre projets que wikipedia (wiktionnaire, wikiquote etc...)
		String subwikiDomain = uri.substring(uri.indexOf('.') + 1);
		subwikiDomain = subwikiDomain.substring(0, subwikiDomain.indexOf('.'));
		//On élimine les non-articles (Modèles, Catégories etc...)
		title = uri;
		String categoryAndTitle = title;
		String category = "";
		//System.out.println("brut uri  : "+uri);
		for (int k = 0; k < 3; k++) {
			//System.out.println(k+" : "+langWikiCategory);
			categoryAndTitle = categoryAndTitle.substring(categoryAndTitle.indexOf('/') + 1);
		}
		category = categoryAndTitle.substring(0, categoryAndTitle.indexOf('/'));
		title = categoryAndTitle.substring(categoryAndTitle.indexOf('/') + 1);
		if (title.indexOf('/') != -1 && title.indexOf(':') != -1) { //arbitrairement on decide que c'est une condition suffisant pour dire que ce n'est pas un aticle valide : what should be the title is in fact still a category and a title
			isArticle = false;
			int index = title.indexOf('/');
			try {
				title = title.substring(index + 1);
			} catch (java.lang.StringIndexOutOfBoundsException bugexc) {
					System.out.println("fatal error : "+premiereBalise);

			}
		}
		if(title.indexOf('/') != -1 && title.indexOf(':') != -1) {
			isArticle = false;
			System.out.println("not an article : "+premiereBalise);
		}

		if(!category.equals("wiki")) isArticle = false;
		
		Crypter.urIToUrl(title);*/
	}
}