package org.eclipse.dirigible.api.v3.log.problems.service;

import org.eclipse.dirigible.api.v3.log.problems.api.IDirigibleProblemsCoreService;
import org.eclipse.dirigible.api.v3.log.problems.exceptions.DirigibleProblemsException;
import org.eclipse.dirigible.api.v3.log.problems.model.DirigibleProblemsModel;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

public class DirigibleProblemsCoreService implements IDirigibleProblemsCoreService {

    private DataSource dataSource = (DataSource) StaticObjects.get(StaticObjects.DATASOURCE);

    private PersistenceManager<DirigibleProblemsModel> persistenceManager = new PersistenceManager<DirigibleProblemsModel>();

    @Override
    public DirigibleProblemsModel createProblem(DirigibleProblemsModel problemsModel) throws DirigibleProblemsException {
        problemsModel.setCreatedBy(UserFacade.getName());
        problemsModel.setCreatedAt(new Timestamp(new java.util.Date().getTime()));

        try (Connection connection = dataSource.getConnection()) {
            persistenceManager.insert(connection, problemsModel);
            return problemsModel;
        } catch (SQLException e) {
            throw new DirigibleProblemsException(e);
        }
    }

    @Override
    public void createOrUpdateProblem(DirigibleProblemsModel toPersist) throws DirigibleProblemsException {

        DirigibleProblemsModel problemsModel = getProblem(toPersist.getLocation(), toPersist.getType(), toPersist.getLine());
        if (problemsModel == null) {
            createProblem(toPersist);
        } else {
            if (!problemsModel.equals(toPersist)) {
                problemsModel.setLocation(toPersist.getLocation());
                problemsModel.setType(toPersist.getType());
                problemsModel.setLine(toPersist.getLine());
                problemsModel.setSource(toPersist.getSource());
                updateProblem(problemsModel);
            }
        }    }

    @Override
    public boolean existsProblem(String location, String type, String line) throws DirigibleProblemsException {
        return getProblem(location, type, line) != null;
    }

    @Override
    public void updateProblem(DirigibleProblemsModel problemsModel) throws DirigibleProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            persistenceManager.update(connection, problemsModel);
        } catch (SQLException e) {
            throw new DirigibleProblemsException(e);
        }
    }

    @Override
    public DirigibleProblemsModel getProblem(String location, String type, String line) throws DirigibleProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = SqlFactory.getNative(connection).select().column("*").from("XSK_PARSER_ERRORS")
                    .where("PE_LOCATION = ? AND PE_TYPE = ? AND PE_LINE = ?").toString();
            List<DirigibleProblemsModel> result = persistenceManager.query(connection, DirigibleProblemsModel.class, sql, Arrays.asList(location, type, line));
            return result.isEmpty()? null : result.get(0);
        } catch (SQLException e) {
            throw new DirigibleProblemsException(e);
        }
    }

    @Override
    public DirigibleProblemsModel getProblemById(Long id) throws DirigibleProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            return persistenceManager.find(connection, DirigibleProblemsModel.class, id);
        } catch (SQLException e) {
            throw new DirigibleProblemsException(e);
        }
    }

    @Override
    public List<DirigibleProblemsModel> getAllProblems() throws DirigibleProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            return persistenceManager.findAll(connection, DirigibleProblemsModel.class);
        } catch (SQLException e) {
            throw new DirigibleProblemsException(e);
        }
    }
}
