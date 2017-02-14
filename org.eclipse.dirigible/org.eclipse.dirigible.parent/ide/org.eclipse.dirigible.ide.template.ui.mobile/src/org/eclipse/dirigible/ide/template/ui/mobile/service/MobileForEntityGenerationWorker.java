package org.eclipse.dirigible.ide.template.ui.mobile.service;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.dirigible.ide.template.ui.common.ContentForEntityGenerationWorker;
import org.eclipse.dirigible.ide.template.ui.common.GenerationModel;
import org.eclipse.dirigible.ide.template.ui.common.TemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.common.TemplateTypeDiscriminator;
import org.eclipse.dirigible.ide.template.ui.mobile.wizard.MobileForEntityTemplateGenerator;
import org.eclipse.dirigible.ide.template.ui.mobile.wizard.MobileForEntityTemplateModel;
import org.eclipse.dirigible.repository.api.IRepository;

public class MobileForEntityGenerationWorker extends ContentForEntityGenerationWorker {

	private static final MobileForEntityTemplateModel model = new MobileForEntityTemplateModel();
	private static final MobileForEntityTemplateGenerator generator = new MobileForEntityTemplateGenerator(model);
	private static final TemplateTypeDiscriminator typeDiscriminator = new MobileForEntityTemplateTypeDiscriminator();

	public MobileForEntityGenerationWorker(IRepository repository, IWorkspace workspace) {
		super(repository, workspace);
	}

	@Override
	protected TemplateGenerator getTemplateGenerator() {
		return this.generator;
	}

	@Override
	protected TemplateTypeDiscriminator getTypeDiscriminator() {
		return this.typeDiscriminator;
	}

	@Override
	protected GenerationModel getTemplateModel() {
		return this.model;
	}
}
