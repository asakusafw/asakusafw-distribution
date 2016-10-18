/*
 * Copyright 2011-2016 Asakusa Framework Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.asakusafw.distribution.gradle.plugins

import org.gradle.util.GradleVersion
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.rules.TestName

import com.asakusafw.gradle.plugins.AsakusafwBasePlugin
import com.asakusafw.gradle.plugins.GradleTestkitHelper
import com.asakusafw.m3bp.gradle.plugins.AsakusafwM3bpPlugin
import com.asakusafw.spark.gradle.plugins.AsakusafwSparkPlugin
import com.asakusafw.vanilla.gradle.plugins.AsakusafwVanillaPlugin

/**
 * Tests for cross Gradle versions compatibility.
 */
class AsakusaUpgradeTest {

    /**
     * temporary project directory.
     */
    @Rule
    public final TemporaryFolder projectDir = new TemporaryFolder()

    /**
     * handles running test name.
     */
    @Rule
    public final TestName testName = new TestName()

    /**
     * Test for the system Gradle version.
     */
    @Test
    void system() {
        doUpgrade(GradleVersion.current().version)
    }

    /**
     * Test for {@code 3.1} (Asakusa Distribution {@code 0.9.0}).
     */
    @Test
    void 'v3.1'() {
        doUpgradeFromTestName()
    }

    /**
     * Test for {@code 2.14.1} (not released).
     */
    @Test
    void 'v2.14.1'() {
        doUpgradeFromTestName()
    }

    private void doUpgradeFromTestName() {
        doUpgrade(testName.methodName.replaceFirst('v', ''))
    }

    private void doUpgrade(String version) {
        Set<File> classpath = GradleTestkitHelper.toClasspath(
            AsakusafwBasePlugin,
            AsakusafwSparkPlugin,
            AsakusafwM3bpPlugin,
            AsakusafwVanillaPlugin,
            'META-INF/gradle-plugins/asakusafw-sdk.properties',
            'META-INF/gradle-plugins/asakusafw-spark.properties',
            'META-INF/gradle-plugins/asakusafw-m3bp.properties',
            'META-INF/gradle-plugins/asakusafw-vanilla.properties')
        String script = GradleTestkitHelper.getSimpleBuildScript(classpath,
            'asakusafw-sdk', 'asakusafw-organizer',
            'asakusafw-spark', 'asakusafw-m3bp', 'asakusafw-vanilla')
        GradleTestkitHelper.runGradle(projectDir.root, version, script, AsakusafwBasePlugin.TASK_UPGRADE)
    }
}
