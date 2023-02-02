/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.javascript.endpoint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.engine.javascript.service.JavascriptService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.RepositoryNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * The Class JavascriptEndpoint.
 */
@RestController
@RequestMapping({BaseEndpoint.PREFIX_ENDPOINT_SECURED + "js", BaseEndpoint.PREFIX_ENDPOINT_PUBLIC + "js"})
public class JavascriptEndpoint extends BaseEndpoint {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(JavascriptEndpoint.class.getCanonicalName());
	
		/** The Constant HTTP_PATH_MATCHER. */
	private static final String HTTP_PATH_MATCHER = "/{projectName}/{*projectFilePath}";
	
	
	/** The javascript service. */
	private final JavascriptService javascriptService;
	
	/** The repository. */
	private final IRepository repository;
	
	/**
	 * Instantiates a new javascript endpoint.
	 *
	 * @param javascriptService the javascript service
	 * @param repository the repository
	 */
	@Autowired
	public JavascriptEndpoint(JavascriptService javascriptService, IRepository repository) {
		this.javascriptService = javascriptService;
		this.repository = repository;
	}

	/**
	 * Gets the.
	 *
	 * @param projectName the project name
	 * @param projectFilePath the project file path
	 * @param debug the debug
	 * @return the response
	 */
	@GetMapping(HTTP_PATH_MATCHER)
	public ResponseEntity<?> get(
			@PathVariable("projectName") String projectName,
			@PathVariable("projectFilePath") String projectFilePath,
			@Nullable @RequestParam(required = false) MultiValueMap<String,String> params
	) {
		return executeJavaScript(projectName, projectFilePath, params, null);
	}
	
	/**
	 * Post.
	 *
	 * @param projectName the project name
	 * @param projectFilePath the project file path
	 * @param debug the debug
	 * @return the response
	 */
	@PostMapping(HTTP_PATH_MATCHER)
	public ResponseEntity<?> post(
			@PathVariable("projectName") String projectName,
			@PathVariable("projectFilePath") String projectFilePath,
			@Nullable @RequestParam(required = false) MultiValueMap<String,String> params
	) {
		return executeJavaScript(projectName, projectFilePath, params, null);
	}

	/**
	 * Post.
	 *
	 * @param projectName the project name
	 * @param projectFilePath the project file path
	 * @param debug the debug
	 * @return the response
	 */
	@PostMapping(value = HTTP_PATH_MATCHER, consumes = "multipart/form-data")
	public ResponseEntity<?> postFile(
			@PathVariable("projectName") String projectName,
			@PathVariable("projectFilePath") String projectFilePath,
			@Nullable @RequestParam(required = false) MultiValueMap<String,String> params,
			@Validated @RequestParam("file") MultipartFile file
	) {
		return executeJavaScript(projectName, projectFilePath, params, new MultipartFile[] {file});
	}
	
	/**
	 * Put.
	 *
	 * @param projectName the project name
	 * @param projectFilePath the project file path
	 * @param debug the debug
	 * @return the response
	 */
	@PutMapping(HTTP_PATH_MATCHER)
	public ResponseEntity<?> put(
			@PathVariable("projectName") String projectName,
			@PathVariable("projectFilePath") String projectFilePath,
			@Nullable @RequestParam(required = false) MultiValueMap<String,String> params
	) {
		return executeJavaScript(projectName, projectFilePath, params, null);
	}
	
	/**
	 * Put.
	 *
	 * @param projectName the project name
	 * @param projectFilePath the project file path
	 * @param debug the debug
	 * @return the response
	 */
	@PutMapping(value = HTTP_PATH_MATCHER, consumes = "multipart/form-data")
	public ResponseEntity<?> putFile(
			@PathVariable("projectName") String projectName,
			@PathVariable("projectFilePath") String projectFilePath,
			@Nullable @RequestParam(required = false) MultiValueMap<String,String> params,
			@Validated @RequestParam("file") MultipartFile file
	) {
		return executeJavaScript(projectName, projectFilePath, params, new MultipartFile[] {file});
	}

	/**
	 * Patch.
	 *
	 * @param projectName the project name
	 * @param projectFilePath the project file path
	 * @param debug the debug
	 * @return the response
	 */
	@PatchMapping(HTTP_PATH_MATCHER)
	public ResponseEntity<?> patch(
			@PathVariable("projectName") String projectName,
			@PathVariable("projectFilePath") String projectFilePath,
			@Nullable @RequestParam(required = false) MultiValueMap<String,String> params
	) {
		return executeJavaScript(projectName, projectFilePath, params, null);
	}

	/**
	 * Delete.
	 *
	 * @param projectName the project name
	 * @param projectFilePath the project file path
	 * @param debug the debug
	 * @return the response
	 */
	@DeleteMapping(HTTP_PATH_MATCHER)
	public ResponseEntity<?> delete(
			@PathVariable("projectName") String projectName,
			@PathVariable("projectFilePath") String projectFilePath,
			@Nullable @RequestParam(required = false) MultiValueMap<String,String> params
	) {
		return executeJavaScript(projectName, projectFilePath, params, null);
	}

