package com.minecraft_ceo.cobblestonezufall;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class MiningRateTracker {
    private static final long MINUTE_WINDOW_MS = 60_000L;
    private static final long HOUR_WINDOW_MS = 3_600_000L;
    private static final long SAVE_INTERVAL_MS = 5_000L;
    private static final String TOTALS_FILE = "CobMiningTotals.csv";
    private static final MiningRateTracker INSTANCE = new MiningRateTracker();

    private final Map<UUID, Deque<Long>> breakTimesHour = new ConcurrentHashMap<>();
    private final Map<UUID, AtomicLong> totalBreaks = new ConcurrentHashMap<>();
    private final AtomicLong lastSaveEpochMs = new AtomicLong(0L);

    private MiningRateTracker() {
    }

    public static MiningRateTracker getInstance() {
        return INSTANCE;
    }

    public void recordBreak(UUID playerUuid) {
        if (playerUuid == null) {
            return;
        }
        long now = System.currentTimeMillis();
        Deque<Long> deque = breakTimesHour.computeIfAbsent(playerUuid, ignored -> new ArrayDeque<>());
        totalBreaks.computeIfAbsent(playerUuid, ignored -> new AtomicLong()).incrementAndGet();
        synchronized (deque) {
            deque.addLast(now);
            trim(deque, now);
        }
        saveIfDue(now);
    }

    public double getAverageBlocksPerSecond(UUID playerUuid) {
        if (playerUuid == null) {
            return 0.0d;
        }
        long now = System.currentTimeMillis();
        Deque<Long> deque = breakTimesHour.get(playerUuid);
        if (deque == null) {
            return 0.0d;
        }
        synchronized (deque) {
            trim(deque, now);
            if (deque.isEmpty()) {
                return 0.0d;
            }

            long firstTs = deque.peekFirst() == null ? now : deque.peekFirst();
            long elapsedMs = Math.max(1L, now - firstTs);
            long effectiveWindowMs = Math.min(MINUTE_WINDOW_MS, elapsedMs);
            double windowSeconds = Math.max(1.0d, effectiveWindowMs / 1000.0d);
            return deque.size() / windowSeconds;
        }
    }

    public double getAverageBlocksPerMinute(UUID playerUuid) {
        if (playerUuid == null) {
            return 0.0d;
        }
        long now = System.currentTimeMillis();
        Deque<Long> deque = breakTimesHour.get(playerUuid);
        if (deque == null) {
            return 0.0d;
        }
        synchronized (deque) {
            trim(deque, now);
            return (double) countRecent(deque, now - MINUTE_WINDOW_MS);
        }
    }

    public double getEstimatedBlocksPerHour(UUID playerUuid) {
        return getAverageBlocksPerMinute(playerUuid) * 60.0d;
    }

    public double getAverageBlocksPerHourRolling(UUID playerUuid) {
        if (playerUuid == null) {
            return 0.0d;
        }
        long now = System.currentTimeMillis();
        Deque<Long> deque = breakTimesHour.get(playerUuid);
        if (deque == null) {
            return 0.0d;
        }
        synchronized (deque) {
            trim(deque, now);
            return (double) countRecent(deque, now - HOUR_WINDOW_MS);
        }
    }

    public long getTotalBlocks(UUID playerUuid) {
        if (playerUuid == null) {
            return 0L;
        }
        AtomicLong total = totalBreaks.get(playerUuid);
        return total == null ? 0L : Math.max(0L, total.get());
    }

    public synchronized void load() {
        Path file = getTotalsFilePath();
        if (file == null || !Files.exists(file)) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.startsWith("PlayerUUID")) {
                    continue;
                }
                String[] parts = line.split(",", 2);
                if (parts.length < 2) {
                    continue;
                }
                try {
                    UUID uuid = UUID.fromString(parts[0].trim());
                    long total = Long.parseLong(parts[1].trim());
                    totalBreaks.put(uuid, new AtomicLong(Math.max(0L, total)));
                } catch (Exception ignored) {
                }
            }
        } catch (IOException ignored) {
        }
    }

    public synchronized void save() {
        Path file = getTotalsFilePath();
        if (file == null) {
            return;
        }
        try {
            Files.createDirectories(file.getParent());
            List<Map.Entry<UUID, AtomicLong>> entries = new ArrayList<>(totalBreaks.entrySet());
            entries.sort(Comparator.comparing(e -> e.getKey().toString()));
            try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                writer.write("PlayerUUID,TotalBlocks");
                writer.newLine();
                for (Map.Entry<UUID, AtomicLong> entry : entries) {
                    if (entry == null || entry.getKey() == null || entry.getValue() == null) {
                        continue;
                    }
                    long total = Math.max(0L, entry.getValue().get());
                    writer.write(entry.getKey().toString());
                    writer.write(",");
                    writer.write(Long.toString(total));
                    writer.newLine();
                }
            }
            lastSaveEpochMs.set(System.currentTimeMillis());
        } catch (IOException ignored) {
        }
    }

    private void saveIfDue(long now) {
        long last = lastSaveEpochMs.get();
        if (now - last < SAVE_INTERVAL_MS) {
            return;
        }
        if (lastSaveEpochMs.compareAndSet(last, now)) {
            save();
        }
    }

    private static void trim(Deque<Long> deque, long now) {
        while (!deque.isEmpty() && now - deque.peekFirst() > HOUR_WINDOW_MS) {
            deque.pollFirst();
        }
    }

    private static int countRecent(Deque<Long> deque, long cutoffEpochMs) {
        int count = 0;
        for (Long ts : deque) {
            if (ts != null && ts >= cutoffEpochMs) {
                count++;
            }
        }
        return count;
    }

    private static Path getTotalsFilePath() {
        CobblestoneZufallPlugin plugin = CobblestoneZufallPlugin.getInstance();
        if (plugin == null || plugin.getDataDirectory() == null) {
            return null;
        }
        return plugin.getDataDirectory().resolve(TOTALS_FILE);
    }
}
