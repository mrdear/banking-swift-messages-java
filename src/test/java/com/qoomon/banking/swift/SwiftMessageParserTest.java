package com.qoomon.banking.swift;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.io.StringReader;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by qoomon on 24/06/16.
 */
public class SwiftMessageParserTest {

    private SoftAssertions softly = new SoftAssertions();

    private SwiftMessageParser classUnderTest = new SwiftMessageParser();

    @Test
    public void parse_WHEN_detecting_whitespaces_between_blocks_THEN_ignore_them() throws Exception {

        // Given
        String swiftMessageText = "{1:} {2:}\t{3:}\n{4:x\nx x   x   -} {5:}";

        // When
        SwiftMessage swiftMessage = classUnderTest.parse(new StringReader(swiftMessageText));

        // Then
        assertThat(swiftMessage.getTextBlock()).isNotNull();
        softly.assertThat(swiftMessage.getTextBlock().getContent()).isEqualTo("x\nx x   x   ");
        softly.assertAll();
    }

    @Test
    public void parse_WHEN_block4_has_wrong_termination_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = "{1:}\n{2:}\n{3:}\n{4:}\n{5:}";

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParserException.class);

        SwiftMessageParserException parseException = (SwiftMessageParserException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(4);

    }




    @Test
    public void parse_WHEN_first_bracket_is_missing_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = "1:}\n{2:}\n{3:}\n{4:-}\n{5:}";

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParserException.class);

        SwiftMessageParserException parseException = (SwiftMessageParserException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);

    }

    @Test
    public void parse_WHEN_block_structure_is_wrong_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = "{:1:}\n{2:}\n{3:}\n{4:-}\n{5:}";

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParserException.class);

        SwiftMessageParserException parseException = (SwiftMessageParserException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(1);

    }

    @Test
    public void parse_WHEN_block_appears_multiple_times_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = "{1:}\n{2:}\n{3:}\n{4:-}\n{5:}\n{1:}";

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParserException.class);

        SwiftMessageParserException parseException = (SwiftMessageParserException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(6);

    }

    @Test
    public void parse_WHEN_unknown_block_appears_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = "{1:}\n{2:}\n{3:}\n{4:-}\n{5:}\n{6:}";

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParserException.class);

        SwiftMessageParserException parseException = (SwiftMessageParserException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(6);

    }

    @Test
    public void parse_WHEN_brackets_are_unbalanced_THEN_throw_exception() throws Exception {

        // Given
        String swiftMessageText = "{1:}\n{2:}\n{3:}\n{4:-}\n{5:";

        // When
        Throwable exception = catchThrowable(() -> classUnderTest.parse(new StringReader(swiftMessageText)));

        // Then
        assertThat(exception).as("Exception").isInstanceOf(SwiftMessageParserException.class);

        SwiftMessageParserException parseException = (SwiftMessageParserException) exception;
        assertThat(parseException.getLineNumber()).isEqualTo(5);

    }


}