package Wiki;

/**
 * 
 * Enumeration permettant de se référer aux differentes langues retenues de wikipedia.
 * 
 * @author Shervin Le Du
 *
 */

public enum LANG {
	
	/*EN(0, "en", 6585000),
	FR(1, "fr", 2101000);*/
	
	/*DE(0,  "de", 2230000),
	FR(1, "fr",  2101000),
	IT(2,  "it", 1574000),
	ES(3,  "es", 1516000),
	ZH(4,  "zh", 1098000),
	EN(5, "en",  6585000);*/
	
	/*FR(0, "fr",  2101000),
	EL(1, "el", 158000),
	HI(2, "hi", 125000),
	AR(3, "ar", 634000);
	*/
	/*
	EN(0, "en", 6585000),
	FR(1, "fr", 2101000),
	ES(2, "es", 1516000),
	IT(3, "it", 1574000),
	DE(4, "de", 2230000);*/
	
	
	//Objets directement construits
	DE(0,  "de", 2230000),
	FR(1, "fr",  2101000),
	IT(2,  "it", 1574000),
	ES(3,  "es", 1516000),
	ZH(4,  "zh", 1098000),
	JA(5,  "ja", 1210000),
	NL(6,  "nl", 1982000),
	PL(7,  "pl", 1317000),
	PT(8,  "pt", 1135000),
	RU(9,  "ru", 1653000),
	FA(10, "fa", 466000),
	FI(11, "fi", 466000),
	HE(12, "he", 233000),
	HI(13, "hi", 125000),
	HR(14, "hr", 170000),
	HU(15, "hu", 439000),
	BG(16, "bg", 270000),
	DA(17, "da", 278000),
	KO(18, "ko", 534000),
	EL(19, "el", 158000),
	NO(20, "no", 545000),
	AR(21, "ar", 634000),
	CS(22, "cs", 473000),
	ET(23, "et", 167000),
	SL(24, "sl", 201000),
	TH(25, "th", 148000),
	TR(26, "tr", 402000),
	UK(27, "uk", 756000),
	EN(28, "en", 6585000);

	
	final static String POSTFIXE = "wiki-20170820-pages-articles.xml";
	
	private String ECHANTILLONS(){
		return "/home/paprika/Documents/wikipedia/";
	}

	
	private int index;
	private String name;
	public int numberOfLinks;

	//Constructeur
	LANG(int index, String name, int numberOfLinks) {
		this.setIndex(index);
		this.setName(name);
		this.numberOfLinks = numberOfLinks;
	}

	public int getIndex() {
		return index;
	}

	private void setIndex(int index) {
		this.index = index;
	}

	private void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public String wikipediaFileName(){
		return ECHANTILLONS()+this.toString()+POSTFIXE;
	}

	public static boolean isLang(String str){
		for(LANG lg : LANG.values()){
			if(lg.toString().equals(str)){
				return true;
			}
		}
		return false;
	}
	
	public static LANG extractLangFromUrl(String url){
		String langExtraite 		= url.substring(0, url.indexOf('.'));
		String name 				= langExtraite.substring(langExtraite.length()-2);
		for(LANG lg : LANG.values()){
			if(lg.toString().equals(name)){
				return lg;
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		/*LANG l1 = LANG.FR;
		LANG l2 = LANG.EN;
		System.out.println(l1 + " " + l2);*/
	}



}