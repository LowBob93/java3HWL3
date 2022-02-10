import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;




public class HomeWorkLSixTest {

    public static Stream<Arguments> dataForArrayAfterFour() {
        List<Arguments> out = new ArrayList<>();
        out.add(Arguments.arguments(new int[] {1, 2, 3, 4, 5, 6, 7, 4, 7 }, new int[] {7}));
        out.add(Arguments.arguments(new int[] {5, 2, 6, 4, 3, 4, 3, 2, 1 }, new int[] {3, 2, 1}));
        out.add(Arguments.arguments(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 4 }, new int[] {}));
        out.add(Arguments.arguments(new int[] {1, 1, 1, 1, 1, 1, 4, 1, 1 }, new int[] {1,1}));
        return out.stream();
    }
    public static Stream<Arguments> dataForArrayHasFourOrOne() {
        List<Arguments> out = new ArrayList<>();
        out.add(Arguments.arguments(new int[]{1, 2, 3}, true));
        out.add(Arguments.arguments(new int[]{6, 4, 9}, true));
        out.add(Arguments.arguments(new int[]{2, 5, 9}, false));
        out.add(Arguments.arguments(new int[]{9, 8, 3}, false));
        return out.stream();
    }



    private HomeWorkLSix homeWorkLSix;

    @BeforeEach
    public void init() {
        homeWorkLSix = new HomeWorkLSix();
    }

    @MethodSource("dataForArrayAfterFour")
    @ParameterizedTest
    public void testArrayAfterFour(int[] array, int[] result) {
        assertArrayEquals(result, homeWorkLSix.ArrayAfterFour(array));
    }

    @Test
    public void testArrayAfterFourException() {
        try {
            homeWorkLSix.ArrayAfterFour(new int[]{3, 2, 5, 7, 6, 9, 12});
            fail();
        } catch (Exception ignored) {
        }
    }

    @MethodSource("dataForArrayHasFourOrOne")
    @ParameterizedTest
    public void testHasArrayOneOrFour(int[] array, boolean result) {
        if (result) {
            assertTrue(homeWorkLSix.ArrayHasFourOrOne(array));
        } else {
            assertFalse(homeWorkLSix.ArrayHasFourOrOne(array));
        }
    }


}