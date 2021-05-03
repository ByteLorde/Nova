package nova;

import java.util.Random;

public class NumberUtil {
    public static int getRandomInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }
}
