package cz.opendata.linked.literal-normalizer;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class TransformerDialog extends BaseConfigDialog<DPUTemplateConfig> {

	public TransformerDialog() {
		super(TransformerConfig.class);
	}

	@Override
	public void setConfiguration(TransformerConfig conf) throws ConfigException {
		// TODO : load configuration from function parameter into dialog
	}

	@Override
	public TransformerConfig getConfiguration() throws ConfigException {
		// TODO : gather information from dialog and store them into configuration, then return it
		return null;
	}

}
