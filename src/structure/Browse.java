package structure;

public class Browse <T> extends List<T>{
	
	public boolean hasNext(){
		return l!=null;
	}
	
	public T next(){
		assert l!=null;
		T res =l.node;
		l=l.next;
		return res;
	}
	
	public Browse(List<T> l){
		this.l=l.l;
	}
	
	public Browse(){
		l=null;
	}
	
	
}
