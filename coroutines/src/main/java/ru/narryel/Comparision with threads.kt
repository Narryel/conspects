package ru.narryel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

fun main() {
    println("threadTime " + getThreadTime())
    println("coroutineTime " + getCoroutineTime())
}

private fun getThreadTime(): Long {
    val countDownLatch = CountDownLatch(3)
    val threadPool = Executors.newSingleThreadExecutor()
    return measureTimeMillis {
        threadPool.submit {
            Thread.sleep(3000)
            println("thread 1")
            countDownLatch.countDown()
        }
        threadPool.submit {
            Thread.sleep(2000)
            println("thread 2")
            countDownLatch.countDown()
        }
        threadPool.submit {
            Thread.sleep(1000)
            println("thread 3")
            countDownLatch.countDown()
        }
        countDownLatch.await()
    }
}

private fun getCoroutineTime(): Long {
    val dispatcher = Dispatchers.IO.limitedParallelism(1)
    return measureTimeMillis {
        runBlocking {
            launch(dispatcher) {
                delay(3000)
                println("coroutine 1")
            }
            launch(dispatcher) {
                delay(2000)
                println("coroutine 2")
            }
            launch(dispatcher) {
                delay(1000)
                println("coroutine 3")
            }
        }
    }
}