package com.old.silence.core.test.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author moryzang
 */
public final class RandomData {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    private RandomData() {

    }

    public static long randomId() {
        return randomPositiveLong();
    }

    public static long randomPositiveLong() {
        return RANDOM.nextLong(Long.MAX_VALUE) + 1L;
    }

    public static BigInteger randomBigId() {
        return new BigInteger(Long.toUnsignedString(randomLong()));
    }

    public static Long randomLong() {
        return RANDOM.nextLong();
    }

    public static int randomInt() {
        return RANDOM.nextInt();
    }

    public static int randomPositiveInt() {
        return RANDOM.nextInt(Integer.MAX_VALUE) + 1;
    }

    public static int randomPositiveInt(int bound) {
        return RANDOM.nextInt(bound) + 1;
    }

    public static long randomPositiveLong(long bound) {
        return RANDOM.nextLong(bound) + 1L;
    }

    public static float randomFloat() {
        return RANDOM.nextFloat();
    }

    public static float randomPositiveFloat() {
        float result;
        do {
            result = (float) RANDOM.nextDouble(3.4028234663852886E38);
        } while (result == 0.0F);

        return result;
    }

    public static float randomPositiveFloat(float bound) {
        float result;
        do {
            result = (float) RANDOM.nextDouble(0.0, (double) bound);
        } while (result == 0.0F);

        return result;
    }

    public static BigDecimal randomBigDecimal(int scale) {
        return BigDecimal.valueOf(randomDouble()).setScale(scale, RoundingMode.HALF_DOWN);
    }

    public static double randomDouble() {
        return RANDOM.nextDouble();
    }

    public static BigDecimal randomPositiveBigDecimal(int scale) {
        return BigDecimal.valueOf(randomPositiveDouble()).setScale(scale, RoundingMode.HALF_DOWN);
    }

    public static double randomPositiveDouble() {
        double result;
        do {
            result = (float) RANDOM.nextDouble(Double.MAX_VALUE);
        } while (result == 0.0);

        return result;
    }

    public static BigDecimal randomPositiveBigDecimal(double bound, int scale) {
        return BigDecimal.valueOf(randomPositiveDouble(bound)).setScale(scale, RoundingMode.HALF_DOWN);
    }

    public static double randomPositiveDouble(double bound) {
        double result;
        do {
            result = (float) RANDOM.nextDouble(0.0, bound);
        } while (result == 0.0);

        return result;
    }

    public static boolean randomBoolean() {
        return RANDOM.nextBoolean();
    }

    public static byte[] randomBytes(int size) {
        byte[] bytes = new byte[size];
        RANDOM.nextBytes(bytes);
        return bytes;
    }

    public static void randomBytes(byte[] bytes) {
        RANDOM.nextBytes(bytes);
    }

    public static <T extends Enum<T>> T randomEnum(Class<T> clazz) {
        var enumConstants = clazz.getEnumConstants();
        return enumConstants[RANDOM.nextInt(enumConstants.length)];
    }

    public static String randomName(String prefix) {
        Objects.requireNonNull(prefix);
        return randomName(prefix, prefix.length() + 3);
    }

    public static String randomName(String prefix, int length) {
        Objects.requireNonNull(prefix);
        return length > 3 && prefix.length() < length - 1 ?
                prefix + "-" + RandomStringUtils.randomNumeric(length - prefix.length() - 1)
                : RandomStringUtils.randomAlphabetic(length);
    }
}
