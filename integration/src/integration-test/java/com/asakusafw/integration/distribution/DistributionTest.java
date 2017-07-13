/**
 * Copyright 2011-2017 Asakusa Framework Team.
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
package com.asakusafw.integration.distribution;

import static com.asakusafw.integration.distribution.Util.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Rule;
import org.junit.Test;

import com.asakusafw.integration.AsakusaConfigurator;
import com.asakusafw.integration.AsakusaProject;
import com.asakusafw.integration.AsakusaProjectProvider;
import com.asakusafw.utils.gradle.Bundle;
import com.asakusafw.utils.gradle.ContentsConfigurator;

/**
 * Test for {@code vanilla}.
 */
public class DistributionTest {

    /**
     * project provider.
     */
    @Rule
    public final AsakusaProjectProvider provider = new AsakusaProjectProvider()
            .withProject(ContentsConfigurator.copy(data("distribution")))
            .withProject(ContentsConfigurator.copy(data("ksv")))
            .withProject(ContentsConfigurator.copy(data("logback-test")))
            .withProject(AsakusaConfigurator.projectHome())
            .withProject(AsakusaConfigurator.hadoop(AsakusaConfigurator.Action.UNSET_ALWAYS));

    /**
     * help.
     */
    @Test
    public void help() {
        AsakusaProject project = provider.newInstance("prj");
        project.gradle("help");
    }

    /**
     * version.
     */
    @Test
    public void version() {
        AsakusaProject project = provider.newInstance("prj");
        project.gradle("asakusaVersions");
    }

    /**
     * upgrade.
     */
    @Test
    public void upgrade() {
        AsakusaProject project = provider.newInstance("prj");
        project.gradle("asakusaUpgrade");
        Bundle contents = project.getContents();
        assertThat(contents.find("gradlew"), is(not(Optional.empty())));
        assertThat(contents.find("gradlew.bat"), is(not(Optional.empty())));
    }

    /**
     * {@code assemble}.
     */
    @Test
    public void assemble() {
        AsakusaProject project = provider.newInstance("prj");
        project.gradle("assemble");
        Bundle contents = project.getContents();
        assertThat(contents.find("build/asakusafw-prj.tar.gz"), is(not(Optional.empty())));
    }

    /**
     * {@code installAsakusafw}.
     */
    @Test
    public void installAsakusafw() {
        AsakusaProject project = provider.newInstance("prj");
        project.gradle("installAsakusafw");
        Bundle framework = project.getFramework();
        assertThat(framework.find("vanilla"), is(not(Optional.empty())));
        assertThat(framework.find("spark"), is(not(Optional.empty())));
        assertThat(framework.find("m3bp"), is(not(Optional.empty())));
    }

    /**
     * {@code test}.
     */
    @Test
    public void test() {
        AsakusaProject project = provider.newInstance("prj")
                .with(AsakusaConfigurator.hadoop(AsakusaConfigurator.Action.UNSET_IF_UNDEFINED));
        project.gradle("installAsakusafw", "test");
    }

    /**
     * {@code yaess-batch.sh} w/ vanilla.
     */
    @Test
    public void yaess_vanilla() {
        AsakusaProject project = provider.newInstance("prj")
                .with(AsakusaConfigurator.hadoop(AsakusaConfigurator.Action.UNSET_IF_UNDEFINED));
        doYaess(project, "attachVanillaBatchapps", "vanilla.perf.average.sort");
    }

    /**
     * {@code yaess-batch.sh} w/ spark.
     */
    @Test
    public void yaess_spark() {
        AsakusaProject project = provider.newInstance("prj")
                .with(AsakusaConfigurator.spark(AsakusaConfigurator.Action.SKIP_IF_UNDEFINED));
        doYaess(project, "attachSparkBatchapps", "spark.perf.average.sort");
    }

    /**
     * {@code yaess-batch.sh} w/ m3bp.
     */
    @Test
    public void yaess_m3bp() {
        AsakusaProject project = provider.newInstance("prj")
                .with(AsakusaConfigurator.hadoop(AsakusaConfigurator.Action.UNSET_IF_UNDEFINED));
        doYaess(project, "attachM3bpBatchapps", "m3bp.perf.average.sort");
    }

    /**
     * {@code yaess-batch.sh} w/ mapreduce.
     */
    @Test
    public void yaess_mapreduce() {
        AsakusaProject project = provider.newInstance("prj")
                .with(AsakusaConfigurator.hadoop(AsakusaConfigurator.Action.SKIP_IF_UNDEFINED));
        doYaess(project, "attachMapreduceBatchapps", "perf.average.sort");
    }

    private static void doYaess(AsakusaProject project, String taskName, String batchId) {
        project.gradle(taskName, "installAsakusafw");

        String[] csv = new String[] {
                "1,1.0,A",
                "2,2.0,B",
                "3,3.0,C",
        };
        project.getContents().put("var/data/input/file.csv", f -> {
            Files.write(f, Arrays.asList(csv), StandardCharsets.UTF_8);
        });


        project.getFramework().withLaunch(
                "yaess/bin/yaess-batch.sh", batchId,
                "-A", "input=input", "-A", "output=output");

        project.getContents().get("var/data/output", dir -> {
            List<String> results = Files.list(dir)
                .flatMap(Util::lines)
                .sorted()
                .collect(Collectors.toList());
            assertThat(results, containsInAnyOrder(csv));
        });
    }
}