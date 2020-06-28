package org.langrid.service.ml;

public interface MachineTranslationService {
	TranslationResult[] translate(
			String sourceLang, String targetLang, String source, int maxResults);
}
