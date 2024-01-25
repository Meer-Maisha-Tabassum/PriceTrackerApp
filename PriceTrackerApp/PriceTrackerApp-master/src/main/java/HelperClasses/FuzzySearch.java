package HelperClasses;

import java.util.ArrayList;
import java.util.List;

public class FuzzySearch {

    // Method to calculate Levenshtein distance
    private static int levenshteinDistance(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(dp[i - 1][j - 1] + costOfSubstitution(s1.charAt(i - 1), s2.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[m][n];
    }

    // Helper method to get the cost of substitution
    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    // Helper method to get the minimum of three values
    private static int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    // Method to perform fuzzy search
    public static List<String> fuzzySearch(String searchTerm, List<String> items, int threshold) {
        List<String> results = new ArrayList<>();

        for (String item : items) {
            if (levenshteinDistance(searchTerm.toLowerCase(), item.toLowerCase()) <= threshold) {
                results.add(item);
            }
        }

        return results;
    }
}

