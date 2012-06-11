package dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

/**
 * Classe pour initialiser la Base de donnée.
 * @author johan
 *
 */
public class InitBaseDAO extends AbstractDataBaseDAO{

	String initFile = "";
	
	/**
	 * Construit la classe d'initialisation de la BD
	 * @param ds : DataSource pour la connection
	 * @param initFile : Chemin absolu du script SQL d'initialisation
	 */
	public InitBaseDAO(DataSource ds, String initFile) {
		super(ds);
		this.initFile = initFile; 
	}
	
	/**
	 * Transforme un fichier texte en String
	 * @param filePath : le path du fichier à transformer
	 * @return la chaine correspondant à ce fichier
	 * @throws java.io.IOException
	 */
	private static String readFileAsString(String filePath)
	throws java.io.IOException{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(
		new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
		String readData = String.valueOf(buf, 0, numRead);
		fileData.append(readData);
		buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
		}
	
	/**
	 * Cree les tables dans la base de donnée. Ajoute des évaluateurs dans ces tables
	 * @param nbEvaluator : nombre d'évaluateurs à ajouter
	 * @throws DAOException
	 */
	public void InitAll() throws DAOException{
		Connection conn = null;
		String requeteSQL;
		try {
			conn = super.getConnection();
			Statement st = conn.createStatement();
			
			// Initialise la BD, avec toutes les tables vides
			requeteSQL = readFileAsString(initFile);
			//System.out.println(requeteSQL);
			String[] requetes = requeteSQL.split(";");
			for(int i =0 ; i<requetes.length - 1; i++){
					st.execute(requetes[i]+";");
				
			}

		} catch (SQLException e) {
			throw new DAOException("Erreur BD " + e.getMessage(), e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new DAOException("Erreur IO " + e.getMessage(), e);
		} finally {
			closeConnection(conn);
		}
	}
	
}
