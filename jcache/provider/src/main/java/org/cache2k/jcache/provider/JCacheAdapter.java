package org.cache2k.jcache.provider;

/*
 * #%L
 * cache2k JCache JSR107 implementation
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

import org.cache2k.Cache;
import org.cache2k.CacheEntry;
import org.cache2k.processor.CacheEntryProcessor;
import org.cache2k.integration.CacheWriterException;
import org.cache2k.processor.EntryProcessingResult;
import org.cache2k.integration.LoadCompletedListener;
import org.cache2k.processor.MutableCacheEntry;
import org.cache2k.CustomizationException;
import org.cache2k.impl.EntryAction;
import org.cache2k.impl.InternalCache;
import org.cache2k.jcache.provider.event.EventHandlingBase;

import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;

import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import javax.cache.processor.MutableEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Jens Wilke; created: 2015-03-28
 */
public class JCacheAdapter<K, V> implements javax.cache.Cache<K, V> {

  JCacheManagerAdapter manager;
  Cache<K, V> cache;
  InternalCache<K, V> cacheImpl;
  boolean storeByValue;
  boolean loaderConfigured = false;
  boolean readThrough = false;
  boolean statisticsEnabled = false;
  boolean configurationEnabled = false;
  AtomicLong iterationHitCorrectionCounter = new AtomicLong();
  AtomicLong missCorrectionCounter = new AtomicLong();
  AtomicLong hitCorrectionCounter = new AtomicLong();

  /** Null, if no complete configuration is effective */
  CompleteConfiguration<K, V> completeConfiguration;

  EventHandling<K,V> eventHandling;

  public JCacheAdapter(JCacheManagerAdapter _manager, Cache<K, V> _cache, CompleteConfiguration<K, V> _completeConfiguration) {
    manager = _manager;
    cache = _cache;
    cacheImpl = (InternalCache<K, V>) _cache;
    completeConfiguration = _completeConfiguration;
  }

  @Override
  public V get(K k) {
    checkClosed();
    if (readThrough) {
      return cache.get(k);
    }
    return cache.peek(k);
  }

  @Override
  public Map<K, V> getAll(Set<? extends K> _keys) {
    checkClosed();
    if (readThrough) {
      return cache.getAll(_keys);
    }
    return cache.peekAll(_keys);
  }

  @Override
  public boolean containsKey(K key) {
    checkClosed();
    return cache.contains(key);
  }

  @Override
  public void loadAll(Set<? extends K> keys, boolean replaceExistingValues, final CompletionListener completionListener) {
    checkClosed();
    if (!loaderConfigured) {
      if (completionListener != null) {
        completionListener.onCompletion();
      }
      return;
    }
    LoadCompletedListener l = null;
    if (completionListener != null) {
      l = new LoadCompletedListener() {
        @Override
        public void loadCompleted() {
          completionListener.onCompletion();
        }

        @Override
        public void loadException(Exception _exception) {
          completionListener.onException(_exception);
        }
      };
    }
    if (replaceExistingValues) {
      cache.reloadAll(keys, l);
    } else {
      cache.loadAll(keys, l);
    }
  }

  @Override
  public void put(K k, V v) {
    checkClosed();
    checkNullValue(v);
    try {
      cache.put(k, v);
    } catch (CacheWriterException ex) {
      throw new javax.cache.integration.CacheWriterException(ex);
    } catch (EntryAction.ListenerException ex) {
      throw new javax.cache.event.CacheEntryListenerException(ex);
    }
  }

  @Override
  public V getAndPut(K key, V _value) {
    checkClosed();
    checkNullValue(_value);
    try {
      return cache.peekAndPut(key, _value);
    } catch (CacheWriterException ex) {
      throw new javax.cache.integration.CacheWriterException(ex);
    } catch (EntryAction.ListenerException ex) {
      throw new javax.cache.event.CacheEntryListenerException(ex);
    }
  }

  void checkNullValue(V _value) {
    if (_value == null) {
      throw new NullPointerException("null value not supported");
    }
  }

  void checkNullKey(K key) {
    if (key == null) {
      throw new NullPointerException("null key not supported");
    }
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> map) {
    checkClosed();
    if (map == null) {
      throw new NullPointerException("null map parameter");
    }
    for (Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
      V v = e.getValue();
      checkNullValue(e.getValue());
    }
    try {
      cache.putAll(map);
    } catch (EntryAction.ListenerException ex) {
      throw new javax.cache.event.CacheEntryListenerException(ex);
    } catch (CacheWriterException ex) {
      throw new javax.cache.integration.CacheWriterException(ex);
    }
  }

