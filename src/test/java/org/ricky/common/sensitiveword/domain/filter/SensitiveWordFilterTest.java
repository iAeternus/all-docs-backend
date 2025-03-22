package org.ricky.common.sensitiveword.domain.filter;

import org.junit.jupiter.api.Test;
import org.ricky.common.sensitiveword.domain.filter.impl.ACFilter;
import org.ricky.common.sensitiveword.domain.filter.impl.ACProFilter;
import org.ricky.common.sensitiveword.domain.filter.impl.DFAFilter;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/22
 * @className SensitiveWordFilterTest
 * @desc
 */
class SensitiveWordFilterTest {

    @Test
    void should_return_false_when_no_sensitive_word() {
        // Given
        List<String> sensitiveList = Arrays.asList("abcdb", "abcbba", "adabca");
        DFAFilter dfaFilter = DFAFilter.getInstance();
        dfaFilter.loadWord(sensitiveList);
        ACFilter acFilter = ACFilter.getInstance();
        acFilter.loadWord(sensitiveList);
        ACProFilter acProFilter = ACProFilter.getInstance();
        acProFilter.loadWord(sensitiveList);

        // When
        boolean dfaRes = dfaFilter.hasSensitiveWord("adabcd");
        boolean acRes = acFilter.hasSensitiveWord("adabcd");
        boolean acProRes = acProFilter.hasSensitiveWord("adabcd");


        // Then
        assertFalse(dfaRes);
        assertFalse(acRes);
        assertFalse(acProRes);
    }

    @Test
    void DFA() {
        // Given
        List<String> sensitiveList = Arrays.asList("abcd", "abcbba", "adabca");
        DFAFilter instance = DFAFilter.getInstance();
        instance.loadWord(sensitiveList);

        // When
        boolean res = instance.hasSensitiveWord("adabcd");

        // Then
        assertTrue(res);
    }

    @Test
    void AC() {
        // Given
        List<String> sensitiveList = Arrays.asList("abcd", "abcbba", "adabca");
        ACFilter instance = ACFilter.getInstance();
        instance.loadWord(sensitiveList);

        // When
        boolean res = instance.hasSensitiveWord("adabcd");

        // Then
        assertTrue(res);
    }

    @Test
    void ACPro() {
        // Given
        List<String> sensitiveList = Arrays.asList("白痴", "你是白痴", "白痴吗");
        ACProFilter acProFilter = ACProFilter.getInstance();
        acProFilter.loadWord(sensitiveList);

        // When
        String res = acProFilter.filter("你是白痴吗");

        // Then
        assertEquals("*****", res);
    }

    @Test
    void DFAMulti() {
        // Given
        List<String> sensitiveList = Arrays.asList("白痴", "你是白痴", "白痴吗");
        DFAFilter instance = DFAFilter.getInstance();
        instance.loadWord(sensitiveList);

        // When
        String res = instance.filter("你是白痴吗");

        // Then
        assertEquals("****吗", res);
    }

    @Test
    void ACMulti() {
        // Given
        List<String> sensitiveList = Arrays.asList("你是白痴", "你是");
        ACFilter instance = ACFilter.getInstance();
        instance.loadWord(sensitiveList);

        // When
        String res = instance.filter("你是白痴吗");

        // Then
        assertEquals("**白痴吗", res);
    }

}