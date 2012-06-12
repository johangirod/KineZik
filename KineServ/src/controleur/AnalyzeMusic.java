package controleur;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import dao.DAOException;
import dao.EvaluatorDAO;
import dao.InitBaseDAO;
import dao.SongDAO;
import entagged.audioformats.exceptions.CannotReadException;
import evaluator.EvaluatorSet;
import evaluator.*;
import evaluator.ServerSong;

/**
 * Servlet implementation class initBase
 */
@WebServlet("/AnalyzeMusic")
public class AnalyzeMusic extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Resource(name = "jdbc/kinezik")
	private DataSource ds;


	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AnalyzeMusic() {
		super();

		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//0 : INITIALIZE TABLE : 
		InitBaseDAO initDAO = new InitBaseDAO(ds, 
				"/home/johan/KINZEZIK/ProjetSpe/KineServ/SQLScript/init_base.sql");

		try {
			initDAO.InitAll();
		} catch (DAOException e) {
			log("Error while initializing Tables : "+e.getMessage());
		}

		//1 : SELECT THE EVALUATOR TO USE : 

		EvaluatorSet evalsSet = new EvaluatorSet();
		evalsSet.addEvaluator(new RockEval());
		evalsSet.addEvaluator(new FolkEval());
		evalsSet.addEvaluator(new MetalEval());
		evalsSet.addEvaluator(new AmbientEval());
		EvaluatorDAO evalDAO = new EvaluatorDAO(ds);
		try {
			evalDAO.saveEvaluators(evalsSet);
		} catch (DAOException e1) {
			log(e1.getMessage());
		}

		
		
		
		
		
		
		
		
		File dir = new File("/home/johan/Musique/");

		// The list of files can also be retrieved as File objects
		File[] files = dir.listFiles();

		//select only file (and not directory)
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		};

		files = dir.listFiles(fileFilter);
		if (files == null){
			throw new IOException("Le dossier n'existe pas");
		}

		//3 : FOR EACH MP3 FILE, CREATE A SONG, EVALUATE IT, PUT IT IN THE BD

		for (int i = 0; i < files.length; i++){
			ServerSong song;
			try {
				System.out.println(files[i].getName());
				song = new ServerSong(files[i]);
				evalsSet.evaluateSong(song);

				// On ajoute la chanson dans la BD ^^
				SongDAO songDao = new SongDAO(ds);

				songDao.saveSong(song);
				log("La chanson " + song.toString() +" a été ajoutée dans la base de donnée");
			} catch (DAOException e) {
				log("Impossible d'ajouter la chanson dans la base de donnée :" + e.getMessage());
			} catch (IOException e1) {
				log("Le fichier " + files[i].getName() +
					"a eu un probleme de lecture");
			} catch (CannotReadException e2) {
				log("Le fichier " + files[i].getName() +
						"n'est pas un fichier MP3 (ou a des mauvais tags):");
			}
		}
		
		
		
		
		
		
		
		
		
//		//2 : GET THE MP3 FILES 
//
//		dir = new File("/media/Packard Bell/Users/JohanG/Music");
//
//		// The list of files can also be retrieved as File objects
//		files = dir.listFiles();
//
//		//select only file (and not directory)
//		 fileFilter = new FileFilter() {
//			public boolean accept(File file) {
//				return file.isFile();
//			}
//		};
//
//		files = dir.listFiles(fileFilter);
//		if (files == null){
//			throw new IOException("Le dossier n'existe pas");
//		}
//
//		//3 : FOR EACH MP3 FILE, CREATE A SONG, EVALUATE IT, PUT IT IN THE BD
//
//		for (int i = 0; i < files.length; i++){
//			ServerSong song;
//			try {
//				System.out.println(files[i].getName());
//				song = new ServerSong(files[i]);
//				evalsSet.evaluateSong(song);
//
//				// On ajoute la chanson dans la BD ^^
//				SongDAO songDao = new SongDAO(ds);
//
//				songDao.saveSong(song);
//				log("La chanson " + song.toString() +" a été ajoutée dans la base de donnée");
//			} catch (DAOException e) {
//				log("Impossible d'ajouter la chanson dans la base de donnée :" + e.getMessage());
//			} catch (IOException e1) {
//				log("Le fichier " + files[i].getName() +
//					"a eu un probleme de lecture");
//			} catch (CannotReadException e2) {
//				log("Le fichier " + files[i].getName() +
//						"n'est pas un fichier MP3 (ou a des mauvais tags):");
//			}
//		}
//
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
