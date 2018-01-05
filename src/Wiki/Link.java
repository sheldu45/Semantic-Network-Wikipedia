package Wiki;

import java.util.HashSet;
import java.util.Set;

import OLL.LinkedElement;

/**
 * 
 * Données relatives aux liens entre les entitées (pondération, liste des langues où le lien a été observé etc...)
 * 
 * @author Shervin Le Du
 *
 */

public class Link extends LinkedElement<Entity>{

	public byte 			value;
	public Set<LANG>		langages;
	
	public Link(byte value, Entity	cible){
		super(cible);
		this.value = value;
		langages = new HashSet<>(LANG.values().length);
	}
	
	public String toString(){
		return "--"+value+"--"+"["+langages.toString()+"]-->";
	}

}
