package org.cache2k.config;

/*
 * #%L
 * cache2k API
 * %%
 * Copyright (C) 2000 - 2020 headissue GmbH, Munich
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
import org.checkerframework.checker.nullness.qual.Nullable;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Container for configuration objects. The container preserves the order of the sections
 * and checks that one type is only added once.
 *
 * @author Jens Wilke
 * @see ConfigWithSections
 */
@org.checkerframework.framework.qual.AnnotatedFor("org.checkerframework.checker.nullness.NullnessChecker")
public class SectionContainer extends AbstractCollection<ConfigSection> implements Collection<ConfigSection>, Serializable {

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Map<Class<? extends ConfigSection>, ConfigSection> class2section = new HashMap<>();

    /**
     * Add a new configuration section to the container.
     *
     * @throws IllegalArgumentException if same type is already present and a singleton
     * @return always {@code true}
     */
    @org.checkerframework.dataflow.qual.Impure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean add(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull SectionContainer this, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ConfigSection section) {
        if (getSection(section.getClass()) != null) {
            throw new IllegalArgumentException("Section of same type already inserted: " + section.getClass().getName());
        }
        class2section.put(section.getClass(), section);
        return true;
    }

    /**
     * Retrieve a single section from the container.
     */
    @org.checkerframework.dataflow.qual.Impure
    public <T extends ConfigSection> T getSection(Class<T> sectionType, T defaultFallback) {
        ConfigSection section = class2section.get(sectionType);
        return section != null ? sectionType.cast(section) : defaultFallback;
    }

    @org.checkerframework.dataflow.qual.Impure
    public <T extends ConfigSection> @org.checkerframework.checker.nullness.qual.Nullable T getSection(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Class<T> sectionType) {
        ConfigSection section = class2section.get(sectionType);
        return sectionType.cast(section);
    }

    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Iterator<ConfigSection> iterator(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull SectionContainer this) {
        return class2section.values().iterator();
    }

    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int size(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull SectionContainer this) {
        return class2section.size();
    }

    @org.checkerframework.dataflow.qual.Impure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String toString(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull SectionContainer this) {
        return getClass().getSimpleName() + class2section.values().toString();
    }
}
