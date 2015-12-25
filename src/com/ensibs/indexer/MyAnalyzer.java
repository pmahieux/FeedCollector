package com.ensibs.indexer;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class MyAnalyzer extends Analyzer {	

	@Override
	protected TokenStreamComponents createComponents(final String field,
			final Reader reader) { 
		Tokenizer tokenizer = new StandardTokenizer(reader);
		TokenStream filter = new StandardFilter(tokenizer);		
		filter = new LowerCaseFilter(filter);
		filter = new StopFilter(filter,StandardAnalyzer.STOP_WORDS_SET);
		filter = new PorterStemFilter(filter);
		return new TokenStreamComponents(tokenizer, filter);
	}	


}
