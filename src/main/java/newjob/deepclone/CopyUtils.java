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

    private static Object getKeyFromValue(Map originalMap, Object value) {
        for (Object mapKey : originalMap.keySet()) {
            if (originalMap.get(mapKey).equals(value)) {
                return mapKey;
            }
        }
        return null;
    }

    public Object deepCopy(Object originalObject) {

        if (isNull.test(originalObject)) {
            throw new IllegalArgumentException("Original object must be not null for Deep Copy");
        }
        if (!originalObject.getClass().isArray()){
            String objectType = originalObject.getClass().getTypeName();
            Class objectClass = getObjectClass(objectType);

            if(objectClass.isPrimitive() || "String".equals(originalObject.getClass().getSimpleName())){
                return originalObject;
            }
        }

        if (originalObject.getClass().isArray()) {
            return getCloneObjectOfArrayType(originalObject);
        } else if (Collection.class.isAssignableFrom(originalObject.getClass())) {
            return getCloneObjectOfCollectionType(originalObject);
        } else if (Map.class.isAssignableFrom(originalObject.getClass())) {
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
        Predicate<Field> isMap = field -> Map.class.isAssignableFrom(field.getType());

        fieldStream.forEach(field -> {
            field.setAccessible(true);
            if (!isNull.test(deepCopyObjectWithFields)) {
                if (isNull.test(getFieldValue(field, originalObject))) {
                    insertValue(deepCopyObjectWithFields, field, null);
                } else if (isString.or(isPrimitive).or(isEnum).test(field)) {
                    insertValue(deepCopyObjectWithFields, field, getFieldValue(field, originalObject));
                } else if (isCollection.test(field)) {
                    insertValue(deepCopyObjectWithFields, field,
                            getCloneObjectOfCollectionType(getFieldValue(field, originalObject)));
                } else if (isArray.test(field)) {
                    insertValue(deepCopyObjectWithFields, field,
                            getCloneObjectOfArrayType(getFieldValue(field, originalObject)));
                } else if (isMap.test(field)) {
                    insertValue(deepCopyObjectWithFields, field,
                            getCloneObjectOfMapType(getFieldValue(field, originalObject)));
                } else {
                    insertValue(deepCopyObjectWithFields, field, deepCopy(getFieldValue(field, originalObject)));
                }
            }
        });

        return deepCopyObjectWithFields;
    }

    private Object getCloneObjectOfMapType(Object original) {
        Map<Object, Object> originalMap = (Map<Object, Object>) original;
        Map<Object, Object> copyMap = (Map<Object, Object>) createNewObject(originalMap);
        Collection mapValues = originalMap.values();
        Stream mapValuesStream = mapValues.stream();

        mapValuesStream.forEach(
                mapValue -> {
                    assert !isNull.test(copyMap);
                    copyMap.put(getMapKey(Objects.requireNonNull(getKeyFromValue(originalMap,
                            mapValue))), getMapValue(mapValue));
                }
        );
        
        return copyMap;
    }

    private Object getMapKey(Object mapKey) {
        Object copyMapKey;
        String mapKeyType = mapKey.getClass().getTypeName();
        Class mapKeyClass = getObjectClass(mapKeyType);

        if (mapKeyClass.isPrimitive() || mapKeyClass.isEnum()
                || "String".equals(mapKey.getClass().getSimpleName())) {
            copyMapKey = mapKey;
        } else if (mapKey.getClass().isArray()) {
            copyMapKey = getCloneObjectOfArrayType(mapKey);
        } else if (Collection.class.isAssignableFrom(mapKey.getClass())) {
            copyMapKey = getCloneObjectOfCollectionType(mapKey);
        } else if (Map.class.isAssignableFrom(mapKey.getClass())) {
            copyMapKey = getCloneObjectOfMapType(mapKey);
        } else {
            copyMapKey = deepCopy(mapKey);
        }

        return copyMapKey;
    }

    private Object getMapValue(Object mapValue) {
        Object copyMapValue;
        String mapValueType = mapValue.getClass().getTypeName();
        Class mapValueClass = getObjectClass(mapValueType);

        if (mapValueClass.isPrimitive() || mapValueClass.isEnum()
                || "String".equals(mapValue.getClass().getSimpleName())) {
            copyMapValue = mapValue;
        } else if (mapValue.getClass().isArray()) {
            copyMapValue = getCloneObjectOfArrayType(mapValue);
        } else if (Collection.class.isAssignableFrom(mapValue.getClass())) {
            copyMapValue = getCloneObjectOfCollectionType(mapValue);
        } else if (Map.class.isAssignableFrom(mapValue.getClass())) {
            copyMapValue = getCloneObjectOfMapType(mapValue);
        } else {
            copyMapValue = deepCopy(mapValue);
        }

        return copyMapValue;
    }

    private Object getCloneObjectOfArrayType(Object original) {
        Object copyObject;
        int arrayLength = Array.getLength(original);
        String theClassName = original.getClass().getComponentType().getTypeName();
        Class arrayClass = getArrayClass(theClassName);

        copyObject = Array.newInstance(arrayClass, arrayLength);

        if ("String".equals(arrayClass.getSimpleName()) || arrayClass.isPrimitive()) {
            System.arraycopy(original, 0, copyObject, 0, arrayLength);
        } else {
            for (int index = 0; index < arrayLength; index++) {
                Array.set(copyObject, index, deepCopy(Array.get(original, index)));
            }
        }

        return copyObject;
    }

    private Object getCloneObjectOfCollectionType(Object original) {
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
            Stream collectionStream = subObject.stream();
            collectionStream.forEach(
                    itemValue -> {
                        copyCollection.add(deepCopy(itemValue));
                    });

            return copyCollection;
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

    private Class getArrayClass(String className) {
        try {
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Class getObjectClass(String className) {
        try {
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}