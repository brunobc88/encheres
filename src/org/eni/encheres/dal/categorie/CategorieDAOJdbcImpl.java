package org.eni.encheres.dal.categorie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eni.encheres.bo.Categorie;
import org.eni.encheres.dal.ConnectionProvider;
import org.eni.encheres.erreur.BusinessException;

public class CategorieDAOJdbcImpl implements CategorieDAO {
	
	private static final String INSERT_CATEGORIE = "INSERT INTO CATEGORIES (libelle) values(?);";
	private static final String UPDATE_CATEGORIE = "UPDATE CATEGORIES SET libelle = ? WHERE no_categorie = ?;";
	private static final String DELETE_CATEGORIE = "DELETE FROM CATEGORIES WHERE no_categorie = ?;";
	private static final String SELECT_CATEGORIE = "SELECT * FROM CATEGORIES";
	
	@Override
	public void insertCategorie(Categorie categorie) throws BusinessException {
		try (Connection connexion = ConnectionProvider.getConnection();
			PreparedStatement pstmt = connexion.prepareStatement(INSERT_CATEGORIE);) {
			
			pstmt.setInt(1, categorie.getNoCategorie());
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			BusinessException exception = new BusinessException();
			exception.ajouterErreur(CodesResultatCategorieDAL.INSERT_CATEGORIE_ERREUR);
			throw exception;
		}
	}

	@Override
	public void updateCategorie(Categorie categorie) throws BusinessException {
		try (Connection connexion = ConnectionProvider.getConnection();
			PreparedStatement pstmt = connexion.prepareStatement(UPDATE_CATEGORIE);) {
			
			pstmt.setInt(1, categorie.getNoCategorie());
			pstmt.setString(2, categorie.getLibelle());
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			BusinessException exception = new BusinessException();
			exception.ajouterErreur(CodesResultatCategorieDAL.UPDATE_CATEGORIE_ERREUR);
			throw exception;
		}
	}

	@Override
	public void deleteCategorie(Categorie categorie) throws BusinessException {
		try (Connection connexion = ConnectionProvider.getConnection();
			PreparedStatement pstmt = connexion.prepareStatement(DELETE_CATEGORIE);) {
			
			pstmt.setInt(1, categorie.getNoCategorie());
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			BusinessException exception = new BusinessException();
			exception.ajouterErreur(CodesResultatCategorieDAL.DELETE_CATEGORIE_ERREUR);
			throw exception;
		}
	}

	@Override
	public List<Categorie> selectCategorie() throws BusinessException {
		List<Categorie> listeCategorie = new ArrayList<Categorie>();
		
		try (Connection connexion = ConnectionProvider.getConnection();
			PreparedStatement pstmt = connexion.prepareStatement(SELECT_CATEGORIE);) {
			
			ResultSet rs = pstmt.executeQuery();
					
			while(rs.next()) {
				listeCategorie.add(new Categorie(rs.getInt("no_categorie"), rs.getString("libelle")));
			}
			rs.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			BusinessException exception = new BusinessException();
			exception.ajouterErreur(CodesResultatCategorieDAL.SELECT_CATEGORIE_ERREUR);
			throw exception;
		}
		return listeCategorie;
	}
	
}
