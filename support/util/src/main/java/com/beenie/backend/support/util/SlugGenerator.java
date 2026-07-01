package com.beenie.backend.support.util;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 게시글 제목 -> URL-friendly slug 변환 유틸.
 * 한글은 Revised Romanization 근사 변환 후, 영문/숫자 외 문자는 '-' 로 치환한다.
 */
public final class SlugGenerator {

    private static final String[] INITIALS = {
            "g", "kk", "n", "d", "tt", "r", "m", "b", "pp", "s", "ss", "", "j", "jj", "ch", "k", "t", "p", "h"
    };
    private static final String[] MEDIALS = {
            "a", "ae", "ya", "yae", "eo", "e", "yeo", "ye", "o", "wa", "wae", "oe", "yo", "u", "weo", "we", "wi",
            "yu", "eu", "ui", "i"
    };
    private static final String[] FINALS = {
            "", "g", "kk", "gs", "n", "nj", "nh", "d", "l", "lg", "lm", "lb", "ls", "lt", "lp", "lh", "m", "b",
            "bs", "s", "ss", "ng", "j", "c", "k", "t", "p", "h"
    };

    private static final int HANGUL_BASE = 0xAC00;
    private static final int HANGUL_END = 0xD7A3;
    private static final Pattern NON_ALNUM = Pattern.compile("[^a-z0-9]+");
    private static final Pattern EDGE_DASH = Pattern.compile("^-+|-+$");

    private SlugGenerator() {
    }

    public static String generate(String title) {
        if (title == null || title.isBlank()) {
            return "post";
        }
        String romanized = romanize(title).toLowerCase(Locale.ROOT);
        String replaced = NON_ALNUM.matcher(romanized).replaceAll("-");
        String trimmed = EDGE_DASH.matcher(replaced).replaceAll("");
        return trimmed.isBlank() ? "post" : trimmed;
    }

    public static String withSuffix(String baseSlug, int suffix) {
        return baseSlug + "-" + suffix;
    }

    private static String romanize(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= HANGUL_BASE && c <= HANGUL_END) {
                int offset = c - HANGUL_BASE;
                int initial = offset / (21 * 28);
                int medial = (offset % (21 * 28)) / 28;
                int finalConsonant = offset % 28;
                sb.append(INITIALS[initial]).append(MEDIALS[medial]).append(FINALS[finalConsonant]).append('-');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
