package controleur;

import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import modele.BayesianTable;
import modele.Song;


import dao.BayesianTableDAO;
import dao.DAOException;
import dao.InitBaseDAO;
import dao.SongDAO;

/**
 * Servlet implementation class initBase
 */
@WebServlet("/initBase")
public class initBase extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Resource(name = "jdbc/kinezik")
	private DataSource ds;


	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public initBase() {
		super();

		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int nbEval = 3; // Number of evaluator to be initialized.
		InitBaseDAO initDAO = new InitBaseDAO(ds, 
				"/home/johan/KINZEZIK/ProjetSpe/KineServ/SQLScript/init_base.sql");

		// INITIALISATION DES TABLES ET DES EVALUATEURS
		try {
			initDAO.InitAll();
		} catch (DAOException e) {
			log("Error while initializing Tables : "+e.getMessage());
		}

		//AJOUT DE TABLES BAYESIENNES ALEATOIRE POUR LES EVALUATEURS
		BayesianTableDAO BTDao = new BayesianTableDAO(ds);
		try {
			// Initialisation des tables de bayes
			//MILES DAVIS
			BayesianTable BT = new BayesianTable(5,5,5, 1);

			for(int i = 0 ; i<5 ; i++){
				for (int j = 0; j<5; j++ ){
					for (int k = 0; k<5; k++){
						BT.bayesMat[i][j][k] = ((float) (5-i)*(5-k)*(5-k))/125;
					}
				}
			}
			BTDao.SaveEvaluatorTable(BT);


			//NINA SIMONE
			BT = new BayesianTable(5,5,5, 2);

			for(int i = 0 ; i<5 ; i++){
				for (int j = 0; j<5; j++ ){
					for (int k = 0; k<5; k++){
						BT.bayesMat[i][j][k] = ((float) i*(5-j)*k ) /125;
					}
				}
			}
			BTDao.SaveEvaluatorTable(BT);

			//ACDC
			BT = new BayesianTable(5,5,5,3);
			for(int i = 0 ; i<5 ; i++){
				for (int j = 0; j<5; j++ ){
					for (int k = 0; k<5; k++){
						BT.bayesMat[i][j][k] = ((float) (i*j* (5-k)) ) /125;
					}
				}
			}
			BTDao.SaveEvaluatorTable(BT);


		} catch (DAOException e) {
			log("Error while adding random Bayesian Tables" + e.getMessage());
		}

	// AJOUT DE CHANSON AVEC DES EVALUATION ARBITRAIRES (TODO)
		ArrayList<Song> ListSongs = new ArrayList<Song>();

		//Vampire Weekend
		Song s = new Song("Vampire Weekend", "Contra", "White Sky");
		s.addEvaluations(1, (float) 0.4);
		s.addEvaluations(2, (float) 0.9);
		s.addEvaluations(3, (float) 0.1);

		ListSongs.add(s);

		//Miles Davis
		s = new Song("Miles Davis", "My Funny Valentine", "My Funny Valentine");
		s.addEvaluations(1, (float) 1);
		s.addEvaluations(2, (float) 0);
		s.addEvaluations(3, (float) 0);

		ListSongs.add(s);

		// ACDC
		s = new Song("AC/DC", "Back In Black", "Back In Black");
		s.addEvaluations(1, (float) 0);
		s.addEvaluations(2, (float) 0);
		s.addEvaluations(3, (float) 1);

		ListSongs.add(s);

		// Nina Simone
		s = new Song("Nina Simone", "You Put A Spell On Me", "Feeling Good");
		s.addEvaluations(1, (float) 0);
		s.addEvaluations(2, (float) 1);
		s.addEvaluations(3, (float) 0);

		ListSongs.add(s);



		SongDAO sDAO= new SongDAO(ds);
		try {
			sDAO.saveSongs(ListSongs);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			log("Error while adding song list" + e.getMessage());
		}
		log("La base de donnée a été initialisée");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
