package structure;

public class Pair<S,T> {
	public S x;
	public T y;
	
	public Pair(S a, T b){
		x=a;
		y=b;
	}
	public String toString(){
		return x.toString()+"; "+y.toString();
	}
}
