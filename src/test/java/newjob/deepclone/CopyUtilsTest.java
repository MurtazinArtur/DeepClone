package newjob.deepclone;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CopyUtilsTest {
    Man originalMan;
    ObjectWithArrays original;
    CopyUtils copyUtils;

    @BeforeEach
    void setUp() {
        copyUtils = new CopyUtils();

        List<String> favoriteBooks = new ArrayList<>();
        favoriteBooks.add("How I do it?");
        favoriteBooks.add("It is working");
        originalMan = new Man("Zeus", 25, Sex.FEMALE, favoriteBooks);

    }

    @AfterEach
    void tearDown() {
        System.out.println("Test is finished ");
    }

    @Test
    @DisplayName("Test Deep Copy Primitive Field Object")
    void deepCopyPrimitiveFieldObject() {

        Man copy = (Man) copyUtils.deepCopy(originalMan);

        originalMan.setAge(28);

        assertNotEquals(copy.getAge(), originalMan.getAge());
    }

    @Test
    @DisplayName("Test Deep Copy String Field Object")
    void deepCopyStringFieldObject() {

        Man copy = (Man) copyUtils.deepCopy(originalMan);

        originalMan.setName("Ares");

        assertNotEquals(copy.getName(), originalMan.getName());
    }

    @Test
    @DisplayName("Test Deep Copy Enum Field Object")
    void deepCopyEnumFieldObject() {

        Man copy = (Man) copyUtils.deepCopy(originalMan);

        originalMan.setSex(Sex.MALE);

        assertNotEquals(copy.getSex(), originalMan.getSex());
    }

    @Test
    @DisplayName("Test Deep Copy Collection Field Object")
    void deepCopyCollectionFieldObject() {

        Man copy = (Man) copyUtils.deepCopy(originalMan);

        originalMan.getFavoriteBooks().add("myths and reality");

        assertNotEquals(copy.getFavoriteBooks(), originalMan.getFavoriteBooks());
    }

    @Test
    @DisplayName("Test Deep Copy Object")
    void deepCopyObject() {

        Man copy = (Man) copyUtils.deepCopy(originalMan);

        originalMan.setName("Ares");
        originalMan.setAge(28);
        originalMan.setSex(Sex.MALE);
        originalMan.getFavoriteBooks().add("myths and reality");

        assertNotEquals(copy, originalMan);
    }

    @Test
    @DisplayName("Test Arrays Deep Copy")
    void deepCopyArrayFieldTest() {
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

        assertNotEquals(copy, original);
    }

    @Test
    @DisplayName("Test Cached Exception")
    void throwsExceptionsTest() {
        originalMan = null;
        assertThrows(IllegalArgumentException.class, () -> {
            copyUtils.deepCopy(originalMan);
        });
    }
}