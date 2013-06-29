package structure;

import other.Writer;

public class Node {
	public double lat;
	public double lon;
	public int id;
	public int idPrimary;
	public int lineID;//-1 if it is a station node

	public boolean equals(Node n) {
		boolean res = id == n.id;
		//res = res && lat == n.lat && lon == n.lon && id == n.id;
		return res;
	}

	public String toString() {
		String s = "Node :";
		s += "\nNode #" + id + " lat : " + lat + ", lon : " + lon;
		s += "\nid1 : " + idPrimary+" line ID: "+lineID;
		return s;
	}
	
	public Node clone() {
		Node n= new Node(idPrimary, lat,lon);
		n.id=id;
		n.lineID=lineID;
		return n;
	}

	public Node() {
		id = -1;
		idPrimary = -1;
		lat = 0;
		lon = 0;
		lineID=-1;
	}

	public Node(double la, double lo) {
		id = -1;
		idPrimary = -1;
		lat = la;
		lon = lo;
		lineID=-1;
	}

	public Node(int idPrimary, double la, double lo) {
		this.idPrimary = idPrimary;
		id = -1;
		lat = la;
		lon = lo;
		lineID=-1;
	}
	public Node(int id, double la, double lo, int idP, int line) {
		this.idPrimary = idP;
		this.id = id;
		lat = la;
		lon = lo;
		lineID=line;
	}

	public void toText(String fileName) {
		String s = id + ";"  + lat + ";"
				+ lon+";"+idPrimary+";"+lineID+";";

		s += "\n";
		Writer.WriteFile(s, fileName);
	}

	public static Node fromText(double[] t) {
		int id = (int) t[0];
		double lat = t[1];
		double lon = t[2];
		int idPrimary = (int) t[3];
		int lineID = (int) t[4];
		Node n = new Node(id, lat, lon, idPrimary, lineID);
		return n;
	}
}
