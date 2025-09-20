import java.util.Arrays;  
import java.util.Scanner;  

public class Solution {  
    public static void main(String[] args) {  
        Scanner scanner = new Scanner(System.in);  
        int n = scanner.nextInt();  
        int[] sequence = new int[n];  
        for (int i = 0; i < n; i++) {  
            sequence[i] = scanner.nextInt();  
        }  
        scanner.close();  
        
        // Check if there exists a decreasing triplet  
        if (hasDecreasingTriplet(sequence)) {  
            System.out.println(-1);  
        } else {  
            // If the sequence is strictly increasing  
            if (isStrictlyIncreasing(sequence)) {  
                System.out.println(n);  
            } else {  
                System.out.println(n - 1);  
            }  
        }  
    }  
    
    private static boolean hasDecreasingTriplet(int[] nums) {  
        int[] dp = new int[3];  
        Arrays.fill(dp, Integer.MAX_VALUE);  
        for (int num : nums) {  
            for (int j = 0; j < 3; j++) {  
                if (num < dp[j]) {  
                    dp[j] = num;  
                    break;  
                }  
            }  
            if (dp[2] < Integer.MAX_VALUE) {  
                return true;  
            }  
        }  
        return false;  
    }  
    
    private static boolean isStrictlyIncreasing(int[] nums) {  
        for (int i = 1; i < nums.length; i++) {  
            if (nums[i] <= nums[i - 1]) {  
                return false;  
            }  
        }  
        return true;  
    }  
}