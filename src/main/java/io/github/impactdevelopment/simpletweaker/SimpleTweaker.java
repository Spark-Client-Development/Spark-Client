/*
 * Copyright 2018 ImpactDevelopment
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

package io.github.impactdevelopment.simpletweaker;

import io.github.impactdevelopment.simpletweaker.argument.Argument;
import io.github.impactdevelopment.simpletweaker.argument.Arguments;
import io.github.impactdevelopment.simpletweaker.transform.SimpleTransformer;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Brady
 * @since 10/10/2018
 */
public class SimpleTweaker implements ITweaker {

    public static final Logger LOGGER = LogManager.getLogger("SimpleTweaker");

    private static boolean setupTransformer = false;

    /**
     * The raw game launch arguments that are provided in {@link SimpleTweaker#acceptOptions(List, File, File, String)}
     */
    private List<String> args;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        (this.args = new ArrayList<>()).addAll(args);
        addArg("gameDir", gameDir);
        addArg("assetsDir", assetsDir);
        addArg("version", profile);
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        doInitialSetup(classLoader);
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    @SuppressWarnings("unchecked")
    public String[] getLaunchArguments() {
        // Parse the arguments that we are able to pass to the game
        List<Argument> parsed = Arguments.parse(this.args);

        // Parse the arguments that are already being passed to the game
        List<Argument> existing = Arguments.parse((List<String>) Launch.blackboard.get("ArgumentList"));

        // Remove any arguments that conflict with existing ones
        parsed.removeIf(argument -> existing.stream().anyMatch(a -> a.conflicts(argument)));

        // Join back the filtered arguments and pass those to the game
        return Arguments.join(parsed).toArray(new String[0]);
    }

    private void addArg(String label, File file) {
        if (file != null)
            addArg(label, file.getAbsolutePath());
    }

    private void addArg(String label, String value) {
        if (!args.contains("--" + label) && value != null) {
            this.args.add("--" + label);
            this.args.add(value);
        }
    }

    private static void doInitialSetup(LaunchClassLoader classLoader) {
        if (!setupTransformer) {
            classLoader.addClassLoaderExclusion("io.github.impactdevelopment.simpletweaker.");
            classLoader.registerTransformer(SimpleTransformer.class.getName());
            setupTransformer = true;
        }
    }
}
