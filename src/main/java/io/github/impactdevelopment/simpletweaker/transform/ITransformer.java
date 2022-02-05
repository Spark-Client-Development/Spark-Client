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

import org.objectweb.asm.tree.ClassNode;

/**
 * A basic class transformer
 *
 * @author Brady
 * @since 10/10/2018
 */
public interface ITransformer {

    /**
     * Transforms the specified {@link ClassNode}
     *
     * @param cn The target {@link ClassNode}
     */
    void transform(ClassNode cn);

    /**
     * Returns whether or not a class is targeted by this transformer.
     * The given class may or may not be given in an obfuscated form.
     *
     * @return True if this transformer targets the given class
     */
    default boolean isTarget(String className) {
        return true;
    }
}
