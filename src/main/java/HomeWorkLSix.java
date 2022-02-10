import java.util.Arrays;

public class HomeWorkLSix {

    public int[] ArrayAfterFour(int[] array) {
        int checkPointAfterFour = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 4) {
                checkPointAfterFour = i;
            }
        }
        if (checkPointAfterFour == -1) {
            throw new RuntimeException("В массиве нет ни одной 4-ки");
        }
        return Arrays.copyOfRange(array, checkPointAfterFour + 1, array.length);
    }

    public boolean ArrayHasFourOrOne(int[] array) {
        boolean checkThePoint = false;
        for (int j = 0; j < array.length; j++) {
            if (array[j] == 1 || array[j] == 4) {
                checkThePoint = true;
                break;
            }
        }
        return checkThePoint;
    }
}

