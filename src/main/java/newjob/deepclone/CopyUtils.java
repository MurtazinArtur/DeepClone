package newjob.deepclone;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

class CopyUtils {

    static <T> T deepCopy(T originalObject) {
        Set<T> deepCopyObjectSet = new HashSet<>();
        Supplier<Stream<T>> streamSupplier = () -> (Stream<T>) deepCopyObjectSet.stream();
        boolean copyObject = false;

        if (isNull(originalObject)) {
            throw new DeepCopyException("Original object must be not null for Deep Copy");
        }

        if (isObjectPrimitiveOrString(getObjectClass(originalObject.getClass().getName()))) {
            return originalObject;
        }

        if (!(deepCopyObjectSet.isEmpty())) {
            copyObject = streamSupplier.get().anyMatch(originalObject::equals);
        }

        if (copyObject) {
            return streamSupplier.get().filter(originalObject::equals).findAny().get();
        } else {

            if (isObjectArrayOrCollection(getObjectClass(originalObject.getClass().getName()))) {
                return getObjectArrayOrCollectionType(originalObject);
            }
        }

        Field[] originalObjectFields = originalObject.getClass().getDeclaredFields();
        T deepCopyObjectWithFields = createNewObject(originalObject);

        Arrays.stream(originalObjectFields).forEach(field -> {
            field.setAccessible(true);
            if (!isNull(deepCopyObjectWithFields)) {
                if (isNull(getFieldValue(field, originalObject))) {
                    insertValue(deepCopyObjectWithFields, field, null);
                } else if (isObjectPrimitiveOrString(getObjectClass(field.getType().getName()))
                        || field.getType().isEnum()) {
                    insertValue(deepCopyObjectWithFields, field, getFieldValue(field, originalObject));
                } else if (field.getType().isArray()) {
                    insertValue(deepCopyObjectWithFields, field,
                            getCloneObjectOfArrayType(getFieldValue(field, originalObject)));
                } else {
                    insertValue(deepCopyObjectWithFields, field, deepCopy(getFieldValue(field, originalObject)));
                }
            }
        });

        deepCopyObjectSet.add(deepCopyObjectWithFields);
        return deepCopyObjectWithFields;
    }

    private static <T> T getObjectArrayOrCollectionType(T originalObject) {
        if (originalObject.getClass().isArray()) {
            return getCloneObjectOfArrayType(originalObject);
        } else if (Collection.class.isAssignableFrom(originalObject.getClass())) {
            return (T) getCloneObjectOfCollectionType(originalObject);
        } else if (Map.class.isAssignableFrom(originalObject.getClass())) {
            return (T) getCloneObjectOfMapType(originalObject);
        }
        throw new DeepCopyException("Error! Collection " + originalObject.getClass() + " can not copy");
    }

    private static <V, K, T> Map<K, V> getCloneObjectOfMapType(T original) {
        Map<K, V> originalMap = (Map<K, V>) original;
        Map<K, V> copyMap = createNewObject(originalMap);

        originalMap.values().forEach(
                mapValue -> {
                    assert !isNull(copyMap);
                    copyMap.put(getMapKey(Objects.requireNonNull(getKeyFromValue(originalMap,
                            mapValue))), getMapValue(mapValue));
                }
        );

        return copyMap;
    }

    private static <T> T getCloneObjectOfArrayType(T original) {
        int arrayLength = Array.getLength(original);
        String theClassName = original.getClass().getComponentType().getTypeName();
        Class<?> arrayClass = getObjectClass(theClassName);

        T copyObject = (T) Array.newInstance(arrayClass, arrayLength);

        if (isObjectPrimitiveOrString(original.getClass())) {
            System.arraycopy(original, 0, copyObject, 0, arrayLength);
        } else {
            for (int index = 0; index < arrayLength; index++) {
                Array.set(copyObject, index, deepCopy(Array.get(original, index)));
            }
        }

        return copyObject;
    }

    private static <T> Collection<T> getCloneObjectOfCollectionType(T original) {
        Collection<T> subObject = (Collection<T>) original;
        Collection<T> copyCollection = createNewObject(subObject);

        if (isObjectPrimitiveOrString(original.getClass())) {
            assert !isNull(copyCollection);

            copyCollection.addAll(subObject);

            return copyCollection;
        } else {
            for (T itemValue : subObject) {
                copyCollection.add(deepCopy(itemValue));
            }

            return copyCollection;
        }
    }

