package org.cache2k.testsuite.eviction;

/*
 * #%L
 * cache2k testsuite on public API
 * %%
 * Copyright (C) 2000 - 2021 headissue GmbH, Munich
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
import org.cache2k.Cache2kBuilder;
import org.cache2k.testing.SimulatedClock;
import org.cache2k.testing.category.TimingTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * Compare idle scanning to the established Time to Idle semantics
 *
 * @author Jens Wilke
 * @see <a href="https://github.com/cache2k/cache2k/issues/39">Github issue #39 - Time to Idle</a>
 */
@Category(TimingTests.class)
public class TimeTracePlaybackTest {

  static final boolean STAT_OUTPUT = true;
  static final boolean DEBUG_OUTPUT = false;

  /** Run with unbounded cache to get trace statistics */
  @Test
  public void outputTraceStatistics() {
    PlaybackResult res;
    res = playback(new LruCache(), Trace.WEBLOG424_NOROBOTS.get());
    if (STAT_OUTPUT) { System.out.println(res); }
  }

  @Test
  public void lruTimeToIdleTab() {
    System.out.println("_Established Time To Idle semantics_");
    System.out.println("| TTI/Minutes | Maximum cache size | Average cache size | Hitrate |");
    System.out.println("|---:|---:|---:|---:|");
    int[] trace = Trace.WEBLOG424_NOROBOTS.get();
    for (int i = 55; i <= 65; i++) {
      PlaybackResult res = playback(new LruCache(i * 60), trace);
      markdownRow(i, res);
    }
  }

  public void markdownRow(int i, PlaybackResult res) {
    System.out.println("| " + i + " | " + res.maxSize + " | " + res.getAverageSize() + " | " + res.getHitrate());
  }

  @Test
  public void cache2kIdleScanTab() {
    System.out.println("_Time To Idle emulation via scanning in cache2k 2.6_");
    System.out.println("| Scan round time/Minutes | Maximum cache size | Average cache size | Hitrate |");
    System.out.println("|---:|---:|---:|---:|");
    int[] trace = Trace.WEBLOG424_NOROBOTS.get();
    for (int i = 25; i <= 35; i++) {
      PlaybackResult res = runWithCache2k(Long.MAX_VALUE, i * 60, trace);
      markdownRow(i, res);
    }
  }

  @Test
  public void cache2kIdleScanTab2000Cap() {
    System.out.println("_Time To Idle emulation via scanning in cache2k 2.6 with capacity limit of 2000 entries_");
    System.out.println("| Scan round time/Minutes | Maximum cache size | Average cache size | Hitrate |");
    int[] trace = Trace.WEBLOG424_NOROBOTS.get();
    for (int i = 25; i <= 35; i++) {
      PlaybackResult res = runWithCache2k(2000, i * 60, trace);
      markdownRow(i, res);
    }
  }

  @Test
  public void cache2kIdleScanTab1000Cap() {
    int[] trace = Trace.WEBLOG424_NOROBOTS.get();
    for (int i = 25; i <= 35; i++) {
      PlaybackResult res = runWithCache2k(1000, i * 60, trace);
      markdownRow(i, res);
    }
  }

  public static PlaybackResult playback(CacheSimulation cache, int[] trace) {
    PlaybackResult result = new PlaybackResult();
    for (int i = 0; i < trace.length; i += 2) {
      int time = trace[i];
      int key = trace[i+1];
      boolean hit = cache.access(time, key);
      result.record(hit, cache.getSize());
      if (DEBUG_OUTPUT) {
        if (i % (trace.length / 1000) == 0) {
          System.err.println(cache);
        }
      }
    }
    return result;
  }

  static String extractColdSizeHotSize(Cache c) {
    String s = c.toString();
    int idx = s.indexOf("coldSize=");
    int idx2 = s.indexOf(", hotMax", idx);
    return s.substring(idx, idx2);
  }

  static long extractScanCount(Cache c) {
    String s = c.toString();
    final String str = "evictionScanCount=";
    int idx = s.indexOf(str);
    int idx2 = s.indexOf(", ", idx);
    return Long.parseLong(s.substring(idx + str.length(), idx2));
  }

