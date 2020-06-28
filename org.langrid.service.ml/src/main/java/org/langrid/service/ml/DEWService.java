package org.langrid.service.ml;

/**
 * An interface definition of Distributed Expression of Words services such as Word2Vec
 * @author Takao Nakaguchi
 *
 */
public interface DEWService {
	SimilarWord[] getSimilarWords(String word);
	double calcSimilarity(String word1, String word2);
}
