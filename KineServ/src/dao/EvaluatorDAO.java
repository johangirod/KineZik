package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import javax.sql.DataSource;

import evaluator.Evaluator;
import evaluator.EvaluatorSet;


public class EvaluatorDAO extends AbstractDataBaseDAO {

	public EvaluatorDAO(DataSource ds) {
		super(ds);
	}


	/**
	 * Sauvegarde une liste d'evaluateur
	 * @param evalSet : l'évaluateur à inserer
	 * @throws DAOException : si un des évaluateur (id) n'a pas pu être inséré
	 */
	public void saveEvaluators(EvaluatorSet evalSet) throws DAOException{
		Iterator<Evaluator> iteEval = evalSet.getIteEval();
		while (iteEval.hasNext()) {
			saveEvaluator(iteEval.next());
		}
	}
	

	/**
	 * Sauvegarde un evaluateur
	 * @param eval : l'évaluateur à inserer
	 * @throws DAOException : si l'évaluateur (id) n'a pas pu être inséré
	 */
	public void saveEvaluator(Evaluator eval) throws DAOException{
		ResultSet rs;
		Connection conn = null;
		try {

			conn = super.getConnection();
			java.sql.PreparedStatement pst = conn.prepareStatement("INSERT INTO Evaluator(Id, Name, BayesianTableObject)" +
					"VALUES(?, ?, ?);");

			pst.setInt(1, eval.getId());
			pst.setString(2, eval.getName());
			pst.setString(3, eval.getBayesTable().toJSONString());

			pst.execute();
		} catch (SQLException e) {
			throw new DAOException("Erreur BD : the evaluator" +eval.toString() + 
					"cannot be insert into BD ("+ e.getMessage() +")", e);
		} finally {
			closeConnection(conn);
		}
	}
}
