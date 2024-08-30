package org.cache2k.spi;

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
import org.cache2k.Cache;
import org.cache2k.CacheManager;
import org.cache2k.config.Cache2kConfig;

/**
 * Interface to the cache2k implementation. This interface is not intended for the application
 * usage. Use the static methods on the {@link CacheManager}.
 *
 * @author Jens Wilke; created: 2015-03-26
 */
@org.checkerframework.framework.qual.AnnotatedFor("org.checkerframework.checker.nullness.NullnessChecker")
public interface Cache2kCoreProvider {

    /**
     * @see CacheManager#setDefaultName(String)
     */
    @org.checkerframework.dataflow.qual.SideEffectFree
    void setDefaultManagerName(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ClassLoader cl, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String s);

    /**
     * @see CacheManager#getDefaultName()
     */
    @org.checkerframework.dataflow.qual.Pure
    String getDefaultManagerName(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ClassLoader cl);

    /**
     * @see CacheManager#getInstance(ClassLoader, String)
     */
    @org.checkerframework.dataflow.qual.Pure
    CacheManager getManager(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ClassLoader cl, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String name);

    /**
     * Default class loader, this is the class loader used to load the cache implementation.
     */
    @org.checkerframework.dataflow.qual.Pure
    ClassLoader getDefaultClassLoader();

    /**
     * Close all cache2k cache managers.
     */
    @org.checkerframework.dataflow.qual.SideEffectFree
    void close();

    /**
     * Close all cache manager associated to this class loader.
     */
    @org.checkerframework.dataflow.qual.SideEffectFree
    void close(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ClassLoader l);

    /**
     * Close a specific cache manager by its name.
     */
    @org.checkerframework.dataflow.qual.SideEffectFree
    void close(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ClassLoader l, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String managerName);

    /**
     * Create a cache, apply external configuration before creating it.
     */
    @org.checkerframework.dataflow.qual.Pure
    <K, V> Cache<K, V> createCache(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CacheManager m, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Cache2kConfig<K, V> cfg);

    /**
     * Return the effective default configuration for this manager. A different default
     * configuration may be provided by the configuration system.
     *
     * @return mutable configuration instance containing the effective configuration defaults,
     *         never {@code null}
     */
    @org.checkerframework.dataflow.qual.Pure
    Cache2kConfig getDefaultConfig(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CacheManager m);

    /**
     * @since 2
     */
    @org.checkerframework.dataflow.qual.Pure
    String getVersion();
}
