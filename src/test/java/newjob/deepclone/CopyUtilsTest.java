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
/*

  @Test
  void deepCopyPrimitiveObjectTest(){
        int originalInt = 5;

        int copy = (int) copyUtils.deepCopy(originalInt);

        originalInt = 4;

        assertNotEquals(originalInt, copy);
  }
*/

    @Test
    @DisplayName("Test Deep Copy Array Of Primitives")
    void deepCopyArrayOfPrimitivesTest() {
        int[] ints = {1, 2, 3};

        int[] copy = (int[]) copyUtils.deepCopy(ints);

        ints = new int[]{4, 5, 6};

        assertNotEquals(ints, copy);

        System.out.println("Original array " + Arrays.toString(ints));
        System.out.println("Deep Copy array " + Arrays.toString(copy));
    }

    @Test
    @DisplayName("Test Deep Copy Array Of Strings")
    void deepCopyArrayOfStringsTest() {
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
    void deepCopyArrayOfObjectsTest() {
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

    @Test
    @DisplayName("Test Deep Copy Collection Of Primitives")
    void deepCopyCollectionOfPrimitivesTest() {
        List<Integer> originalList = new ArrayList();
        originalList.add(1);
        originalList.add(2);
        List copy = (List) copyUtils.deepCopy(originalList);
        ListIterator<Integer> listIterator = originalList.listIterator();

        if (listIterator.hasNext()) {
            listIterator.next();
            listIterator.set(3);
        }

        assertNotEquals(copy, originalList);

        System.out.println("Original collection " + originalList);
        System.out.println("DeepCopy collection " + copy);
    }

    @Test
    @DisplayName("Test Deep Copy Collection Of Strings")
    void deepCopyCollectionOfStringsTest() {
        List<String> originalList = new ArrayList();
        originalList.add("How I do it?");
        originalList.add("It is working");

        List copy = (List) copyUtils.deepCopy(originalList);
        ListIterator<String> listIterator = originalList.listIterator();

        if (listIterator.hasNext()) {
            listIterator.next();
            listIterator.set("Testing String");
        }

        assertNotEquals(copy, originalList);

        System.out.println("Original collection " + originalList);
        System.out.println("DeepCopy collection " + copy);
    }

    @Test
    @DisplayName("Test Deep Copy Collection Of Objects")
    void deepCopyCollectionOfObjectsTest() {
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
    @DisplayName("Test Deep Copy Map Of Primitives")
    void deepCopyMapOfPrimitivesTest() {
        Map<Integer, Integer> originalMap = new HashMap<>();
        originalMap.put(0, 1);
        originalMap.put(1, 2);
        Map copy = (Map) copyUtils.deepCopy(originalMap);

        originalMap.put(1, 3);

        assertNotEquals(copy, originalMap);

        System.out.println("Original map " + originalMap);
        System.out.println("DeepCopy map " + copy);
    }

    @Test
    @DisplayName("Test Deep Copy Map Of Strings")
    void deepCopyMapOfStringsTest() {
        Map<String, String> originalMap = new HashMap<>();
        originalMap.put("One", "How I do it?");
        originalMap.put("Two", "It is working");

        Map copy = (Map) copyUtils.deepCopy(originalMap);

        originalMap.put("One", "Testing String");

        assertNotEquals(copy, originalMap);

        System.out.println("Original map " + originalMap);
        System.out.println("DeepCopy map " + copy);
    }


    @Test
    @DisplayName("Test Deep Copy Map Of Objects")
    void deepCopyMapOfObjectsTest() {
        Map<Man, Man> originalMap = new HashMap<>();
        Man manTwo = new Man("Ares", 28, Sex.MALE, null);

        originalMap.put(originalMan, manTwo);
        originalMap.put(manTwo, originalMan);

        Map copy = (Map) copyUtils.deepCopy(originalMap);

        manTwo.setName("Hermes");
        manTwo.setAge(33);
        manTwo.setSex(Sex.FEMALE);
        manTwo.setFavoriteBooks(null);

        originalMap.put(originalMan, manTwo);

        assertNotEquals(copy, originalMap);

        System.out.println("Original map " + originalMap);
        System.out.println("DeepCopy map " + copy);
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
    @DisplayName("Test Deep Copy Object")
    void deepCopyObject() {
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

        assertNotEquals(copy.getTestPrimitiveArray(), originalArray.getTestPrimitiveArray());

        System.out.println("Original object " + originalArray);
        System.out.println("DeepCopy object " + copy);
    }

    @Test
    @DisplayName("Test Field Array Of Strings Deep Copy")
    void deepCopyFieldArrayOfStringsTest() {
        ObjectWithArrays copy = (ObjectWithArrays) copyUtils.deepCopy(originalArray);

        originalArray.getTestStringArray()[0] = "Воздух";
        originalArray.getTestStringArray()[1] = "Огонь";

        assertNotEquals(copy.getTestStringArray(), originalArray.getTestStringArray());

        System.out.println("Original object " + originalArray);
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

        assertNotEquals(copy.getTestObjectArray(), originalArray.getTestObjectArray());

        System.out.println("Original object " + originalArray);
        System.out.println("DeepCopy object " + copy);
    }

    @Test
    @DisplayName("Test Cached Exception")
    void throwsExceptionsNullObjectTest() {
        originalMan = null;

        assertThrows(IllegalArgumentException.class, () -> {

            copyUtils.deepCopy(originalMan);
        });
    }

    @Test
    @DisplayName("Test Cached Exception")
    void throwsExceptionsPrimitiveObjectTest() {
        int originalInt = 5;

        assertThrows(IllegalArgumentException.class, () -> {

            copyUtils.deepCopy(originalInt);
        });
    }
/*
    @Test
    @DisplayName("Test Cached Exception")
    void throwsExceptionsStringObjectTest() {
        String originalString = "Testing String";

        assertThrows(IllegalArgumentException.class, () -> {

            copyUtils.deepCopy(originalString);
        });
    }*/
}