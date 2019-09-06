/*
 * Copyright 2012 the original author or authors.
 *
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
 */
package net.minecraftforge.gradle.userdev.util;

import com.google.common.base.Splitter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Minimized copy of Gradle's Jvm class.
 *
 * Forge edits:
 * - Use no internal Gradle API
 * - Use new Path API
 */
class Jvm {

    static Path findJavacExecutable() {
        Path javaHome = findJavaHome();
        Path javac = javaHome.resolve("bin").resolve(getJavacExecutableName());
        if (Files.exists(javac)) {
            return javac;
        }
        for (String path : Splitter.on(File.pathSeparatorChar).split(System.getenv("PATH"))) {
            javac = Paths.get(path, getJavacExecutableName());
            if (Files.exists(javac)) {
                return javac;
            }
        }
        return null;
    }

    private static String getJavacExecutableName() {
        return isWindows() ? "javac.exe" : "javac";
    }

    private static Path findJavaHome() {
        Path givenJavaHome = Paths.get(System.getProperty("java.home"));
        Path toolsJar = findToolsJar(givenJavaHome);
        if (toolsJar != null) {
            return toolsJar.subpath(0, toolsJar.getNameCount() - 2);
        } else if (givenJavaHome.getFileName().toString().equalsIgnoreCase("jre")
            && Files.exists(givenJavaHome.resolveSibling("bin/java"))) {
            return givenJavaHome.getParent();
        } else {
            return givenJavaHome;
        }
    }

    private static Path findToolsJar(Path javaHome) {
        Path toolsJar = javaHome.resolve("lib/tools.jar");
        if (Files.exists(toolsJar)) {
            return toolsJar;
        }
        String dirName = javaHome.getFileName().toString();
        if (dirName.equalsIgnoreCase("jre")) {
            toolsJar = javaHome.resolveSibling("lib/tools.jar");
            if (Files.exists(toolsJar)) {
                return toolsJar;
            }
        }

        if (isWindows()) {
            String version = System.getProperty("java.version");
            if (dirName.matches("jre\\d+") || dirName.equals("jre" + version)) {
                toolsJar = javaHome.resolveSibling("jdk" + version).resolve("lib/tools.jar");
                if (Files.exists(toolsJar)) {
                    return toolsJar;
                }
            }
        }

        return null;
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").contains("windows");
    }

}
