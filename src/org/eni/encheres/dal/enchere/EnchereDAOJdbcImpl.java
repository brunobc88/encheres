package org.eni.encheres.dal.enchere;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eni.encheres.bo.ArticleVendu;
import org.eni.encheres.bo.Categorie;
import org.eni.encheres.bo.Enchere;
import org.eni.encheres.bo.Utilisateur;
import org.eni.encheres.dal.ConnectionProvider;
import org.eni.encheres.erreur.BusinessException;


public class EnchereDAOJdbcImpl implements EnchereDAO {
	
	private static final String INSERT_ENCHERE = "INSERT INTO ENCHERES (date_enchere, montant_enchere, no_utilisateur, no_article) values (?, ?, ?, ?);";
	private static final String SELECT_ENCHERE_BY_ARTICLE_VENDU = "SELECT * FROM ENCHERES AS ench INNER JOIN ARTICLES_VENDUS AS art ON ench.no_article = art.no_article INNER JOIN UTILISATEURS AS util ON ench.no_utilisateur = util.no_utilisateur WHERE art.no_article = ? ORDER BY montant_enchere DESC;";
	private static final String SELECT_ENCHERE_BY_UTILISATEUR = "SELECT * FROM ENCHERES AS ench INNER JOIN ARTICLES_VENDUS AS art ON ench.no_article = art.no_article INNER JOIN CATEGORIES AS cat ON art.no_categorie = cat.no_categorie INNER JOIN UTILISATEURS AS util ON art.no_utilisateur = util.no_utilisateur WHERE ench.no_utilisateur = ? ORDER BY no_enchere DESC;";
	private static final String SELECT_ENCHERE_REMPORTE_BY_UTILISATEUR = "SELECT * FROM ENCHERES AS ench INNER JOIN ARTICLES_VENDUS AS art ON ench.no_article = art.no_article INNER JOIN CATEGORIES AS cat ON art.no_categorie = cat.no_categorie INNER JOIN UTILISATEURS AS util ON art.no_utilisateur = util.no_utilisateur WHERE art.etat_vente = 'vendu' OR art.etat_vente = 'archive' ORDER BY ench.no_article ASC, ench.montant_enchere DESC;";

	
	@Override
	public void insertEnchere(Enchere enchere) throws BusinessException {
		try (Connection connexion = ConnectionProvider.getConnection();
			PreparedStatement pstmt = connexion.prepareStatement(INSERT_ENCHERE);) {
			
			
			int index = 1;
			pstmt.setDate(index++, java.sql.Date.valueOf(enchere.getDateEnchere()));
			pstmt.setInt(index++, enchere.getMontanEnchere());
			pstmt.setInt(index++, enchere.getUtilisateur().getNoUtilisateur());
			pstmt.setInt(index++, enchere.getArticleVendu().getNoArticle());
			pstmt.executeUpdate();
			
		} catch (SQLException ex) {
			ex.printStackTrace();
			BusinessException exception = new BusinessException();
			exception.ajouterErreur(CodesResultatEnchereDAL.INSERT_ENCHERE_ERREUR);
		}
	}

