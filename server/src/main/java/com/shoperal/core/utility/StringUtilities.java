package com.shoperal.core.utility;

import java.util.Objects;

/**
 * Composed entirely of static methods for String manipulations. This class has
 * a private constructor, so you cannot instantiate it
 * 
 * @author Julius Krah
 */
public class StringUtilities {
    private StringUtilities() {
    }

    /**
     * First removes all leading and trailing spaces. Afterwards removes all other
     * spaces
     * 
     * @param str the String to remove all spaces from
     * @return string with all spaces removed
     * @throws NullPointerException if {@code str} is {@code null}
     */
    public static String trimAllWhiteSpaces(CharSequence str) {
        str = Objects.requireNonNull(str, "'str' must not be null");
        var string = str.toString();
        return string.strip().replaceAll("\\s+", "");
    }

    /**
     * First removes all leading and trailing spaces. Then replaces remaining white
     * spaces with hyphens
     * 
     * @param str the input string
     * @return hypenated string
     * @throws NullPointerException if {@code str} is {@code null}
     */
    public static String replaceAllWhiteSpacesWithHypens(CharSequence str) {
        str = Objects.requireNonNull(str, "'str' must not be null");
        var string = str.toString();
        return string.strip().replaceAll("\\s+", "-");
    }
}
