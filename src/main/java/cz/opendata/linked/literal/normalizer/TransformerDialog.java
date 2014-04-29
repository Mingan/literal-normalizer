package cz.opendata.linked.literal.normalizer;

import com.vaadin.data.Validator;
import com.vaadin.ui.*;
import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
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
    private TextArea textAreaReplacements;

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
        textAreaReplacements.setValue(config.getReplacementsInStringWithoutDuplicates());
        textFieldLang.setValue(config.getLanguage());
        checkboxRegexp.setValue(config.isRegexp());
        checkboxCase.setValue(config.isCaseSensitive());
   }

    @Override
    public TransformerConfig getConfiguration() throws ConfigException {
        TransformerConfig config = new TransformerConfig();

        config.setCondition(textAreaCondition.getValue().trim());
        config.setTripleToDelete(textFieldRemove.getValue().trim());
        config.setPairs(parseToMatch(), parseReplacements());
        config.setLanguage(textFieldLang.getValue());
        config.setRegexp(checkboxRegexp.getValue());
        config.setCaseSensitive(checkboxCase.getValue());

        return config;
    }

    private List<String> parseToMatch() {
        return Arrays.asList(StringUtils.split(textAreaToMatch.getValue(), '\n'));
    }

    private List<String> parseReplacements() {
        return Arrays.asList(StringUtils.splitPreserveAllTokens(textAreaReplacements.getValue(), '\n'));
    }

    private void buildMainLayout() {
        // top-level component properties
        setWidth("100%");
        setHeight("100%");

        // common part: create layout
        mainLayout = new GridLayout(3, 8);
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.setMargin(false);
        mainLayout.setSpacing(true);


        mainLayout.setColumnExpandRatio(0, 0.40f);
        mainLayout.setColumnExpandRatio(1, 0.40f);
        mainLayout.setColumnExpandRatio(2, 0.20f);

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
        mainLayout.addComponent(labelCondition, 0, 0, 2, 0);

        textAreaCondition = new TextArea();
        textAreaCondition.setNullRepresentation("");
        textAreaCondition.setImmediate(true);
        textAreaCondition.setWidth("100%");
        textAreaCondition.setHeight("100px");
        textAreaCondition.setInputPrompt("?s ?p ?o .");
        textAreaCondition.setDescription("SPARQL condition snippet, ?o must be the literal being matched, no prefixes");
        textAreaCondition.addValidator(getRequiredValidator("SPARQL condition snippet is required. If the normalization should be applied to all literals enter \"?s ?p ?o\" ."));
        textAreaCondition.addValidator(getRequiredLiteralValidator("Literal which is being replaced must be bound to variable ?o."));
        mainLayout.addComponent(textAreaCondition, 0, 1, 2, 1);
    }

    private void buildRemoveField() {
        labelRemove = new Label();
        labelRemove.setImmediate(false);
        labelRemove.setWidth("100%");
        labelRemove.setHeight("-1px");
        labelRemove.setValue("Triple to change:");
        mainLayout.addComponent(labelRemove, 0, 2, 2, 2);

        textFieldRemove = new TextField();
        textFieldRemove.setNullRepresentation("");
        textFieldRemove.setImmediate(true);
        textFieldRemove.setWidth("100%");
        textFieldRemove.setInputPrompt("?s ?p ?o .");
        textFieldRemove.setDescription("Triple which will be modified, usually identical to a part of SPARQL condition");
        textFieldRemove.addValidator(getRequiredValidator("Triple is required."));
        textFieldRemove.addValidator(getRequiredLiteralValidator("Literal which is being replaced must be bound to variable ?o."));
        mainLayout.addComponent(textFieldRemove, 0, 3, 2, 3);
    }

    private void buildToMatchField() {
        labelToMatch = new Label();
        labelToMatch.setImmediate(false);
        labelToMatch.setWidth("100%");
        labelToMatch.setHeight("-1px");
        labelToMatch.setValue("Strings to replace:");
        mainLayout.addComponent(labelToMatch, 0, 4);

        textAreaToMatch = new TextArea();
        textAreaToMatch.setNullRepresentation("");
        textAreaToMatch.setImmediate(true);
        textAreaToMatch.setWidth("100%");
        textAreaToMatch.setHeight("200px");
        textAreaToMatch.setInputPrompt("czk\nKč\neur\neuro");
        textAreaToMatch.setDescription("List of strings to be replaced, each value on a new line. The value is treated as a partial regular expression when the option is on");
        textAreaToMatch.addValidator(getRequiredValidator("Enter at least one string to replace."));
        mainLayout.addComponent(textAreaToMatch, 0, 5, 0, 7);
    }
    
    private void buildReplacementField() {
        labelReplacement = new Label();
        labelReplacement.setImmediate(false);
        labelReplacement.setWidth("100%");
        labelReplacement.setHeight("-1px");
        labelReplacement.setValue("Normalized values:");
        mainLayout.addComponent(labelReplacement, 1, 4);

        textAreaReplacements = new TextArea();
        textAreaReplacements.setDescription("List of normalized values, each value on a new line. Lines correspond to left column. If multiple strings have one normalized value, enter the normalized value only on the first line and then add blanks. The values are treated as a partial regular expression when the option is on");
        textAreaReplacements.setNullRepresentation("");
        textAreaReplacements.setImmediate(true);
        textAreaReplacements.setWidth("100%");
        textAreaReplacements.setHeight("200px");
        textAreaReplacements.setInputPrompt("CZK\n\n€");
        textAreaReplacements.addValidator(getRequiredValidator("Enter at least one replacement string."));
        mainLayout.addComponent(textAreaReplacements, 1, 5, 1, 7);
    }

    private void buildLangField() {
        textFieldLang = new TextField("Language tag:");
        textFieldLang.setNullRepresentation("");
        textFieldLang.setImmediate(true);
        textFieldLang.setWidth("120px");
        textFieldLang.setInputPrompt("cs");
        textFieldLang.setDescription("Tag is used both for filtering and tagging replacement values.");
        mainLayout.addComponent(textFieldLang, 2, 5);
    }
    
    private void buildRegexpField() {
        checkboxRegexp = new CheckBox("Use regular expression mode");
        checkboxRegexp.setDescription("Strings to replace should be treated as a partial regular expression");
        checkboxRegexp.setHeight("-1px");
        checkboxRegexp.setWidth("120px");
        mainLayout.addComponent(checkboxRegexp, 2, 6);
    }
    
    private void buildCaseInsensitiveField() {
        checkboxCase = new CheckBox("Case sensitivity");
        checkboxCase.setDescription("Adds i flag in regular expression mode, compares strings transformed by LCASE() function in SPARQL in simple mode");
        checkboxCase.setHeight("-1px");
        checkboxCase.setWidth("120px");
        mainLayout.addComponent(checkboxCase, 2, 7);
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
