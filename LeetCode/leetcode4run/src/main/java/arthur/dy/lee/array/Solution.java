package arthur.dy.lee.array;

/**
 * Created by arthur.dy.lee on 2019-12-02.
 */
public class Solution {

    public int removeElement(int[] nums, int val) {
        int m = 0;
        for(int i = 0; i < nums.length; i++){
            if(nums[i] != val){
                nums[m] = nums[i];
                m++;
            }
        }
        return m;
    }

    public static void main(String[] args) {
        Solution s = new Solution();
        int[] A = new int[5];
        A[0] = 1;
        A[1] = 7;
        A[2] = 3;
        A[3] = 5;
        A[4] = 3;
        System.out.println(s.removeElement(A, 3));
    }
}
