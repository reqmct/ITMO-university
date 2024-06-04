import java.util.Scanner;
import java.util.Arrays;

public class ReverseSum {
    public static int[][] upSize(int[][] arr) {
        int[][] newArr = Arrays.copyOf(arr, arr.length * 2);
        return newArr;
    }

    public static int[] upSize(int[] arr) {
        int[] newArr = Arrays.copyOf(arr, arr.length * 2);
        return newArr;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // close scanner
        int n = 0;
        int[][] arr = new int[1][];
        int[] sizeLine = new int[1];
        int maxM = 0;
        while(in.hasNextLine()) {
            int[] add = new int[1];
            int m = 0;
            Scanner scanLine = new Scanner(in.nextLine());
            while(scanLine.hasNextInt()) {
                add[m] = scanLine.nextInt();
                m++;
                if (m == add.length) {
                    add = upSize(add);
                }
            }
            scanLine.close();
            arr[n] = add;
            sizeLine[n] = m;
            if(m > maxM) {
                maxM = m;
            }
            n++;
            if(n == arr.length) {
                arr = upSize(arr);
                sizeLine = upSize(sizeLine);
            }
        }
        int[] sumColumns = new int[maxM];
        for(int j = 0; j < maxM; j++) {
            for(int i = 0; i < n; i++) {
                if(sizeLine[i] >= j) {
                    sumColumns[j] += arr[i][j]; 
                }
            }
        }
        for(int i = 0; i < n; i++) {
            int sumLine = 0;
            for(int j = 0; j < sizeLine[i]; j++) {
                sumLine += arr[i][j];
            }
            for(int j = 0; j < sizeLine[i]; j++) {
                System.out.print(sumLine + sumColumns[j] - arr[i][j] + " ");
            }
            System.out.println();
        }
        in.close();
    }
}