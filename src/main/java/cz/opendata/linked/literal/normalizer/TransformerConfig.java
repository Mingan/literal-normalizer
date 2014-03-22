package cz.opendata.linked.literal.normalizer;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Put your DPU's configuration here.
 * 
 * You can optionally implement {@link #isValid()} to provide possibility
 * to validate the configuration.
 * 
 * <b>This class must have default (parameter less) constructor!</b>
 */
public class TransformerConfig extends DPUConfigObjectBase {

    private List<String> toMatch;
    private String replacement;
    private boolean regexp;
    private boolean caseSensitive;
    private String condition;
    private String tripleToDelete;

    // TransformerConfig must provide public non-parametric constructor
    public TransformerConfig() {
        setToMatch(new LinkedList<String>());
        setReplacement("");
        setCondition("");
        setTripleToDelete("");
        setRegexp(false);
        setCaseSensitive(true);
    }


    public List<String> getToMatch() {
        return toMatch;
    }

    public void setToMatch(List<String> toMatch) {
        this.toMatch = toMatch;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
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
}
