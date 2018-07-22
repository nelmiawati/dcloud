package id.ac.polibatam.mj.dcloud.math;

import java.util.Random;

public class IDAMath {

    public static int generateRandomSeed(final GFMath gfMath, final int[] unsignedVSecretKey) {
        int seed = 0;
        for (int elm : unsignedVSecretKey) {
            seed = gfMath.add(seed, elm);
        }
        return seed;
    }

    public static int[][] generateSalt(final Random random, final int nbRow, final int nbColumn) {

        final int[][] salt = new int[nbRow][nbColumn];
        for (int i = 0; i < salt.length; i++) {
            final int r = random.nextInt(256);
            for (int j = 0; j < salt[i].length; j++) {
                salt[i][j] = r;
            }
        }

        return salt;
    }

}
