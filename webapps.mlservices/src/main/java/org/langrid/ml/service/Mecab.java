package org.langrid.ml.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.go.nict.langrid.language.ISO639_1LanguageTags;
import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.AccessLimitExceededException;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.LanguageNotUniquelyDecidedException;
import jp.go.nict.langrid.service_1_2.NoAccessPermissionException;
import jp.go.nict.langrid.service_1_2.NoValidEndpointsException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServerBusyException;
import jp.go.nict.langrid.service_1_2.ServiceNotActiveException;
import jp.go.nict.langrid.service_1_2.ServiceNotFoundException;
import jp.go.nict.langrid.service_1_2.UnsupportedLanguageException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.MorphologicalAnalysisService;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;

public class Mecab implements MorphologicalAnalysisService{
	@Override
	public Morpheme[] analyze(String language, String text)
			throws AccessLimitExceededException, InvalidParameterException, LanguageNotUniquelyDecidedException,
			NoAccessPermissionException, NoValidEndpointsException, ProcessFailedException, ServerBusyException,
			ServiceNotActiveException, ServiceNotFoundException, UnsupportedLanguageException {
		try{
			if(!ISO639_1LanguageTags.ja.matches(Language.parse(language))) {
				throw new UnsupportedLanguageException("language", language);
			}
			return runMecab(text).toArray(new Morpheme[] {});
		} catch(InvalidLanguageTagException e) {
			throw new InvalidParameterException("language", language);
		} catch(IOException e) {
			throw new ProcessFailedException(e);
		}
	}

	private static List<Morpheme> runMecab(String text) throws IOException {
		ProcessBuilder pb = new ProcessBuilder("/usr/local/bin/docker-compose run --rm nlp01 mecab".split(" "));
		pb.environment().put("PATH", pb.environment().get("PATH") + ":/usr/local/bin");
		pb.directory(new File("docker"));
		Process p = pb.start();
		List<Morpheme> ret = new ArrayList<>();
		try(PrintWriter pw = new PrintWriter(new OutputStreamWriter(p.getOutputStream(), "UTF-8"));
				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"))){
			pw.println(text);
			pw.flush();
			String line = null;
			while((line = br.readLine()) != null) {
				System.out.println(line);
				if(line.equals("EOS")) break;
				String[] elements = line.split("[ \t,]");
				String word = elements[0];
				String lemma = elements[7];
				String pos = elements[1];
				String subPos = elements[2];
				ret.add(new Morpheme(word, lemma, toLangridPos(pos, subPos)));
			}
		}
		try(BufferedReader er = new BufferedReader(new InputStreamReader(p.getErrorStream(), "UTF-8"))){
			String line = null;
			while((line = er.readLine()) != null) {
				System.out.println(line);
			}
		}
		return ret;
	}

	private static String toLangridPos(String pos, String subPos) {
		switch(pos + ":" + subPos) {
		case "名詞:一般": return PartOfSpeech.noun_common.name();
		case "名詞:固有名詞": return PartOfSpeech.noun_proper.name();
		case "名詞:代名詞": return PartOfSpeech.noun_pronoun.name();
		}
		switch(pos) {
		case "名詞": return PartOfSpeech.noun_other.name();
		case "動詞": return PartOfSpeech.verb.name();
		case "助詞": return PartOfSpeech.adjective.name();
		case "助動詞": return PartOfSpeech.adverb.name();
		}
		return PartOfSpeech.unknown.name();
	}
	
	public static void main(String[] args) throws Throwable{
		System.out.println(runMecab("京都大学の記者が汽車で帰社した。彼は元気だ。"));
	}
}
