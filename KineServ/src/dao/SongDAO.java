package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import modele.Song;


public class SongDAO extends AbstractDataBaseDAO {

	public SongDAO(DataSource ds) {
		super(ds);
	}

	/**
	 * Met à jour la liste de chanson avec les évaluation et l'id de ces chansons. 
	 * @param ListSongs : la liste des chansons à mettre à jour. <b>Post-condition</b> : les 
	 * chansons non présentes dans la BD ont été supprimée de la liste
	 * @throws DAOException 
	 */
	public void setSongsEvaluation(ArrayList<Song> ListSongs) throws DAOException{
		Iterator<Song> iteSong = ListSongs.iterator();
		while(iteSong.hasNext()){
			setSongEvaluation(iteSong.next());
		}
	}
	/**
	 * Met à jour la chanson avec les évaluations et l'id contenues dans la base de donnée. 
	 * @param song : la chanson à mettre à jour
	 * @return vrai si la chanson est dans la base de donnée, faux sinon
	 * @throws DAOException 
	 */
	public boolean setSongEvaluation(Song song) throws DAOException{
		song.clearEvaluations();
		ResultSet rs = null;
		Connection conn = null;
		String requeteSQL = "";
		try {
			conn = super.getConnection();
			java.sql.PreparedStatement pst = conn.prepareStatement("select Id,Genre from AnalyzedSongs WHERE "+
					"(Artist = ? AND Album = ? AND Title = ?);");
			pst.setString(1, StringTransfo.escape(song.getArtist()));
			pst.setString(2, StringTransfo.escape(song.getAlbum()));
			pst.setString(3, StringTransfo.escape(song.getTitle()));
			rs = pst.executeQuery();
			//System.out.println("OK jusque là \n");
			if (!rs.next()) {
				// La chanson n'existe pas
				System.out.println("La chanson "+song.toString() + " n'existe pas dans la base de donnée");
				return false;
			} else {
				// On met à jour la chanson avec les évaluations et l'id
				song.setGenre(rs.getString("Genre"));
				song.setId(rs.getLong("Id"));
				requeteSQL = "select IdEval, Evaluation from Evaluations " +
						"WHERE (IdSong = " + rs.getLong("Id") + ");";
				rs = pst.executeQuery(requeteSQL);
				while (rs.next()) {
					song.addEvaluations(rs.getInt("IdEval"), rs.getFloat("Evaluation"));
				}

				return true;

			}
		} catch (SQLException e) {
			throw new DAOException("Erreur BD " + e.getMessage(), e);
		} finally {
			closeConnection(conn);
		}
	}

	/**
	 * Add the song to the table of userSong
	 * @param song : the song to add
	 * @param instal : the user where to add it
	 * @return : true if the song wasn't there, false else
	 * @throws DAOException 
	 */
	public boolean addUserSong(Song song, String UUID) throws DAOException{
		boolean songFound = false;
		Connection conn = null;
		if (UUID == null || song == null){
			throw new IllegalArgumentException();
		}
		try {
			conn = super.getConnection();
			java.sql.PreparedStatement pst = conn.prepareStatement("select * from UserSongs WHERE "+
					"(idSong= ? AND idUser= ?);");
			pst.setString(2, UUID);
			pst.setLong(1, song.getId());
			ResultSet rs = pst.executeQuery();
			if ( ! rs.next()) {
				// On met à jour la base de donné
				pst = conn.prepareStatement("INSERT INTO UserSongs VALUES (?, ?);");
				pst.setString(1, UUID);
				pst.setLong(2, song.getId());
				pst.execute();
				songFound = true;
			}
		} catch (SQLException e) {
			throw new DAOException("Erreur BD " + e.getMessage(), e);
		} finally {
			closeConnection(conn);
		}
		return songFound;
	}

	/**
	 * Retourne la chanson spécifiée grâce au titre, artiste et album.  
	 * @param artist Artiste de la chanson à retrouver
	 * @param album Album de la chanson à retrouver
	 * @param title Titre de la chanson à retrouver
	 * @return Une instance de la classe chanson, à partir des informations contenues dans la base de donnée.
	 * Si aucune chanson correspondante n'est trouvée, retourne <b>null</b>
	 */
	public Song getSong(String artist, String album, String title){
		return null;
	}


