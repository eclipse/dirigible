package org.eclipse.dirigible.api.v3.log.problems.facade;

import org.eclipse.dirigible.api.v3.log.problems.api.IDirigibleProblemsCoreService;
import org.eclipse.dirigible.api.v3.log.problems.exceptions.DirigibleProblemsException;
import org.eclipse.dirigible.api.v3.log.problems.model.DirigibleProblemsModel;
import org.eclipse.dirigible.api.v3.log.problems.service.DirigibleProblemsCoreService;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirigibleProblemsFacade implements IScriptingFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirigibleProblemsFacade.class);

    public static final void newProblem(String location, String type, String line, String column,
                                        String category, String module, String source, String program) throws DirigibleProblemsException {

        IDirigibleProblemsCoreService dirigibleProblemsCoreService = new DirigibleProblemsCoreService();
        DirigibleProblemsModel problemsModel = new DirigibleProblemsModel(location, type, line, column, category, module, source, program);
        dirigibleProblemsCoreService.createOrUpdateProblem(problemsModel);
        LOGGER.error(problemsModel.toJson());
    }
}
