package newjob.deepclone;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CopyUtilsTest {
    Man originalMan;
    ObjectWithArrays originalArray;
    CopyUtils copyUtils;

    @BeforeEach
    void setUp() {
        copyUtils = new CopyUtils();

        List<String> originalList = new ArrayList<>();
        originalList.add("How I do it?");
        originalList.add("It is working");
        originalMan = new Man("Zeus", 25, Sex.FEMALE, originalList);

        Man manTwo = new Man("Ares", 28, Sex.MALE, originalList);
        int[] ints = {1, 2, 3};
        String[] strings = {"Земля", "Вода"};
        Man[] mans = {originalMan, manTwo};
        originalArray = new ObjectWithArrays(ints, strings, mans);
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test is finished ");
    }

    @Test
    @DisplayName("Test Deep Copy Array Of Primitives")
    void deepCopyArrayOfPrimitivesTest(){
        int[] ints = {1, 2, 3};

        int[] copy = (int[]) copyUtils.deepCopy(ints);

        ints = new int[]{4, 5, 6};

        assertNotEquals(ints, copy);

        System.out.println("Original array " + Arrays.toString(ints));
        System.out.println("Deep Copy array " + Arrays.toString(copy));
    }

    @Test
    @DisplayName("Test Deep Copy Array Of Strings")
    void deepCopyArrayOfStringsTest(){
        String[] strings = {"Земля", "Вода"};

        String[] copy = (String[]) copyUtils.deepCopy(strings);

        strings[0] = "Воздух";
        strings[1] = "Огонь";

        assertNotEquals(strings, copy);

        System.out.println("Original array " + Arrays.toString(strings));
        System.out.println("Deep Copy array " + Arrays.toString(copy));
    }

    @Test
    @DisplayName("Test Deep Copy Array Of Objects")
    void deepCopyArrayOfObjectsTest(){
        Man manTwo = new Man("Ares", 28, Sex.MALE, null);
        Man[] mans = {originalMan, manTwo};

        Man[] copy = (Man[]) copyUtils.deepCopy(mans);

        Man man = mans[1];
        man.setName("Hermes");
        man.setAge(33);
        man.setSex(Sex.FEMALE);
        man.setFavoriteBooks(null);

        assertNotEquals(mans, copy);

        System.out.println("Original array " + Arrays.toString(mans));
        System.out.println("Deep Copy array " + Arrays.toString(copy));
    }

 /*   @Test
    @DisplayName("Test Deep Copy Collection Of Strings")
    void deepCopyCollectionOfPrimitivesTest(){
        Map<Integer, Integer> originalList = new HashMap<>();
        originalList.put(0, 1);
        originalList.put(1, 2);
        Map copy = (Map) copyUtils.deepCopy(originalList);

        originalList.put(2, 3);

        assertNotEquals(copy, originalList);

        System.out.println("Original collection " + originalList);
        System.out.println("DeepCopy collection " + copy);
    }*/

    @Test
    @DisplayName("Test Deep Copy Collection Of Strings")
    void deepCopyCollectionOfStringsTest(){
        Map<Integer,String> originalList = new HashMap<>();
        originalList.put(1, "How I do it?");
        originalList.put(2, "It is working");

        Map copy = (Map) copyUtils.deepCopy(originalList);

        originalList.put(3, "Testing String");

        assertNotEquals(copy, originalList);

        System.out.println("Original collection " + originalList);
        System.out.println("DeepCopy collection " + copy);
    }

    @Test
    @DisplayName("Test Deep Copy Collection Of Objects")
    void deepCopyCollectionOfObjectsTest(){
        List<Man> originalList = new ArrayList<>();
        Man manTwo = new Man("Ares", 28, Sex.MALE, null);

        originalList.add(originalMan);
        originalList.add(manTwo);

        List copy = (List) copyUtils.deepCopy(originalList);

        manTwo.setName("Hermes");
        manTwo.setAge(33);
        manTwo.setSex(Sex.FEMALE);
        manTwo.setFavoriteBooks(null);

        assertNotEquals(copy, originalList);

        System.out.println("Original collection " + originalList);
        System.out.println("DeepCopy collection " + copy);
    }

    @Test
    @DisplayName("Test Deep Copy Primitive Field Object")
    void deepCopyPrimitiveFieldObject() {
        Man copy = (Man) copyUtils.deepCopy(originalMan);

        originalMan.setAge(28);

        assertNotEquals(copy.getAge(), originalMan.getAge());

        System.out.println("Original object " + originalMan);
        System.out.println("DeepCopy object " + copy);
    }

    @Test
    @DisplayName("Test Deep Copy String Field Object")
    void deepCopyStringFieldObject() {
        Man copy = (Man) copyUtils.deepCopy(originalMan);

        originalMan.setName("Ares");

        assertNotEquals(copy.getName(), originalMan.getName());

        System.out.println("Original object " + originalMan);
        System.out.println("DeepCopy object " + copy);
    }

    @Test
    @DisplayName("Test Deep Copy Enum Field Object")
    void deepCopyEnumFieldObject() {
        Man copy = (Man) copyUtils.deepCopy(originalMan);

        originalMan.setSex(Sex.MALE);

        assertNotEquals(copy.getSex(), originalMan.getSex());

        System.out.println("Original object " + originalMan);
        System.out.println("DeepCopy object " + copy);
    }

    @Test
    @DisplayName("Test Deep Copy Collection Field Object")
    void deepCopyCollectionFieldObject() {
        Man copy = (Man) copyUtils.deepCopy(originalMan);

        originalMan.getFavoriteBooks().add("myths and reality");

        assertNotEquals(copy.getFavoriteBooks(), originalMan.getFavoriteBooks());

        System.out.println("Original object " + originalMan);
        System.out.println("DeepCopy object " + copy);
    }

    @Test
    @DisplayName("Test Deep Copy Object Field Object")
    void deepCopyObjectFieldObject() {
        Man copy = (Man) copyUtils.deepCopy(originalMan);

        originalMan.setName("Ares");
        originalMan.setAge(28);
        originalMan.setSex(Sex.MALE);
        originalMan.getFavoriteBooks().add("myths and reality");

        assertNotEquals(copy, originalMan);

        System.out.println("Original object " + originalMan);
        System.out.println("DeepCopy object " + copy);
    }

    @Test
    @DisplayName("Test Field Array Of Primitives Deep Copy")
    void deepCopyFieldArrayOfPrimitivesTest() {
        ObjectWithArrays copy = (ObjectWithArrays) copyUtils.deepCopy(originalArray);

        originalArray.setTestPrimitiveArray(new int[]{4, 5, 6});

        assertNotEquals(copy, originalArray);

        System.out.println("Original object " + originalMan);
        System.out.println("DeepCopy object " + copy);
    }

    @Test
    @DisplayName("Test Field Array Of Strings Deep Copy")
    void deepCopyFieldArrayOfStringsTest() {
        ObjectWithArrays copy = (ObjectWithArrays) copyUtils.deepCopy(originalArray);

        originalArray.getTestStringArray()[0] = "Воздух";
        originalArray.getTestStringArray()[1] = "Огонь";

        assertNotEquals(copy, originalArray);

        System.out.println("Original object " + originalMan);
        System.out.println("DeepCopy object " + copy);
    }

    @Test
    @DisplayName("Test Field Array Of Objects Deep Copy")
    void deepCopyFieldArrayOfObjectsTest() {
        ObjectWithArrays copy = (ObjectWithArrays) copyUtils.deepCopy(originalArray);

        Man man = (Man) originalArray.getTestObjectArray()[1];
        man.setName("Hermes");
        man.setAge(33);
        man.setSex(Sex.FEMALE);
        man.setFavoriteBooks(null);

        assertNotEquals(copy, originalArray);

        System.out.println("Original object " + originalMan);
        System.out.println("DeepCopy object " + copy);
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