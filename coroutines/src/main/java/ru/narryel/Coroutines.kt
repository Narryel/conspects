package ru.narryel

import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
suspend fun main() {

//    MainScope() == ContextScope(SupervisorJob() + Dispatchers.Main)
//    MainScope().launch { throw RuntimeException() } - Кинет эксепшн

//    runBlocking { supervisorScope { launch { throw RuntimeException() } } }

//    runBlocking {
//        val deferred: Deferred<Any>
//        supervisorScope {
//            deferred = async { throw RuntimeException() }
//        }
//        println(deferred.isCompleted)
//        deferred.await()
//    }


    runBlocking(Dispatchers.Default) {
        awaitAndWrite(5)
        println("hello from coroutine scope")
    }
    println("Hello from main thread")

}


suspend fun awaitAndWrite(time: Int) {

    delay(time * 1000L)
    println("hello from sleeping fun")
}