    private static <K> K getMapKey(K mapKey) {
        K copyMapKey;

        if (isObjectPrimitiveOrString(mapKey.getClass())) {
            copyMapKey = mapKey;
        } else if (isObjectArrayOrCollection(mapKey.getClass())) {
            copyMapKey = getObjectArrayOrCollectionType(mapKey);
        } else {
            copyMapKey = deepCopy(mapKey);
        }

        return copyMapKey;
    }

    private static <V> V getMapValue(V mapValue) {
        V copyMapValue;

        if (isObjectPrimitiveOrString(mapValue.getClass())) {
            copyMapValue = mapValue;
        } else if (isObjectArrayOrCollection(mapValue.getClass())) {
            copyMapValue = getObjectArrayOrCollectionType(mapValue);
        } else {
            copyMapValue = deepCopy(mapValue);
        }

        return copyMapValue;
    }

    private static <K, V> K getKeyFromValue(Map<K, V> originalMap, V value) {
        for (K mapKey : originalMap.keySet()) {
            if (originalMap.get(mapKey).equals(value)) {
                return mapKey;
            }
        }
        throw new DeepCopyException("Error while Map get key from value for copy");
    }

    private static <T> T createNewObject(T original) {
        Constructor<?>[] declaredConstructors = original.getClass().getDeclaredConstructors();
        try {
            if (hasDefaultConstructor(declaredConstructors)) {
                return createNewObjectWithDefaultConstructor(original);
            } else {
                return createNewObjectWithOutDefaultConstructor(original, declaredConstructors[0]);
            }
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException
                | NoSuchMethodException e) {
            throw new DeepCopyException("Error while create new instance for copy", e);
        }
    }

    private static <T> T createNewObjectWithDefaultConstructor(T original) throws IllegalAccessException,
            InvocationTargetException, InstantiationException, NoSuchMethodException {
        Constructor<T> declaredConstructor = (Constructor<T>) original.getClass().getDeclaredConstructor();
        declaredConstructor.setAccessible(true);

        return declaredConstructor.newInstance();
    }

    private static <T> T createNewObjectWithOutDefaultConstructor(T original, Constructor<?> objectConstructor)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Field[] objectFields = original.getClass().getDeclaredFields();
        List<Object> fieldsValue = new ArrayList<>();

        for (Field field : objectFields) {
            field.setAccessible(true);
            if (field.getType().isPrimitive()) {
                fieldsValue.add(0);
            } else {
                fieldsValue.add(null);
            }
        }

        return (T) objectConstructor.newInstance(fieldsValue.toArray());
    }

    private static void insertValue(Object clone, Field cloningField, Object copySubObject) {
        try {
            Field deepCopyObjectField = clone.getClass().getDeclaredField(cloningField.getName());
            deepCopyObjectField.setAccessible(true);

            deepCopyObjectField.set(clone, copySubObject);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new DeepCopyException("Error while insert the copy value to field for copy", e);
        }
    }

    private static Object getFieldValue(Field field, Object object) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new DeepCopyException("Error while getting the value of field for copy", e);
        }
    }

    private static Class<?> getObjectClass(String className) {
        try {
            if ("byte".equals(className) || "java.lang.Byte".equals(className)) {
                return byte.class;
            } else if ("short".equals(className) || "java.lang.Short".equals(className)) {
                return short.class;
            } else if ("int".equals(className) || "java.lang.Integer".equals(className)) {
                return int.class;
            } else if ("long".equals(className) || "java.lang.Long".equals(className)) {
                return long.class;
            } else if ("double".equals(className) || "java.lang.Double".equals(className)) {
                return double.class;
            } else if ("float".equals(className) || "java.lang.Float".equals(className)) {
                return float.class;
            } else if ("char".equals(className) || "java.lang.Character".equals(className)) {
                return char.class;
            } else {
                return Class.forName(className);
            }
        } catch (ClassNotFoundException e) {
            throw new DeepCopyException("Error while getting the instance class name for copy", e);
        }
    }

    private static boolean isNull(Object object) {
        return object == null;
    }

    private static boolean isObjectPrimitiveOrString(Class<?> originalObjectClass) {
        if (!originalObjectClass.isArray()) {
            return originalObjectClass.isPrimitive() || "String".equals(originalObjectClass.getSimpleName());
        }

        return false;
    }

    private static boolean isObjectArrayOrCollection(Class<?> originalObjectClass) {
        return originalObjectClass.isArray() || Map.class.isAssignableFrom(originalObjectClass) ||
                Collection.class.isAssignableFrom(originalObjectClass);
    }

    private static boolean hasDefaultConstructor(Constructor[] constructors) {
        for (Constructor constructor : constructors) {
            if (constructor.getGenericParameterTypes().length == 0) {
                return true;
            }
        }

        return false;
    }
}