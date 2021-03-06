package newjob.deepclone;

import newjob.deepclone.copyutils.CopyUtils;
import newjob.deepclone.exception.DeepCopyException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CopyUtilsTest {
    private Man originalMan;
    private Man manTwo;
    private ObjectWithArrays originalArray;
    private int[] ints = {1, 2, 3};
    private String[] strings = {"Земля", "Вода"};
    private Man[] mans;

    @BeforeEach
    void setUp() {

        List<String> originalList = new ArrayList<>();
        originalList.add("How I do it?");
        originalList.add("It is working");
        originalMan = new Man("Zeus", 25, Sex.FEMALE, originalList);
        manTwo = new Man("Ares", 28, Sex.MALE, originalList);

        mans = new Man[]{originalMan, manTwo};

        originalArray = new ObjectWithArrays();
        originalArray.setTestPrimitiveArray(ints);
        originalArray.setTestStringArray(strings);
        originalArray.setTestObjectArray(mans);
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test is finished ");
    }

    @Test
    @DisplayName("Test Deep Copy Primitive")
    void deepCopyPrimitiveTest() {
        int x = 5;

        System.out.println("Original primitive: " + x);

        int y = CopyUtils.deepCopy(x);

        assertEquals(x, y);

        x = 10;

        assertNotEquals(x, y);

        System.out.println("Changed original primitive: " + x);
        System.out.println("Deep Copy primitive: " + y);
    }

    @Test
    @DisplayName("Test Deep Copy String")
    void deepCopyStringTest() {
        String originalString = "originalString";

        System.out.println("Original string: " + originalString);

        String copy = CopyUtils.deepCopy(originalString);

        assertEquals(originalString, copy);

        originalString = "Testing String";

        assertNotEquals(originalString, copy);

        System.out.println("Changed original string: " + originalString);
        System.out.println("Deep Copy string: " + copy);
    }

    @Test
    @DisplayName("Test Deep Copy Array Of Primitives")
    void deepCopyArrayOfPrimitivesTest() {
        System.out.println("Original primitive array: " + Arrays.toString(ints));

        int[] copy = CopyUtils.deepCopy(ints);

        assertEquals(Arrays.toString(ints), Arrays.toString(copy));

        ints = new int[]{4, 5, 6};

        assertNotEquals(ints, copy);

        System.out.println("Changed original primitive array: " + Arrays.toString(ints));
        System.out.println("Deep Copy primitive array: " + Arrays.toString(copy));
    }

    @Test
    @DisplayName("Test Deep Copy Array Of Strings")
    void deepCopyArrayOfStringsTest() {
        System.out.println("Original string array: " + Arrays.toString(strings));

        String[] copy = CopyUtils.deepCopy(strings);

        assertEquals(Arrays.toString(strings), Arrays.toString(copy));

        strings[0] = "Воздух";
        strings[1] = "Огонь";

        assertNotEquals(Arrays.toString(strings), Arrays.toString(copy));

        System.out.println("Changed original string array: " + Arrays.toString(strings));
        System.out.println("Deep Copy string array: " + Arrays.toString(copy));
    }

    @Test
    @DisplayName("Test Deep Copy Array Of Objects")
    void deepCopyArrayOfObjectsTest() {
        System.out.println("Original object array: " + Arrays.toString(mans));

        Man[] copy = CopyUtils.deepCopy(mans);

        assertEquals(Arrays.toString(mans), Arrays.toString(copy));

        Man man = mans[1];
        man.setName("Hermes");
        man.setAge(33);
        man.setSex(Sex.FEMALE);
        man.setFavoriteBooks(null);

        assertNotEquals(Arrays.toString(mans), Arrays.toString(copy));

        System.out.println("Changed original object array: " + Arrays.toString(mans));
        System.out.println("Deep Copy object array: " + Arrays.toString(copy));
    }

    @Test
    @DisplayName("Test Deep Copy Collection Of Primitives")
    void deepCopyCollectionOfPrimitivesTest() {
        List<Integer> originalList = new ArrayList<>();
        originalList.add(1);
        originalList.add(2);

        System.out.println("Original primitive collection: " + originalList);

        List copy = CopyUtils.deepCopy(originalList);

        assertEquals(copy, originalList);

        ListIterator<Integer> listIterator = originalList.listIterator();

        if (listIterator.hasNext()) {
            listIterator.next();
            listIterator.set(3);
        }

        assertNotEquals(copy, originalList);

        System.out.println("Changed original primitive collection: " + originalList);
        System.out.println("DeepCopy primitive collection: " + copy);
    }

    @Test
    @DisplayName("Test Deep Copy Collection Of Strings")
    void deepCopyCollectionOfStringsTest() {
        List<String> originalList = new ArrayList<>();
        originalList.add("How I do it?");
        originalList.add("It is working");

        System.out.println("Original string collection: " + originalList);

        List copy = CopyUtils.deepCopy(originalList);

        assertEquals(copy, originalList);

        ListIterator<String> listIterator = originalList.listIterator();

        if (listIterator.hasNext()) {
            listIterator.next();
            listIterator.set("Testing String");
        }

        assertNotEquals(copy, originalList);

        System.out.println("Changed original string collection: " + originalList);
        System.out.println("DeepCopy string collection: " + copy);
    }

    @Test
    @DisplayName("Test Deep Copy Collection Of Objects")
    void deepCopyCollectionOfObjectsTest() {
        List<Man> originalList = new ArrayList<>();

        originalList.add(originalMan);
        originalList.add(manTwo);

        System.out.println("Original object collection: " + originalList);

        List copy = CopyUtils.deepCopy(originalList);

        assertEquals(copy, originalList);

        manTwo.setName("Hermes");
        manTwo.setAge(33);
        manTwo.setSex(Sex.FEMALE);
        manTwo.setFavoriteBooks(null);

        assertNotEquals(copy, originalList);

        System.out.println("Changed original object collection: " + originalList);
        System.out.println("DeepCopy object collection: " + copy);
    }

    @Test
    @DisplayName("Test Deep Copy Map Of Primitives")
    void deepCopyMapOfPrimitivesTest() {
        Map<Integer, Integer> originalMap = new HashMap<>();
        originalMap.put(0, 1);
        originalMap.put(1, 2);

        System.out.println("Original primitive map: " + originalMap);

        Map copy = CopyUtils.deepCopy(originalMap);

        assertEquals(copy, originalMap);

        originalMap.put(1, 3);

        assertNotEquals(copy, originalMap);

        System.out.println("Changed original primitive map: " + originalMap);
        System.out.println("DeepCopy primitive map: " + copy);
    }

    @Test
    @DisplayName("Test Deep Copy Map Of Strings")
    void deepCopyMapOfStringsTest() {
        Map<String, String> originalMap = new HashMap<>();
        originalMap.put("One", "How I do it?");
        originalMap.put("Two", "It is working");

        System.out.println("Original string map: " + originalMap);

        Map copy = CopyUtils.deepCopy(originalMap);

        assertEquals(copy, originalMap);

        originalMap.put("One", "Testing String");

        assertNotEquals(copy, originalMap);

        System.out.println("Changed original string map: " + originalMap);
        System.out.println("DeepCopy string map: " + copy);
    }


    @Test
    @DisplayName("Test Deep Copy Map Of Objects")
    void deepCopyMapOfObjectsTest() {
        Map<Man, Man> originalMap = new HashMap<>();

        originalMap.put(originalMan, manTwo);
        originalMap.put(manTwo, originalMan);

        System.out.println("Original object map: " + originalMap);

        Map copy = CopyUtils.deepCopy(originalMap);

        assertEquals(copy, originalMap);

        manTwo.setName("Hermes");
        manTwo.setAge(33);
        manTwo.setSex(Sex.FEMALE);
        manTwo.setFavoriteBooks(null);

        originalMap.put(originalMan, manTwo);

        assertNotEquals(copy, originalMap);

        System.out.println("Changed original object map: " + originalMap);
        System.out.println("DeepCopy object map: " + copy);
    }

    @Test
    @DisplayName("Test Deep Copy Primitive Field Object")
    void deepCopyPrimitiveFieldObject() {
        System.out.println("Original object: " + originalMan);

        Man copy = CopyUtils.deepCopy(originalMan);

        assertEquals(copy.getAge(), originalMan.getAge());

        originalMan.setAge(28);

        assertNotEquals(copy.getAge(), originalMan.getAge());

        System.out.println("Changed original object: " + originalMan);
        System.out.println("DeepCopy object: " + copy);
    }

    @Test
    @DisplayName("Test Deep Copy String Field Object")
    void deepCopyStringFieldObject() {
        System.out.println("Original object: " + originalMan);

        Man copy = CopyUtils.deepCopy(originalMan);

        assertEquals(copy.getName(), originalMan.getName());

        originalMan.setName("Ares");

        assertNotEquals(copy.getName(), originalMan.getName());

        System.out.println("Changed original object: " + originalMan);
        System.out.println("DeepCopy object: " + copy);
    }

    @Test
    @DisplayName("Test Deep Copy Enum Field Object")
    void deepCopyEnumFieldObject() {
        System.out.println("Original object: " + originalMan);

        Man copy = CopyUtils.deepCopy(originalMan);

        assertEquals(copy.getSex(), originalMan.getSex());

        originalMan.setSex(Sex.MALE);

        assertNotEquals(copy.getSex(), originalMan.getSex());

        System.out.println("Changed original object: " + originalMan);
        System.out.println("DeepCopy object: " + copy);
    }

    @Test
    @DisplayName("Test Deep Copy Collection Field Object")
    void deepCopyCollectionFieldObject() {
        System.out.println("Original object: " + originalMan);

        Man copy = CopyUtils.deepCopy(originalMan);

        assertEquals(copy.getFavoriteBooks(), originalMan.getFavoriteBooks());

        originalMan.getFavoriteBooks().add("myths and reality");

        assertNotEquals(copy.getFavoriteBooks(), originalMan.getFavoriteBooks());

        System.out.println("Changed original object: " + originalMan);
        System.out.println("DeepCopy object: " + copy);
    }

    @Test
    @DisplayName("Test Deep Copy Object")
    void deepCopyObject() {
        System.out.println("Original object: " + originalMan);

        Man copy = CopyUtils.deepCopy(originalMan);

        assertEquals(copy, originalMan);

        originalMan.setName("Ares");
        originalMan.setAge(28);
        originalMan.setSex(Sex.MALE);
        originalMan.getFavoriteBooks().add("myths and reality");

        assertNotEquals(copy, originalMan);

        System.out.println("Changed original object: " + originalMan);
        System.out.println("DeepCopy object: " + copy);
    }

    @Test
    @DisplayName("Test Field Array Of Primitives Deep Copy")
    void deepCopyFieldArrayOfPrimitivesTest() {
        System.out.println("Original object: " + originalArray);

        ObjectWithArrays copy = CopyUtils.deepCopy(originalArray);

        assertEquals(Arrays.toString(copy.getTestPrimitiveArray()), Arrays.toString(originalArray.getTestPrimitiveArray()));

        originalArray.setTestPrimitiveArray(new int[]{4, 5, 6});

        assertNotEquals(copy.getTestPrimitiveArray(), originalArray.getTestPrimitiveArray());

        System.out.println("Changed original object: " + originalArray);
        System.out.println("DeepCopy object: " + copy);
    }

    @Test
    @DisplayName("Test Field Array Of Strings Deep Copy")
    void deepCopyFieldArrayOfStringsTest() {
        System.out.println("Original object: " + originalArray);

        ObjectWithArrays copy = CopyUtils.deepCopy(originalArray);

        assertEquals(Arrays.toString(copy.getTestStringArray()), Arrays.toString(originalArray.getTestStringArray()));

        originalArray.getTestStringArray()[0] = "Воздух";
        originalArray.getTestStringArray()[1] = "Огонь";

        assertNotEquals(Arrays.toString(copy.getTestStringArray()), Arrays.toString(originalArray.getTestStringArray()));

        System.out.println("Changed original object: " + originalArray);
        System.out.println("DeepCopy object: " + copy);
    }

    @Test
    @DisplayName("Test Field Array Of Objects Deep Copy")
    void deepCopyFieldArrayOfObjectsTest() {
        System.out.println("Original object: " + originalArray);

        ObjectWithArrays copy = CopyUtils.deepCopy(originalArray);

        assertEquals(Arrays.toString(copy.getTestObjectArray()), Arrays.toString(originalArray.getTestObjectArray()));

        Man man = (Man) originalArray.getTestObjectArray()[1];
        man.setName("Hermes");
        man.setAge(33);
        man.setSex(Sex.FEMALE);
        man.setFavoriteBooks(null);

        assertNotEquals(Arrays.toString(copy.getTestObjectArray()), Arrays.toString(originalArray.getTestObjectArray()));

        System.out.println("Changed original object: " + originalArray);
        System.out.println("DeepCopy object: " + copy);
    }

    @Test
    @DisplayName("Test Cached Exception")
    void throwsExceptionsNullObjectTest() {
        originalMan = null;

        assertThrows(DeepCopyException.class, () -> {

            CopyUtils.deepCopy(originalMan);
        });
    }
}