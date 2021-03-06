package org.eni.encheres.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eni.encheres.bll.ArticleVenduManager;
import org.eni.encheres.bo.ArticleVendu;
import org.eni.encheres.bo.Categorie;
import org.eni.encheres.erreur.BusinessException;

/**
 * Servlet implementation class afficherListeArticle
 */
@WebServlet("/RechercheArticles")
public class ServletRechercheArticles extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
			List<ArticleVendu> listeArticleVendu = new ArrayList<>();
			ArticleVenduManager articleVenduManager = new ArticleVenduManager();
			Categorie categorie = new Categorie();
			categorie.setNoCategorie(Integer.parseInt(request.getParameter("categorie")));
			
			try {
				List<ArticleVendu> listeArticleVenduTemporaire = new ArrayList<>();
				listeArticleVenduTemporaire = articleVenduManager.selectArticleVendu(categorie, request.getParameter("keyword"));
				
				for (ArticleVendu articleVendu : listeArticleVenduTemporaire) {
					if (articleVendu.getEtatVente().equals("en_vente")) {
						listeArticleVendu.add(articleVendu);
					}
				}
				
			} catch (BusinessException e) {
				request.setAttribute("listeCodesErreur", e.getListeCodesErreur());
			}
			
			request.setAttribute("listeArticleVendu", listeArticleVendu);
			
			RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/pageRechercheArticles.jsp");
			rd.forward(request, response);
	}

}
