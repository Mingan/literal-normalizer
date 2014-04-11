package cz.opendata.linked.literal.normalizer;

import java.util.Iterator;

public class SparqlQueryBuilder {

    private TransformerConfig config;

    public static String buildQueryFromConfig(TransformerConfig config) {
        SparqlQueryBuilder instance = new SparqlQueryBuilder(config);
        return instance.buildQuery();
    }

    private SparqlQueryBuilder(TransformerConfig config) {
        this.config = config;
    }

    private String buildQuery() {
        String where;
        if (config.isRegexp()) {
            where = buildRegexCondition();
        } else {
            if (config.isCaseSensitive()) {
                where = buildSimpleCondition();
            } else {
                where = buildSimpleInsensitiveCondition();
            }
        }
        String query =
                "DELETE { " + config.getTripleToDelete() + " }\n" +
                        "INSERT { " + config.getTripleToInsert() + " }\n" +
                        "WHERE {\n" + where + "\n}";
        System.out.println(query);
        return query;
    }

    private String buildRegexCondition() {
        String query;
        String regexp = buildRegexpFromAlternatives();
        String flags = getRegexpFlags();
        query =
                buildCondition() +
                buildLanguageFilter() +
                buildRegexpFilter(regexp, flags) +
                buildRegexpReplacement(regexp, flags);
        return query;
    }

    private String getRegexpFlags () {
        if (config.isCaseInsensitive()) {
            return "i";
        }
        return "";
    }

    private String buildRegexpFromAlternatives() {
        return config.getToMatchInString("|");
    }

    private String buildCondition() {
        return config.getCondition() + "\n";
    }

    private String buildLanguageFilter() {
        return "FILTER(langMatches(lang(?o), '" + config.getLanguage() + "'))" + "\n";
    }

    private String buildRegexpFilter(String regexp, String flags) {
        return "FILTER(REGEX(?o, '" + regexp + "', '" + flags + "'))\n";
    }

    private String buildRegexpReplacement(String val, String flags) {
        return "BIND(" +
                "REPLACE(" +
                "?o, " +
                "'" + val + "'," +
                "'" + config.getReplacement() + "', " +
                "'" + flags + "'" +
                ")" +
                " AS ?replacement)\n";
    }

    private String buildSimpleCondition() {
        String query =
                buildCondition() +
                buildValuesSection() +
                buildSimpleReplacement();
        return query;
    }

    private String buildValuesSection() {
        return buildValuesSection(false);
    }

    private String buildValuesSection(boolean temp) {
        String var;
        if (temp) {
            var = "?o_temp";
        } else {
            var = "?o";
        }
        Iterator<String> it = config.getToMatch().iterator();
        String query = "VALUES " + var + " {\n";
        while (it.hasNext()) {
            String val = it.next();
            query += "'" + val + "'" + getLangTag() + "\n";
        }
        query += "}\n";
        return query;
    }

    private String getLangTag () {
        if (config.getLanguage() == "") {
            return "";
        } else {
            return "@" + config.getLanguage();
        }
    }

    private String buildSimpleReplacement() {
        return "BIND('" + config.getReplacement() + "'" + getLangTag() + " AS ?replacement)\n";
    }


    private String buildSimpleInsensitiveCondition() {
        String query =
                buildCondition() +
                buildLanguageFilter() +
                buildCaseInsensitiveFilter() +
                buildValuesSection(true) +
                buildSimpleReplacement();
        return query;
    }

    private String buildCaseInsensitiveFilter() {
        return "FILTER(LCASE(?o) = ?o_temp)\n";
    }
}