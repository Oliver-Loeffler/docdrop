/*-
 * #%L
 * docdrop
 * %%
 * Copyright (C) 2023 Oliver Loeffler, Raumzeitfalle.net
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package net.raumzeitfalle.docdrop.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import io.quarkus.qute.Template;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import net.raumzeitfalle.docdrop.Configuration;

@Startup
/**
 * Creates static HTML files needed for the web server to redirect from empty
 * non-functional locations to the upload or status form.
 */
public class ArtifactStorageInitializer {

    @Inject
    Configuration configuration;
    
    @Inject
    Template rootIndex;

    /**
     * Creates the {@code index.html} file inside the storage root directory.
     * This file will automatically forward to the upload form.
     */
    @PostConstruct
    public void createDataRootIndexFile() throws IOException {
        String uploadUrl = configuration.getUploadUrl();
        Path fileName = configuration.getStorageRoot().resolve("index.html");
        String html = rootIndex.instance().data("forwardurl", uploadUrl).render();
        Files.write(fileName, html.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
    }
    
}
