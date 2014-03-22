package cz.opendata.linked.literal.normalizer;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

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

    // TransformerConfig must provide public non-parametric constructor
    public TransformerConfig() {
        toMatch = new LinkedList<>();
        replacement = "";
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
}
