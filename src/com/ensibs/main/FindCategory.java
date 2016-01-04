package com.ensibs.main;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.ensibs.indexer.TextFileIndexer;
import com.ensibs.object.RSSObject;
import com.ensibs.object.Utils;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * Classe principale decrivant la recherche d'une categorie via prediction
 * Tous les contenus predis sont stockes dans Lucene
 * Algorithme pour predire une categorie d'un flux
 * On charge les donnees en BDD
 * On parcours chaque contenu de chaque flux
 * pour chaque contenu on sauvegarde dans une hashmap un objet RSSObject avec
 * la categorie predite egale a "".
 * En parallele du parcours de chaque contenu d'un flux, on estime sa categorie predite
 * par rapport aux contenus de la BDD. Cette categorie predite pour ce contenu est stockee dans une liste.
 * A la fin du parcours des contenus du flux, on prend la categorie presente le plus de fois la liste
 * et on l'affecte aux objets RSSObject
 * Pour finir ces flux avec une categorie predite sont stockes dans un dossier prediction pour
 * separer le contenu de la BDD et ceux-ci.
 * @author Pascal Mahieux et Maxime Jeusselin
 * @version 1.0
 */
public class FindCategory {

	/**
	 * Fonction principale
	 * @param args fichier soumis pour tester les flux et les categories
	 * @throws IOException 
	 */
	@SuppressWarnings({ "unchecked" })
	public static void main(String[] args) throws IOException{

		/* Indexer pour recuperer les objets RSSObject */
		TextFileIndexer indexergetData = new TextFileIndexer("." + File.separator + "save" + File.separator + "learning");

		/* Indexer pour stocker les flux avec la categorie predite */
		TextFileIndexer indexerSaveData = new TextFileIndexer("." + File.separator + "save" + File.separator + "prediction");

		/* Le fichier contenant les flux d'apprentissage en parametre */
		if(args.length > 0) {

			/* map correspondant aux objets a stocker */
			HashMap<String, RSSObject> myNewMap = new HashMap<String, RSSObject>();			

			try {
				/* Detection de la langue */
				DetectorFactory.loadProfile("." + File.separator + "lib" + File.separator + "profiles");

				/* Liste des flux RSS contenus dans le fichier */
				ArrayList<String> contentsLink = Utils.readFile("." + File.separator + args[0]);

				/* Pour chaque flux RSS */
				for(int i = 0; i < contentsLink.size(); i+=2){
					String contentLink = contentsLink.get(i);
					System.out.println("debut : "+contentLink);
					URL url = null;

					/* Map contenant toutes les categories predites des contenus d'un flux  (String, int) pour nom categorie et frequence d'apparence */
					HashMap<String, Integer> categoriesPrediction = new HashMap<String, Integer>();

					try {
						url = new URL(contentLink);

						HttpURLConnection httpcon = null;
						try {
							httpcon = (HttpURLConnection)url.openConnection();

							SyndFeedInput syndFeedInput = new SyndFeedInput();  
							SyndFeed syndFeed = null;
							try {
								syndFeed = syndFeedInput.build(new XmlReader(httpcon));

								List<SyndFeed> entries = syndFeed.getEntries();  
								Iterator<SyndFeed> itEntries = entries.iterator();

								Detector detector = null;
								try {
									detector = DetectorFactory.create();

									/* Pour toutes les entrees existantes */
									while (itEntries.hasNext()) {  

										SyndEntry entry = (SyndEntry) itEntries.next();  

										/* On genere son pid */
										String pid = null;
										try {
											pid = Utils.getHashCode(entry.getTitle().replaceAll("\\n", "").replaceAll("\\t", "") + entry.getLink());

											String description = "";

											/* On recupere le contenu de la page */
											String content = null;
											try {
												content = Utils.printUrl(entry.getLink());

												if(entry.getDescription() != null) {
													description = entry.getDescription().getValue();
												}
												detector.append(entry.getTitle().replaceAll("\\n", "") + description + content);

												/* Determination de la categorie du contenu et stockage dans une arraylist */
												String category = null;
												try {
													category = indexergetData.getBestCategory(content);
													if(category != null && category != "NONE"){
														
														System.out.println("Stockage categorie");
														if(categoriesPrediction.get(category) != null) {
															categoriesPrediction.put(category , categoriesPrediction.get(category) + 1);
														}
														else {
															categoriesPrediction.put(category , 1);
														}

														/* On stocke l'objet RSS sans categorie predite */
														try {	
															String language = detector.detect();
															RSSObject rssObject = new RSSObject(
																	pid,
																	indexergetData.segment(language, entry.getTitle().replaceAll("\\n", "").replaceAll("\\t", "")),
																	entry.getUri(), entry.getLink(), entry.getPublishedDate().toString(),
																	indexergetData.segment(language,description),
																	indexergetData.segment(language,content),
																	language,
																	"",
																	"", /* La categorie prediction est passee apres determination de celle-ci a partir de tous les contenus */
																	new Date().toString());
															myNewMap.put(pid, rssObject);
														} catch (LangDetectException e) {}
													}
												}catch(IOException e){}												
											} catch (Exception e) {}
										} catch (Exception e) {}
									}
								} catch (LangDetectException e) {}
							} catch (IllegalArgumentException e) {} 
							catch (FeedException e) {} 
							catch (IOException e) {}  
						} catch (IOException e) {}  
					} catch (MalformedURLException e) {}

					System.out.println("Recherche meilleure catégorie...");
					/* Determination de la meilleure categorie predite */
					String bestCategory = "NONE";
					int maxOccurence = 0;
					int totalContent = 0;
					for(Entry<String, Integer> entry : categoriesPrediction.entrySet()){
						if(entry.getValue() > maxOccurence){
							maxOccurence = entry.getValue();
							bestCategory = entry.getKey();
						}
						totalContent += entry.getValue();
					}
					System.out.println("Categorie prédite : " + bestCategory + " avec un score de " + maxOccurence + " contenus sur " + totalContent);
					System.out.println("fin : "+contentLink);
					
					/* Ajout de la categorie predite a tous les RSSObject qui ont une categorie predite vide */
					for(Entry<String, RSSObject> entry : myNewMap.entrySet()){
						if(entry.getValue().getPredictCategory().equals("")){
							entry.getValue().setStreamCategory(bestCategory);
							entry.getValue().setPredictCategory(bestCategory);
						}
					}
				}
			} catch (LangDetectException e) {}

			/* Sauvegarde des nouveaux objets dans le dossier prediction */
			indexerSaveData.index(myNewMap);
		}
	}
}
