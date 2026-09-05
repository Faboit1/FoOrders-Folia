package me.foesio.core.number;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Formats amounts and prices for display.
 *
 * <p>Whole values print without a decimal part - {@code 1}, never {@code 1.0} -
 * and large values collapse to a suffix, so an order for a million items reads
 * as {@code 1M} rather than filling the lore line.
 */
public final class NumberFormatters {
    private static final String[] SUFFIXES = {"K", "M", "B", "T"};
    private static final double COMPACT_THRESHOLD = 10_000D;
    private static final DecimalFormatSymbols SYMBOLS = DecimalFormatSymbols.getInstance(Locale.US);
    private static final DecimalFormat GROUPED = new DecimalFormat("#,##0.##", SYMBOLS);
    private static final DecimalFormat PLAIN = new DecimalFormat("0.##", SYMBOLS);
    private static final DecimalFormat COMPACT = new DecimalFormat("0.##", SYMBOLS);

    private NumberFormatters() {}

    /** A money value with thousands separators, e.g. {@code 1,234.56}. */
    public static String money(double value) {
        if (!Double.isFinite(value)) {
            return "0";
        }
        synchronized (GROUPED) {
            return GROUPED.format(round(value));
        }
    }

    /** A compact value, e.g. {@code 64}, {@code 1,500}, {@code 2.5M}. */
    public static String compact(double value) {
        if (!Double.isFinite(value)) {
            return "0";
        }

        double magnitude = Math.abs(value);
        if (magnitude < COMPACT_THRESHOLD) {
            synchronized (GROUPED) {
                return GROUPED.format(round(value));
            }
        }

        double scaled = value;
        int suffix = -1;
        while (Math.abs(scaled) >= 1000D && suffix < SUFFIXES.length - 1) {
            scaled /= 1000D;
            suffix++;
        }
        if (suffix < 0) {
            synchronized (GROUPED) {
                return GROUPED.format(round(value));
            }
        }
        synchronized (COMPACT) {
            return COMPACT.format(round(scaled)) + SUFFIXES[suffix];
        }
    }

    /** A plain value with no grouping, e.g. {@code 1234.5}. */
    public static String plain(double value) {
        if (!Double.isFinite(value)) {
            return "0";
        }
        synchronized (PLAIN) {
            return PLAIN.format(round(value));
        }
    }

    /** Rounds to two decimals so floating point noise never reaches a label. */
    private static BigDecimal round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
    }
}
