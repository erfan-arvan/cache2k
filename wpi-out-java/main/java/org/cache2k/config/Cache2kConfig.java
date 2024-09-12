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
import org.cache2k.Cache2kBuilder;
import org.cache2k.Weigher;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.cache2k.event.CacheEntryOperationListener;
import org.cache2k.event.CacheLifecycleListener;
import org.cache2k.expiry.ExpiryPolicy;
import org.cache2k.expiry.ExpiryTimeValues;
import org.cache2k.io.AdvancedCacheLoader;
import org.cache2k.io.AsyncCacheLoader;
import org.cache2k.io.CacheLoader;
import org.cache2k.io.CacheWriter;
import org.cache2k.io.ExceptionPropagator;
import org.cache2k.io.ResiliencePolicy;
import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Configuration for a cache2k cache.
 *
 * <p>To create a cache, the {@link Cache2kBuilder} is used. All configuration properties
 * are present on the builder and are documented in this place. Consequently all properties
 * refer to the corresponding builder method.
 *
 * <p>The configuration bean is designed to be serializable. This is used for example to copy
 * default configurations. The builder allows object references to customizations to be set.
 * If this happens the configuration is not serializable. Such configuration is only used for
 * immediate creation of one cache via the builder.
 *
 * <p>The configuration may contain additional beans, called configuration sections, that are
 * used to configure extensions or sub modules.
 *
 * <p>Within the XML configuration of a cache manager different default configuration
 * values may be specified. To get a configuration bean with the effective defaults of
 * a specific manager do {@code Cache2kBuilder.forUnknownTypes().manager(...).toConfiguration()}
 *
 * @author Jens Wilke
 */
@org.checkerframework.framework.qual.AnnotatedFor("org.checkerframework.checker.nullness.NullnessChecker")
public class Cache2kConfig<K, V> implements ConfigBean<Cache2kConfig<K, V>, Cache2kBuilder<K, V>>, ConfigWithSections {

    /**
     * A marker duration used in {@link #setExpireAfterWrite(Duration)}, to request
     * eternal expiry. The maximum duration after the duration is considered as eternal
     * for our purposes.
     */
    public static final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Duration EXPIRY_ETERNAL = Duration.ofMillis(ExpiryTimeValues.ETERNAL);

    /**
     * Marker duration that {@code setEternal(false)} was set.
     */
    public static final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Duration EXPIRY_NOT_ETERNAL = Duration.ofMillis(ExpiryTimeValues.ETERNAL - 1);

    public static final  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull long UNSET_LONG = -1;

    private  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean storeByReference;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull String name;

    private  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean nameWasGenerated;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull CacheType<K> keyType;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable CacheType<V> valueType;

    private  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull long entryCapacity = UNSET_LONG;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable Duration expireAfterWrite = null;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable Duration timerLag = null;

    private  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull long maximumWeight = UNSET_LONG;

    private  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int loaderThreadCount;

    private  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean eternal = false;

    private  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean keepDataAfterExpired = false;

    private  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean sharpExpiry = false;

    private  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean strictEviction = false;

    private  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean refreshAhead = false;

    private  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean permitNullValues = false;

    private  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean recordModificationTime = false;

    private  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean boostConcurrency = false;

    private  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean disableStatistics = false;

    private  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean disableMonitoring = false;

    private  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean externalConfigurationPresent = false;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull CustomizationSupplier<? extends Executor> loaderExecutor;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull CustomizationSupplier<? extends Executor> refreshExecutor;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull CustomizationSupplier<? extends Executor> asyncListenerExecutor;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull CustomizationSupplier<? extends Executor> executor;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull CustomizationSupplier<? extends ExpiryPolicy<K, V>> expiryPolicy;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable CustomizationSupplier<? extends ResiliencePolicy<K, V>> resiliencePolicy;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull CustomizationSupplier<? extends CacheLoader<K, V>> loader;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull CustomizationSupplier<? extends CacheWriter<K, V>> writer;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull CustomizationSupplier<? extends AdvancedCacheLoader<K, V>> advancedLoader;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull CustomizationSupplier<? extends AsyncCacheLoader<K, V>> asyncLoader;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull CustomizationSupplier<? extends ExceptionPropagator<K, V>> exceptionPropagator;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull CustomizationSupplier<? extends Weigher<K, V>> weigher;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull CustomizationCollection<CacheEntryOperationListener<K, V>> listeners;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull CustomizationCollection<CacheEntryOperationListener<K, V>> asyncListeners;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull Collection<CustomizationSupplier<CacheLifecycleListener>> lifecycleListeners;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull Set<Feature> features;

    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull SectionContainer sections;

