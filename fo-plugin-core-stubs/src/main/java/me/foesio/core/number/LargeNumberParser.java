package me.foesio.core.number;

import java.util.Locale;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.regex.Pattern;

/**
 * Parses the positive amounts and prices players type, accepting the compact
 * suffixes the dialogs advertise ({@code 1k}, {@code 2.5m}).
 */
public final class LargeNumberParser {
    /** Digits with at most one decimal point - deliberately stricter than
     * {@link Double#parseDouble}, which would also accept "NaN", "Infinity",
     * "1d" and hex float literals. */
    private static final Pattern NUMBER = Pattern.compile("\\d+(\\.\\d+)?");
    private static final String SUFFIXES = "kmbt";

    private LargeNumberParser() {}

    public static OptionalInt parsePositiveInt(String input) {
        OptionalDouble parsed = parsePositiveDouble(input);
        if (parsed.isEmpty()) {
            return OptionalInt.empty();
        }
        double value = parsed.getAsDouble();
        if (value < 1D || value > Integer.MAX_VALUE) {
            return OptionalInt.empty();
        }
        return OptionalInt.of((int) Math.floor(value));
    }

    public static OptionalDouble parsePositiveDouble(String input) {
        if (input == null) {
            return OptionalDouble.empty();
        }

        String text = input.trim().replace(",", "").replace("_", "").replace(" ", "");
        if (text.startsWith("$")) {
            text = text.substring(1);
        }
        if (text.isEmpty()) {
            return OptionalDouble.empty();
        }

        double multiplier = 1D;
        int suffix = SUFFIXES.indexOf(Character.toLowerCase(text.charAt(text.length() - 1)));
        if (suffix >= 0) {
            multiplier = Math.pow(1000D, suffix + 1);
            text = text.substring(0, text.length() - 1);
            if (text.isEmpty()) {
                return OptionalDouble.empty();
            }
        }

        if (!NUMBER.matcher(text).matches()) {
            return OptionalDouble.empty();
        }

        double value;
        try {
            value = Double.parseDouble(text);
        } catch (NumberFormatException exception) {
            return OptionalDouble.empty();
        }

        double result = value * multiplier;
        if (result <= 0D || !Double.isFinite(result)) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(result);
    }

    /** Exposed for tests and callers that want the canonical suffix list. */
    public static String suffixes() {
        return SUFFIXES.toUpperCase(Locale.ROOT);
    }
}
