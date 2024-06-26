package uk.matvey.dukt.random

import java.util.concurrent.ThreadLocalRandom
import kotlin.random.asKotlinRandom

object RandomSupport {

    val LETTERS = 'a'..'z'

    val random = ThreadLocalRandom.current().asKotlinRandom()

    fun randomInt() = random.nextInt()

    fun randomLong() = random.nextLong()

    fun randomStr(length: Int) = (1..length).map { LETTERS.random(random) }.joinToString("")
}