  @Override
  public boolean putIfAbsent(K key, V _value) {
    checkClosed();
    checkNullValue(_value);
    try {
      return cache.putIfAbsent(key, _value);
    } catch (EntryAction.ListenerException ex) {
      throw new javax.cache.event.CacheEntryListenerException(ex);
    } catch (CacheWriterException ex) {
      throw new javax.cache.integration.CacheWriterException(ex);
    }
  }

  @Override
  public boolean remove(K key) {
    checkClosed();
    try {
      return cacheImpl.containsAndRemove(key);
    } catch (EntryAction.ListenerException ex) {
      throw new javax.cache.event.CacheEntryListenerException(ex);
    } catch (CacheWriterException ex) {
      throw new javax.cache.integration.CacheWriterException(ex);
    }
  }

  @Override
  public boolean remove(K key, V _oldValue) {
    checkClosed();
    checkNullValue(_oldValue);
    try {
      return cache.removeIfEquals(key, _oldValue);
    } catch (EntryAction.ListenerException ex) {
      throw new javax.cache.event.CacheEntryListenerException(ex);
    } catch (CacheWriterException ex) {
      throw new javax.cache.integration.CacheWriterException(ex);
    }
  }

  @Override
  public V getAndRemove(K key) {
    checkClosed();
    try {
      return cache.peekAndRemove(key);
    } catch (EntryAction.ListenerException ex) {
      throw new javax.cache.event.CacheEntryListenerException(ex);
    } catch (CacheWriterException ex) {
      throw new javax.cache.integration.CacheWriterException(ex);
    }
  }

  @Override
  public boolean replace(K key, V _oldValue, V _newValue) {
    checkClosed();
    checkNullValue(_oldValue);
    checkNullValue(_newValue);
    try {
      return cache.replaceIfEquals(key, _oldValue, _newValue);
    } catch (EntryAction.ListenerException ex) {
      throw new javax.cache.event.CacheEntryListenerException(ex);
    } catch (CacheWriterException ex) {
      throw new javax.cache.integration.CacheWriterException(ex);
    }
  }

  @Override
  public boolean replace(K key, V _value) {
    checkClosed();
    checkNullValue(_value);
    try {
      return cache.replace(key, _value);
    } catch (EntryAction.ListenerException ex) {
      throw new javax.cache.event.CacheEntryListenerException(ex);
    } catch (CacheWriterException ex) {
      throw new javax.cache.integration.CacheWriterException(ex);
    }
  }

  @Override
  public V getAndReplace(K key, V _value) {
    checkClosed();
    checkNullValue(_value);
    try {
      return cache.peekAndReplace(key, _value);
    } catch (EntryAction.ListenerException ex) {
      throw new javax.cache.event.CacheEntryListenerException(ex);
    } catch (CacheWriterException ex) {
      throw new javax.cache.integration.CacheWriterException(ex);
    }
  }

  @Override
  public void removeAll(Set<? extends K> keys) {
    checkClosed();
    try {
      cache.removeAll(keys);
    } catch (EntryAction.ListenerException ex) {
      throw new javax.cache.event.CacheEntryListenerException(ex);
    } catch (CacheWriterException ex) {
      throw new javax.cache.integration.CacheWriterException(ex);
    }
  }

  @Override
  public void removeAll() {
    checkClosed();
    try {
      cache.removeAll();
    } catch (EntryAction.ListenerException ex) {
      throw new javax.cache.event.CacheEntryListenerException(ex);
    } catch (CacheWriterException ex) {
      throw new javax.cache.integration.CacheWriterException(ex);
    }
  }

