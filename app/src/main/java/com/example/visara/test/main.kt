package com.example.visara.test

fun main() {
    println(Solution().removeElement(intArrayOf(0,1,2,2,3,0,4,2), 2))
}

class Solution {
    fun removeElement(nums: IntArray, `val`: Int): Int {
        var result = 0
        var fast = 0
        var slow = 0

        while (fast < nums.size) {
            if (nums[fast] != `val`) {
                nums[slow] = nums[fast]
                slow++
                fast++
                result++
            } else {
                fast++
            }
        }

        return result
    }
}
// nums = [0,1,2,2,3,0,4,2], val = 2
