package newjob.deepclone;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

class CopyUtilsTest {
    Man originalMan;
    ObjectWithArrays original;
    CopyUtils copyUtils;

    @BeforeEach
    void setUp() {
        copyUtils = new CopyUtils();
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test is finished ");
    }

    @Test
    @DisplayName("Test Deep Copy")
    void deepCopy() {
        List<String> favoriteBooks = new ArrayList<>();
        favoriteBooks.add("How I do it?");
        favoriteBooks.add("It is working");
        originalMan = new Man("Zeus", 25, Sex.FEMALE, favoriteBooks);

        Man copy = (Man) copyUtils.deepCopy(originalMan);

        originalMan.setName("Ares");
        originalMan.setAge(28);
        originalMan.setSex(Sex.MALE);
        favoriteBooks.add("myths and reality");

        Assertions.assertNotEquals(copy, originalMan);
    }

    @Test
    @DisplayName("Test Arrays Deep Copy")
    void deepCopyObjectWithArrayFields() {
        List<String> favoriteBooks = new ArrayList<>();
        favoriteBooks.add("How I do it?");
        favoriteBooks.add("It is working");
        Man manOne = new Man("Zeus", 25, Sex.FEMALE, favoriteBooks);
        Man manTwo = new Man("Ares", 28, Sex.MALE, favoriteBooks);
        int[] ints = {1, 2, 3};
        String[] strings = {"Земля", "Вода"};
        Man[] mans = {manOne, manTwo};
        original = new ObjectWithArrays(ints, strings, mans);

        ObjectWithArrays copy = (ObjectWithArrays) copyUtils.deepCopy(original);

        original.setTestPrimitiveArray(new int[]{4, 5, 6});
        original.getTestStringArray()[0] = "Воздух";
        original.getTestStringArray()[1] = "Огонь";
        Man man = (Man) original.getTestObjectArray()[1];
        man.setName("Hermes");
        man.setAge(33);
        man.setSex(Sex.FEMALE);
        man.setFavoriteBooks(null);

        Assertions.assertNotEquals(copy, original);
    }

    @Test
    @DisplayName("Test Cached Exception")
    void throwsExceptionsTest() {
        originalMan = null;
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            copyUtils.deepCopy(originalMan);
        });
    }
}