package other;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import structure.*;

import javax.swing.JFileChooser;

import main.main;




public class GetDonnees {
	// download data from .csv files and convert then in double[][]

	public static BusLine[] getBusLines(String fil, Node[] nodes){
			// convert a array from a .csv file into a double[][]

			// find the length ot the file
			int count = 0;
			int n = 1;
			try {
				File fichier = new File(fil);
				FileReader lecteurDeFichier;
				BufferedReader buff;
				String ligne;
				lecteurDeFichier = new FileReader(fichier);
				buff = new BufferedReader(lecteurDeFichier);
				ligne = "";
				count = 0;
				while (count < n) {
					try {
						ligne = buff.readLine();
						java.util.StringTokenizer coupeur = new java.util.StringTokenizer(
								ligne, ";");
						n++;
						count++;
					} catch (NullPointerException e) {
						buff.close();
					}
				}
				buff.close();
			} catch (IOException err) {
			}
			// read the file

			BusLine[] result = new BusLine[n];
			try {
				/** Lecture de ce fichier */

				File fichier = new File(fil);
				FileReader lecteurDeFichier;
				BufferedReader buff;
				String ligne;
				lecteurDeFichier = new FileReader(fichier);
				buff = new BufferedReader(lecteurDeFichier);
				ligne = "";
				count = 0;

				while (count < n) {
					try {
						ligne = buff.readLine();
						// System.out.println(ligne);
						java.util.StringTokenizer coupeur = new java.util.StringTokenizer(
								ligne, ";");
						String str = coupeur.nextToken();
						int ligneID = Integer.valueOf(str).intValue();
						result[count]=new BusLine(ligneID);
						while(coupeur.hasMoreElements()){
							String str1 = coupeur.nextToken();
							String str2 = coupeur.nextToken();
							String str3 = coupeur.nextToken();
							int stopId = Integer.valueOf(str1).intValue();
							int pos = Integer.valueOf(str2).intValue();
							String time = str3;
							int nodeId=-1;
							for(int i=0; i<nodes.length; i++){
								if(nodes[i].idPrimary==stopId){
									nodeId=i;
								}
							}
							assert(nodeId!=-1);
							result[count].addStop(nodes[nodeId], time, pos);
						}
						count++;
					} catch (NullPointerException e) {
						buff.close();
						return result;
					}
				}// Fin while
				buff.close();
				/** Fermeture du buffer */
			}// fin try
			catch (IOException err) {
				JFileChooser fc = new JFileChooser(".");
				System.out.println("Erreur : " + err + fc);
			}// fin catch
			return result;
	}
	
	public static double[][] getTab(String fil) {
		// convert a array from a .csv file into a double[][]

		// find the length ot the file
		int count = 0;
		int n = 1;
		try {
			File fichier = new File(fil);
			FileReader lecteurDeFichier;
			BufferedReader buff;
			String ligne;
			lecteurDeFichier = new FileReader(fichier);
			buff = new BufferedReader(lecteurDeFichier);
			ligne = "";
			count = 0;
			while (count < n) {
				try {
					ligne = buff.readLine();
					java.util.StringTokenizer coupeur = new java.util.StringTokenizer(
							ligne, ";");
					n++;
					count++;
				} catch (NullPointerException e) {
					buff.close();
				}
			}
			buff.close();
		} catch (IOException err) {
		}

		// find the number of columns
		// find the length ot the file
		count = 0;
		int nbColumn = 1;
		try {
			File fichier = new File(fil);
			FileReader lecteurDeFichier;
			BufferedReader buff;
			String ligne;
			lecteurDeFichier = new FileReader(fichier);
			buff = new BufferedReader(lecteurDeFichier);
			ligne = "";
			count = 0;
			try {
				ligne = buff.readLine();
				java.util.StringTokenizer coupeur = new java.util.StringTokenizer(
						ligne, ";");
				while (count < nbColumn) {
					coupeur.nextToken();
					count++;
					nbColumn++;
				}
			} catch (Exception err) {
				buff.close();
			}
			buff.close();
		} catch (IOException err) {
		}
		nbColumn--;
		// read the file

		double[][] result = new double[n][nbColumn];
		try {
			/** Lecture de ce fichier */

			File fichier = new File(fil);
			FileReader lecteurDeFichier;
			BufferedReader buff;
			String ligne;
			lecteurDeFichier = new FileReader(fichier);
			buff = new BufferedReader(lecteurDeFichier);
			ligne = "";
			count = 0;

			while (count < n) {
				try {
					ligne = buff.readLine();
					// System.out.println(ligne);
					java.util.StringTokenizer coupeur = new java.util.StringTokenizer(
							ligne, ";");
					for (int i = 0; i < nbColumn; i++) {
						String s = coupeur.nextToken();
						result[count][i] = Double.valueOf(s)
								.doubleValue();
					}
					count++;
				} catch (NullPointerException e) {
					double[][] resultOptimal = new double[count][nbColumn];
					for (int i = 0; i < count; i++) {
						for (int j = 0; j < nbColumn; j++) {
							resultOptimal[i][j] = result[i][j];
						}
					}
					buff.close();
					return resultOptimal;
				}
			}// Fin while
			buff.close();
			/** Fermeture du buffer */
		}// fin try
		catch (IOException err) {
			JFileChooser fc = new JFileChooser(".");
			System.out.println("Erreur : " + err + fc);
		}// fin catch
		return result;
	}
	
	
}
