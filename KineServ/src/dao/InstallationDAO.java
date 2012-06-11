package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import modele.Installation;



public class InstallationDAO extends AbstractDataBaseDAO {

	public InstallationDAO(DataSource ds) {
		super(ds);
	}


	/**
	 * TODO !!!?
	 * @param i : Une installation i
	 * @return Vrai si l'installation est déjà enregistré dans la base de donnée
	 */
	public boolean isRegistered(Installation i) {
		return false;
	}

	/**
	 * Renvoie une Installation, 
	 * contenant les informations enregistrées dans la base de donnée 
	 * pour l'installation d'UUID donné. 
	 * @param uuid : l'identifiant de l'installation
	 * @return L'objet installation contenant toutes les informations concerant l'installation correspondant à 
	 * l'uuid donné dans la base de donnée. Si aucune installation n'existe pour cet UUID, une nouvelle entrée 
	 * est crée dans la base de donnée. 
	 * @throws DAOException : Erreur levée en cas de problème accès BD
	 */
	public Installation findInstallation(String uuid) throws DAOException {
		ResultSet rs = null;
		Connection conn = null;
		String requeteSQL = "";
		Installation inst;
		try {
			conn = super.getConnection();
			Statement st = conn.createStatement();
			requeteSQL = "select * from Installation where uuid = \""
					+ uuid + "\"";
			rs = st.executeQuery(requeteSQL);

			if (!rs.next()) {
				inst = createInstallation(uuid, conn);
			} else {
				inst = new Installation(rs.getString("UUID"));
			}
		} catch (SQLException e) {
			throw new DAOException("Erreur BD " + e.getMessage(), e);
		} finally {
			closeConnection(conn);
		}
		return inst;
	}

	/**
	 * Ajoute un champ dans la table installation de la bd, concenant le nouvel uuid;
	 * @param uuid : un uuid non présent dans la table
	 * @return l'installation associée à cet UUID (vide)
	 * @throws SQLException : Erreur levé en cas de problème accès BD
	 */
	private Installation createInstallation(String uuid, Connection conn) throws SQLException {
		String requeteSQL = "";

		Statement st = conn.createStatement();
		requeteSQL = "INSERT INTO Installation(UUID) VALUES ('" +
				uuid + "');";
		st.execute(requeteSQL);

		return new Installation(uuid);

	}



	public boolean isTableUpToDate() {
		// TODO Auto-generated method stub
		return false;
	}

}
