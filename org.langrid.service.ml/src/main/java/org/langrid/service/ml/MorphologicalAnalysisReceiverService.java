package org.langrid.service.ml;

import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;

public interface MorphologicalAnalysisReceiverService {
	void onAnalyzeResult(String processId, Morpheme[] results);
}
