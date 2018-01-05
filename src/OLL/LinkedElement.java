package OLL;

public class LinkedElement<T extends Comparable> implements Linkable {
	
	public T elem;
	LinkedElement<T> next;

	public LinkedElement(T elem){
		this.elem = elem;
	}	
	
	//renvoie l'élément s'il y est,
	//renvoi null et l'ajoute sinon
	/**
	 * @param t la valeur du LinkedElement qu'on veut reccuperer s'il est contenu
	 * @param add le LinkedElement qu'on veut ajouter si la valeur n'a pas été trouvé
	 * @param head la tête de la liste chaînée
	 * @return
	 */
	public LinkedElement<T> getOrAdd(T t, LinkedElement<T> add, LinkedElement<T> head) {
		LinkedElement<T> temp = head;
		LinkedElement<T> prev = null;
		int i = 0;
		while(temp!= null && (i=temp.elem.compareTo(t)) <= 0) {
			if(i == 0) return temp;
			prev = temp;
			temp = temp.next;
		}
		prev.next 	= add;
		add.next 		= temp;
		return null;
	}	
	
	@Override
	public String toString() {
		return this.elem.toString();
	}

	@Override
	public boolean hasNext() {
		return this.next!=null;
	}

	@Override
	public Linkable next() {
		return (Linkable) this.next;
	}

	@Override
	@SuppressWarnings("unchecked")
	public int compareTo(Linkable o) {
		LinkedElement<T> realO = (LinkedElement<T>) o;
		return this.elem.compareTo(realO.elem);
	}
	
	public static void main(String[] args){
		LinkedElement<Integer> head = new LinkedElement<Integer>(2);
		head.getOrAdd(new Integer(4), new LinkedElement<Integer>(4), head);
		head.getOrAdd(new Integer(7), new LinkedElement<Integer>(7), head);
		head.getOrAdd(new Integer(3), new LinkedElement<Integer>(3), head);
	}
	
}
