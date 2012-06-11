package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.sql.DataSource;

import modele.BayesianTable;

import org.json.JSONException;


/**
 * classe permettant de lier la classe BayesianTable à son implémentation dans la base de donnée. 
 * @author johan
 *
 */
public class BayesianTableDAO extends AbstractDataBaseDAO{

	/**
	 * Construit la DAO sur la dataSource passé en paramètre
	 * @param ds
	 */
	public BayesianTableDAO(DataSource ds) {
		super(ds);
	}

	/**
	 * Récupère toutes les tables bayesiennes de correspondance entre descripteur et évaluateur
	 * @return Une hashMap de toutes les tables bayesiennes, indexée par l'id de l'évaluateur qu'elles décrivent.
	 * @throws DAOException
	 */
	public HashMap<Integer,BayesianTable> GetAllEvaluatorTables() throws DAOException {
		ResultSet rs = null;
		Connection conn = null;
		String requeteSQL = "";
		HashMap<Integer,BayesianTable> BTMap = new HashMap<Integer,BayesianTable>();
		try {
			conn = super.getConnection();
			Statement st = conn.createStatement();
			requeteSQL = "select Id from Evaluator ;";
			rs = st.executeQuery(requeteSQL);

			while (rs.next()) {
				int id = rs.getInt("Id");
				BTMap.put(id, GetEvaluatorTable(id));
			}
		} catch (SQLException e) {
			throw new DAOException("Erreur BD " + e.getMessage(), e);
		} finally {
			closeConnection(conn);
		}
		return BTMap;
	}

	/**
	 * Retourne la table bayesienne associée à l'évaluateur indentifiée par id. 
	 * @param id : identifiant de l'évaluateur dans la base de donnée
	 * @return la table bayesienne de correspondance entre descripteur et cette évaluateur
	 * @throws DAOException
	 */
	public BayesianTable GetEvaluatorTable(int id) throws DAOException{
		ResultSet rs = null;
		Connection conn = null;
		String requeteSQL = "";
		BayesianTable BT = null;
		try {
			conn = super.getConnection();
			Statement st = conn.createStatement();
			requeteSQL = "select BayesianTableObject from Evaluator where Id ="+ id + ";";
			rs = st.executeQuery(requeteSQL);

			if (rs.next()) {

				//System.out.println(rs.getString("BayesianTableObject"));	
				BT = new BayesianTable(rs.getString("BayesianTableObject"));
				assert (BT.getIdEval()==id);
			}
		} catch (SQLException e) {
			throw new DAOException("Erreur BD " + e.getMessage(), e);
		} catch (JSONException e) {
			throw new DAOException("Erreur JSON " + e.getMessage(), e);
		} finally {
			closeConnection(conn);
		}
		return BT;
	}

	/**
	 * Ajoute la table bayésienne BT dans la base de donnée. 
	 * @param BT : la table Bayesienne à ajouter
	 * @throws DAOException
	 */
	public void SaveEvaluatorTable(BayesianTable BT) throws DAOException{
		Connection conn = null;
		try {
			conn = super.getConnection();
			PreparedStatement st = conn.prepareStatement("UPDATE Evaluator SET BayesianTableObject=?WHERE id=?;");
			st.setString(1, BT.toJSONString());
			st.setLong(2, BT.getIdEval());
			st.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erreur BD " + e.getMessage(), e);
		} finally {
			closeConnection(conn);
		}
	}
}
