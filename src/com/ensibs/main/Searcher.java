package com.ensibs.main;

import java.io.File;
import java.io.IOException;

import com.ensibs.indexer.TextFileIndexer;

/**
 * Classe principale recherchant les meilleurs contenus a partir d'une requete
 * Tous les contenus sont stockes dans Lucene
 * @author Pascal Mahieux et Maxime Jeusselin
 * @version 1.0
 */
public class Searcher {

	/**
	 * Fonction principale
	 * @param args requete
	 * @throws IOException
	 */
	public static void main(String[] args) {
		
		if(args.length > 0){
			
			/* Indexer pour recuperer les objets RSSObject */
			TextFileIndexer indexerGetData = new TextFileIndexer("." + File.separator + "save" + File.separator + "learning");
			
			/* Recherche des meilleurs contenus a partir de l'argument passe en parametre */
			try {
				indexerGetData.searchIndex(args[0]);
			} catch (IOException e) {}
		}
	}
	
}
