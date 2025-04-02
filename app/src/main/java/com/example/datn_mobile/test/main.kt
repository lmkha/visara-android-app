package com.example.datn_mobile.test

fun main() {
    val x = tailRecFactorial(5)
    println(x)
}

tailrec fun tailRecFactorial(n: Long, a: Long = 1) : Long {
    return if (n == 1L) a
    else {
        tailRecFactorial(n - 1, n * a)
    }
}
