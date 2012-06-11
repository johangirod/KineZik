package controleur;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import modele.Installation;
import dao.DAOException;
import dao.InstallationDAO;


/**
 * Servlet implementation class GetSession 
 * Cette Servlet est appelée lorsqu'aucune session n'a été ouverte durant
 */
@WebServlet("/GetSession")
public class GetSession extends HttpServlet {
	private static final long serialVersionUID = 1L;

    @Resource(name = "jdbc/kinezik")
    private DataSource ds;
    
    
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		log("Server initialization");
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetSession() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Action de la servlet (commune pour POST et GET)
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String UUID = request.getParameter("UUID");
		log("UUID reçu : "+ UUID+"\n");
		if (UUID != null && Installation.isCorrect(UUID)) {
			InstallationDAO instDAO = new InstallationDAO(ds);
			try {
				Installation inst = instDAO.findInstallation(UUID);
				log("Installation ok");
				request.getSession(true).setAttribute("Installation", inst);
				log("La requete a bien été faite" + inst.toString());
				getServletContext().
				getRequestDispatcher("/").
				forward(request, response);
			} catch (DAOException e) {
				log("Erreur DAO : " + e.getMessage());
			}
		} else {
			// Si la requete ne contient pas de champs UUID ou que la syntaxe de ce dernier n'est pas bonne	
			log("Mauvais UUID pour l'installation");
		}
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	

}
