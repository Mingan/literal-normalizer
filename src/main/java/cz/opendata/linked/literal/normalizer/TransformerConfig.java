package cz.opendata.linked.literal.normalizer;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Put your DPU's configuration here.
 * 
 * You can optionally implement {@link #isValid()} to provide possibility
 * to validate the configuration.
 * 
 * <b>This class must have default (parameter less) constructor!</b>
 */
public class TransformerConfig extends DPUConfigObjectBase {

    private LinkedHashMap<String, String> pairs;
    private boolean regexp;
    private boolean caseSensitive;
    private String condition;
    private String tripleToDelete;
    private String language;

    public TransformerConfig() {
        setPairs(new LinkedHashMap<String, String>());
        setCondition("");
        setTripleToDelete("");
        setRegexp(false);
        setCaseSensitive(true);
        setLanguage("");
    }

    public Map<String, String> getPairs() {
        return pairs;
    }

    public void setPairs(LinkedHashMap<String, String> pairs) {
        this.pairs = pairs;
    }

    /**
     * Converts to lists from dialog to map of strings × strings, empty replacements are filled by previous replacements
     * @param toMatch
     * @param replacements
     */
    public void setPairs(List<String> toMatch, List<String> replacements) {
        pairs = new LinkedHashMap<>();
        String lastReplacement = null;
        for (int i = 0; i < toMatch.size(); i++) {
            String replacement = replacements.get(i);
            if (replacement == null || replacement.trim().equals("")) {
                replacement = lastReplacement;
            } else {
                lastReplacement = replacement;
            }
            if (replacement != null) {
                pairs.put(toMatch.get(i), replacement);
            }
        }
    }

    public List<String> getToMatch() {
        List<String> list = new LinkedList<>();
        list.addAll(pairs.keySet());
        return list;
    }

    public List<String> getReplacementsWithoutDuplicates() {
        List<String> replacements = new LinkedList<>();
        String lastDifferentReplacement = null;
        for (Map.Entry<String, String> entry : pairs.entrySet()) {
            String val = entry.getValue();
            if (val == lastDifferentReplacement) {
                replacements.add("");
            } else {
                replacements.add(val);
                lastDifferentReplacement = val;
            }
        }
        return replacements;
    }

    public void setRegexp(boolean regexp) {
        this.regexp = regexp;
    }

    public boolean isRegexp() {
        return regexp;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public String getTripleToDelete() {
        return tripleToDelete;
    }

    public void setTripleToDelete(String tripleToDelete) {
        this.tripleToDelete = tripleToDelete;
    }

    public String getTripleToInsert() {
        return getTripleToDelete().replace("?o", "?replacement");
    }

    public Boolean isCaseInsensitive() {
        return !isCaseSensitive();
    }

    public String getToMatchInString() {
        return StringUtils.join(getToMatch(), "\n");
    }

    public String getReplacementsInStringWithoutDuplicates() {
        return StringUtils.join(getReplacementsWithoutDuplicates(), "\n");
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "Condition: " + getCondition()
                + "; Triple: " + getTripleToDelete()
                + "; Pairs: " + pairs.toString()
                + "; Regexp on: " + (isRegexp() ? "yes" : "no")
                + "; Case insensitive on: " + (isCaseInsensitive() ? "yes" : "no")
                + "; Language: " + getLanguage();
    }
}
