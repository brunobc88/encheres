package org.eni.encheres.servlets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eni.encheres.authentification.Authentification;
import org.eni.encheres.bll.UtilisateurManager;
import org.eni.encheres.erreur.BusinessException;

/**
 * Servlet implementation class ServletSupprimerProfil
 */
@WebServlet("/SupprimerProfil")
public class ServletSupprimerProfil extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	UtilisateurManager um = new UtilisateurManager();
	Authentification authentification = new Authentification();

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			um.deleteCompte(authentification.getUtilisateurFromSession(request));
			// supprime le compte et les ventes en cours et encheres associées
			this.getServletContext().getRequestDispatcher("/Deconnexion").forward(request, response);
			// déconnecte l'utilisateur et le redirige vers la page d'accueil

		} catch (BusinessException | SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
