package disassembler;

public class Functions {
    public static int bytesToInt(int[] source) { // перевод массива байт в int (little endian)
        int ans = 0;
        int multiplier = 1;
        for (int i = 0; i < source.length; i++) {
            ans = ans + source[i] * multiplier;
            multiplier = multiplier * 256;
        }
        return ans;
    }

    public static int slice(int value, int finish, int start) {// срез числа
        if (finish == 31 && start == 0) {
            return value;
        }
        return (value >>> start) & ((1 << (finish - start + 1)) - 1);
    }

    public static int getValue(int value, int numberOfBytes) {// вывод числа с учетом дополнения до двух
        if (((value >> numberOfBytes) & 1) == 0) {
            return value;
        }
        return -slice((~value) + 1, numberOfBytes, 0);
    }

    public static String getReg(int value) {// нужные регистры
        switch (value) {
            case 0 -> {
                return "zero";
            }
            case 1 -> {
                return "ra";
            }
            case 2 -> {
                return "sp";
            }
            case 3 -> {
                return "gp";
            }
            case 4 -> {
                return "tp";
            }
            case 5, 6, 7 -> {
                return "t" + (value - 5);
            }
            case 8, 9 -> {
                return "s" + (value - 8);
            }
            case 10, 11, 12, 13, 14, 15, 16, 17 -> {
                return "a" + (value - 10);
            }
            case 18, 19, 20, 21, 22, 23, 24, 25, 26, 27 -> {
                return "s" + (value - 16);
            }
            case 28, 29, 30, 31 -> {
                return "t" + (value - 25);
            }
        }
        return "";
    }
}
