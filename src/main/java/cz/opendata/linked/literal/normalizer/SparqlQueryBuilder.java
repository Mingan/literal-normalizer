package cz.opendata.linked.literal.normalizer;

import java.util.Iterator;
import java.util.Map;

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
        //where += buildValuesSection();
        String query =
                "DELETE { " + config.getTripleToDelete() + " }\n" +
                        "INSERT { " + config.getTripleToInsert() + " }\n" +
                        "WHERE {\n" + where + "\n}";
        System.out.println(query);
        return query;
    }

    private String buildRegexCondition() {
        String query;
        String flags = getRegexpFlags();
        query =
                buildCondition() +
                buildLanguageFilter() +
                buildValuesSection(true) +
                buildRegexpFilter(flags) +
                buildRegexpReplacement(flags);
        return query;
    }

    private String getRegexpFlags () {
        if (config.isCaseInsensitive()) {
            return "i";
        }
        return "";
    }

    private String buildCondition() {
        return config.getCondition() + "\nFILTER(isLiteral(?o))\n";
    }

    private String buildLanguageFilter() {
        return "FILTER(langMatches(lang(?o), '" + config.getLanguage() + "'))" + "\n";
    }

    private String buildRegexpFilter( String flags) {
        return "FILTER(REGEX(?o, ?o_temp, '" + flags + "'))\n";
    }

    private String buildRegexpReplacement(String flags) {
        String replace = "REPLACE(" +
                "?o, " +
                "?o_temp, " +
                "?replacement_item, " +
                "'" + flags + "'" +
                ")";
        if (config.getLanguage() != "") {
            replace = "STRLANG(" + replace +
                            ", '" + config.getLanguage() + "') ";
        }
        return "BIND(" +
                 replace +
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
        String query = "VALUES (" + var + " ?replacement_item) {\n";
        for(Map.Entry<String, String> entry : config.getPairs().entrySet()) {

            query += "('" + entry.getKey() + "'" + getLangTag() + " '" + entry.getValue() + "')\n";
        }
        while (it.hasNext()) {
            String val = it.next();
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
        return "BIND(STRLANG(?replacement_item, '" + config.getLanguage() + "') AS ?replacement)\n";
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