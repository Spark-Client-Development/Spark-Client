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

package io.github.impactdevelopment.simpletweaker.transform;

import io.github.impactdevelopment.simpletweaker.SimpleTweaker;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A basic transformer that uses ClassNodes
 *
 * @author Brady
 * @since 10/10/2018
 */
public class SimpleTransformer implements IClassTransformer {

    /**
     * The instance of {@link SimpleTransformer}
     */
    private static SimpleTransformer instance;

    /**
     * A {@link List} of all of the transformers
     */
    private final List<ITransformer> transformers = new ArrayList<>();

    // This should only be used by the launchwrapper
    public SimpleTransformer() {
        // Set the instance
        instance = this;
    }

    @Override
    public final byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null)
            return null;

        final List<ITransformer> transformers = getTransformers(transformedName);

        if (!transformers.isEmpty()) {
            try {
                ClassNode cn = new ClassNode();
                ClassReader cr = new ClassReader(basicClass);
                cr.accept(cn, 0);

                // Run all transformers on the Class
                transformers.forEach(transformer -> transformer.transform(cn));

                // Return transformed class bytecode
                ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                cn.accept(cw);
                return cw.toByteArray();
            } catch (Exception e) {
                SimpleTweaker.LOGGER.error("An exception occurred while transforming class", e);
            }
        }

        return basicClass;
    }

    /**
     * Registers all of the specified transformer classpaths.
     *
     * @param transformers The transformer classpaths
     */
    public final void registerAll(String... transformers) {
        Arrays.stream(transformers)
                .map(this::createTransformer)
                .filter(Objects::nonNull)
                .forEach(this.transformers::add);
    }

    /**
     * Registers all of the specified transformers.
     *
     * @param transformers The transformers
     */
    public final void registerAll(ITransformer... transformers) {
        this.transformers.addAll(Arrays.asList(transformers));
    }

    /**
     * Finds transformers that target the specified class. If a transformer doesn't
     * have any targets, it is assumed that it will accept any class.
     *
     * @param name Target class name
     * @return Valid transformers
     */
    private List<ITransformer> getTransformers(String name) {
        return transformers.stream()
                .filter(transformer -> transformer.isTarget(name))
                .collect(Collectors.toList());
    }

    /**
     * Instantiates a new transformer using reflection. Transformer must
     * have a constructor that takes no parameters.
     *
     * @param clazz The transformer class name
     * @return The transformer, {@code null} if an exception occurred.
     */
    private ITransformer createTransformer(String clazz) {
        try {
            return (ITransformer) Class.forName(clazz).newInstance();
        } catch (Exception e) {
            SimpleTweaker.LOGGER.error("Unable to instantiate Transformer", e);
        }
        return null;
    }

    /**
     * @return The instance of {@link SimpleTransformer}
     */
    public static SimpleTransformer getInstance() {
        return instance;
    }
}
