package Wiki;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Explo {

	private static 	Logger logger 						= Logger.getLogger(Parser.class.getName());
	
	private static void printInfoBox(LANG lang) throws IOException {
		
		BufferedReader brWikpedia = null;
		String filename = lang.wikipediaFileName();
		double numberOfBytesInWikipedia = -1;
		File fileDir = new File(filename);
		try {
			//reading
			numberOfBytesInWikipedia = Statistics.countOctets(fileDir);	
			
		} catch (Exception e) {
			System.out.println("wrong file");
			try {
				if (brWikpedia != null)
					brWikpedia.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		brWikpedia = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
		
		
		System.out.println("begin wikipedia " + lang);
		
		long start = System.currentTimeMillis();
		//Crypter crypt = new Crypter();
		
		Pattern balisePage = Pattern.compile("[ ]*<page>[ ]*");
		Pattern baliseTitle = Pattern.compile("[ ]*<title>[ ]*");
		Pattern openInfo = Pattern.compile("\\{\\{Infobox");
		Pattern closeInfo = Pattern.compile("\\}\\}");
		Pattern metadata = Pattern.compile("^[ ]*[<|{=}]");
		Pattern separation = Pattern.compile("(^[ ]*==|[ ]*</page>[ ]*)");
		Pattern link = Pattern.compile("\\[\\[[^#%:\\.\\]]+\\]\\]");
		Pattern sentenceSeparator = Pattern.compile("[\\.\\?\\!]");

		String sCurrentLine;

		double j = 1;
		double oct = 0;
		Entity mainEntity = null;
		boolean open = false;
		boolean text = false;
		String title = "";
		int numberOfTitlePrint = 0;
		int numberOfLinePrint = 0;
		boolean articleFinished = false;
		//boolean print = false;
		//String uri = null;
		HashSet<String> infobox = new HashSet<String>(500);
		while ((sCurrentLine = brWikpedia.readLine()) != null) {
			//if (text && !articleFinished && numberOfLinePrint<1) System.out.println("\t\t"+sCurrentLine);
			// Check encoded sizes
			final byte[] utf8Bytes = sCurrentLine.getBytes("UTF-8");
			oct += utf8Bytes.length;
			if (j % Statistics.step == 0) {
				long elapsedTimeMillis = System.currentTimeMillis() - start;
				// Get elapsed time in seconds
				float elapsedTimeSec = elapsedTimeMillis / 1000F;
				double remainingTime = (elapsedTimeSec * (numberOfBytesInWikipedia / Statistics.factor) / (oct / Statistics.factor)) - elapsedTimeSec;
				String linkNature = Entity.doubleLink ? "Double linked" : "OrientedLink";
				logger.log(Level.INFO, "\nLang : " + lang + "\n" + ((int) (oct / Statistics.factor) + "/" + (int) (numberOfBytesInWikipedia / Statistics.factor)) + "\nspent time : "
						+ elapsedTimeSec + " seconds\nremaining time : " + remainingTime + " secondes" + "\n" + linkNature);
				//System.out.println("numberOfLinks : "+numberOfLinks);
			}
			if (!open) {
				Matcher matcherBalisePage = balisePage.matcher(sCurrentLine);
				if (matcherBalisePage.matches()) {
					open = true;
				}
			} else {
				Matcher matcherMetadata = metadata.matcher(sCurrentLine);
				if (matcherMetadata.find()) {
					Matcher matcherTitle = baliseTitle.matcher(sCurrentLine);
					if (matcherTitle.find()) {
						articleFinished = false;
						numberOfTitlePrint = 0;
						numberOfLinePrint = 0;
						title = "";
						String brutTitle = sCurrentLine;
						brutTitle = brutTitle.substring(brutTitle.indexOf(">") + 1);
						brutTitle = brutTitle.substring(0, brutTitle.indexOf("<"));
						brutTitle = Crypter.normalise(brutTitle);
						title = brutTitle;
					}
					Matcher matcherSepartaion = separation.matcher(sCurrentLine);
					if (matcherSepartaion.find()) {
						open = false;
						articleFinished = true;
					}
					Matcher openText = openInfo.matcher(sCurrentLine);
					if(openText.find() && !articleFinished){	
						if (numberOfLinePrint == 0){
							int index = sCurrentLine.indexOf("Infobox")+"Infobox".length();
							if(index>-1){
								try{
								String r = sCurrentLine.substring(index+1);
								LinkedList<Integer> list = new LinkedList<>();
								int l = r.indexOf('/');
								int m = r.indexOf("}}");
								int n = r.indexOf('|');
								int o = r.indexOf("&lt");
								if(l!=-1) list.add(l);
								if(m!=-1) list.add(m);
								if(n!=-1) list.add(n);
								if(o!=-1) list.add(o);
								int min = list.isEmpty()?-1:list.get(0);
								for(int i = 0; i<list.size(); i++){
									min = Math.min(min, list.get(i));
								}
								int endIndex = min;
								if(endIndex!=-1) {
									r = r.substring(0, endIndex);
								}
								if(r.charAt(r.length()-1)==' '){
									r=r.substring(0, r.length()-1);
								}
								infobox.add(r);
								}
								catch(java.lang.StringIndexOutOfBoundsException e){
									//System.out.println("exception found : "+sCurrentLine);
									continue;
								}
							}
							numberOfLinePrint++;
						}
						text = true;
					}
					Matcher closeText = closeInfo.matcher(sCurrentLine);
					/*if(closeText.find() && !articleFinished && numberOfLinePrint == 0){
						text = false;
						System.out.println("\t\t"+sCurrentLine);
					}*/
				}
			}
			j++;
		}
		
		for(String i : infobox){
			System.out.println(i);
		}
	}	

	/*
	public static void main(String[] args) throws IOException {
		printInfoBox(LANG.FR);
	}*/

}
