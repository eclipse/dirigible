package test.org.eclipse.dirigible.repository.ext;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.dirigible.repository.project.ProjectMetadata;
import org.eclipse.dirigible.repository.project.ProjectMetadataDependency;
import org.eclipse.dirigible.repository.project.ProjectMetadataLicense;
import org.eclipse.dirigible.repository.project.ProjectMetadataRepository;
import org.eclipse.dirigible.repository.project.ProjectMetadataUtils;
import org.junit.Test;

public class ProjectMetadataTest {

	@Test
	public void testSerialize() throws IOException {
		ProjectMetadata projectMetadata = new ProjectMetadata();
		projectMetadata.setGuid("travel_airports_services");
		projectMetadata.setName("Airports Services");
		projectMetadata.setComponent("Airports");
		projectMetadata.setDescription("OpenFlights Airports Services");
		projectMetadata.setAuthor("https://github.com/dirigiblelabs");

		ProjectMetadataLicense projectMetadataLicense = new ProjectMetadataLicense();
		projectMetadataLicense.setName("Eclipse Public License - v 1.0");
		projectMetadataLicense.setUrl("https://www.eclipse.org/legal/epl-v10.html");
		projectMetadata.setLicenses(new ProjectMetadataLicense[] { projectMetadataLicense });

		ProjectMetadataRepository projectMetadataRepository = new ProjectMetadataRepository();
		projectMetadataRepository.setType("git");
		projectMetadataRepository.setUrl("https://github.com/dirigiblelabs/travel_airports_services.git");
		projectMetadataRepository.setBranch("v1.0");
		projectMetadata.setRepository(projectMetadataRepository);

		ProjectMetadataDependency projectMetadataDependency = new ProjectMetadataDependency();
		projectMetadataDependency.setGuid("travel_airports_data");
		projectMetadataDependency.setType("git");
		projectMetadataDependency.setUrl("https://github.com/dirigiblelabs/travel_airports_data.git");
		projectMetadataDependency.setBranch("v1.0");
		projectMetadata.setDependencies(new ProjectMetadataDependency[] { projectMetadataDependency });

		String json = ProjectMetadataUtils.toJson(projectMetadata);
		System.out.println(json);

		ProjectMetadata projectMetadata2 = ProjectMetadataUtils.fromJson(json);

		assertTrue(projectMetadata.getLicenses()[0].getName().equals(projectMetadata2.getLicenses()[0].getName()));

	}

}
