package net.botwithus.rs3cook;

import net.botwithus.rs3.script.LoopingScript;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public final class Delays {
    private Delays() {}

    /** Wait until condition true or timeout. Default poll 80–140 ms. */
    public static boolean delayUntil(LoopingScript script,
                                     BooleanSupplier condition,
                                     long timeoutMs) {
        return delayUntil(script, condition, timeoutMs, 80, 140);
    }

    /** Wait until condition true or timeout, polling every fixed pollMs. */
    public static boolean delayUntil(LoopingScript script,
                                     BooleanSupplier condition,
                                     long timeoutMs,
                                     int pollMs) {
        if (condition.getAsBoolean()) return true;

        final long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMs);
        while (System.nanoTime() < deadline) {
            script.delay(pollMs); // yield to BWU scheduler
            if (condition.getAsBoolean()) return true;
        }
        return false;
    }

    /** Wait until condition true or timeout, polling between min/max ms each loop. */
    public static boolean delayUntil(LoopingScript script,
                                     BooleanSupplier condition,
                                     long timeoutMs,
                                     int minPollMs,
                                     int maxPollMs) {
        if (condition.getAsBoolean()) return true;

        final long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMs);
        while (System.nanoTime() < deadline) {
            script.delay(minPollMs); // yield to BWU scheduler with jitter
            if (condition.getAsBoolean()) return true;
        }
        return false;
    }

    /** Wait while predicate is true (i.e., until it becomes false) or timeout. Default poll 80–140 ms. */
    public static boolean delayWhile(LoopingScript script,
                                     BooleanSupplier predicate,
                                     long timeoutMs) {
        return delayUntil(script, () -> !predicate.getAsBoolean(), timeoutMs);
    }

    /** Wait while predicate is true with fixed poll. */
    public static boolean delayWhile(LoopingScript script,
                                     BooleanSupplier predicate,
                                     long timeoutMs,
                                     int pollMs) {
        return delayUntil(script, () -> !predicate.getAsBoolean(), timeoutMs, pollMs);
    }

    /** Wait while predicate is true with min/max poll. */
    public static boolean delayWhile(LoopingScript script,
                                     BooleanSupplier predicate,
                                     long timeoutMs,
                                     int minPollMs,
                                     int maxPollMs) {
        return delayUntil(script, () -> !predicate.getAsBoolean(), timeoutMs, minPollMs, maxPollMs);
    }
}
