package org.eni.encheres.bll;

import java.util.regex.Pattern;

import org.eni.encheres.bo.Utilisateur;
import org.eni.encheres.dal.DAOFactory;
import org.eni.encheres.dal.utilisateur.UtilisateurDAO;
import org.eni.encheres.erreur.BusinessException;

public class UtilisateurManager {

	private UtilisateurDAO userDAO;
	
	public UtilisateurManager() {
		userDAO = DAOFactory.getUtilisateurDAO();
	}
	
	/**
	 * MODIFIER SON PROFIL
	 * @param utilisateur
	 * @throws BusinessException
	 */
	public void modifierProfil(Utilisateur utilisateur) throws BusinessException {
		BusinessException businessException = new BusinessException();
		validerUtilisateur(utilisateur, businessException);
		userDAO.createOrUpdateUtilisateur(utilisateur);
	}
	
	
	//CREER UN COMPTE
	public void saveNewOrExistingCompte(Utilisateur user) throws BusinessException {
		BusinessException businessException = new BusinessException();
		
		validerUtilisateur(user, businessException);
		if(businessException.hasErreurs()) {
			throw businessException;
		}
		
		if (user.getNoUtilisateur() != 0) {
			//si le compte est modifiable et modifié, enregistrer les modifs
			userDAO.createOrUpdateUtilisateur(user);
		} else {
			//création d'un compte
			//mettre le crédit à '100' et administrateur à 'false'
			Utilisateur newUser = new Utilisateur(	user.getPseudo(), 
					user.getNom(), 
					user.getPrenom(), 
					user.getEmail(),
					user.getTelephone(),
					user.getRue(),
					user.getCodePostal(),
					user.getVille(),
					user.getMotDePasse(),
					100, 
					false,
					true);
			
			userDAO.createOrUpdateUtilisateur(newUser);
		}
		
		

	}
	
	//SE CONNECTER
	public void connection(String pseudoOuNom) throws BusinessException {
		userDAO.selectByNomOrPseudo(pseudoOuNom);
	}
	
	/**
	 * Mise à jour du mot de passe après demande de réinitialisation
	 * vérifie que le mdp ne dépasse pas 30 caractères
	 */
	public void newPassword(int id, String pwd) throws BusinessException {
		BusinessException businessException = new BusinessException();
		if (pwd.length()>30) {
			businessException.ajouterErreur(CodesResultatBLL.REGLE_PASSWORD_ERREUR);
			throw businessException;
		}
		userDAO.updatePassword(id, pwd);
	}
	
	/**
	 * AFFICHER SON PROFIL
	 * @param id
	 * @return Utilisateur
	 * @throws BusinessException
	 */
	public Utilisateur voirProfil(int id) throws BusinessException {
		return userDAO.selectById(id);
	}
	
	/**
	 * voir le profil d'un autre utilisateur (avec pseudo en paramètre)
	 * @param pseudo
	 * @return
	 * @throws BusinessException
	 */
	public Utilisateur voirProfil(String pseudo) throws BusinessException {
		return userDAO.selectByPseudo(pseudo);
	}
	

	
	/**
	 * supprimer le compte
	 * @param id
	 * @throws BusinessException
	 */
	public void deleteCompte(int id) throws BusinessException {
		userDAO.deleteUtilisateur(id);
	}
	
	
	/**
	 * Vérifier un @Utilisateur avant de l'envoyer dans la DAO
	 * @param utilisateur
	 * @param businessException
	 * conditions : pseudo et mail uniques / pas de caract. spécial dans le pseudo
	 */
	private void validerUtilisateur(Utilisateur utilisateur, BusinessException businessException) {
		try {
			
			//vérif que le pseudo est unique OU qu'il ne contient pas de caractère spécial
			if(userDAO.selectByPseudo(utilisateur.getPseudo()) != null | Pattern.matches("[^\\w]", utilisateur.getPseudo())) {
				businessException.ajouterErreur(CodesResultatBLL.REGLE_PSEUDO_ERREUR);
			}
			
			//vérif que le mail est unique + 
			if(userDAO.selectByEmail(utilisateur.getEmail()) != null) {
				businessException.ajouterErreur(CodesResultatBLL.REGLE_EMAIL_ERREUR);
				//TODO vérif format de l'adresse mail
			}
			
			if (utilisateur.getMotDePasse().length()>30) {
				businessException.ajouterErreur(CodesResultatBLL.REGLE_PASSWORD_ERREUR);
			}
		
		} catch (BusinessException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			
		}
	}
}
