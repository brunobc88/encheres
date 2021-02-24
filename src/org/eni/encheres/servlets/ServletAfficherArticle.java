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
import org.eni.encheres.bll.ImageManager;
import org.eni.encheres.bll.RetraitManager;
import org.eni.encheres.bo.ArticleVendu;
import org.eni.encheres.bo.Image;
import org.eni.encheres.bo.Retrait;
import org.eni.encheres.erreur.BusinessException;

/**
 * Servlet implementation class AfficherArticle
 */
@WebServlet("/AfficherArticle")
public class ServletAfficherArticle extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ArticleVenduManager articleVenduManager = new ArticleVenduManager();
		RetraitManager retraitManager = new RetraitManager();
		ImageManager imageManager = new ImageManager();
		
		ArticleVendu articleVendu = new ArticleVendu();
		Retrait retrait = new Retrait();
		List<Image> listeImage = new ArrayList<>();
		Image image = new Image();
		
		try {
			articleVendu = articleVenduManager.selectArticleVenduById(Integer.parseInt(request.getParameter("noArticle")));
			retrait = retraitManager.selectRetrait(articleVendu);
			listeImage = imageManager.selectImage(articleVendu);
			
			if (listeImage.size() == 0) {
				image.setCheminUrl("images/image_indisponible.png");
				listeImage.add(image);
			}
			
		} catch (BusinessException e) {
			request.setAttribute("listeCodesErreur", e.getListeCodesErreur());
		}
		
		request.setAttribute("articleVendu", articleVendu);
		request.setAttribute("retrait", retrait);
		request.setAttribute("listeImage", listeImage);
		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/pageAfficherArticle.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
