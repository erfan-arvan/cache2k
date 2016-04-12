package org.cache2k.customization;

/*
 * #%L
 * cache2k API only package
 * %%
 * Copyright (C) 2000 - 2016 headissue GmbH, Munich
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

import java.util.concurrent.TimeUnit;

/**
 * Interface to add to a value object if it is possible to derive the
 * expiry time from the value. If no explicit expiry calculator is set
 * and this interface is detected on the value, the expiry requested
 * from the value by the cache.
 *
 * @author Jens Wilke; created: 2014-10-15
 */
public interface ValueWithExpiryTime {

  /**
   * Point in time in milliseconds since when the value should expire.
   *
   * @return time the time of expiry in millis since epoch. {@link ExpiryCalculator#NO_CACHE} if it should not cached.
   *              {@link ExpiryCalculator#ETERNAL} if there is no specific expiry time known or needed.
   *              The effective expiry duration will never be longer than the
   *              configured expiry value via {@link org.cache2k.CacheBuilder#expiryDuration(long, TimeUnit)} (long, TimeUnit)}.
   *              If a negative value is returned, the negated value will be the expiry time
   *              used, but sharp expiry is requested always,
   *              ignoring {@link org.cache2k.CacheBuilder#sharpExpiry(boolean)}.
   */
  long getCacheExpiryTime();

}