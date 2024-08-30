package org.cache2k.integration;

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
import org.cache2k.io.LoadExceptionInfo;

/**
 * @author Jens Wilke
 * @deprecated replaced with {@link LoadExceptionInfo}, to be removed in version 2.2
 */
@org.checkerframework.framework.qual.AnnotatedFor("org.checkerframework.checker.nullness.NullnessChecker")
public interface ExceptionInformation {

    /**
     * The exception propagator in effect.
     *
     * @since 1.4
     */
    @org.checkerframework.dataflow.qual.Pure
    @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ExceptionPropagator getExceptionPropagator();

    /**
     * The original exception generated by the last recent loader call.
     */
    @org.checkerframework.dataflow.qual.Pure
    @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Throwable getException();

    /**
     * Number of retry attempts to load the value for the requested key.
     * The value is starting 0 for the first load attempt that yields an
     * exception. The counter is incremented for each consecutive
     * loader exception. After a successful attempt to load the value the
     * counter is reset.
     *
     * @return counter starting at 0 for the first load attempt that
     *         yields an exception.
     */
    @org.checkerframework.dataflow.qual.Pure
     @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int getRetryCount();

    /**
     * Start time of the load that generated the first exception.
     *
     * @return time in millis since epoch
     */
    @org.checkerframework.dataflow.qual.Pure
     @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull long getSinceTime();

    /**
     * Start time of the load operation that generated the recent exception.
     *
     * @return time in millis since epoch
     */
    @org.checkerframework.dataflow.qual.Pure
     @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull long getLoadTime();

    /**
     * Time in millis until the next retry attempt.
     * This property is only set in the context of the {@link ExceptionPropagator}.
     *
     * @return time in millis since epoch
     */
    @org.checkerframework.dataflow.qual.Pure
     @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull long getUntil();
}
