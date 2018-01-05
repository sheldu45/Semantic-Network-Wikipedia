package OLL;

import java.util.ArrayList;
import java.util.Iterator;

public class SemiOrderedHashMap<T extends ID> extends ArrayList<OrderedLinkedList<T>> {
	
	@SuppressWarnings("unchecked")
	public SemiOrderedHashMap(int n) {
		super(n);
		for(int i = 0; i < n; i++) {
			this.add(new OrderedLinkedList<T>());
		}
	}
	
	public boolean isEmpty() {
		boolean b = true;
		for(OrderedLinkedList<T> list : this) {
			if(!list.isEmpty()) b = false;
		}
		return b;
	}

	//renvoie l'élément s'il y est,
	//renvoi null et l'ajoute sinon
	public LinkedElement<T> getOrAdd(T t, LinkedElement<T> e) {
		Integer id = t.getID();
		int 	pos = id%this.size();
		return 	this.get(pos).getOrAdd(t, e);
	}
	
	public String toString() {
		String s = "[";
		for(OrderedLinkedList<T> list : this) {
			s += list.toString();
		}
		s+="]";
		return s;
	}

	public static void main(String[] args){
		OrderedLinkedList<Integer> list = new OrderedLinkedList<Integer>();
		list.getOrAdd(new Integer(2), new LinkedElement<Integer>(2));
		list.getOrAdd(new Integer(4), new LinkedElement<Integer>(4));
		list.getOrAdd(new Integer(7), new LinkedElement<Integer>(7));
		list.getOrAdd(new Integer(3), new LinkedElement<Integer>(3));

		System.out.println(list);
	}
	
	
}
