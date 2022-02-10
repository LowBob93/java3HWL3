import java.util.Arrays;

public class HomeWorkLSix {

    public int[] ArrayAfterFour(int[] array) { // сделал проход по массиву с конца в начало
        int checkPointAfterFour = -1;
        for (int i = array.length-1; i >= 0; i--) {
            if (array[i] == 4) {
                checkPointAfterFour = i;
                break;
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

