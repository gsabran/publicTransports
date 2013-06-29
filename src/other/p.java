package other;

import structure.*;

public class p {
	public static void w(String s){
		System.out.println(s);
	}
	public static void w(int s){
		System.out.println(s);
	}
	public static void w(double s){
		System.out.println(s);
	}
	public static void w(Node s){
		System.out.println(s);
	}
	public static void w(Edge s){
		System.out.println(s);
	}
	
	public static void w(List l){
		System.out.println(l);
	}
	
	public static void w(Edge[] t) {
		System.out.print("Array of edges :");
		for (int i = 0; i < t.length; i++) {
			System.out.println(t[i]);
		}
	}
	
	public static void w(boolean[] t) {
		System.out.print("Array of boolean :");
		for (int i = 0; i < t.length; i++) {
			System.out.println(t[i]? 1:0);
		}
	}

	public static void w(Node[] t) {
		System.out.println("Array of nodes :");
		for (int i = 0; i < t.length; i++) {
			System.out.println(t[i]);
		}
	}

	public static void w(double[][] t) {
		System.out.println("Matrix :");
		for (int i = 0; i < t[0].length; i++) {
			System.out.println();
			System.out.print(i+": ");
			for (int j = 0; j < t.length; j++) {
				System.out.print(t[j][i] + " ");
			}
		}
		System.out.println();
	}
	
	public static void w(int[][] t) {
		System.out.println("Matrix :");
		for (int i = 0; i < t[0].length; i++) {
			System.out.println();
			System.out.print(i+": ");
			for (int j = 0; j < t.length; j++) {
				System.out.print(t[j][i] + " ");
			}
		}
		System.out.println();
	}

	public static void w(double[] t) {
		System.out.println("array :");
		for (int i = 0; i < t.length; i++) {
			System.out.println(i+": "+t[i] + " ");
		}
		System.out.println();
	}
	
	public static boolean w(double[] t,double[] t2) {
		System.out.println("array :");
		boolean res=true;
		for (int i = 0; i < t.length; i++) {
			res=res&&t[i]==t2[i];
			if(t[i]!=t2[i]){
				p.w(i);
			}
			System.out.println(i+": "+t[i] + " "+t2[i] + " ");
		}
		System.out.println();
		return res;
	}
	
	public static void w(String[] s , double[] when, boolean[] b){
		for(int i=0; i<s.length; i++){
			if(!b[i]){
				p.w("thread "+i+" has been doing: "+s[i]+" for "+(int)((System.currentTimeMillis()-when[i])/1000)+"s");
			}
		}
	}
	
	public static void w(int[] t) {
		System.out.println("array :");
		for (int i = 0; i < t.length; i++) {
			System.out.print(t[i] + " ");
		}
		System.out.println();
	}
}