	/**
	 * Execute java script.
	 *
	 * @param projectName the project name
	 * @param projectFilePath the project file path
	 * @param debug the debug
	 * @return the response
	 */
	private ResponseEntity<?> executeJavaScript(String projectName, String projectFilePath, 
			MultiValueMap<String,String> params, MultipartFile[] files) {
		String projectFilePathParam = extractPathParam(projectFilePath);
		projectFilePath = extractProjectFilePath(projectFilePath);
		return executeJavaScript(projectName, projectFilePath, projectFilePathParam, params, files);
	}
	
	
	/** The Constant CJS. */
	private static final String CJS = ".cjs/";

	/** The Constant MJS. */
	private static final String MJS = ".mjs/";

	/** The Constant JS. */
	private static final String JS = ".js/";

	/**
	 * Extract project file path.
	 *
	 * @param projectFilePath the project file path
	 * @return the string
	 */
	private String extractProjectFilePath(String projectFilePath) {
		if (projectFilePath.indexOf(JS) > 0) {
			projectFilePath = projectFilePath.substring(0, projectFilePath.indexOf(JS) + 3);
		} else if (projectFilePath.indexOf(MJS) > 0) {
			projectFilePath = projectFilePath.substring(0, projectFilePath.indexOf(MJS) + 4);
		} else if (projectFilePath.indexOf(CJS) > 0) {
			projectFilePath = projectFilePath.substring(0, projectFilePath.indexOf(CJS) + 4);
		}
		return projectFilePath;
	}

	/**
	 * Extract path param.
	 *
	 * @param projectFilePath the project file path
	 * @return the string
	 */
	private String extractPathParam(String projectFilePath) {
		String projectFilePathParam = "";
		if (projectFilePath.indexOf(JS) > 0) {
			projectFilePathParam = projectFilePath.substring(projectFilePath.indexOf(JS) + 3);
		} else if (projectFilePath.indexOf(MJS) > 0) {
			projectFilePathParam = projectFilePath.substring(projectFilePath.indexOf(MJS) + 4);
		} else if (projectFilePath.indexOf(CJS) > 0) {
			projectFilePathParam = projectFilePath.substring(projectFilePath.indexOf(CJS) + 4);
		}
		return projectFilePathParam;
	}

	/**
	 * Execute java script.
	 *
	 * @param projectName the project name
	 * @param projectFilePath the project file path
	 * @param projectFilePathParam the project file path param
	 * @param debug the debug
	 * @return the response
	 */
	private ResponseEntity<?> executeJavaScript(String projectName, String projectFilePath, String projectFilePathParam, 
			MultiValueMap<String,String> params, MultipartFile[] files) {
		try {
			if (!isValid(projectName) || !isValid(projectFilePath)) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN);
			}
			Map<Object,Object> context = new HashMap<Object, Object>();
			if (params != null) {
				context.put("params", params);
			}
			List<MultipartFile> filesList = new ArrayList<MultipartFile>();
			if (files != null) {
				for (MultipartFile file : files) {
					filesList.add(file);
				}
				context.put("files", filesList);
			}
			
			Object result = getJavascriptHandler().handleRequest(projectName,
					normalizePath(projectFilePath), normalizePath(projectFilePathParam),
					context, context.get("debug") != null);
			return ResponseEntity.ok(result);
		} catch (RepositoryNotFoundException e) {
			String message = e.getMessage() + ". Try to publish the service before execution.";
			throw new RepositoryNotFoundException(message, e);
		}
	}

	/**
	 * Gets the javascript handler.
	 *
	 * @return the javascript handler
	 */
	private JavascriptService getJavascriptHandler() {
		return javascriptService;
	}

	/**
	 * Gets the dirigible working directory.
	 *
	 * @return the dirigible working directory
	 */
	private java.nio.file.Path getDirigibleWorkingDirectory() {
		String publicRegistryPath = repository.getInternalResourcePath(IRepositoryStructure.PATH_REGISTRY_PUBLIC);
		return java.nio.file.Path.of(publicRegistryPath);
	}

	/**
	 * Checks if is valid.
	 *
	 * @param inputPath the input path
	 * @return true, if is valid
	 */
	public boolean isValid(String inputPath) {
		String registryPath = getDirigibleWorkingDirectory().toString();
		String normalizedInputPath = java.nio.file.Path.of(inputPath).normalize().toString();
		File file = new File(registryPath, normalizedInputPath);
		try {
			return file.getCanonicalPath().startsWith(registryPath);
		} catch (IOException e) {
			return false;
		}
	}
	
	private String normalizePath(String path) {
		if (path != null) {
			if (path.startsWith(IRepository.SEPARATOR)) {
				return path.substring(1);
			}
		}
		return path;
	}

}
