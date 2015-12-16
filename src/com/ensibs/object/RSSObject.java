package com.ensibs.object;

import java.io.Serializable;

/**
 * Classe decrivant un objet RSS
 * @author Pascal Mahieux et Maxime Jeusselin
 * @version 1.0
 */
@SuppressWarnings("serial")
public class RSSObject implements Serializable {

	/* pid unique */
	private String pid;

	/* titre de l'objet RSS  */
	private String title;

	/* lien de la source */
	private String linkSource;

	/* lien de la page */
	private String linkPage;

	/* derniere date de modification */
	private String lastUpdate;

	/* description du flux */
	private String description;

	/* contenu du flux */
	private String content;

	/* langue de la page */
	private String language;
	
	/* categorie du flux */
	private String streamCategory;
	
	/* categorie predite */
	private String predictCategory;
	
	/* date de collecte */
	private String lastCheck; 

	/**
	 * Constructeur de l'objet RSS
	 * @param pid pid unique
	 * @param title titre de l'objet RSS
	 * @param linkSource lien de la source
	 * @param linkPage lien de la page
	 * @param lastUpdate derniere date de modification
	 * @param description description du flux
	 * @param content contenu du flux
	 * @param language langue de la page
	 * @param streamCategory categorie du flux
	 * @param predictCategory categorie predite
	 * @param lastCheck derniere date de collecte
	 */
	public RSSObject(String pid, String title, String linkSource, String linkPage, String lastUpdate, String description, String content, String language, String streamCategory, String predictCategory, String lastCheck) {
		this.pid = pid;
		this.title = title;
		this.linkSource =linkSource;
		this.linkPage = linkPage;
		this.lastUpdate = lastUpdate;
		this.description = description;
		this.content = content;
		this.language = language;
		this.streamCategory = streamCategory;
		this.predictCategory = predictCategory;
		this.lastCheck = lastCheck;
	}	

	/**
	 * Recuperer le pid de l'objet
	 * @return le pid de l'objet
	 */
	public String getPid() {
		return this.pid;
	}

	/**
	 * Recuperer le titre de l'objet
	 * @return le titre de l'objet
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Recuperer le lien de la source
	 * @return le lien de la source
	 */
	public String getLinkSource() {
		return this.linkSource;
	}

	/**
	 * Recuperer le lien de la page
	 * @return le lien de la page
	 */
	public String getLinkPage() {
		return this.linkPage;
	}

	/**
	 * Recuperer la derniere date de modification
	 * @return la derniere date de modification
	 */
	public String getLastUpdate() {
		return this.lastUpdate;
	}

	/**
	 * Recuperer la description de la page
	 * @return la description de la page
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Recuperer le contenu de la page
	 * @return le contenu de la page
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Recuperer la langue de la page
	 * @return la langue de la page
	 */
	public String getLanguage() {
		return this.language;
	}
	
	/**
	 * Recuperer la categorie du flux
	 * @return la categorie du flux
	 */
	public String getStreamCategory() {
		return streamCategory;
	}

	/**
	 * Recuperer la categorie predite
	 * @return la categorie predite
	 */
	public String getPredictCategory() {
		return predictCategory;
	}

	/**
	 * Recuperer la derniere date de verification
	 * @return la derniere date de verification
	 */
	public String getLastCheck() {
		return lastCheck;
	}

	/**
	 * Modifier le pid de l'objet
	 * @param pid le nouveau pid de l'objet
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * Modifier le titre de l'objet
	 * @param title le nouveau titre de l'objet
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Modifier le lien de la source
	 * @param linkSource le nouveau lien de la source
	 */
	public void setLinkSource(String linkSource) {
		this.linkSource = linkSource;
	}

	/**
	 * Modifier le lien de la page
	 * @param linkPage le nouveau lien de la page
	 */
	public void setLinkPage(String linkPage) {
		this.linkPage = linkPage;
	}

	/**
	 * Modifier la date
	 * @param date la nouvelle derniere modification du flux
	 */
	public void setLastUpdate(String date) {
		this.lastUpdate = date;
	}

	/**
	 * Modifier la description
	 * @param description la nouvelle description du flux
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Modifier le contenu
	 * @param content le nouveau contenu du flux
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Modifier la langue de la page
	 * @param language la nouvelle langue de la page
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Modifier la categorie de flux
	 * @param streamCategory la nouvelle categorie de flux
	 */
	public void setStreamCategory(String streamCategory) {
		this.streamCategory = streamCategory;
	}

	/**
	 * Modifier la categorie predite
	 * @param predictCategory la nouvelle categorie predite
	 */
	public void setPredictCategory(String predictCategory) {
		this.predictCategory = predictCategory;
	}

	/**
	 * Modifier la derniere date de verification
	 * @param lastCheck la nouvelle derniere date de modification
	 */
	public void setLastCheck(String lastCheck) {
		this.lastCheck = lastCheck;
	}

	/**
	 * Affichage de l'objet
	 * @return l'objet
	 */
	public String toString(){
		return 
				"Hashcode :\t" + this.pid + "\n" +
				"Title :\t" + this.title + "\n" +
				"Link source :\t" + this.linkSource + "\n" +
				"Link page :\t" + this.linkPage + "\n" +
				"Description :\t" + this.description + "\n" +
				"Contenu :\t" + this.content + "\n" +
				"Language :\t" + this.language + "\n" +
				"Categorie du flux :\t" + this.streamCategory + "\n" +
				"Categorie predite :\t" + this.predictCategory + "\n"+
				"Last check :\t" + this.lastCheck.toString();
	}
}
