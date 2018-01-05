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
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class Clean {
/*
	private static String pathRels() {
		if (Datas.SERVEUR >= 3)
			return "/data/shervin/Rel.csv";
		else if (Datas.SERVEUR == 2)
			return "D:/BNF/workspace-neon/WikiParse/Rel.csv";
		else
			return "D:/BNF/workspace-neon/WikiParse/testRel.csv";
	}

	private static String pathNewRels() {
		if (Datas.SERVEUR >= 3)
			return "/data/shervin/NewRel.csv";
		else if (Datas.SERVEUR == 2)
			return "D:/BNF/workspace-neon/WikiParse/NewRel.csv";
		else
			return "D:/BNF/workspace-neon/WikiParse/testNewRel.csv";
	}

	private static String pathNodesCSV() {
		if (Datas.SERVEUR >= 3)
			return "/data/shervin/Nodes.csv";
		else if (Datas.SERVEUR == 2)
			return "D:/BNF/workspace-neon/WikiParse/Nodes.csv";
		else
			return "D:/BNF/workspace-neon/WikiParse/testNodes.csv";
	}

	private static String pathNewNodes() {
		if (Datas.SERVEUR >= 3)
			return "/data/shervin/NewNodes.csv";
		else if (Datas.SERVEUR == 2)
			return "D:/BNF/workspace-neon/WikiParse/NewNodes.csv";
		else
			return "D:/BNF/workspace-neon/WikiParse/testNewNodes.csv";
	}

	private static String pathLexicon() {
		if (Datas.SERVEUR >= 3)
			return "/data/shervin/Lexicon.csv";
		else if (Datas.SERVEUR == 2)
			return "D:/BNF/workspace-neon/WikiParse/Lexicon.csv";
		else
			return "D:/BNF/workspace-neon/WikiParse/testLexicon.csv";
	}
	
	private static String pathNewLexicon() {
		if (Datas.SERVEUR >= 3)
			return "/data/shervin/NewLexicon.csv";
		else if (Datas.SERVEUR == 2)
			return "D:/BNF/workspace-neon/WikiParse/NewLexicon.csv";
		else
			return "D:/BNF/workspace-neon/WikiParse/testNewLexicon.csv";
	}

	private static String pathRedirect() {
		if (Datas.SERVEUR >= 3)
			return "/data/shervin/Redirect.csv";
		else if (Datas.SERVEUR == 2)
			return "D:/BNF/workspace-neon/WikiParse/Redirect.csv";
		else
			return "D:/BNF/workspace-neon/WikiParse/testRedirect.csv";
	}

	private static String pathNewRedirect() {
		if (Datas.SERVEUR >= 3)
			return "/data/shervin/NewRedirect.csv";
		else if (Datas.SERVEUR == 2)
			return "D:/BNF/workspace-neon/WikiParse/NewRedirect.csv";
		else
			return "D:/BNF/workspace-neon/WikiParse/testNewRedirect.csv";
	}

	public static void initNodeFile(PrintWriter out) {
		out.print("nodeId:ID(Node-ID) title weight\r\n");
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

	public static void main(String[] args) {
		
		System.out.println(("Salut le ch \" d'\"ely").replaceAll("\"", "\\\\\""));
		
		//Reading Nodes
		BufferedReader brNodes = null;

		//Reading Rels
		BufferedReader brRels = null;

		//Reading Redirect
		BufferedReader brRedirect = null;

		//Reading Lexicon
		BufferedReader brLexicon = null;
		
		//Writing NewNodes
		BufferedWriter bwNewNodes = null;
		PrintWriter outNewNodes = null;

		//Writing NewRels
		BufferedWriter bwNewRels = null;
		PrintWriter outNewRels = null;

		//Writing NewRedirect
		BufferedWriter bwNewRedirects = null;
		PrintWriter outNewRedirect = null;

		//Writing NewLexicon
		BufferedWriter bwNewLexicon = null;
		PrintWriter outNewLexicon = null;

		try {
			File nodes = new File(Clean.pathNodesCSV());
			brNodes = new BufferedReader(new InputStreamReader(new FileInputStream(nodes), StandardCharsets.UTF_8));

			File redirect = new File(Clean.pathRedirect());
			brRedirect = new BufferedReader(new InputStreamReader(new FileInputStream(redirect), StandardCharsets.UTF_8));

			File rels = new File(Clean.pathRels());
			brRels = new BufferedReader(new InputStreamReader(new FileInputStream(rels), StandardCharsets.UTF_8));

			File lexicon = new File(Clean.pathLexicon());
			brLexicon = new  BufferedReader(new InputStreamReader(new FileInputStream(lexicon), StandardCharsets.UTF_8));
			
			bwNewNodes = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathNewNodes()), StandardCharsets.UTF_8));
			outNewNodes = new PrintWriter(bwNewNodes);

			bwNewRels = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathNewRels()), StandardCharsets.UTF_8));
			outNewRels = new PrintWriter(bwNewRels);

			bwNewRedirects = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathNewRedirect()), StandardCharsets.UTF_8));
			outNewRedirect = new PrintWriter(bwNewRedirects);

			bwNewLexicon = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathNewLexicon()), StandardCharsets.UTF_8));
			outNewLexicon = new PrintWriter(bwNewLexicon);
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		initRedirectFile(outNewRedirect);
		initLexiconFile(outNewLexicon);
		initNodeFile(outNewNodes);
		initRelationFile(outNewRels);

		BigInteger countRedirect 	= new BigInteger("0");
		BigInteger one 				= new BigInteger("1");
		
		try { //on lit le fichier [Nodes] pour integrer les titres non-untitled comme Lexicon et comme NewRedirection vers ce lien et aussi réécrire les Nodes dans NewNodes
			String sCurrentLine = brNodes.readLine();
			int i = 1;
			while ((sCurrentLine = brNodes.readLine()) != null) {
				i++;
				try {
					outNewNodes.println(sCurrentLine.replaceAll("\"", "\\\""));
				} catch (java.lang.ArrayIndexOutOfBoundsException exc) {
					System.out.println("erreur : " + sCurrentLine);
			}
			}
			System.out.println("Nodes : " + i);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try { //on lit le fichier [Redirection] pour integrer les termes comme Lexicon et recopier comme NewRedirection
			String sCurrentLine = brRedirect.readLine();
			int i = 1;
			while ((sCurrentLine = brRedirect.readLine()) != null) {
				i++;
				outNewRedirect.println(sCurrentLine.replaceAll("\"", "\\\""));
				
			}
			System.out.println("Redirection : " + i);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try { //on lit le fichier [Rel.csv] changer l'ordre
			String sCurrentLine = brRels.readLine();
			int i = 1;
			while ((sCurrentLine = brRels.readLine()) != null) {
				i++;
				try {
					outNewRels.println(sCurrentLine.replaceAll("\"", "\\\""));
				} catch (java.lang.ArrayIndexOutOfBoundsException exc) {
					System.out.println("erreur : " + sCurrentLine);
				}
			}
			System.out.println("Rel : "+i);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try { //on lit le fichier [Rel.csv] changer l'ordre
			String sCurrentLine = brRels.readLine();
			int i = 1;
			while ((sCurrentLine = brRels.readLine()) != null) {
				i++;
				try {
					outNewRels.println(sCurrentLine.replaceAll("\"", "\\\""));
				} catch (java.lang.ArrayIndexOutOfBoundsException exc) {
					System.out.println("erreur : " + sCurrentLine);
				}
			}
			System.out.println("Rel : "+i);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try { //on lit le fichier [Lexicon.csv] changer l'ordre
			String sCurrentLine = brLexicon.readLine();
			int i = 1;
			while ((sCurrentLine = brLexicon.readLine()) != null) {
				i++;
				try {
					outNewLexicon.println(sCurrentLine.replaceAll("\"", "\\\\\""));
				} catch (java.lang.ArrayIndexOutOfBoundsException exc) {
					System.out.println("erreur : " + sCurrentLine);
				}
			}
			System.out.println("Rel : "+i);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		//Closing readers and writers
				try {
					if (brNodes != null)
						brNodes.close();
					if (brRels != null)
						brRels.close();
					if (brRedirect != null)
						brRedirect.close();
					if (brLexicon != null)
						brLexicon.close();
					if (bwNewNodes != null)
						bwNewNodes.close();
					if (outNewNodes != null)
						outNewNodes.close();
					if (bwNewRels != null)
						bwNewRels.close();
					if (outNewRels != null)
						outNewRels.close();
					if (bwNewRedirects != null)
						bwNewRedirects.close();
					if (outNewRedirect != null)
						outNewRedirect.close();
					if (bwNewLexicon != null)
						bwNewLexicon.close();
					if (outNewLexicon != null)
						outNewLexicon.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}*/
/*
	private static String pathSpeech() {
		if (Datas.SERVEUR >= 3)
			return "/data/shervin/SAVE/Speech/";
		return "";
	}
	
	static String csvFileName = "Ecrivains";
	
	public static void main(String[] args) {
		
		
		BigInteger one 				= new BigInteger("1");
		
		try { //on lit le fichier [Articles] pour subdiviser en petits fichiers et enlever les parenthèses
			//Reading Article
			BufferedReader brArticle = null;
			File articles = new File(Clean.pathSpeech()+csvFileName+".csv");
			brArticle = new BufferedReader(new InputStreamReader(new FileInputStream(articles), StandardCharsets.UTF_8));
		
			
			
			String sCurrentLine = brArticle.readLine();

			
			while ((sCurrentLine = brArticle.readLine()) != null) {
				try {
					int countApostrophe = 0;
					boolean write = true;
					boolean firstWord=true;
					StringBuilder sb = new StringBuilder(sCurrentLine.length());
					int j = 0;
					while(j<sCurrentLine.length()){
						char c = sCurrentLine.charAt(j);
						if(j<sCurrentLine.length()-1){
							char cPlusUn = sCurrentLine.charAt(j+1);
							if(cPlusUn=='(' && !firstWord){
								write = false;
							}
						}
					    if(c=='(' && !firstWord){
					    	write = false;
					    }
					    if(write == true) sb.append(c);
					    if(c==')' && !firstWord){
					    	write = true;
					    }
					    if(c == '"'){
					    	countApostrophe++;
					    	firstWord = false;
					    }
					    if(j>0 && firstWord && c == '"'){
					    	firstWord = false;
					    }

					    j++;
					}
					System.out.println(sb.toString());
				} catch (java.lang.ArrayIndexOutOfBoundsException exc) {
					System.out.println("erreur : " + sCurrentLine);
				}	
			}
			
			if (brArticle != null)
				brArticle.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
			*/
			
			
			/*while ((sCurrentLine = brArticle.readLine()) != null) {
				if(i%100 == 0){
					numberOfFiles++;
					if (bwWriter != null)
						bwWriter.close();
					if (outWriter != null)
						outWriter.close();
					try {
						bwWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathSpeech()+"Clustered/"+csvFileName+numberOfFiles+".csv"), StandardCharsets.UTF_8));
						System.out.println("numberOfFiles : "+numberOfFiles);
						outWriter = new PrintWriter(bwWriter);
						
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				i++;
				try {
					boolean write = true;
					boolean firstWord=true;
					StringBuilder sb = new StringBuilder(sCurrentLine.length());
					int j = 0;
					while(j<sCurrentLine.length()){
						char c = sCurrentLine.charAt(j);  
					    if(c=='(' && !firstWord){
					    	write = false;
					    }
					    if(write == true) sb.append(c);
					    if(c==')' && !firstWord){
					    	write = true;
					    }
					    if(j>0 && firstWord && c == '"'){
					    	firstWord = false;
					    }
					    j++;
					}
					outWriter.println(sb);
				} catch (java.lang.ArrayIndexOutOfBoundsException exc) {
					System.out.println("erreur : " + sCurrentLine);
				}
				
			}
			if (brArticle != null)
				brArticle.close();
			System.out.println("Articles finished ! " + numberOfFiles +" files created");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}*/
	
}
