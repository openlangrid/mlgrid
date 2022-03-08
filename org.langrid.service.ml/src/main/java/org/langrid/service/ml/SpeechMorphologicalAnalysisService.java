package org.langrid.service.ml;

public interface SpeechMorphologicalAnalysisService {
	String startAnalysis(String language, MorphologicalAnalysisReceiverService receiver);
	void processAnalysis(String processId, byte[] audio);
	void stopAnalysis(String processId);
}
