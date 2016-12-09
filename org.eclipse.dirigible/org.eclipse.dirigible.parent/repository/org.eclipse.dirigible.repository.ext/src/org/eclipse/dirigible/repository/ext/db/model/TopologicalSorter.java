package org.eclipse.dirigible.repository.ext.db.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TopologicalSorter {

	private static final String CYCLIC_DEPENDENCY_S_IN_S = "Cyclic dependency %s in %s";

	public static void sort(Map<String, DataStructureModel> input, List<String> output, List<String> external)
			throws EDataStructureModelFormatException {

		List<String> processing = new ArrayList<String>();
		for (Entry<String, DataStructureModel> entry : input.entrySet()) {
			DataStructureModel dataStructureModel = entry.getValue();
			followDependencies(input, output, external, processing, dataStructureModel);
			String name = dataStructureModel.getName();
			if (!output.contains(name)) {
				output.add(name);
			}
		}

	}

	protected static void followDependencies(Map<String, DataStructureModel> input, List<String> output, List<String> external,
			List<String> processing, DataStructureModel dataStructureModel) throws EDataStructureModelFormatException {
		processing.add(dataStructureModel.getName());
		List<DependencyModel> dependencies = dataStructureModel.getDependencies();
		for (DependencyModel dependencyModel : dependencies) {
			String dependencyName = dependencyModel.getName();
			if (input.containsKey(dependencyName)) {
				if (processing.contains(dependencyName)) {
					throw new EDataStructureModelFormatException(
							String.format(CYCLIC_DEPENDENCY_S_IN_S, dataStructureModel.getName(), dependencyName));
				}
				DataStructureModel dependentModel = input.get(dependencyName);
				if (!output.contains(dependencyName)) {
					followDependencies(input, output, external, processing, dependentModel);
					output.add(dependencyName);
				}
			} else {
				external.add(dependencyName);
			}
		}
		processing.remove(dataStructureModel.getName());
	}

}
