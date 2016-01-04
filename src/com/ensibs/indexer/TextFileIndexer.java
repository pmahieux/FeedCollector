package com.ensibs.indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.PatternSyntaxException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
/**
 * This terminal application creates an Apache Lucene index in a folder and adds files into this index
 * based on the input of the user.
 */




import com.ensibs.object.RSSObject;

/**
 * 
 * SNOWBALL
 * @author Pascal Mahieux et Maxime Jeusselin
 * @version 1.0
 */
public class TextFileIndexer {

	//private StandardAnalyzer analyzer = new StandardAnalyzer();
	private Analyzer analyzer = new MyAnalyzer();
	private IndexWriter writer;
	private String indexLocation;
	private HashMap<String,ArrayList<String>> stopwords;

	/**
	 * Constructor
	 * @param indexDir the name of the folder in which the index should be created
	 * @throws java.io.IOException when exception creating index.
	 */
	@SuppressWarnings("deprecation")
	public TextFileIndexer(String indexDir){
		// the boolean true parameter means to create a new index everytime, 
		// potentially overwriting any existing files there.
		FSDirectory dir;
		this.indexLocation = indexDir;
		try{
			dir = FSDirectory.open(new File(indexDir));
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_45, analyzer);
			writer = new IndexWriter(dir, config);
		}catch(IOException e){
			e.printStackTrace();
		}
		//Création des stopwords
		stopwords = new HashMap<String, ArrayList<String>>();
		File directory = new File("." + File.separator + "resources" + File.separator + "stopwords");
		for(File f : directory.listFiles()){			
			try {
				BufferedReader buff;
				buff = new BufferedReader(new FileReader(f.getAbsolutePath()));
				String stopword;
				ArrayList<String> listStopwords = new ArrayList<String>();
				while((stopword = buff.readLine()) != null){
					listStopwords.add(stopword);
				}
				stopwords.put(f.getName(), listStopwords);
				buff.close();
			} catch (IOException e) {				
				e.printStackTrace();
			}			
		}
	}

	public void index(HashMap<String,RSSObject> myMap){
		for(Entry<String,RSSObject> entry : myMap.entrySet()){
			RSSObject myObject = entry.getValue();
			Document doc = new Document();			
			//===================================================
			// add contents of file
			//===================================================
			doc.add(new TextField("pid", myObject.getPid(), Field.Store.YES));
			doc.add(new TextField("title", myObject.getTitle(), Field.Store.YES));
			doc.add(new TextField("linkSource", myObject.getLinkSource(), Field.Store.YES));
			doc.add(new TextField("linkPage", myObject.getLinkPage(), Field.Store.YES));
			doc.add(new TextField("lastUpdate", myObject.getLastUpdate()+"", Field.Store.YES));
			doc.add(new TextField("description", myObject.getDescription(), Field.Store.YES));
			doc.add(new TextField("content", myObject.getContent(), Field.Store.YES));
			doc.add(new TextField("language", myObject.getLanguage(), Field.Store.YES));
			doc.add(new TextField("streamCategory", myObject.getStreamCategory(), Field.Store.YES));
			doc.add(new TextField("predictCategory", myObject.getPredictCategory(), Field.Store.YES));
			doc.add(new TextField("lastCheck", myObject.getLastCheck()+"", Field.Store.YES));			

			try {
				writer.addDocument(doc);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			this.closeIndex();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, RSSObject> readDB() {
		HashMap<String, RSSObject> map = new HashMap<String, RSSObject>();

		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
			RSSObject object = null;
			System.out.println("nb docs : "+reader.numDocs());
			for(int i = 0; i < reader.numDocs(); i++){
				object = new RSSObject(
						reader.document(i).getField("pid").stringValue(), 
						reader.document(i).getField("title").stringValue(), 
						reader.document(i).getField("linkSource").stringValue(), 
						reader.document(i).getField("linkPage").stringValue(), 
						reader.document(i).getField("lastUpdate").stringValue(), 
						reader.document(i).getField("description").stringValue(), 
						reader.document(i).getField("content").stringValue(), 
						reader.document(i).getField("language").stringValue(), 
						reader.document(i).getField("streamCategory").stringValue(), 
						reader.document(i).getField("predictCategory").stringValue(), 
						reader.document(i).getField("lastCheck").stringValue());
				map.put(reader.document(i).getField("pid").stringValue(), object);
			}
			reader.close();
		} catch (IOException e) {}
		return map;
	}

	/**
	 * Search a string in the documents indexed
	 * @param string
	 * @throws IOException 
	 */
	public void searchIndex(String searchString) throws IOException {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
		IndexSearcher searcher = new IndexSearcher(reader);

		try {
			System.out.println("Query="+searchString);
			MultiFieldQueryParser queryParser = new MultiFieldQueryParser(new String[]{"title","description","content"}, analyzer);
			Query query = queryParser.parse(searchString);
			TopDocs hits =searcher.search(query, 10);
			// 4. display results
			if(hits.scoreDocs.length == 0){
				System.out.println("Mot introuvable");
			}
			for(int i=0;i<hits.scoreDocs.length;++i) {
				int docId = hits.scoreDocs[i].doc;
				Document d = searcher.doc(docId);
				System.out.print((i + 1) + ". " + d.get("title") + " score=" + hits.scoreDocs[i].score + " : ");
				System.out.print(hits.scoreDocs[i].score + " / " + hits.getMaxScore() + " = " + hits.scoreDocs[i].score / hits.getMaxScore());
				if(hits.scoreDocs[i].score / hits.getMaxScore()>0.50){
					System.out.print(" Good Score");
				}
				System.out.println();				
			}
		} catch (Exception e) {
			System.out.println("Error searching " + searchString + " : " + e.getMessage());
		}
	}
	
	/**
	 * Recuperer la meilleure categorie d'un contenu a partir de la BDD 
	 * @param content contenu permettant de trouver sa categorie
	 * @return la meilleure categorie
	 * @throws IOException
	 */
	public String getBestCategory(String content) throws IOException {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
		IndexSearcher searcher = new IndexSearcher(reader);

		String category = null;
		
		try {
			MultiFieldQueryParser queryParser = new MultiFieldQueryParser(new String[]{"title","description","content"}, analyzer);
			Query query = queryParser.parse(content);
			TopDocs hits =searcher.search(query, 10);
			// 4. display results

			/* score maximal */
			float maxScore = 0;
			
			for(int i=0;i<hits.scoreDocs.length;++i) {
				int docId = hits.scoreDocs[i].doc;
				Document d = searcher.doc(docId);
				
				/* Si le score maximal est inferieur au score du document, 
				 * on stocke le score de ce document ainsi que sa categorie */
				if(hits.scoreDocs[i].score / hits.getMaxScore()> maxScore){
					maxScore = hits.scoreDocs[i].score / hits.getMaxScore();
					category = d.get("predictCategory");
				}
			}
		} catch (Exception e) {}
		
		return category;
	}

	/**
	 * Permet de stemmer une chaîne et de supprimer les stopwords
	 * @param language
	 * @param text
	 * @return texte segmenté
	 */
	@SuppressWarnings("rawtypes")
	public String segment(String language, String text){
		try{
			Class stemClass;
			stemClass = Class.forName("com.ensibs.indexer.ext." +
					language + "Stemmer");

			SnowballStemmer stemmer;
			stemmer = (SnowballStemmer) stemClass.newInstance();
			List<String> wordList = Arrays.asList(text.split(" "));
			ArrayList<String> sw = stopwords.get(language);
			for(String word : wordList){
				if(!sw.isEmpty()){
					if(sw.contains(word)){
						//Suppression du stopword dans le texte
						text = text.replaceFirst(word + " ", "");
					}else{						
						try{
							//Stem du mot
							stemmer.setCurrent(word);
							stemmer.stem();
							text = text.replace(word, stemmer.getCurrent());
						}catch(PatternSyntaxException e){
							
						}
						
					}
				}				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return text;

	}



	/**
	 * Close the index.
	 * @throws java.io.IOException when exception closing
	 */
	public void closeIndex() throws IOException {
		writer.close();
	}
}