	@Override
	public List<Enchere> selectEnchere(ArticleVendu articleVendu) throws BusinessException {
		List<Enchere> listeEncheres = new ArrayList<Enchere>();
		
		try (Connection connexion = ConnectionProvider.getConnection();
			PreparedStatement pstmt = connexion.prepareStatement(SELECT_ENCHERE_BY_ARTICLE_VENDU);) {
			
			pstmt.setInt(1, articleVendu.getNoArticle());
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Utilisateur utilisateur = new Utilisateur(rs.getInt("no_utilisateur"),
															rs.getString("pseudo"),
															rs.getString("nom"),
															rs.getString("prenom"),
															rs.getString("email"),
															rs.getString("telephone"),
															rs.getString("rue"),
															rs.getString("code_postal"),
															rs.getString("ville"),
															rs.getString("mot_de_passe"),
															rs.getInt("credit"),
															rs.getBoolean("administrateur"),
															rs.getBoolean("compte_actif"));
															
				listeEncheres.add(new Enchere(rs.getDate("date_enchere").toLocalDate(), rs.getInt("montant_enchere"), utilisateur, articleVendu));
			}
			
			rs.close();
			
		} catch (SQLException ex) {
			ex.printStackTrace();
			BusinessException exception = new BusinessException();
			exception.ajouterErreur(CodesResultatEnchereDAL.SELECT_ENCHERE_ERREUR);
		}
		return listeEncheres;
	}
	
	@Override
	public List<Enchere> selectEnchere(Utilisateur utilisateur) throws BusinessException {
		List<Enchere> listeEncheres = new ArrayList<Enchere>();
		
		try (Connection connexion = ConnectionProvider.getConnection();
			PreparedStatement pstmt = connexion.prepareStatement(SELECT_ENCHERE_BY_UTILISATEUR);) {
			 
			pstmt.setInt(1, utilisateur.getNoUtilisateur());
			ResultSet rs = pstmt.executeQuery();
			ArticleVendu articleVendu = new ArticleVendu();
			
			while(rs.next()) {
				if (articleVendu.getNoArticle() != rs.getInt("no_article")) {
				
				Categorie categorie = new Categorie(rs.getInt("no_categorie"), rs.getString("libelle"));
				
				Utilisateur vendeur = new Utilisateur(rs.getInt("no_utilisateur"),
													rs.getString("pseudo"),
													rs.getString("nom"),
													rs.getString("prenom"),
													rs.getString("email"),
													rs.getString("telephone"),
													rs.getString("rue"),
													rs.getString("code_postal"),
													rs.getString("ville"),
													rs.getString("mot_de_passe"),
													rs.getInt("credit"),
													rs.getBoolean("administrateur"),
													rs.getBoolean("compte_actif"));
				
				articleVendu = new ArticleVendu(rs.getInt("no_article"),
															rs.getString("nom_article"),
															rs.getString("description"),
															rs.getDate("date_debut_encheres").toLocalDate(),
															rs.getDate("date_fin_encheres").toLocalDate(),
															rs.getInt("prix_initial"),
															rs.getInt("prix_vente"),
															rs.getString("etat_vente"),
															vendeur, categorie);
															
				listeEncheres.add(new Enchere(rs.getDate("date_enchere").toLocalDate(), rs.getInt("montant_enchere"), utilisateur, articleVendu));
				}
			}
			rs.close();
			
		} catch (SQLException ex) {
			ex.printStackTrace();
			BusinessException exception = new BusinessException();
			exception.ajouterErreur(CodesResultatEnchereDAL.SELECT_ENCHERE_ERREUR);
		}
		return listeEncheres;
	}

	@Override
	public List<Enchere> selectEnchereRemporteByUtilisateur(Utilisateur utilisateur) throws BusinessException {
		List<Enchere> listeEncheres = new ArrayList<Enchere>();
		
		try (Connection connexion = ConnectionProvider.getConnection();
			PreparedStatement pstmt = connexion.prepareStatement(SELECT_ENCHERE_REMPORTE_BY_UTILISATEUR);) {
			 
			ResultSet rs = pstmt.executeQuery();
			ArticleVendu articleVendu = new ArticleVendu();
			
			while(rs.next()) {
				if (articleVendu.getNoArticle() != rs.getInt("no_article") && utilisateur.getNoUtilisateur() == rs.getInt("ENCHERES.no_utilisateur")) {
				
				Categorie categorie = new Categorie(rs.getInt("no_categorie"), rs.getString("libelle"));
				
				Utilisateur vendeur = new Utilisateur(rs.getInt("UTILISATEURS.no_utilisateur"),
													rs.getString("pseudo"),
													rs.getString("nom"),
													rs.getString("prenom"),
													rs.getString("email"),
													rs.getString("telephone"),
													rs.getString("rue"),
													rs.getString("code_postal"),
													rs.getString("ville"),
													rs.getString("mot_de_passe"),
													rs.getInt("credit"),
													rs.getBoolean("administrateur"),
													rs.getBoolean("compte_actif"));
				
				articleVendu = new ArticleVendu(rs.getInt("no_article"),
															rs.getString("nom_article"),
															rs.getString("description"),
															rs.getDate("date_debut_encheres").toLocalDate(),
															rs.getDate("date_fin_encheres").toLocalDate(),
															rs.getInt("prix_initial"),
															rs.getInt("prix_vente"),
															rs.getString("etat_vente"),
															vendeur, categorie);
															
				listeEncheres.add(new Enchere(rs.getDate("date_enchere").toLocalDate(), rs.getInt("montant_enchere"), utilisateur, articleVendu));
				}
			}
			rs.close();
			
		} catch (SQLException ex) {
			ex.printStackTrace();
			BusinessException exception = new BusinessException();
			exception.ajouterErreur(CodesResultatEnchereDAL.SELECT_ENCHERE_ERREUR);
		}
		return listeEncheres;
	}
	
}
