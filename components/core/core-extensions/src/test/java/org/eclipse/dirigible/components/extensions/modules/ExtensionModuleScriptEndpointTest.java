/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.extensions.modules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

/**
 * A page reads its JSON descriptor as the global it names, and nothing but a registered JSON module
 * can be read that way.
 */
@ExtendWith(MockitoExtension.class)
class ExtensionModuleScriptEndpointTest {

    @Mock
    private ExtensionModuleReader reader;

    @Test
    void serves_a_registered_json_module_as_the_global_the_page_reads() {
        Map<String, Object> descriptor = new LinkedHashMap<>();
        descriptor.put("id", "gitProjects");
        descriptor.put("label", "Git \"Projects\"");
        descriptor.put("contentTypes", List.of("text/plain"));
        when(reader.readRegisteredJsonModule("/view-git/configs/git-projects.json")).thenReturn(
                Optional.of(new ExtensionModule("view-git/configs/git-projects.json", null, descriptor)));

        ResponseEntity<String> response = endpoint().script("/view-git/configs/git-projects.json", "viewData");

        assertEquals("const viewData = {\"id\":\"gitProjects\",\"label\":\"Git \\\"Projects\\\"\",\"contentTypes\":[\"text/plain\"]};\n",
                response.getBody());
        assertEquals(MediaType.parseMediaType("text/javascript;charset=UTF-8"), response.getHeaders()
                                                                                        .getContentType());
        assertEquals("private, no-cache", response.getHeaders()
                                                  .getFirst(HttpHeaders.CACHE_CONTROL));
    }

    @Test
    void answers_not_found_for_a_module_no_visible_extension_registers() {
        when(reader.readRegisteredJsonModule("/app/data.json")).thenReturn(Optional.empty());

        ResponseStatusException refused =
                assertThrows(ResponseStatusException.class, () -> endpoint().script("/app/data.json", "viewData"));

        assertEquals(HttpStatus.NOT_FOUND, refused.getStatusCode());
    }

    @Test
    void refuses_a_global_that_is_not_an_identifier() {
        ResponseStatusException refused =
                assertThrows(ResponseStatusException.class, () -> endpoint().script("/app/view.json", "x;alert(1)//"));

        assertEquals(HttpStatus.BAD_REQUEST, refused.getStatusCode());
        verify(reader, never()).readRegisteredJsonModule(anyString());
    }

    private ExtensionModuleScriptEndpoint endpoint() {
        return new ExtensionModuleScriptEndpoint(reader);
    }
}
