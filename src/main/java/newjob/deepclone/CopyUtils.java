package newjob.deepclone;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CopyUtils {
    Predicate<Object> isNull = Objects::isNull;

    public Object deepCopy(Object originalObject) {
        if (isNull.test(originalObject)) {
            throw new IllegalArgumentException("Original object must be not null for Deep Copy");
        }
        if (originalObject.getClass().isArray()) {
            return getCloneObjectOfArrayType(originalObject);
        }else if (Collection.class.isAssignableFrom(originalObject.getClass())) {
            return getCloneObjectOfCollectionType(originalObject);
        }else if (Map.class.isAssignableFrom(originalObject.getClass())){
            return getCloneObjectOfMapType(originalObject);
        }

            Field[] originalObjectFields = originalObject.getClass().getDeclaredFields();
            Object deepCopyObjectWithFields = createNewObject(originalObject);
            Stream<Field> fieldStream = Arrays.stream(originalObjectFields);
            Predicate<Field> isPrimitive = field -> field.getType().isPrimitive();
            Predicate<Field> isString = field -> "String".equals(field.getType().getSimpleName());
            Predicate<Field> isCollection = field -> Collection.class.isAssignableFrom(field.getType());
            Predicate<Field> isArray = field -> field.getType().isArray();
            Predicate<Field> isEnum = field -> field.getType().isEnum();

            fieldStream.forEach(field -> {
                field.setAccessible(true);
                if (!isNull.test(deepCopyObjectWithFields)) {
                    if (isNull.test(getFieldValue(field, originalObject))) {
                        insertValue(deepCopyObjectWithFields, field, null);
                    } else if (isString.or(isPrimitive).or(isEnum).test(field)) {
                        cloneObjectFieldPrimitiveOrStringType(originalObject, deepCopyObjectWithFields, field);
                    } else if (isCollection.test(field)) {
                        cloneObjectFieldCollectionType(originalObject, deepCopyObjectWithFields, field);
                    } else if (isArray.test(field)) {
                        cloneObjectFieldArrayType(originalObject, deepCopyObjectWithFields, field);
                    } else {
                        cloneObjectFieldObjectType(originalObject, deepCopyObjectWithFields, field);
                    }
                }
            });
            return deepCopyObjectWithFields;

    }

    private Object getCloneObjectOfMapType(Object original) {
        Map<Object, Object> subObject = (Map<Object, Object>) original;
        Map<Object, Object>copyCollection = (Map<Object, Object>) createNewObject(subObject);

       // subObject.forEach(getKeyFromValue());

        return null;
    }
    private static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    private Object getCloneObjectOfArrayType(Object original) {
        Object copyObject = null;
        int arrayLength = Array.getLength(original);
        String theClassName = original.getClass().getComponentType().getTypeName();

        try {
            Class arrayClass = getArrayClass(theClassName);

            copyObject = Array.newInstance(arrayClass, arrayLength);

            if ("String".equals(arrayClass.getSimpleName()) || arrayClass.isPrimitive()) {
                System.arraycopy(original, 0, copyObject, 0, arrayLength);
            } else {

                for (int index = 0; index < arrayLength; index++) {
                    Array.set(copyObject, index, deepCopy(Array.get(original, index)));
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return copyObject;
    }

    private Object getCloneObjectOfCollectionType(Object original){
        try {
            Collection<Object> subObject = (Collection<Object>) original;
            Collection<Object> copyCollection = (Collection<Object>) createNewObject(subObject);
            Object collectionItem = subObject.iterator().next();
            String collectionType = collectionItem.getClass().getTypeName();
            Class collectionClass = getObjectClass(collectionType);

            if ("String".equals(collectionItem.getClass().getSimpleName()) || collectionClass.isPrimitive()) {
                assert !isNull.test(copyCollection);

                copyCollection.addAll(subObject);

                return copyCollection;
            } else {

                for (int i = 0; i < subObject.size(); i++) {
                    if (subObject.iterator().hasNext()) {

                        assert !isNull.test(copyCollection);
                        copyCollection.add(deepCopy(subObject.iterator().next()));
                    }
                }
                return copyCollection;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void cloneObjectFieldPrimitiveOrStringType(Object original, Object clone, Field cloningField) {
        insertValue(clone, cloningField, getFieldValue(cloningField, original));
    }

    private void cloneObjectFieldCollectionType(Object original, Object clone, Field cloningField) {
        try {
            Collection<Object> subObject = (Collection<Object>) getFieldValue(cloningField, original);
            Collection<Object> copyCollection = (Collection<Object>) createNewObject(subObject);
            Object collectionItem = subObject.iterator().next();
            String collectionType = collectionItem.getClass().getTypeName();
            Class collectionClass = getObjectClass(collectionType);

            if ("String".equals(collectionItem.getClass().getSimpleName()) || collectionClass.isPrimitive()) {
                assert !isNull.test(copyCollection);
                copyCollection.addAll(subObject);

                insertValue(clone, cloningField, copyCollection);
            } else {

                for (int i = 0; i < subObject.size(); i++) {
                    Object collectionObjectItem = subObject.iterator().next();

                    assert !isNull.test(copyCollection);
                    copyCollection.add(deepCopy(collectionObjectItem));

                }
                insertValue(clone, cloningField, copyCollection);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void cloneObjectFieldObjectType(Object original, Object clone, Field cloningField) {
        Object subObject = getFieldValue(cloningField, original);
        Object cloneSubObject = deepCopy(subObject);

        insertValue(clone, cloningField, cloneSubObject);
    }

    private void cloneObjectFieldArrayType(Object original, Object clone, Field cloningField) {
        try {
            Object subObject = getFieldValue(cloningField, original);
            int arrayLength = Array.getLength(subObject);
            String arrayType = subObject.getClass().getComponentType().getTypeName();
            Class arrayClass = getArrayClass(arrayType);
            Object copySubObject = Array.newInstance(arrayClass, arrayLength);

            if ("String".equals(arrayClass.getSimpleName()) || arrayClass.isPrimitive()) {
                System.arraycopy(subObject, 0, copySubObject, 0, arrayLength);
                insertValue(clone, cloningField, copySubObject);
            } else {

                for (int index = 0; index < arrayLength; index++) {
                    Array.set(copySubObject, index, deepCopy(Array.get(subObject, index)));
                }

                insertValue(clone, cloningField, copySubObject);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Object createNewObject(Object original) {
        try {
            Constructor constructor = original.getClass().getDeclaredConstructor();
            constructor.setAccessible(true);

            return constructor.newInstance();

        } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void insertValue(Object clone, Field cloningField, Object copySubObject) {
        try {
            Field deepCopyObjectField = clone.getClass().getDeclaredField(cloningField.getName());
            deepCopyObjectField.setAccessible(true);

            deepCopyObjectField.set(clone, copySubObject);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Object getFieldValue(Field field, Object object) {
        try {

            return field.get(object);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Class getArrayClass(String className) throws ClassNotFoundException {
        switch (className) {
            case "byte":
                return byte.class;
            case "short":
                return short.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "double":
                return double.class;
            case "float":
                return float.class;
            case "char":
                return char.class;
            default:
                return Class.forName(className);
        }
    }

    private Class getObjectClass(String className) throws ClassNotFoundException {
        switch (className) {
            case "java.lang.Byte":
                return byte.class;
            case "java.lang.Short":
                return short.class;
            case "java.lang.Integer":
                return int.class;
            case "java.lang.Long":
                return long.class;
            case "java.lang.Double":
                return double.class;
            case "java.lang.Float":
                return float.class;
            case "java.lang.Character":
                return char.class;
            default:
                return Class.forName(className);
        }
    }
}