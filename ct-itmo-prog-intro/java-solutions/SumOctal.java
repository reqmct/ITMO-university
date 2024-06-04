public class SumOctal {
    public static void main(String[] args) {
        int sum = 0;
        for(int i = 0; i < args.length; i++) {
            int left = 0;
            for(int right = 0; right < args[i].length(); right++) {
                char symbol = args[i].charAt(right);
                String number = "";
                if (Character.isWhitespace(symbol)) { 
                    number = args[i].substring(left, right);
                    left = right + 1;
                } else if (right == args[i].length() - 1) {
                    number = args[i].substring(left, right + 1);    
                }
                if (!number.isEmpty()) {
                    if (number.charAt(number.length() - 1) == 'o' || number.charAt(number.length() - 1) == 'O') {
                        sum += (int) Long.parseLong(number.substring(0, number.length() - 1), 8);
                    } else { 
                        sum += (int) Long.parseLong(number);
                    }
                }
            }
        }
        System.out.println(sum);
    }    
}