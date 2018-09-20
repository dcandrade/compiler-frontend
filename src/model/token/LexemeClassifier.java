package model.token;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class LexemeClassifier {

    public static final String LINE_COMMENT_REGEX = "//.*";
    static final String SPACE_REGEX = "([ \t\n])*";
    static final String DIGIT_REGEX = "[0-9]";
    static final String LETTER_REGEX = "([a-z]|[A-Z])";
    static final String IDENTIFIER_REGEX = LETTER_REGEX + "(" + LETTER_REGEX + "|" + DIGIT_REGEX + "|_)*";
    static final String ARITHMETICAL_OPERATOR_REGEX = "\\+|-|\\*|/|\\+\\+|--";
    static final String RELATIONAL_OPERATOR_REGEX = "!=|==|\\<|\\<=|\\>|\\>=|=";
    static final String RESERVERD_WORD_REGEX = "class|const|variables|method|return|main|if|then|else|while|read|write|void|int|float|bool|string|true|false|extends";
    static final String LOGICAL_OPERATOR_REGEX = "!|&&|\\|\\|";
    static final String DELIMITER_REGEX = ";|,|\\(|\\)|\\[|\\]|\\{|\\}|\\.";
    static final String BLOCK_COMMENT_START_REGEX = "/\\*";
    static final String BLOCK_COMMENT_END_REGEX = "\\*/";
    static String SYMBOL_REGEX;
    static String STRING_REGEX;
    private final Map<String, String> categories2Regex;

    public LexemeClassifier() {
        this.generatePendingRegexes();
        this.categories2Regex = new LinkedHashMap<>(); // MUST be linked hash map to preserve order of insertion
        this.populateClassificationMap();
    }

    static String getDelimiters() {
        return DELIMITER_REGEX.replace("|", "");
    }

    static String getOperators() {
        String logical = LOGICAL_OPERATOR_REGEX.replace("|", "");
        String arithmetical = ARITHMETICAL_OPERATOR_REGEX.replace("|", "");
        String relational = RELATIONAL_OPERATOR_REGEX.replace("|", "");

        return logical + arithmetical + relational;
    }

    public static String getAllCompilerDemiliters() {
        return getDelimiters() + getOperators() + " \t\n";
    }

    public static void main(String[] args) {
        System.out.println(STRING_REGEX);
        System.out.println(Pattern.matches(STRING_REGEX, "\"a_123\""));
    }

    public Optional<String> classify(String token) {

        for (Map.Entry<String, String> entry : this.categories2Regex.entrySet()) {
            String category = entry.getKey();
            String regex = entry.getValue();

            if (Pattern.matches(regex, token)) {
                return Optional.of(category);
            }
        }

        return Optional.empty();
    }

    public Optional<String> checkForPrimitiveTypes(String token) {
        for (String type : TokenTypes.PRIMITIVE_TYPES) {
            if (this.checkTokenType(token, type)) {
                return Optional.of(type);
            }
        }

        return Optional.empty();
    }

    public boolean checkTokenType(String token, String type) {
        String regex = this.categories2Regex.get(type);

        return regex != null && Pattern.matches(regex, token);
    }

    private void populateClassificationMap() {
        this.categories2Regex.put(TokenTypes.RESERVED_WORD, RESERVERD_WORD_REGEX);
        this.categories2Regex.put(TokenTypes.IDENTIFIER, IDENTIFIER_REGEX);
        this.categories2Regex.put(TokenTypes.NUMBER, TokenTypes.NUMBER_REGEX);
        this.categories2Regex.put(TokenTypes.RELATIONAL_OPERATOR, RELATIONAL_OPERATOR_REGEX);
        this.categories2Regex.put(TokenTypes.LOGICAL_OPERATOR, LOGICAL_OPERATOR_REGEX);
        this.categories2Regex.put(TokenTypes.ARITHMETICAL_OPERATOR, ARITHMETICAL_OPERATOR_REGEX);
        this.categories2Regex.put(TokenTypes.DELIMITER, DELIMITER_REGEX);
        this.categories2Regex.put(TokenTypes.STRING, STRING_REGEX);
        this.categories2Regex.put(TokenTypes.SPACE, SPACE_REGEX);
        this.categories2Regex.put(TokenTypes.BLOCK_COMMENT_START, BLOCK_COMMENT_START_REGEX);
        this.categories2Regex.put(TokenTypes.BLOCK_COMMENT_END, BLOCK_COMMENT_END_REGEX);
    }

    private void generatePendingRegexes() {
        SYMBOL_REGEX = this.generateSymbolRegex();
        STRING_REGEX = "\"(" + LETTER_REGEX + "|" + DIGIT_REGEX + "|" + SYMBOL_REGEX + ")*\"";
    }

    private String generateSymbolRegex() {
        String specialChars = "[]()*-+?|{}" + (char) 34;

        StringBuilder symbolRegexBuilder = new StringBuilder();
        for (int ascii_index = 32; ascii_index <= 126; ascii_index++) {
            char ch = (char) ascii_index;

            if (specialChars.indexOf(ch) != -1) {
                symbolRegexBuilder.append("\\").append(ch);
                symbolRegexBuilder.append("|");
            } else {
                symbolRegexBuilder.append(ch);
                symbolRegexBuilder.append("|");
            }
        }
        symbolRegexBuilder.deleteCharAt(symbolRegexBuilder.length() - 1); //deleting last |

        return symbolRegexBuilder.toString();
    }

}
