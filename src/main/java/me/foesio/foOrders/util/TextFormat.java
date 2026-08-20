package me.foesio.foOrders.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextFormat {
    private static final char SECTION = '§';
    private static final Pattern HEX_PATTERN = Pattern.compile("#([0-9a-fA-F]{6})");
    private static final String COLOR_CHARS = "0123456789abcdefklmnorABCDEFKLMNOR";

    private TextFormat() {
    }

    public static String colorize(String message) {
        if (message == null || message.isEmpty()) {
            return message == null ? "" : message;
        }
        String result = translateHexColors(message);
        result = translateAmpersandCodes(result);
        return result;
    }

    private static String translateHexColors(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder(message.length());
        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder();
            replacement.append(SECTION).append('x');
            for (char c : hex.toCharArray()) {
                replacement.append(SECTION).append(c);
            }
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement.toString()));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String translateAmpersandCodes(String message) {
        char[] chars = message.toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == '&' && COLOR_CHARS.indexOf(chars[i + 1]) > -1) {
                chars[i] = SECTION;
            }
        }
        return new String(chars);
    }

    public static String applyPlaceholders(String message, Map<String, String> placeholders) {
        String formatted = message == null ? "" : message;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String value = entry.getValue() == null ? "" : entry.getValue();
            formatted = formatted
                .replace("{" + entry.getKey() + "}", value)
                .replace("%" + entry.getKey() + "%", value);
        }
        return formatted;
    }

    public static Map<String, String> placeholders(Object... values) {
        Map<String, String> placeholders = new LinkedHashMap<>();
        if (values == null) {
            return placeholders;
        }
        for (int index = 0; index + 1 < values.length; index += 2) {
            placeholders.put(String.valueOf(values[index]), String.valueOf(values[index + 1]));
        }
        return placeholders;
    }
}
