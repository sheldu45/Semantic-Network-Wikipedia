package Wiki;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Une classe statique collectant deux fonctions statiques pour traduire reciproquement les URL en URI, ainsi qu'une fonction pour extraire le titre et pour le normaliser
 * l'URI est le format utilisé sur wikidata pour décrire un article
 * l'URL contient le titre de l'article wikipedia associé
 * 
 * @author Shervin Le Du
 */
public class Crypter {

	static String urIToUrl(String uristr) throws URISyntaxException, MalformedURLException {

		String result = null;
		String articleName = uristr;

		try {
			articleName = articleName.replaceAll("_", " ");
			result = URLDecoder.decode(articleName, "UTF-8");
			result = result.replaceAll("%20", "+");
			result = result.replaceAll("%21", "\\!");
			result = result.replaceAll("%27", "'");
			result = result.replaceAll("%28", "\\(");
			result = result.replaceAll("%29", "\\)");
			result = result.replaceAll("%7E", "~");
			result = result.replaceAll("%5C", "\\\\");
			result = result.replaceAll("%60", "`");
			result = result.replaceAll("%5E", "\\^");
		} catch (UnsupportedEncodingException e) {
			System.out.println("error decoding uri");
		}

		return result;
	}

	private static Pattern ponctuation = Pattern.compile("[ _,:'\"]");
	
	public static String normalise(String s) {
		s = s.replace("#039;", "'"); //on remet le guillemet simple avant pour qu'il soit pris en compte par l'expression regulière
		s = s.replace("&'", "'");
		StringBuilder 	sb = new StringBuilder(s.length());

		boolean upper = true;							//on capitalise la premiere lettre de chaque mot
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (upper) {
				sb.append((c + "").toUpperCase());
				upper = false;
			} else {
				sb.append(c);
			}
			Matcher m = ponctuation.matcher((c + ""));
			if (m.matches()) {
				upper = true;
			}
		}
		String retour = sb.toString();
		retour = retour.replace("\"", "\\\""); 			//on despecialise le guillemet double pour Neo4j
		retour = retour.replaceAll(" ", "_");			//on normalise l'espace
		return retour;
	}
	
	public static void main(String [] args) throws MalformedURLException, URISyntaxException{
		System.out.println(urIToUrl("https://pt.wikipedia.org/wiki/Universo"));
		
	}
}
