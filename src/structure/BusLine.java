package structure;

public class BusLine {
	public int id;
	public int idPrimary;
	public List<Node> stops;
	public List<String> stopTime;// in s from 00:00
	public List<Integer> position;// in s from 00:00
	public int period;
	
	public BusLine(){
		stops = new List<Node>();
		stopTime = new List<String>();
		position = new List<Integer>();
		id=-1;
		idPrimary = -1;
	}
	
	public BusLine(int idPrimary){
		stops = new List<Node>();
		stopTime = new List<String>();
		position = new List<Integer>();
		id=-1;
		this.idPrimary = idPrimary;
	}
	
	public void addStop(Node n, String t, int pos){
		stops.add(n);
		stopTime.add(t);
		position.add(pos);
	}
	
	public boolean followSamePath(BusLine bus){
		boolean res = true;
		Browse<Node> b = stops.browse();
		Browse<Node> b2 = bus.stops.browse();
		while(b.hasNext() && b2.hasNext()){
			Node n1 = b.next();
			Node n2 = b2.next();
			res = res && n1== n2;
		}
		res = res && !b.hasNext() && !b2.hasNext();
		return res;
	}
	
	public String toString(){
		Browse<Node> b =stops.browse();
		String s = "bus line #"+id+" (idPrimary: "+idPrimary+") period: "+period+" travel: \n"+b.next().id;
		while(b.hasNext()){
			s+="->"+b.next().id;
		}
		return s;
	}
}
