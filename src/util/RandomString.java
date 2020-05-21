package util;

import java.security.SecureRandom;
import java.util.Random;

public class RandomString {

    private final Random random;

    private final char[] symbols;

    private final char[] string;

    public RandomString(int length) {
        this.random = new SecureRandom();
        this.symbols = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        this.string = new char[length];
    }

    public String nextString() {
        for (int i = 0; i < string.length; ++i)
            string[i] = symbols[random.nextInt(symbols.length)];
        return new String(string);
    }
}