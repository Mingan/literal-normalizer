package cz.opendata.linked.literal.normalizer;

import com.vaadin.data.Validator;
import com.vaadin.ui.*;
import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class TransformerDialog extends BaseConfigDialog<TransformerConfig> {

    private GridLayout mainLayout;

    private Label labelCondition;
    private TextArea textAreaCondition;

    private Label labelRemove;
    private TextField textFieldRemove;

    private Label labelToMatch;
    private TextArea textAreaToMatch;

    private Label labelReplacement;
    private TextField textFieldReplacement;

    private CheckBox checkboxRegexp;

    private CheckBox checkboxCase;

    private Label labelLang;
    private TextField textFieldLang;

    public TransformerDialog() {
		super(TransformerConfig.class);
        buildMainLayout();
        setCompositionRoot(mainLayout);
	}

    @Override
    public void setConfiguration(TransformerConfig config) throws ConfigException {
        textAreaCondition.setValue(config.getCondition());
        textFieldRemove.setValue(config.getTripleToDelete());
        textAreaToMatch.setValue(config.getToMatchInString());
        textFieldReplacement.setValue(config.getReplacement());
        textFieldLang.setValue(config.getLanguage());
        checkboxRegexp.setValue(config.isRegexp());
        checkboxCase.setValue(config.isCaseSensitive());
   }

    @Override
    public TransformerConfig getConfiguration() throws ConfigException {
        TransformerConfig config = new TransformerConfig();

        config.setCondition(textAreaCondition.getValue().trim());
        config.setTripleToDelete(textFieldRemove.getValue().trim());
        config.setToMatch(parseToMatch());
        config.setReplacement(textFieldReplacement.getValue());
        config.setLanguage(textFieldLang.getValue());
        config.setRegexp(checkboxRegexp.getValue());
        config.setCaseSensitive(checkboxCase.getValue());

        return config;
    }

    private List<String> parseToMatch() {
        List<String> list = Arrays.asList(StringUtils.split(textAreaToMatch.getValue(), '\n'));
        List<String> filtered = new LinkedList<>();

        for (String line : list) {
            String cleaned = line.trim();
            if (cleaned.length() > 0) {
                filtered.add(cleaned);
            }
        }

        return filtered;
    }

    private void buildMainLayout() {
        // top-level component properties
        setWidth("100%");
        setHeight("100%");

        // common part: create layout
        mainLayout = new GridLayout(4, 10);
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.setMargin(false);
        mainLayout.setSpacing(true);


        mainLayout.setColumnExpandRatio(0, 0.49f);
        mainLayout.setColumnExpandRatio(1, 0.01f);
        mainLayout.setColumnExpandRatio(2, 0.01f);
        mainLayout.setColumnExpandRatio(3, 0.49f);

        buildConditionField();
        buildRemoveField();
        buildToMatchField();
        buildReplacementField();
        buildLangField();
        buildRegexpField();
        buildCaseInsensitiveField();
    }

    private void buildConditionField() {
        labelCondition = new Label();
        labelCondition.setImmediate(false);
        labelCondition.setWidth("100%");
        labelCondition.setHeight("-1px");
        labelCondition.setValue("SPARQL condition:");
        mainLayout.addComponent(labelCondition, 0, 0, 3, 0);

        textAreaCondition = new TextArea();
        textAreaCondition.setNullRepresentation("");
        textAreaCondition.setImmediate(true);
        textAreaCondition.setWidth("100%");
        textAreaCondition.setHeight("100px");
        textAreaCondition.setInputPrompt("?s ?p ?o .");
        textAreaCondition.setDescription("SPARQL condition snippet, ?o must be the literal being matched, no prefixes");
        textAreaCondition.addValidator(getRequiredValidator("SPARQL condition snippet is required. If the normalization should be applied to all literals enter \"?s ?p ?o\" ."));
        textAreaCondition.addValidator(getRequiredLiteralValidator("Literal which is being replaced must be bound to variable ?o."));
        mainLayout.addComponent(textAreaCondition, 0, 1, 3, 1);
    }

    private void buildRemoveField() {
        labelRemove = new Label();
        labelRemove.setImmediate(false);
        labelRemove.setWidth("100%");
        labelRemove.setHeight("-1px");
        labelRemove.setValue("Triple to change:");
        mainLayout.addComponent(labelRemove, 0, 2, 3, 2);

        textFieldRemove = new TextField();
        textFieldRemove.setNullRepresentation("");
        textFieldRemove.setImmediate(true);
        textFieldRemove.setWidth("100%");
        textFieldRemove.setInputPrompt("?s ?p ?o .");
        textFieldRemove.setDescription("Triple which will be modified, usually identical to a part of SPARQL condition");
        textFieldRemove.addValidator(getRequiredValidator("Triple is required."));
        textFieldRemove.addValidator(getRequiredLiteralValidator("Literal which is being replaced must be bound to variable ?o."));
        mainLayout.addComponent(textFieldRemove, 0, 3, 3, 3);
    }

    private void buildToMatchField() {
        labelToMatch = new Label();
        labelToMatch.setImmediate(false);
        labelToMatch.setWidth("100%");
        labelToMatch.setHeight("-1px");
        labelToMatch.setValue("Strings to replace:");
        mainLayout.addComponent(labelToMatch, 0, 4, 0, 4);

        textAreaToMatch = new TextArea();
        textAreaToMatch.setNullRepresentation("");
        textAreaToMatch.setImmediate(true);
        textAreaToMatch.setWidth("100%");
        textAreaToMatch.setHeight("200px");
        textAreaToMatch.setInputPrompt("czk\nKč");
        textAreaToMatch.setDescription("List of strings to be replaced, each value on a new line. The value is treated as a partial regular expression when the option is on");
        textAreaToMatch.addValidator(getEmptyListValidator("Enter at least one string to replace."));
        mainLayout.addComponent(textAreaToMatch, 0, 5, 0, 9);
    }
    
    private void buildReplacementField() {
        labelReplacement = new Label();
        labelReplacement.setImmediate(false);
        labelReplacement.setWidth("100%");
        labelReplacement.setHeight("-1px");
        labelReplacement.setValue("Normalized value:");
        mainLayout.addComponent(labelReplacement, 2, 4, 3, 4);

        textFieldReplacement = new TextField();
        textFieldReplacement.setNullRepresentation("");
        textFieldReplacement.setImmediate(true);
        textFieldReplacement.setWidth("100%");
        textFieldReplacement.setInputPrompt("CZK");
        mainLayout.addComponent(textFieldReplacement, 2, 5, 3, 5);
    }

    private void buildLangField() {
        labelLang = new Label();
        labelLang.setImmediate(false);
        labelLang.setWidth("100%");
        labelLang.setHeight("-1px");
        labelLang.setValue("Language tag:");
        mainLayout.addComponent(labelLang, 2, 6, 3, 6);

        textFieldLang = new TextField();
        textFieldLang.setNullRepresentation("");
        textFieldLang.setImmediate(true);
        textFieldLang.setWidth("100%");
        textFieldLang.setInputPrompt("cs");
        textFieldLang.setDescription("Tag is used both for filtering and tagging replacement values.");
        mainLayout.addComponent(textFieldLang, 2, 7, 3, 7);
    }
    
    private void buildRegexpField() {
        checkboxRegexp = new CheckBox("Use regular expression mode");
        checkboxRegexp.setDescription("Strings to replace should be treated as a partial regular expression");
        checkboxRegexp.setHeight("20px");
        mainLayout.addComponent(checkboxRegexp, 2, 9, 3, 9);
    }
    
    private void buildCaseInsensitiveField() {
        checkboxCase = new CheckBox("Case sensitivity");
        checkboxCase.setDescription("Adds i flag in regular expression mode, compares strings transformed by LCASE() function in SPARQL in simple mode");
        checkboxCase.setHeight("20px");
        mainLayout.addComponent(checkboxCase, 2, 8, 3, 8);
    }

    private Validator getRequiredValidator(final String message) {
        return new Validator() {
            @Override
            public void validate(Object value) throws InvalidValueException {
                if (value == null || value.equals("")) {
                    if (!getContext().isTemplate()) {
                        throw new InvalidValueException(message);
                    }
                }
            }
        };
    }

    private Validator getEmptyListValidator(final String message) {
        return new Validator() {
            @Override
            public void validate(Object value) throws InvalidValueException {
                if (value == null || value.equals("") || parseToMatch().size() == 0) {
                    if (!getContext().isTemplate()) {
                        throw new InvalidValueException(message);
                    }
                }
            }
        };
    }

    private Validator getRequiredLiteralValidator(final String message) {
        return new Validator() {
            @Override
            public void validate(Object value) throws InvalidValueException {
                Pattern regexp = Pattern.compile(".*\\s\\?o\\b.*", Pattern.DOTALL);
                Matcher matcher = regexp.matcher((String) value);
                if (!matcher.matches()) {
                    if (!getContext().isTemplate()) {
                        throw new InvalidValueException(message + " " + value);
                    }
                }
            }
        };
    }

}
