package newjob.deepclone;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

class CopyUtils {
    private static Predicate<Object> isNull = Objects::isNull;
    private static Set<Object> deepCopyObjectSet = new HashSet<>();
    private static Supplier<Stream<Object>> streamSupplier = () -> deepCopyObjectSet.stream();
    private static boolean isThisObjectInTheSet = false;

    private static Object getKeyFromValue(Map originalMap, Object value) {
        for (Object mapKey : originalMap.keySet()) {
            if (originalMap.get(mapKey).equals(value)) {
                return mapKey;
            }
        }
        throw new DeepCopyException("Error while Map get key from value for copy");
    }

    static Object deepCopy(Object originalObject) {
        if (isNull.test(originalObject)) {
            throw new DeepCopyException("Original object must be not null for Deep Copy");
        }

        if (!originalObject.getClass().isArray()) {
            String objectType = originalObject.getClass().getTypeName();
            Class objectClass = getObjectClass(objectType);
            if (objectClass.isPrimitive() || "String".equals(originalObject.getClass().getSimpleName())) {
                return originalObject;
            }
        }

        if (originalObject.getClass().isArray()) {
            return getCloneObjectOfArrayType(originalObject);
        } else if (Collection.class.isAssignableFrom(originalObject.getClass())) {
            Object cloneObjectOfCollectionType = getCloneObjectOfCollectionType(originalObject);
            return cloneObjectOfCollectionType;
        } else if (Map.class.isAssignableFrom(originalObject.getClass())) {
            return getCloneObjectOfMapType(originalObject);
        }

        if (!(deepCopyObjectSet.isEmpty())) {
            isThisObjectInTheSet = streamSupplier.get().anyMatch(originalObject::equals);
        }

        if (isThisObjectInTheSet) {
            return streamSupplier.get().filter(originalObject::equals).findAny().get();
        } else {
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

            deepCopyObjectSet.add(deepCopyObjectWithFields);
            return deepCopyObjectWithFields;
        }
    }

    private static Object getCloneObjectOfMapType(Object original) {
        Map<Object, Object> originalMap = (Map<Object, Object>) original;
        Map<Object, Object> copyMap = createNewObject(originalMap);
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

    private static Object getMapKey(Object mapKey) {
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

    private static Object getMapValue(Object mapValue) {
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

    private static Object getCloneObjectOfArrayType(Object original) {
        Object copyObject;
        int arrayLength = Array.getLength(original);
        String theClassName = original.getClass().getComponentType().getTypeName();
        Class arrayClass = getObjectClass(theClassName);

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

    private static Object getCloneObjectOfCollectionType(Object original) {
        Collection<Object> subObject = (Collection<Object>) original;
        Collection<Object> copyCollection = createNewObject(subObject);
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

    private static <T> T createNewObject(Object original) {
        Constructor[] declaredConstructors = original.getClass().getDeclaredConstructors();
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

    private static <T> T createNewObjectWithDefaultConstructor(Object original) throws IllegalAccessException,
            InvocationTargetException, InstantiationException, NoSuchMethodException {

        Constructor declaredConstructor = original.getClass().getDeclaredConstructor();
        declaredConstructor.setAccessible(true);

        return (T) declaredConstructor.newInstance();

    }

    private static <T> T createNewObjectWithOutDefaultConstructor(Object original, Constructor objectConstructor) throws IllegalAccessException, InvocationTargetException, InstantiationException {
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

    private static boolean hasDefaultConstructor(Constructor[] constructors) {
        for (Constructor constructor : constructors) {
            if (constructor.getGenericParameterTypes().length == 0) {
                return true;
            }
        }
        return false;
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
            switch (className) {
                case "byte":
                    return byte.class;
                case "java.lang.Byte":
                    return byte.class;
                case "short":
                    return short.class;
                case "java.lang.Short":
                    return short.class;
                case "int":
                    return int.class;
                case "java.lang.Integer":
                    return int.class;
                case "long":
                    return long.class;
                case "java.lang.Long":
                    return long.class;
                case "double":
                    return double.class;
                case "java.lang.Double":
                    return double.class;
                case "float":
                    return float.class;
                case "java.lang.Float":
                    return float.class;
                case "char":
                    return char.class;
                case "java.lang.Character":
                    return char.class;
                default:
                    return Class.forName(className);
            }
        } catch (ClassNotFoundException e) {
            throw new DeepCopyException("Error while getting the instance class name for copy", e);
        }
    }
}