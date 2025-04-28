package com.example.visara.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

//object AppScope : CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.IO)

object AppScope : CoroutineScope {
    private var job = SupervisorJob()
    override val coroutineContext = Dispatchers.IO + job
    fun cancelAllJobs() {
        job.cancel()
        job = SupervisorJob()
    }
}
