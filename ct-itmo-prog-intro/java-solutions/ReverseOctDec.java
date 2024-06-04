public class ReverseOctDec {
    public static String[] upSize(String[] arr) {
        String[] newArr = new String[arr.length * 2];
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        return newArr;
    }

    public static int[] upSize(int[] arr) {
        int[] newArr = new int[arr.length * 2];
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        return newArr;
    }

    public static void main(String[] args) {
        MyScanner in = new MyScanner(System.in);
        int n = 0;
        String[] answer = new String[10];
        while (in.hasNextLine()) {
            answer[n] = in.nextLine();
            n++;
            if (n == answer.length) {
                answer = upSize(answer);
            }
        }
        for (int i = n - 1; i >= 0; i--) {
            int[] lineNumbers = new int[10];
            MyScanner scanLine = new MyScanner(answer[i]);
            int m = 0;
            while (scanLine.hasNextInt()) {
                lineNumbers[m] = scanLine.nextInt();
                m++;
                if (m == lineNumbers.length) {
                    lineNumbers = upSize(lineNumbers);
                }
            }
            for (int j = m - 1; j >= 0; j--) {
                System.out.print(lineNumbers[j] + " ");
            }
            System.out.println();
        }
    }
}