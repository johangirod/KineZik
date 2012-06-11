package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import javax.sql.DataSource;

import modele.Feedback;

public class FeedbackDAO extends AbstractDataBaseDAO {

	public FeedbackDAO(DataSource ds) {
		super(ds);
	}

	/**
	 * 
	 * @param feedback
	 * @throws DAOException 
	 */
	public void saveFeedback(Feedback feedback, String UUID) throws DAOException{
		//TODO : the method
		ResultSet rs;
		Connection conn = null;
		int id = -1;
		try {

			//Insertion in the table Feedback
			conn = super.getConnection();

			java.sql.PreparedStatement pst = conn.prepareStatement("SELECT Id FROM AnalyzedSongs WHERE (Id = "+
					feedback.getIdSong() + ");");
			rs = pst.executeQuery();
			if (!rs.next()){
				return;
			}
			pst = conn.prepareStatement("INSERT INTO Feedback " +
					"(idUser, idSong, actionTime, feedbackType, desc1, desc2, desc3) " +
					"VALUES(?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, UUID);
			pst.setLong(2, feedback.getIdSong());
			pst.setLong(3, feedback.getTime());
			pst.setInt(4, feedback.getAction());
			pst.setFloat(5, feedback.getDesc1());
			pst.setFloat(6, feedback.getDesc2());
			pst.setFloat(7, feedback.getDesc3());
			pst.execute();
			//st.execute(requeteSQL, Statement.RETURN_GENERATED_KEYS);
			rs = pst.getGeneratedKeys();
			if (! rs.next()) {
				throw new DAOException("Aucune clé générée lors de l'insertion du feedback");
			} else {
				id = rs.getInt(1);
			}

			//Insertion into FeedbackResult
			Iterator<Integer> iteEvaluator = feedback.iteratorEval();
			while (iteEvaluator.hasNext()) {
				pst = conn.prepareStatement("INSERT INTO EvaluatorFeedback()" +
						"VALUES(?, ?);");
				pst.setInt(1, id);
				pst.setInt(2, iteEvaluator.next());
				pst.execute();
			}
		} catch (SQLException e) {
			throw new DAOException("Erreur BD : The feedback cannot be saved "+e.getMessage(), e);
		} finally {
			closeConnection(conn);
		}
	}

}
