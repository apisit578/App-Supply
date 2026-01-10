package th.co.cjexpress.store.sage.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer =
        object : Observer<T> {
            override fun onChanged(t: T) {
                data = t
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }

    this.observeForever(observer)

    // Wait for LiveData to emit a value
    if (!latch.await(time, timeUnit)) {
        throw RuntimeException("LiveData value was never set.")
    }

    return data as T
}
