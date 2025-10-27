import java.util.Scanner;  

public class Solution {  
    public static void main(String[] args) {  
        Scanner scanner = new Scanner(System.in);  
        int T = scanner.nextInt();  
        scanner.nextLine(); // consume newline
        
        for (int t = 0; t < T; t++) {  
            String number = scanner.nextLine().trim();  
            System.out.println(maxDeletions(number));  
        }  
        scanner.close();  
    }  
    
    private static int maxDeletions(String number) {  
        int n = number.length();  
        if (n == 1) {  
            return 0; // Can't delete the last digit  
        }  
        
        // Count digits by their modulo 3 values  
        int[] count = new int[3]; // count[0], count[1], count[2] for mod 0, 1, 2  
        int digitSum = 0;  
        
        for (char c : number.toCharArray()) {  
            int digit = c - '0';  
            digitSum += digit;  
            count[digit % 3]++;  
        }  
        
        int totalMod = digitSum % 3;  
        int deletions = 0;  
        
        // If the number is already divisible by 3  
        if (totalMod == 0) {  
            // We can delete all digits that are multiples of 3, except we must leave at least one digit  
            deletions = count[0];  
            if (deletions == n) {  
                deletions = n - 1; // Must leave at least one digit  
            }  
        } else if (totalMod == 1) {  
            // We need to remove digits with sum ≡ 1 (mod 3)  
            // Option 1: Remove one digit ≡ 1 (mod 3)  
            // Option 2: Remove two digits ≡ 2 (mod 3)  
            if (count[1] > 0) {  
                deletions = 1;  
                // After removing one digit ≡ 1 (mod 3), we can remove all remaining digits ≡ 0 (mod 3)  
                deletions += count[0];  
                if (deletions == n) {  
                    deletions = n - 1;  
                }  
            } else if (count[2] >= 2) {  
                deletions = 2;  
                // After removing two digits ≡ 2 (mod 3), we can remove all remaining digits ≡ 0 (mod 3)  
                deletions += count[0];  
                if (deletions == n) {  
                    deletions = n - 1;  
                }  
            } else {  
                deletions = 0; // Cannot make it divisible by 3  
            }  
        } else { // totalMod == 2  
            // We need to remove digits with sum ≡ 2 (mod 3)  
            // Option 1: Remove one digit ≡ 2 (mod 3)  
            // Option 2: Remove two digits ≡ 1 (mod 3)  
            if (count[2] > 0) {  
                deletions = 1;  
                // After removing one digit ≡ 2 (mod 3), we can remove all remaining digits ≡ 0 (mod 3)  
                deletions += count[0];  
                if (deletions == n) {  
                    deletions = n - 1;  
                }  
            } else if (count[1] >= 2) {  
                deletions = 2;  
                // After removing two digits ≡ 1 (mod 3), we can remove all remaining digits ≡ 0 (mod 3)  
                deletions += count[0];  
                if (deletions == n) {  
                    deletions = n - 1;  
                }  
            } else {  
                deletions = 0; // Cannot make it divisible by 3  
            }  
        }  
        
        return deletions;  
    }  
}