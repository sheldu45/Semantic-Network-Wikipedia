package OLL;

public abstract class ID implements Comparable<ID> {

	public abstract Integer getID();
	public int compareTo(ID c) {
		return c.getID() - this.getID();
	}
	
}
