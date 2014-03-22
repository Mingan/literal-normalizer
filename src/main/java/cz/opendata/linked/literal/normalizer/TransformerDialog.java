package cz.opendata.linked.literal.normalizer;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
        checkboxRegexp.setValue(config.isRegexp());
        checkboxCase.setValue(config.isCaseInsensitive());

        checkboxCase.setEnabled(config.isRegexp());
    }

    @Override
    public TransformerConfig getConfiguration() throws ConfigException {
        TransformerConfig config = new TransformerConfig();

        config.setCondition(textAreaCondition.getValue().trim());
        config.setTripleToDelete(textFieldRemove.getValue().trim());
        config.setToMatch(parseToMatch());
        config.setReplacement(textFieldReplacement.getValue());
        config.setRegexp(checkboxRegexp.getValue());
        config.setCaseSensitive(checkboxCase.getValue());

        return config;
    }

    private List<String> parseToMatch() {
        List<String> list = Arrays.asList(textAreaToMatch.getValue());
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
        mainLayout = new GridLayout(4, 9);
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
        mainLayout.addComponent(textAreaToMatch, 0, 5, 0, 8);
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
    
    private void buildRegexpField() {
        checkboxRegexp = new CheckBox("Use regular expression mode");
        checkboxRegexp.setDescription("Strings to replace should be treated as a partial regular expression");
        checkboxRegexp.setHeight("20px");
        checkboxRegexp.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                checkboxCase.setEnabled(checkboxRegexp.getValue());
            }
        });
        mainLayout.addComponent(checkboxRegexp, 2, 6, 3, 6);
    }
    
    private void buildCaseInsensitiveField() {
        checkboxCase = new CheckBox("Use case insensitive regular expressions");
        checkboxCase.setDescription("Applicable only in regular expression mode");
        checkboxCase.setHeight("20px");
        mainLayout.addComponent(checkboxCase, 2, 7, 3, 7);
    }

}
