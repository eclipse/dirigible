package org.eclipse.dirigible.runtime.chrome.debugger.communication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.chrome.debugger.DebugConfiguration;
import org.eclipse.dirigible.runtime.chrome.debugger.utils.URIUtils;

public class PageGetResourceTreeResponse extends MessageResponse {

	private Integer id;
	private Result result;

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Result getResult() {
		return this.result;
	}

	public static class Result {

		private FrameTree frameTree;

		public FrameTree getFrameTree() {
			return this.frameTree;
		}
	}

	public static class FrameTree {

		private Map<String, String> frame;
		private List<Map<String, String>> resources;

		public Map<String, String> getFrame() {
			return this.frame;
		}

		public List<Map<String, String>> getResources() {
			return this.resources;
		}
	}

	public static synchronized PageGetResourceTreeResponse buildForProjectsWithResources(
			Map<String, List<IResource>> resources) {
		if (resources == null) {
			throw new IllegalArgumentException("No resources were set for current debug session!");
		}
		final PageGetResourceTreeResponse resourceTree = new PageGetResourceTreeResponse();
		final Result result = new Result();
		result.frameTree = getFrameTreeFromResourcesMap(resources);
		resourceTree.result = result;
		return resourceTree;
	}

	private static FrameTree getFrameTreeFromResourcesMap(Map<String, List<IResource>> resources) {
		final FrameTree tree = new FrameTree();
		tree.frame = getFrameForResourcesMap(resources);
		tree.resources = getResourcesForResourcesMap(resources);
		return tree;
	}

	private static Map<String, String> getFrameForResourcesMap(Map<String, List<IResource>> resources) {
		final Map<String, String> frame = new HashMap<String, String>();
		IResource firstResource = getShortestPathResource(resources);
		String id = getRandomId();
		String loaderId = getLoaderId(id);
		frame.put("id", id);
		frame.put("loaderId", loaderId);
		frame.put("securityOrigin", DebugConfiguration.getBaseSourceUrl());
		frame.put("url", URIUtils.getUrlForResource(firstResource));
		return frame;
	}

	private static IResource getShortestPathResource(Map<String, List<IResource>> resources) {
		Collection<List<IResource>> projectResources = resources.values();
		List<IResource> resourceNames = new ArrayList<IResource>();
		for (List<IResource> project : projectResources) {
			resourceNames.addAll(project);
		}

		Collections.sort(resourceNames, new Comparator<IResource>() {
			public int compare(IResource o1, IResource o2) {
				String resourcePath1 = URIUtils.getUrlForResource(o1);
				String resourcePath2 = URIUtils.getUrlForResource(o2);
				return resourcePath1.compareTo(resourcePath2);
			}
		});
		return resourceNames.get(0);
	}

	private static String getRandomId() {
		Integer id = Integer.valueOf(new Random().nextInt(9999) + 1);
		return String.format("%d.%d", id, 2);
	}

	private static String getLoaderId(String id) {
		String[] split = id.split("\\.");
		String base = split[0];
		Integer remaining = Integer.valueOf(new Random().nextInt(99) + 1);
		return String.format("%s.%d",base, remaining );
	}

	private static List<Map<String, String>> getResourcesForResourcesMap(Map<String, List<IResource>> resources) {
		final List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		for (Map.Entry<String, List<IResource>> e : resources.entrySet()) {
			List<IResource> projectResources = e.getValue();
			for (IResource resource : projectResources) {
				Map<String, String> frameResource = getFrameResource(resource);
				result.add(frameResource);
			}
		}
		return result;
	}

	private static Map<String, String> getFrameResource(IResource resource) {
		final Map<String, String> resEl = new HashMap<String, String>();
		resEl.put("url", URIUtils.getUrlForResource(resource));
		final String contentType = resource.getContentType();
		resEl.put("mimeType", contentType);
		final String resourceType = URIUtils.getResourceTypeFromContentType(contentType);
		resEl.put("type", resourceType);
		return resEl;
	}
}