	/**
	 * Sauvegarde une chanson dans la base de donnée
	 * @param song : la chanson à inserer
	 * @throws DAOException : si cette chanson (couple artist, album, titre) est déjà présente
	 */
	public void saveSong(Song song) throws DAOException{
		ResultSet rs;
		Connection conn = null;
		String requeteSQL = "";
		try {

			conn = super.getConnection();
			Statement st = conn.createStatement();
			requeteSQL = "INSERT INTO AnalyzedSongs(Artist, Album, Title, Genre) VALUES(" +
					"'" + StringTransfo.escape(song.getArtist()) + "'," + 
					"'" + StringTransfo.escape(song.getAlbum()) + "'," + 
					"'" + StringTransfo.escape(song.getTitle()) + "'," + 
					"'" + StringTransfo.escape(song.getGenre()) + "')"; 

			st.execute(requeteSQL, Statement.RETURN_GENERATED_KEYS);
			rs = st.getGeneratedKeys();
			if (! rs.next()) {
				throw new DAOException("Aucune clé générée lors de l'insertion de la chanson");
			} else {
				song.setId(rs.getLong(1));
			}
			Iterator<Map.Entry<Integer, Float>> iteEval = song.evaluationIterator();
			while (iteEval.hasNext()){
				Map.Entry<Integer, Float> currentEval = iteEval.next();
				requeteSQL = "INSERT INTO Evaluations(IdEval, IdSong, Evaluation) VALUES (" +
						"'" + currentEval.getKey() + "'," +
						"'" + song.getId() + "'," +
						"'" + currentEval.getValue() + "');";
				System.out.println("INSERT INTO Evaluations(IdEval, IdSong, Evaluation) VALUES (" +
						"'" + currentEval.getKey() + "'," +
						"'" + song.getId() + "'," +
						"'" + currentEval.getValue() + "');");

				st.execute(requeteSQL);
			}

		} catch (SQLException e) {
			throw new DAOException("Erreur BD " + e.getMessage(), e);
		} finally {
			closeConnection(conn);
		}
	}













	/**
	 * Ajoute la liste de chanson dans la base de donnée
	 * @param ListSongs : liste de chanson à ajouter
	 * @throws DAOException 
	 */
	public void saveSongs(ArrayList<Song> ListSongs) throws DAOException{
		ResultSet rs;
		Connection conn = null;
		String requeteSQL = "";
		Iterator<Song> iteSong = ListSongs.iterator();
		try {
			while (iteSong.hasNext()){
				Song currentSong = iteSong.next();
				conn = super.getConnection();
				Statement st = conn.createStatement();
				requeteSQL = "INSERT INTO AnalyzedSongs(Artist, Album, Title) VALUES(" +
						"'" + currentSong.getArtist() + "'," + 
						"'" + currentSong.getAlbum() + "'," + 
						"'" + currentSong.getTitle() + "')"; 

				st.execute(requeteSQL, Statement.RETURN_GENERATED_KEYS);
				rs = st.getGeneratedKeys();
				if (! rs.next()) {
					throw new DAOException("Aucune clé générée lors de l'insertion de la chanson");
				} else {
					currentSong.setId(rs.getLong(1));
				}
				Iterator<Map.Entry<Integer, Float>> iteEval = currentSong.evaluationIterator();
				while (iteEval.hasNext()){
					Map.Entry<Integer, Float> currentEval = iteEval.next();
					requeteSQL = "INSERT INTO Evaluations(IdEval, IdSong, Evaluation) VALUES (" +
							"'" + currentEval.getKey() + "'," +
							"'" + currentSong.getId() + "'," +
							"'" + currentEval.getValue() + "');";
					System.out.println("INSERT INTO Evaluations(IdEval, IdSong, Evaluation) VALUES (" +
							"'" + currentEval.getKey() + "'," +
							"'" + currentSong.getId() + "'," +
							"'" + currentEval.getValue() + "');");

					st.execute(requeteSQL);
				}
			}
		} catch (SQLException e) {
			throw new DAOException("Erreur BD " + e.getMessage(), e);
		} finally {
			closeConnection(conn);
		}
	}


}
