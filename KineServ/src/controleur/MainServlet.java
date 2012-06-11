package controleur;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import modele.BayesianTable;
import modele.Feedback;
import modele.Installation;
import modele.JSONTranslator;
import modele.Song;

import org.json.JSONException;

import dao.BayesianTableDAO;
import dao.DAOException;
import dao.FeedbackDAO;
import dao.SongDAO;


/**
 * Servlet implementation class MainServlet
 */
@WebServlet({ "/MainServlet", "/" })
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Resource(name = "jdbc/kinezik")
	private DataSource ds;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MainServlet() {
		super();
	}

	/**
	 * Factorisation du traitement de la requète pour doPost et doGet
	 *
	 */
	private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Si il n'y a pas de session, on en crée une. 
		if (request.getSession(false) == null) {
			getServletContext().
			getRequestDispatcher("/GetSession").
			forward(request, response);
			//return;
		} 

		String action = request.getParameter("action");
		// Si il n'y a pas de paramètre action, action recoit une chaine vide
		if (action == null) {action = "";}
		if (action.equals("getSongEval")){
			log ("getSongEval");
			//TODO : JSON vers SONG, puis SONG -> BD -> SONG, puis SONG vers JSON
			String json = request.getParameter("JSON");
			//			ArrayList<Song> listSongs = null;
			//			try {
			//				listSongs = JSONTranslator.JSONToSongList(json);
			//			} catch (JSONException e1) {
			//				// TODO Auto-generated catch block
			//				e1.printStackTrace();
			//			}
			//			
			//			SongDAO songDAO = new SongDAO(ds);
			//			try {
			//				songDAO.setSongsEvaluation(listSongs);
			//	
			//			json = null;
			//			json = JSONTranslator.SongListToJSON(listSongs);
			//			} catch (DAOException e) {
			//				e.printStackTrace();
			//			}

			Song song = null;
			try {
				song = new Song(json);
				log("JSONSOng envoyé  : " + json);

				SongDAO songDAO = new SongDAO(ds);

				if (song != null && songDAO.setSongEvaluation(song)) {
					songDAO.addUserSong(song, request.getParameter("UUID"));
				} else {
					log("getSongEval : song null ou pas dans la BD");
				}
				json = song.toJSONString();
			} catch (DAOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			response.setContentType("application/json");  
			PrintWriter output = response.getWriter();
			output.println(json); 
			log("JSON : " + json);

		} else if (action.equals("getBayesTable")) {
			log ("getBayesTable");
			BayesianTableDAO BayesTDAO = new BayesianTableDAO(ds);
			try {
				HashMap<Integer,BayesianTable> hashBayesTable = BayesTDAO.GetAllEvaluatorTables();
				String JSONString = null;
				JSONString = JSONTranslator.getJSONFromTables(hashBayesTable);

				response.setContentType("application/json");  
				PrintWriter output = response.getWriter();
				output.println(JSONString);
				log("JSON : " + JSONString);
			} catch (DAOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (action.equals("sendFeedback")) {
			String JSONFeedback = request.getParameter("JSON");
			log("On server : feedback received : " + JSONFeedback);
			if (JSONFeedback != null){
				Feedback feedback;
				try {
					feedback = new Feedback(JSONFeedback);
					FeedbackDAO feedDAO = new FeedbackDAO(ds);
					Installation instal = (Installation) request.getSession(false).getAttribute("Installation");
					log("UUID instal in sendFeedback = "+ instal.getUUID());
					feedDAO.saveFeedback(feedback, instal.getUUID());
				} catch (DAOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				 catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		} else {
			try {
				JSONTranslator.JSONToTableMap(
						"{\"3\":{\"id\":3,\"data\":[[[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224]],[[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224]],[[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224]],[[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224]],[[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224],[1,0.800000011920929,0.6000000238418579,0.4000000059604645,0.20000000298023224]]]},\"2\":{\"id\":2,\"data\":[[[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5]],[[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5]],[[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5]],[[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5]],[[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5],[0.5,0.5,0.5,0.5,0.5]]]},\"1\":{\"id\":1,\"data\":[[[0,0,0,0,0],[0,0,0,0,0],[0,0,0,0,0],[0,0,0,0,0],[0,0,0,0,0]],[[0,0,0,0,0],[0.03999999910593033,0.03999999910593033,0.03999999910593033,0.03999999910593033,0.03999999910593033],[0.07999999821186066,0.07999999821186066,0.07999999821186066,0.07999999821186066,0.07999999821186066],[0.11999999731779099,0.11999999731779099,0.11999999731779099,0.11999999731779099,0.11999999731779099],[0.1599999964237213,0.1599999964237213,0.1599999964237213,0.1599999964237213,0.1599999964237213]],[[0,0,0,0,0],[0.07999999821186066,0.07999999821186066,0.07999999821186066,0.07999999821186066,0.07999999821186066],[0.1599999964237213,0.1599999964237213,0.1599999964237213,0.1599999964237213,0.1599999964237213],[0.23999999463558197,0.23999999463558197,0.23999999463558197,0.23999999463558197,0.23999999463558197],[0.3199999928474426,0.3199999928474426,0.3199999928474426,0.3199999928474426,0.3199999928474426]],[[0,0,0,0,0],[0.11999999731779099,0.11999999731779099,0.11999999731779099,0.11999999731779099,0.11999999731779099],[0.23999999463558197,0.23999999463558197,0.23999999463558197,0.23999999463558197,0.23999999463558197],[0.36000001430511475,0.36000001430511475,0.36000001430511475,0.36000001430511475,0.36000001430511475],[0.47999998927116394,0.47999998927116394,0.47999998927116394,0.47999998927116394,0.47999998927116394]],[[0,0,0,0,0],[0.1599999964237213,0.1599999964237213,0.1599999964237213,0.1599999964237213,0.1599999964237213],[0.3199999928474426,0.3199999928474426,0.3199999928474426,0.3199999928474426,0.3199999928474426],[0.47999998927116394,0.47999998927116394,0.47999998927116394,0.47999998927116394,0.47999998927116394],[0.6399999856948853,0.6399999856948853,0.6399999856948853,0.6399999856948853,0.6399999856948853]]]}}");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			log("wrong action");
		}
	}



	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

}
