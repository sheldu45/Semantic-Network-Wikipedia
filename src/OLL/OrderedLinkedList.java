package OLL;

import java.util.Iterator;

public class OrderedLinkedList<T extends Comparable> implements Iterable<Linkable>{

	public LinkedElement<T> head;
	
	public OrderedLinkedList() {
		
	}

	public boolean isEmpty() {
		return this.head==null;
	}

	//renvoie l'élément s'il y est,
	//renvoi null et l'ajoute sinon
	public LinkedElement<T> getOrAdd(T t, LinkedElement<T> e) {
		try{
			return e.getOrAdd(t, e, head);
		}
		catch(java.lang.NullPointerException premierElement){
			this.head = e;
		}
		return null;
	}
	
	public String toString() {
		String s = "[";
		LinkedElement<T> temp = this.head;
		while(temp!= null) {
			s+=temp.elem.toString()+",";
			temp = temp.next;
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

	@Override
	public Iterator<Linkable> iterator() {
		return this.head;
	}


}
