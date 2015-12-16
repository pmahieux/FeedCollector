package com.ensibs.object;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;

import org.apache.tika.Tika;

import de.l3s.boilerpipe.extractors.ArticleExtractor;

/**
 * Classe contenant differentes fonctions necessaires pour l'indexeur
 */
public class Utils {

	/**
	 * Fonction qui effectue un hash sur du contenu et retourne le hash
	 * @param content le contenu a hasher
	 * @return le hash
	 * @throws Exception une exception
	 */
	public static String getHashCode(String content) throws Exception {
		content = content.replaceAll("[^\\w]", "");          	

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(content.getBytes());

		byte byteData[] = md.digest();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));        }

		return sb.toString();
	}

	/**
	 * Traiter le contenu de chaque page
	 * @param url l'url de la page
	 * @return le contenu traite
	 * @throws Exception une exception
	 */
	public static String printUrl(String url) throws Exception {
		Tika t = new Tika();
		URL u = new URL(url);
		String content= t.parseToString(u);
		if(!url.contains(".pdf") || !url.contains(".doc") || !url.contains(".xml")){
			content = ArticleExtractor.INSTANCE.getText(u);
		}
		return content;
	}

	/**
	 * Lire un fichier contenant des flux rss
	 * @param filePath le chemin du fichier
	 * @return la liste des contenus du fichier et chacune des categories associees
	 */
	public static ArrayList<String> readFile(String filePath){
		ArrayList<String> content = new ArrayList<String>();
		try{
			BufferedReader buff = new BufferedReader(new FileReader(filePath));
			try {
				String line;
				String[] split;
				while ((line = buff.readLine()) != null) {
					split = line.split(" ");
					try {
					content.add(split[0]);
					content.add(split[1]);
					} catch(ArrayIndexOutOfBoundsException e){
						e.printStackTrace();
						System.out.println("Votre flux RSS n'a pas de categorie ! Veuillez en rajouter une. Ex : [http://www.../rss] [espace] [CATEGORIE]");
					}
				}
			} finally {
				buff.close();
			}
		} catch (IOException ioe) {}
		return content;
	}
}
