import java.util.Arrays;

public class IntList {
    private int size;
    private int[] array = new int[10];

    public void add(int element) {
        if(size == array.length){
            upSize();
        }
        array[size] = element;
        size++;
    }

    public int get(int index) throws IndexOutOfBoundsException {
        if(index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }
        return array[index];
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder arrayToString = new StringBuilder();
        for(int i = 0; i < size(); i++) {
            arrayToString.append(String.valueOf(get(i)));
            if(i != size() - 1) {
                arrayToString.append(" ");
            }
        }
        return arrayToString.toString();
    }

    private void upSize() {
        array = Arrays.copyOf(array, array.length * 2);
    }
}
