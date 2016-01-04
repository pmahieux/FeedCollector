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
 * Classe principale decrivant la liste des flux RSS stockes dans un fichier .txt.
 * Tous les contenus sont stockes dans Lucene
 * @author Pascal Mahieux et Maxime Jeusselin
 * @version 1.0
 */
public class RSSManager {

	/**
	 * Fonction principale
	 * @param args fichier soumis avec des flux pour stocker tous les contenus
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException{
		
		/* Indexer pour stocker et recuperer les objets RSSObject */
		TextFileIndexer textFileIndexer = new TextFileIndexer("." + File.separator + "save" + File.separator + "learning");
		
		/* Le fichier contenant les flux d'apprentissage en parametre */
		if(args.length > 0) {
			
			/* map correspondant aux objets recuperes */
			HashMap<String, RSSObject> myOriginalMap = new HashMap<String, RSSObject>(textFileIndexer.readDB());
			
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
											
											/* On verifie si le pid existe ou non, s'il n'existe pas, on stocke l'objet rss */
											if(!myOriginalMap.containsKey(pid)){
												String description = "";

												/* On recupere le contenu de la page */
												String content = null;
												try {
													content = Utils.printUrl(entry.getLink());

													if(entry.getDescription() != null) {
														description = entry.getDescription().getValue();
													}
													detector.append(entry.getTitle().replaceAll("\\n", "") + description + content);

													/* On stocke l'objet RSS */
													try {	
														String language = detector.detect();
														RSSObject rssObject = new RSSObject(pid, textFileIndexer.segment(language, entry.getTitle().replaceAll("\\n", "").replaceAll("\\t", "")), entry.getUri(), entry.getLink(), entry.getPublishedDate().toString(), textFileIndexer.segment(language,description), textFileIndexer.segment(language,content), language, contentsLink.get(i+1), contentsLink.get(i+1), new Date().toString());
														myNewMap.put(pid, rssObject);
														System.out.println("\t"+pid);
														//System.out.println(textFileIndexer.segment(language,content));
													} catch (LangDetectException e) {}
												} catch (Exception e) {}
											}
										} catch (Exception e) {}
									}
								} catch (LangDetectException e) {}
							} catch (IllegalArgumentException e) {} 
							catch (FeedException e) {} 
							catch (IOException e) {}  
						} catch (IOException e) {}  
					} catch (MalformedURLException e) {}
					System.out.println("fin : "+contentLink);
				}
			} catch (LangDetectException e) {}
			
			/* Sauvegarde des nouveaux objets */
			
			//textFileIndexer.index(myNewMap);
		}
	}
}
