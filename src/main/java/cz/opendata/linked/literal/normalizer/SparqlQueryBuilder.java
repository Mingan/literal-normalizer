package cz.opendata.linked.literal.normalizer;

import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.LinkedList;

public class SparqlQueryBuilder {

    static public String buildQueryFromConfig(TransformerConfig config) {
        String query =
                "DELETE { " + config.getTripleToDelete() + " }\n" +
                        "INSERT { " + config.getTripleToInsert() + " }\n" +
                        "WHERE {\n" +
                        buildAlternativeConditions(config) +
                        "}";

        System.out.println(query);
        return query;
    }

    static private String buildAlternativeConditions(TransformerConfig config) {
        LinkedList<String> alternatives = new LinkedList<>();
        Iterator<String> it = config.getToMatch().iterator();
        while (it.hasNext()) {
            String val = it.next();

            String query =
                    "{\n" +
                            config.getCondition() + "\n" +
                            "FILTER(isLiteral(?o))\n" +
                            buildConditionWithReplacement(config, val) +
                            "}\n";
            alternatives.add(query);
        }
        return StringUtils.join(alternatives, "UNION\n");
    }

    static private String buildConditionWithReplacement(TransformerConfig config, String val) {
        String query;
        if (config.isRegexp()) {
            query = "FILTER(REGEX(?o, '" + val + "', '" + getRegexpFlags(config) + "'))\n" +
                    "BIND(REPLACE(?o, '^(.*)" + val + "(.*)$', '$1" + config.getReplacement() + "$2', '" + getRegexpFlags(config) + "') AS ?replacement)\n";
        } else {
            query = "FILTER(?o = '" + val + "')\n" +
                    "BIND('" + config.getReplacement() + "' AS ?replacement)\n";
        }
        return query;
    }

    static private String getRegexpFlags(TransformerConfig config) {
        if (config.isCaseInsensitive()) {
            return "i";
        }
        return "";
    }
}