    /**
     * Construct a config instance setting the type parameters and returning a
     * proper generic type.
     *
     * @see Cache2kBuilder#keyType(Class)
     * @see Cache2kBuilder#valueType(Class)
     */
    @org.checkerframework.dataflow.qual.Impure
    public static <K, V> @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Cache2kConfig<K, V> of(Class<K> keyType, Class<V> valueType) {
        Cache2kConfig<K, V> c = new Cache2kConfig<K, V>();
        c.setKeyType(CacheType.of(keyType));
        c.setValueType(CacheType.of(valueType));
        return c;
    }

    /**
     * Construct a config instance setting the type parameters and returning a
     * proper generic type.
     *
     * @see Cache2kBuilder#keyType(CacheType)
     * @see Cache2kBuilder#valueType(CacheType)
     */
    @org.checkerframework.dataflow.qual.Impure
    public static <K, V> @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Cache2kConfig<K, V> of(CacheType<K> keyType, CacheType<V> valueType) {
        Cache2kConfig<K, V> c = new Cache2kConfig<K, V>();
        c.setKeyType(keyType);
        c.setValueType(valueType);
        return c;
    }

    /**
     * @see Cache2kBuilder#name(String)
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable String getName() {
        return name;
    }

    /**
     * @see Cache2kBuilder#name(String)
     */
    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.name" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setName(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String name) {
        this.name = name;
    }

    /**
     * True if name is generated and not set by the cache client.
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean isNameWasGenerated() {
        return nameWasGenerated;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setNameWasGenerated(boolean v) {
        this.nameWasGenerated = v;
    }

    /**
     * @see Cache2kBuilder#entryCapacity
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull long getEntryCapacity() {
        return entryCapacity;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setEntryCapacity( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull long v) {
        this.entryCapacity = v;
    }

    /**
     * @see Cache2kBuilder#refreshAhead(boolean)
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean isRefreshAhead() {
        return refreshAhead;
    }

    /**
     * @see Cache2kBuilder#refreshAhead(boolean)
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setRefreshAhead( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean v) {
        this.refreshAhead = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable CacheType<K> getKeyType() {
        return keyType;
    }

    /**
     * @throws NullPointerException if parameter is null
     */
    @org.checkerframework.dataflow.qual.SideEffectFree
    public static void checkNull(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Object obj) {
        if (obj == null) {
            throw new NullPointerException("Null value not allowed");
        }
    }

    /**
     * @see Cache2kBuilder#keyType(CacheType)
     * @see CacheType for a general discussion on types
     */
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setKeyType(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CacheType<K> v) {
        if (v == null) {
            valueType = null;
            return;
        }
        if (v.isArray()) {
            throw new IllegalArgumentException("Arrays are not supported for keys");
        }
        keyType = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable CacheType<V> getValueType() {
        return valueType;
    }

    /**
     * @see Cache2kBuilder#valueType(CacheType)
     * @see CacheType for a general discussion on types
     */
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setValueType(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CacheType<V> v) {
        if (v == null) {
            valueType = null;
            return;
        }
        if (v.isArray()) {
            throw new IllegalArgumentException("Arrays are not supported for values");
        }
        valueType = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable Duration getExpireAfterWrite() {
        return expireAfterWrite;
    }

    /**
     * Sets expire after write. The value is capped at {@link #EXPIRY_ETERNAL}, meaning
     * an equal or higher duration is treated as eternal expiry.
     *
     * @see Cache2kBuilder#expireAfterWrite
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setExpireAfterWrite(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Duration v) {
        this.expireAfterWrite = durationCheckAndSanitize(v);
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean isEternal() {
        return eternal;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setEternal( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean v) {
        this.eternal = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable Duration getTimerLag() {
        return timerLag;
    }

    /**
     * @see Cache2kBuilder#timerLag(long, TimeUnit)
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setTimerLag(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Duration v) {
        this.timerLag = durationCheckAndSanitize(v);
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean isKeepDataAfterExpired() {
        return keepDataAfterExpired;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull long getMaximumWeight() {
        return maximumWeight;
    }

    /**
     * @see Cache2kBuilder#maximumWeight
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setMaximumWeight( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull long v) {
        if (entryCapacity >= 0) {
            throw new IllegalArgumentException("entryCapacity already set, setting maximumWeight is illegal");
        }
        maximumWeight = v;
    }

    /**
     * @see Cache2kBuilder#keepDataAfterExpired(boolean)
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setKeepDataAfterExpired( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean v) {
        this.keepDataAfterExpired = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean isSharpExpiry() {
        return sharpExpiry;
    }

    /**
     * @see Cache2kBuilder#sharpExpiry(boolean)
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setSharpExpiry( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean v) {
        this.sharpExpiry = v;
    }

    /**
     * An external configuration for the cache was found and is applied.
     * This is {@code true} if default values are set via the XML configuration or
     * if there is a specific section for the cache name.
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean isExternalConfigurationPresent() {
        return externalConfigurationPresent;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setExternalConfigurationPresent(boolean v) {
        externalConfigurationPresent = v;
    }

    /**
     * Mutable collection of additional configuration sections
     */
    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.sections" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull SectionContainer getSections(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Cache2kConfig<K, V> this) {
        if (sections == null) {
            sections = new SectionContainer();
        }
        return sections;
    }

    /**
     * Adds the collection of sections to the existing list. This method is intended to
     * improve integration with bean configuration mechanisms that use the set method and
     * construct a set or list, like Springs' bean XML configuration.
     */
    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.sections" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setSections(Collection<ConfigSection> c) {
        getSections().addAll(c);
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable CustomizationSupplier<? extends CacheLoader<K, V>> getLoader() {
        return loader;
    }

    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.loader" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setLoader(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CustomizationSupplier<? extends CacheLoader<K, V>> v) {
        loader = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable CustomizationSupplier<? extends AdvancedCacheLoader<K, V>> getAdvancedLoader() {
        return advancedLoader;
    }

    /**
     * @see Cache2kBuilder#loader(AdvancedCacheLoader)
     */
    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.advancedLoader" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setAdvancedLoader(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CustomizationSupplier<? extends AdvancedCacheLoader<K, V>> v) {
        advancedLoader = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable CustomizationSupplier<? extends AsyncCacheLoader<K, V>> getAsyncLoader() {
        return asyncLoader;
    }

    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.asyncLoader" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setAsyncLoader(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CustomizationSupplier<? extends AsyncCacheLoader<K, V>> v) {
        asyncLoader = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int getLoaderThreadCount() {
        return loaderThreadCount;
    }

    /**
     * @see Cache2kBuilder#loaderThreadCount(int)
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setLoaderThreadCount( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int v) {
        loaderThreadCount = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable CustomizationSupplier<? extends ExpiryPolicy<K, V>> getExpiryPolicy() {
        return expiryPolicy;
    }

    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.expiryPolicy" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setExpiryPolicy(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CustomizationSupplier<? extends ExpiryPolicy<K, V>> v) {
        expiryPolicy = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable CustomizationSupplier<? extends CacheWriter<K, V>> getWriter() {
        return writer;
    }

    /**
     * @see Cache2kBuilder#writer(CacheWriter)
     */
    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.writer" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setWriter(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CustomizationSupplier<? extends CacheWriter<K, V>> v) {
        writer = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean isStoreByReference() {
        return storeByReference;
    }

    /**
     * @see Cache2kBuilder#storeByReference(boolean)
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setStoreByReference( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean v) {
        storeByReference = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable CustomizationSupplier<? extends ExceptionPropagator<K, V>> getExceptionPropagator() {
        return exceptionPropagator;
    }

    /**
     * @see Cache2kBuilder#exceptionPropagator(ExceptionPropagator)
     */
    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.exceptionPropagator" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setExceptionPropagator(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CustomizationSupplier<? extends ExceptionPropagator<K, V>> v) {
        exceptionPropagator = v;
    }

    /**
     * A set of listeners. Listeners added in this collection will be
     * executed in a synchronous mode, meaning, further processing for
     * an entry will stall until a registered listener is executed.
     * The expiry will be always executed asynchronously.
     *
     * <p>A listener can be added by adding it to the collection.
     * Duplicate (in terms of equal objects) listeners will be ignored.
     *
     * @return Mutable collection of listeners
     */
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.listeners" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CustomizationCollection<CacheEntryOperationListener<K, V>> getListeners() {
        if (listeners == null) {
            listeners = new DefaultCustomizationCollection<CacheEntryOperationListener<K, V>>();
        }
        return listeners;
    }

    /**
     * @return True if listeners are added to this configuration.
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean hasListeners() {
        return listeners != null && !listeners.isEmpty();
    }

    /**
     * Adds the collection of customizations to the existing list. This method is intended to
     * improve integration with bean configuration mechanisms that use the set method and
     * construct a set or list, like Springs' bean XML configuration.
     */
    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.listeners" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setListeners(Collection<CustomizationSupplier<CacheEntryOperationListener<K, V>>> c) {
        getListeners().addAll(c);
    }

    /**
     * A set of listeners. A listener can be added by adding it to the collection.
     * Duplicate (in terms of equal objects) listeners will be ignored.
     *
     * @return Mutable collection of listeners
     */
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.asyncListeners" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CustomizationCollection<CacheEntryOperationListener<K, V>> getAsyncListeners() {
        if (asyncListeners == null) {
            asyncListeners = new DefaultCustomizationCollection<CacheEntryOperationListener<K, V>>();
        }
        return asyncListeners;
    }

    /**
     * @return True if listeners are added to this configuration.
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean hasAsyncListeners() {
        return asyncListeners != null && !asyncListeners.isEmpty();
    }

    /**
     * Adds the collection of customizations to the existing list. This method is intended to
     * improve integration with bean configuration mechanisms that use the set method and
     * construct a set or list, like Springs' bean XML configuration.
     */
    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.asyncListeners" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setAsyncListeners(Collection<CustomizationSupplier<CacheEntryOperationListener<K, V>>> c) {
        getAsyncListeners().addAll(c);
    }

    /**
     * A set of listeners. A listener can be added by adding it to the collection.
     * Duplicate (in terms of equal objects) listeners will be ignored.
     *
     * @return Mutable collection of listeners
     */
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.lifecycleListeners" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Collection<CustomizationSupplier<? extends CacheLifecycleListener>> getLifecycleListeners() {
        if (lifecycleListeners == null) {
            lifecycleListeners = new DefaultCustomizationCollection<CacheLifecycleListener>();
        }
        return (Collection<CustomizationSupplier<? extends CacheLifecycleListener>>) (Object) lifecycleListeners;
    }

    /**
     * @return True if listeners are added to this configuration.
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean hasLifecycleListeners() {
        return lifecycleListeners != null && !lifecycleListeners.isEmpty();
    }

    /**
     * Adds the collection of customizations to the existing list. This method is intended to
     * improve integration with bean configuration mechanisms that use the set method and
     * construct a set or list, like Springs' bean XML configuration.
     */
    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.lifecycleListeners" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setLifecycleListeners(Collection<CustomizationSupplier<? extends CacheLifecycleListener>> c) {
        getLifecycleListeners().addAll(c);
    }

    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.features" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Set<Feature> getFeatures() {
        if (features == null) {
            features = new HashSet<>();
        }
        return features;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean hasFeatures() {
        return features != null && !features.isEmpty();
    }

    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.features" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setFeatures(Set<? extends Feature> v) {
        getFeatures().addAll(v);
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable CustomizationSupplier<? extends ResiliencePolicy<K, V>> getResiliencePolicy() {
        return resiliencePolicy;
    }

    /**
     * @see Cache2kBuilder#resiliencePolicy
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setResiliencePolicy(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable CustomizationSupplier<? extends ResiliencePolicy<K, V>> v) {
        resiliencePolicy = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean isStrictEviction() {
        return strictEviction;
    }

    /**
     * @see Cache2kBuilder#strictEviction(boolean)
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setStrictEviction( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean v) {
        strictEviction = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean isPermitNullValues() {
        return permitNullValues;
    }

    /**
     * @see Cache2kBuilder#permitNullValues(boolean)
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setPermitNullValues( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean v) {
        permitNullValues = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean isDisableStatistics() {
        return disableStatistics;
    }

    /**
     * @see Cache2kBuilder#disableStatistics
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setDisableStatistics( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean v) {
        disableStatistics = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable CustomizationSupplier<? extends Executor> getLoaderExecutor() {
        return loaderExecutor;
    }

    /**
     * @see Cache2kBuilder#loaderExecutor(Executor)
     */
    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.loaderExecutor" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setLoaderExecutor(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CustomizationSupplier<? extends Executor> v) {
        loaderExecutor = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean isRecordModificationTime() {
        return recordModificationTime;
    }

    /**
     * @see Cache2kBuilder#recordModificationTime
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setRecordModificationTime( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean v) {
        recordModificationTime = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable CustomizationSupplier<? extends Executor> getRefreshExecutor() {
        return refreshExecutor;
    }

    /**
     * @see Cache2kBuilder#refreshExecutor(Executor)
     */
    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.refreshExecutor" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setRefreshExecutor(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CustomizationSupplier<? extends Executor> v) {
        refreshExecutor = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable CustomizationSupplier<? extends Executor> getExecutor() {
        return executor;
    }

    /**
     * @see Cache2kBuilder#executor(Executor)
     */
    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.executor" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setExecutor(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CustomizationSupplier<? extends Executor> v) {
        executor = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable CustomizationSupplier<? extends Executor> getAsyncListenerExecutor() {
        return asyncListenerExecutor;
    }

    /**
     * @see Cache2kBuilder#asyncListenerExecutor(Executor)
     */
    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.asyncListenerExecutor" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setAsyncListenerExecutor(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CustomizationSupplier<? extends Executor> v) {
        asyncListenerExecutor = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable CustomizationSupplier<? extends Weigher<K, V>> getWeigher() {
        return weigher;
    }

    /**
     * @see Cache2kBuilder#weigher(Weigher)
     */
    @org.checkerframework.checker.nullness.qual.EnsuresNonNull({ "this.weigher" })
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setWeigher(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CustomizationSupplier<? extends Weigher<K, V>> v) {
        /*
    if (entryCapacity >= 0) {
      throw new IllegalArgumentException(
        "entryCapacity already set, specifying a weigher is illegal");
    }
    */
        weigher = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean isBoostConcurrency() {
        return boostConcurrency;
    }

    /**
     * @see Cache2kBuilder#boostConcurrency(boolean)
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setBoostConcurrency( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean v) {
        boostConcurrency = v;
    }

    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean isDisableMonitoring() {
        return disableMonitoring;
    }

    /**
     * @see Cache2kBuilder#disableMonitoring(boolean)
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    public void setDisableMonitoring( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean disableMonitoring) {
        this.disableMonitoring = disableMonitoring;
    }

    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.RequiresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Impure
    private @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable Duration durationCheckAndSanitize(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Duration v) {
        if (v == null) {
            return null;
        }
        if (v.isNegative()) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        if (EXPIRY_ETERNAL.compareTo(v) <= 0) {
            v = EXPIRY_ETERNAL;
        }
        return v;
    }

    /**
     * Creates a cache builder from the configuration.
     */
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.advancedLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListenerExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.asyncLoader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.exceptionPropagator" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.executor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.expiryPolicy" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.features" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.keyType" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.lifecycleListeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.listeners" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loader" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.loaderExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.name" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.refreshExecutor" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.sections" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.weigher" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.framework.qual.EnsuresQualifier(expression = { "this.writer" }, qualifier = org.checkerframework.checker.nullness.qual.Nullable.class)
    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Cache2kBuilder<K, V> builder(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Cache2kConfig<K, V> this) {
        return Cache2kBuilder.of(this);
    }
}