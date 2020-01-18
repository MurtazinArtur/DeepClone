package newjob.deepclone;

import java.util.Arrays;

public class ObjectWithArrays {
    private int[] testPrimitiveArray;
    private String[] testStringArray;
    private Object[] testObjectArray;

    public ObjectWithArrays() {
    }

    public int[] getTestPrimitiveArray() {
        return testPrimitiveArray;
    }

    public void setTestPrimitiveArray(int[] testPrimitiveArray) {
        this.testPrimitiveArray = testPrimitiveArray;
    }

    public String[] getTestStringArray() {
        return testStringArray;
    }

    public void setTestStringArray(String[] testStringArray) {
        this.testStringArray = testStringArray;
    }

    public Object[] getTestObjectArray() {
        return testObjectArray;
    }

    public void setTestObjectArray(Object[] testObjectArray) {
        this.testObjectArray = testObjectArray;
    }

    @Override
    public String toString() {
        return "newjob.deepclone.ArraysClass{" +
                "testPrimitiveArray=" + Arrays.toString(testPrimitiveArray) +
                ", testStringArray=" + Arrays.toString(testStringArray) +
                ", testObjectArray=" + Arrays.toString(testObjectArray) +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectWithArrays)) return false;
        ObjectWithArrays that = (ObjectWithArrays) o;
        return Arrays.equals(getTestPrimitiveArray(), that.getTestPrimitiveArray()) &&
                Arrays.equals(getTestStringArray(), that.getTestStringArray()) &&
                Arrays.equals(getTestObjectArray(), that.getTestObjectArray());
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(getTestPrimitiveArray());
        result = 31 * result + Arrays.hashCode(getTestStringArray());
        result = 31 * result + Arrays.hashCode(getTestObjectArray());
        return result;
    }
}
