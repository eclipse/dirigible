package org.eclipse.dirigible.runtime.scripting;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.ext.generation.IGenerationService;
import org.eclipse.dirigible.repository.ext.generation.IGenerationWorker;
import org.eclipse.dirigible.repository.ext.generation.IGenerationWorkerProvider;
import org.eclipse.dirigible.repository.logging.Logger;
import org.osgi.framework.InvalidSyntaxException;

public class GenerationServiceFactoryNonOSGi implements IGenerationService {

	private static final Logger logger = Logger.getLogger(GenerationServiceFactoryOSGi.class);

	static List<IGenerationWorkerProvider> generationWorkerProviders = new ArrayList<IGenerationWorkerProvider>();

	static String dbGenerationWorkerProvider = "org.eclipse.dirigible.ide.template.ui.db.service.DataStructuresGenerationWorkerProvider";
	static String webGenerationWorkerProvider = "org.eclipse.dirigible.ide.template.ui.html.service.WebContentForEntityGenerationWorkerProvider";
	static String jsGenerationWorkerProvider = "org.eclipse.dirigible.ide.template.ui.js.service.ScriptingServicesGenerationWorkerProvider";
	static String mobileGenerationWorkerProvider = "org.eclipse.dirigible.ide.template.ui.mobile.service.MobileForEntityGenerationWorkerProvider";

	static {
		generationWorkerProviders.add(createGenerationWorkerProvider(dbGenerationWorkerProvider));
		generationWorkerProviders.add(createGenerationWorkerProvider(webGenerationWorkerProvider));
		generationWorkerProviders.add(createGenerationWorkerProvider(jsGenerationWorkerProvider));
		generationWorkerProviders.add(createGenerationWorkerProvider(mobileGenerationWorkerProvider));
	}

	@Override
	public IGenerationWorker getGenerationWorker(String type, HttpServletRequest request) {
		try {
			for (IGenerationWorkerProvider provider : generationWorkerProviders) {
				if (provider.getType().equals(type)) {
					return provider.createWorker(request);
				}
			}
		} catch (InvalidSyntaxException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public String[] getGenerationWorkerTypes() {
		List<String> types = new ArrayList<String>();
		for (IGenerationWorkerProvider provider : generationWorkerProviders) {
			types.add(provider.getType());
		}
		return types.toArray(new String[] {});
	}

	private static IGenerationWorkerProvider createGenerationWorkerProvider(String clazz) {
		try {
			return (IGenerationWorkerProvider) Class.forName(clazz).newInstance();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
