package org.eni.encheres.servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eni.encheres.bll.ArticleVenduManager;
import org.eni.encheres.bll.FormulaireNouvelleVente;
import org.eni.encheres.bll.ImageManager;
import org.eni.encheres.bll.RetraitManager;
import org.eni.encheres.bo.ArticleVendu;
import org.eni.encheres.bo.Image;
import org.eni.encheres.bo.Retrait;
import org.eni.encheres.erreur.BusinessException;

/**
 * Servlet implementation class NouvelleVente
 */
@WebServlet("/NouvelleVente")
public class ServletNouvelleVente extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final String ATT_ARTICLE_VENDU	= "articleVendu";
	public static final String ATT_RETRAIT			= "retrait";
	public static final String ATT_CATEGORIE		= "categorie";
	public static final String ATT_IMAGE			= "image";
	public static final String VUE_NOUVELLE_VENTE 	= "pageCreationArticle.jsp";
    
   
	//affichage du formulaire
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	// réinjection de la liste de catégories. Surement facultatif
    	HttpSession session = request.getSession();
    	session.setAttribute("listeCategorie", session.getAttribute("listeCategorie"));
    	
    	RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/pageCreationArticle.jsp");
		rd.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");

		try {
		FormulaireNouvelleVente formulaire = new FormulaireNouvelleVente();
		
		ArticleVenduManager articleVenduManager= new ArticleVenduManager();
		RetraitManager retraitManager = new RetraitManager();
		ImageManager imageManager = new ImageManager();
		
        ArticleVendu articleVendu = formulaire.creerArticle(request);
        request.setAttribute(ATT_ARTICLE_VENDU, articleVendu);
        
        Retrait retrait = formulaire.creerRetrait(request);
        request.setAttribute(ATT_RETRAIT, retrait);
        
        Image image = formulaire.loadImage(request);
        request.setAttribute(ATT_RETRAIT, image);
        
        articleVendu = articleVenduManager.insertArticleVendu(articleVendu); 
        
        retrait.setArticleVendu(articleVendu);
        retraitManager.insertRetrait(retrait);
        
        image.setArticleVendu(articleVendu);
        imageManager.insertImage(image);
		
			
        } catch (BusinessException ex) {
            ex.printStackTrace();
            request.setAttribute("listeCodesErreur", ex.getListeCodesErreur());
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/pageMesArticles.jsp");
        rd.forward(request, response);
    }
}


