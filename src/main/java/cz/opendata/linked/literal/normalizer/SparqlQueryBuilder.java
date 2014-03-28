package cz.opendata.linked.literal.normalizer;

import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.LinkedList;

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
        String query =
                "DELETE { " + config.getTripleToDelete() + " }\n" +
                        "INSERT { " + config.getTripleToInsert() + " }\n" +
                        "WHERE {\n" +
                        buildAlternativeConditions() +
                        buildSimpleReplacement() +
                "}";
        return query;
    }

    private String buildAlternativeConditions() {
        LinkedList<String> alternatives = new LinkedList<>();
        Iterator<String> it = config.getToMatch().iterator();
        while (it.hasNext()) {
            String val = it.next();

            String query =
                    "{\n" +
                            config.getCondition() + "\n" +
                            "FILTER(isLiteral(?o))\n" +
                            buildLanguageFilterForRegexp() +
                            buildCondition(val) + "\n" +
                            "}\n";
            alternatives.add(query);
        }
        return StringUtils.join(alternatives, "UNION\n");
    }

    private String buildSimpleReplacement() {
        if (config.isRegexp() == false) {
            return "BIND('" + config.getReplacement() + "'" + getLangTag() + " AS ?replacement)\n";
        } else {
            return "";
        }
    }

    private String buildLanguageFilterForRegexp () {
        if (config.isRegexp()) {
            return "FILTER(langMatches(lang(?o), '" + config.getLanguage() + "'))" + "\n";
        } else {
            return "";
        }
    }

    private String buildCondition(String val) {
        String query;
        if (config.isRegexp()) {
            query = buildRegexConditionWithReplacement(val);
        } else {
            query = buildSimpleCondition(val);
        }
        return query;
    }

    private String buildRegexConditionWithReplacement(String val) {
        String query;
        query = "FILTER(REGEX(?o, '" + val + "', '" + getRegexpFlags() + "'))\n" +
                "BIND(" +
                    buildRegexpReplacement(val) +
                "AS ?replacement)\n";
        return query;
    }

    private String buildSimpleCondition(String val) {
        String query;
        if (config.isCaseSensitive()) {
            query = "FILTER(?o = '" + val + "'" + getLangTag() + ")\n";
        } else {
            query = "FILTER(LCASE(?o) = '" + val.toLowerCase() + "'" + getLangTag() + ")\n";
        }

        return query;
    }

    private String buildRegexpReplacement(String val) {
        return "REPLACE(" +
                "?o, " +
                "'^(.*)" + val + "(.*)$', '" +
                "$1" + config.getReplacement() + "$2', " +
                "'" + getRegexpFlags() + "'" +
                ")";
    }

    private String getLangTag () {
        if (config.getLanguage() == "") {
            return "";
        } else {
            return "@" + config.getLanguage();
        }
    }

    private String getRegexpFlags () {
        if (config.isCaseInsensitive()) {
            return "i";
        }
        return "";
    }
}