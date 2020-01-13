package newjob.deepclone;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CopyUtils {
    Predicate<Object> isNull = Objects::isNull;

    public Object deepCopy(Object originalObject) {
        if (isNull.test(originalObject)) {
            throw new IllegalArgumentException("Original object must be not null for Deep Copy");
        }

        Field[] originalObjectFields = originalObject.getClass().getDeclaredFields();
        Object deepCopyObject = createNewObject(originalObject);
        Stream<Field> fieldStream = Arrays.stream(originalObjectFields);
        Predicate<Field> isPrimitive = field -> field.getType().isPrimitive();
        Predicate<Field> isString = field -> "String".equals(field.getType().getSimpleName());
        Predicate<Field> isCollection = field -> Collection.class.isAssignableFrom(field.getType());
        Predicate<Field> isArray = field -> field.getType().isArray();
        Predicate<Field> isEnum = field -> field.getType().isEnum();

        fieldStream.forEach(field -> {
            field.setAccessible(true);
            if (!isNull.test(deepCopyObject)) {
                if (isNull.test(getFieldValue(field, originalObject))) {
                    insertValue(deepCopyObject, field, null);
                } else if (isString.or(isPrimitive).or(isEnum).test(field)) {
                    cloneObjectFieldPrimitiveOrStringType(originalObject, deepCopyObject, field);
                } else if (isCollection.test(field)) {
                    cloneObjectFieldCollectionType(originalObject, deepCopyObject, field);
                } else if (isArray.test(field)) {
                    cloneObjectFieldArrayType(originalObject, deepCopyObject, field);
                } else {
                    cloneObjectFieldObjectType(originalObject, deepCopyObject, field);
                }
            }
        });

        return deepCopyObject;
    }

    private void cloneObjectFieldPrimitiveOrStringType(Object original, Object clone, Field cloningField) {
        insertValue(clone, cloningField, getFieldValue(cloningField, original));
    }

    private void cloneObjectFieldCollectionType(Object original, Object clone, Field cloningField) {
        Collection<Object> subObject = (Collection<Object>) getFieldValue(cloningField, original);
        Collection<Object> copyCollection = (Collection<Object>) createNewObject(subObject);
        Object collectionItem = subObject.iterator().next();

        if ("String".equals(collectionItem.getClass().getSimpleName())) {
            assert !isNull.test(copyCollection);
            copyCollection.addAll(subObject);
        } else {

            for (int i = 0; i < subObject.size(); i++) {
                Object collectionObjectItem = subObject.iterator().next();

                assert !isNull.test(copyCollection);
                copyCollection.add(deepCopy(collectionObjectItem));
            }
        }

        insertValue(clone, cloningField, copyCollection);
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
            String theClassName = subObject.getClass().getComponentType().getTypeName();
            Class arrayClass = getArrayClass(theClassName);
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
}