package org.eclipse.dirigible.commons.api.topology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class TopologicalSorter {
	
	
    private Stack<TopologicalSorterNode> stack;
 
    public TopologicalSorter() {
        this.stack = new Stack<>();
    }
    
    public List<ITopologicallySortable> sort(List<ITopologicallySortable> list) {
    	Map<String, TopologicalSorterNode> nodes = new HashMap<>();
    	for (ITopologicallySortable sortable : list) {
    		TopologicalSorterNode node = new TopologicalSorterNode(sortable, nodes);
    		nodes.put(sortable.getId(), node);
    	}
    	
    	for (TopologicalSorterNode node : nodes.values()) {
	        topologicalSort(node);
    	}
    	
    	List<ITopologicallySortable> results = new ArrayList<>();
    	for (TopologicalSorterNode node : stack) {
    		results.add(node.getData());
    	}
    	
    	return results;
    }

	private void topologicalSort(TopologicalSorterNode node) {
		List<TopologicalSorterNode> dependencies = node.getDependencies();
		for (int i = 0; i < dependencies.size(); i++) {
			TopologicalSorterNode n = dependencies.get(i);
		    if (n != null && !n.isVisited()) {
		    	topologicalSort(n);
		        n.setVisited(true);
		    }
		}
		if (!stack.contains(node)) {
			stack.push(node);
		}
	}

}
