package com.minecraft_ceo.cobblestonezufall;

import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyBridge {
    private final Map<String, BigDecimal> balances = new HashMap<>();
    private Path storageFile;

    public static final class BalanceResult {
        public final boolean success;
        public final double balance;
        public final String message;

        private BalanceResult(boolean success, double balance, String message) {
            this.success = success;
            this.balance = balance;
            this.message = message;
        }

        public static BalanceResult ok(double balance) {
            return new BalanceResult(true, Math.max(0.0d, balance), "");
        }

        public static BalanceResult fail(String message) {
            return new BalanceResult(false, 0.0d, message);
        }
    }

    public static final class ChargeResult {
        public final boolean success;
        public final String message;

        private ChargeResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public static ChargeResult ok() {
            return new ChargeResult(true, "");
        }

        public static ChargeResult fail(String message) {
            return new ChargeResult(false, message);
        }
    }

    public synchronized void load() {
        balances.clear();
        try {
            CobblestoneZufallPlugin plugin = CobblestoneZufallPlugin.getInstance();
            if (plugin == null || plugin.getDataDirectory() == null) {
                return;
            }
            Files.createDirectories(plugin.getDataDirectory());
            storageFile = plugin.getDataDirectory().resolve("money.properties");
            if (!Files.exists(storageFile)) {
                save();
                return;
            }
            java.util.Properties props = new java.util.Properties();
            try (java.io.InputStream in = Files.newInputStream(storageFile)) {
                props.load(in);
            }
            for (String key : props.stringPropertyNames()) {
                String raw = props.getProperty(key);
                if (raw == null || raw.isBlank()) {
                    continue;
                }
                try {
                    balances.put(key, sanitize(new BigDecimal(raw.trim())));
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
    }

    public synchronized void save() {
        try {
            CobblestoneZufallPlugin plugin = CobblestoneZufallPlugin.getInstance();
            if (plugin == null || plugin.getDataDirectory() == null) {
                return;
            }
            Files.createDirectories(plugin.getDataDirectory());
            if (storageFile == null) {
                storageFile = plugin.getDataDirectory().resolve("money.properties");
            }
            java.util.Properties props = new java.util.Properties();
            for (Map.Entry<String, BigDecimal> entry : balances.entrySet()) {
                props.setProperty(entry.getKey(), sanitize(entry.getValue()).toPlainString());
            }
            try (java.io.OutputStream out = Files.newOutputStream(storageFile)) {
                props.store(out, "CobblestoneZufall local economy");
            }
        } catch (Exception ignored) {
        }
    }

    public ChargeResult chargePlayer(PlayerRef playerRef, double amount) {
        if (playerRef == null) {
            return ChargeResult.fail("Player unavailable.");
        }
        BigDecimal charge = sanitize(BigDecimal.valueOf(amount));
        if (charge.compareTo(BigDecimal.ZERO) <= 0) {
            return ChargeResult.ok();
        }

        UUID uuid = playerRef.getUuid();
        BigDecimal current = balanceOf(uuid);
        if (current.compareTo(charge) < 0) {
            return ChargeResult.fail("Not enough money.");
        }
        setBalance(uuid, current.subtract(charge));
        return ChargeResult.ok();
    }

    public BalanceResult getBalance(PlayerRef playerRef) {
        if (playerRef == null) {
            return BalanceResult.fail("Player unavailable.");
        }
        return BalanceResult.ok(balanceOf(playerRef.getUuid()).doubleValue());
    }

    public synchronized BigDecimal getBalance(UUID uuid) {
        return balanceOf(uuid);
    }

    public synchronized BigDecimal setBalance(UUID uuid, BigDecimal value) {
        if (uuid == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal sanitized = sanitize(value);
        balances.put(uuid.toString(), sanitized);
        save();
        return sanitized;
    }

    public synchronized BigDecimal addBalance(UUID uuid, BigDecimal value) {
        if (uuid == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal current = balanceOf(uuid);
        BigDecimal next = current.add(sanitize(value));
        if (next.compareTo(BigDecimal.ZERO) < 0) {
            next = BigDecimal.ZERO;
        }
        balances.put(uuid.toString(), next);
        save();
        return next;
    }

    public synchronized boolean removeBalance(UUID uuid, BigDecimal value) {
        if (uuid == null) {
            return false;
        }
        BigDecimal current = balanceOf(uuid);
        BigDecimal remove = sanitize(value);
        if (current.compareTo(remove) < 0) {
            return false;
        }
        balances.put(uuid.toString(), current.subtract(remove));
        save();
        return true;
    }

    private BigDecimal balanceOf(UUID uuid) {
        if (uuid == null) {
            return BigDecimal.ZERO;
        }
        return balances.getOrDefault(uuid.toString(), BigDecimal.ZERO);
    }

    private static BigDecimal sanitize(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return value;
    }
}