  public static PlaybackResult runWithCache2k(long entryCapacity, long scanTimeSeconds, int[] trace) {
    SimulatedClock clock = new SimulatedClock(START_OFFSET_MILLIS);
    Cache<Integer, Data> cache =
      Cache2kBuilder.of(Integer.class, Data.class)
        .timeReference(clock)
        .executor(clock.wrapExecutor(ForkJoinPool.commonPool()))
        .idleScanTime(scanTimeSeconds, TimeUnit.SECONDS)
        .entryCapacity(entryCapacity)
        .strictEviction(true)
        .build();
    PlaybackResult res = playback(new Cache2kCache(clock, cache), trace);
    if (DEBUG_OUTPUT) {
      long scans = extractScanCount(cache);
      System.out.println("Scan count: " + scans);
      System.out.println("Scan per hour: " + (scans / 24));
      System.out.println(res);
      System.out.println(cache);
    }
    cache.close();
    return res;
  }

  static class PlaybackResult {
    long hitCount;
    long accumulatedSize;
    long requestCount;
    int maxSize = 0;

    public void record(boolean hit, int size) {
      requestCount++;
      if (hit) { hitCount++; }
      accumulatedSize += size;
      maxSize = Math.max(maxSize, size);
    }

    public String getHitrate() {
      return String.format("%.2f", hitCount * 100D / requestCount);
    }

    public long getAverageSize() {
      return accumulatedSize / requestCount;
    }

    @Override
    public String toString() {
      return "PlaybackResult{" +
        "hitCount=" + hitCount +
        ", averageSize=" + (accumulatedSize / requestCount) +
        ", maxSize=" + maxSize +
        ", requestCount=" + requestCount +
        ", hitrate=" + getHitrate() +
        '}';
    }
  }

  interface CacheSimulation {
    boolean access(int now, int key);
    int getSize();
  }

  static class Data {
    int key;
    Data prev, next = this;
    int lastAccess;
    private void remove() {
      next.prev = prev;
      prev.next = next;
      next = prev = null;
    }
    private void insert(Data head) {
      prev = head;
      next = head.next;
      next.prev = this;
      head.next = this;
    }
  }

  final static long START_OFFSET_MILLIS = 1000;

  static class Cache2kCache implements CacheSimulation {

    private final SimulatedClock clock;
    private final Cache<Integer, Data> cache;
    private int lastNow = -1;

    public Cache2kCache(SimulatedClock clock, Cache<Integer, Data> cache) {
      this.clock = clock;
      this.cache = cache;
    }

    @Override
    public boolean access(int time, int key) {
      advanceClock(time);
      Data d = cache.get(key);
      boolean hit = true;
      if (d == null) {
        d = new Data();
        d.key = key;
        cache.put(key, d);
        hit = false;
      }
      d.lastAccess = time;
      return hit;
    }

    private void advanceClock(int now) {
      if (now != lastNow) {
        long moveClock = (now * 1000 + START_OFFSET_MILLIS) - clock.millis();
        if (moveClock > 0) {
          try {
            clock.sleep(moveClock);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("interrupted");
          }
        }
      }
    }

    @Override
    public int getSize() {
      return cache.asMap().size();
    }

    @Override
    public String toString() {
      return extractColdSizeHotSize(cache);
    }

  }

  /**
   * Use LRU list for time to idle.
   */
  static class LruCache implements CacheSimulation {

    private final int timeToIdle;
    private final Data lru = new Data();
    private Map<Integer, Data> map = new HashMap<>();

    /** Default constructor, do not apply time to idle */
    public LruCache() {
      this(Integer.MAX_VALUE / 2);
    }

    public LruCache(int timeToIdle) {
      this.timeToIdle = timeToIdle;
    }

    @Override
    public boolean access(int now, int key) {
      Data d = map.get(key);
      boolean hit;
      if (hit = d != null) {
        d.remove();
      } else {
        d = new Data();
        d.key = key;
        map.put(key, d);
      }
      d.lastAccess = now;
      d.insert(lru);
      expireIdle(now);
      return hit;
    }

    private void expireIdle(int now) {
      while (lru.prev != lru && lru.prev.lastAccess <= (now - timeToIdle)) {
        map.remove(lru.prev.key);
        lru.prev.remove();
      }
    }

    @Override
    public int getSize() {
      return map.size();
    }

  }

}