  @Override
  public void clear() {
    cache.clear();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <C extends Configuration<K, V>> C getConfiguration(Class<C> _class) {
    if (_class.isAssignableFrom(CompleteConfiguration.class)) {
      if (completeConfiguration != null) {
        return (C) completeConfiguration;
      }
      MutableConfiguration<K, V> cfg = new MutableConfiguration<K, V>();
      cfg.setTypes((Class<K>) cacheImpl.getKeyType(), (Class<V>) cacheImpl.getValueType());
      cfg.setStoreByValue(storeByValue);
      return (C) cfg;
    }
    return (C) new Configuration<K, V>() {
      @Override
      public Class<K> getKeyType() {
        return (Class<K>) cacheImpl.getKeyType();
      }

      @Override
      public Class<V> getValueType() {
        return (Class<V>) cacheImpl.getValueType();
      }

      @Override
      public boolean isStoreByValue() {
        return storeByValue;
      }
    };
  }

  @Override
  public <T> T invoke(K key, EntryProcessor<K, V, T> entryProcessor, Object... arguments) throws EntryProcessorException {
    checkClosed();
    checkNullKey(key);
    Map<K, EntryProcessorResult<T>> m = invokeAll(Collections.singleton(key), entryProcessor, arguments);
    return !m.isEmpty() ? m.values().iterator().next().get() : null;
  }

  @Override
  public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> keys, final EntryProcessor<K, V, T> entryProcessor, Object... arguments) {
    checkClosed();
    if (entryProcessor == null) {
      throw new NullPointerException("processor is null");
    }
    CacheEntryProcessor<K, V, T> p = new CacheEntryProcessor<K, V, T>() {
      @Override
      public T process(final MutableCacheEntry<K, V> e, Object... _objs) throws Exception {
        final boolean _alreadyExisting = e.exists();
        MutableEntryAdapter<K, V> me = new MutableEntryAdapter<K, V>(e);
        T _result = entryProcessor.process(me, _objs);
        if (!me.getInvoked && !_alreadyExisting) {
          missCorrectionCounter.incrementAndGet();
        }
        if (me.putRemoveInvoked && _alreadyExisting) {
          hitCorrectionCounter.incrementAndGet();
        }
        return _result;
      }
    };
    Map<K, EntryProcessingResult<T>> _result = cache.invokeAll(keys, p, arguments);
    Map<K, EntryProcessorResult<T>> _mappedResult = new HashMap<K, EntryProcessorResult<T>>();
    for (Map.Entry<K, EntryProcessingResult<T>> e : _result.entrySet()) {
      final EntryProcessingResult<T> pr = e.getValue();
      EntryProcessorResult<T> epr = new EntryProcessorResult<T>() {
        @Override
        public T get() throws EntryProcessorException {
          Throwable t = pr.getException();
          if (t != null) {
            if (t instanceof CustomizationException && t.getCause() instanceof EntryProcessorException) {
              throw (EntryProcessorException) t.getCause();
            }
            throw new EntryProcessorException(t);
          }
          return pr.getResult();
        }
      };
      _mappedResult.put(e.getKey(), epr);
    }
    return _mappedResult;
  }

  @Override
  public String getName() {
    return cache.getName();
  }

  @Override
  public CacheManager getCacheManager() {
    return manager;
  }

  @Override
  public void close() {
    cache.close();
  }

  @Override
  public boolean isClosed() {
    return cache.isClosed();
  }

  @Override
  public <T> T unwrap(Class<T> clazz) {
    if (Cache.class.equals(clazz)) {
      return (T) cache;
    }
    throw new IllegalArgumentException("requested class unknown");
  }

  @Override
  public void registerCacheEntryListener(CacheEntryListenerConfiguration<K, V> cfg) {
    eventHandling.registerListener(cfg);
  }

  @Override
  public void deregisterCacheEntryListener(CacheEntryListenerConfiguration<K, V> cfg) {
    eventHandling.unregisterListener(cfg);
  }

  @Override
  public Iterator<Entry<K, V>> iterator() {
    checkClosed();
    final Iterator<CacheEntry<K, V>> it = cache.iterator();
    return new Iterator<Entry<K, V>>() {

      CacheEntry<K, V> entry;

      @Override
      public boolean hasNext() {
        while(it.hasNext()) {
          entry = it.next();
          if (entry.getException() == null) {
            return true;
          }
        }
        entry = null;
        return false;
      }

      @Override
      public Entry<K, V> next() {
        if (entry == null && !hasNext()) {
          throw new NoSuchElementException();
        }
        iterationHitCorrectionCounter.incrementAndGet();
        return new Entry<K, V>() {
          @Override
          public K getKey() {
            return entry.getKey();
          }

          @Override
          public V getValue() {
            return entry.getValue();
          }

          @Override
          public <T> T unwrap(Class<T> _class) {
            if (CacheEntry.class.equals(_class)) {
              return (T) entry;
            }
            return null;
          }
        };
      }

      @Override
      public void remove() {
        it.remove();
      }
    };
  }

  private void checkClosed() {
    if (cache.isClosed()) {
      throw new IllegalStateException("cache is closed");
    }
  }

  private class MutableEntryAdapter<K, V> implements MutableEntry<K, V> {

    private boolean getInvoked = false;
    private boolean putRemoveInvoked = false;
    private final MutableCacheEntry<K, V> e;

    public MutableEntryAdapter(MutableCacheEntry<K, V> e) {
      this.e = e;
    }

    @Override
    public boolean exists() {
      return e.exists();
    }

    @Override
    public void remove() {
      putRemoveInvoked = true;
      e.remove();
    }

    @Override
    public void setValue(V value) {
      putRemoveInvoked = true;
      e.setValue(value);
    }

    @Override
    public K getKey() {
      getInvoked = true;
      return e.getKey();
    }

    @Override
    public V getValue() {
      return e.getValue();
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
      return null;
    }
  }

  static class EventHandling<K,V> extends EventHandlingBase<K,V,V> {

    @Override
    protected V extractValue(final V _value) {
      return _value;
    }
  }

}