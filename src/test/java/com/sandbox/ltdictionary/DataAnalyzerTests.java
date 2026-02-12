package com.sandbox.ltdictionary;

import com.sandbox.ltdictionary.service.util.DataAnalyzer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

class DataAnalyzerTests {

	List<String> ruTextExample = List.of(
		"Внезапно, контрагенты не замедлили контрагенту с выводами,",
		"а также свежий взгляд на привычные контрагенту вещи — безусловно, ",
		"открывает новые горизонты. В рамках контрагентов концепции демократизации, ",
		"выводы сделаны. Идейные соображения высшего порядка, как принято считать контрагент, неоднозначным. "
	);

	@Test
	void shouldFindMostFrequent() {
		Assertions.assertEquals(Set.of("контрагенту", "контрагенты", "контрагентов", "контрагент"), DataAnalyzer.findMostFrequentLemmaWords(ruTextExample));
	}

}
