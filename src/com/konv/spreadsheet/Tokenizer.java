package com.konv.spreadsheet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {

    private Pattern mTokenPatterns;

    public Tokenizer() {
        StringBuilder tokenPatternsBuilder = new StringBuilder();
        for (TokenType tokenType : TokenType.values()) {
            tokenPatternsBuilder.append(String.format("|(?<%s>%s)", tokenType.name(), tokenType.pattern));
            mTokenPatterns = Pattern.compile(tokenPatternsBuilder.substring(1));
        }
    }

    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        Matcher matcher = mTokenPatterns.matcher(input.toUpperCase());
        while (matcher.find()) {
            if (matcher.group(TokenType.NUMBER.name()) != null) {
                tokens.add(new Token(TokenType.NUMBER, matcher.group(TokenType.NUMBER.name())));
            } else if (matcher.group(TokenType.BINARYOP.name()) != null) {
                tokens.add(new Token(TokenType.BINARYOP, matcher.group(TokenType.BINARYOP.name())));
            } else if (matcher.group(TokenType.REFERENCE.name()) != null) {
                tokens.add(new Token(TokenType.REFERENCE, matcher.group(TokenType.REFERENCE.name())));
            } else if (matcher.group(TokenType.BRACEOPEN.name()) != null) {
                tokens.add(new Token(TokenType.BRACEOPEN, matcher.group(TokenType.BRACEOPEN.name())));
            } else if (matcher.group(TokenType.BRACECLOSE.name()) != null) {
                tokens.add(new Token(TokenType.BRACECLOSE, matcher.group(TokenType.BRACECLOSE.name())));
            } else if (matcher.group(TokenType.INVALID.name()) != null) {
                return null;
            }
        }
        return tokens;
    }

    public static class Token {
        public TokenType type;
        public String data;

        public Token(TokenType type, String data) {
            this.type = type;
            this.data = data;
        }

        @Override
        public String toString() {
            return String.format("(%s %s)", type.name(), data);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Token) {
                Token other = (Token) obj;
                return type.equals(other.type) && data.equals(other.data);
            } else {
                return false;
            }
        }
    }

    public enum TokenType {
        NUMBER("-?\\d+"), BINARYOP("[+|\\-|\\*|\\/|\\^|\\||\\&]"), REFERENCE("[A-Za-z][1-9]\\d?"),
        WHITESPACE("\\s"), BRACEOPEN("\\("), BRACECLOSE("\\)"), INVALID(".");

        public final String pattern;

        TokenType(String pattern) {
            this.pattern = pattern;
        }
    }

    public static void main(String[] args) {
        Tokenizer tokenizer = new Tokenizer();
        String input = "3 + 2 - 1 + (4 / 7)";
        System.out.println(tokenizer.tokenize(input));
    }
}
