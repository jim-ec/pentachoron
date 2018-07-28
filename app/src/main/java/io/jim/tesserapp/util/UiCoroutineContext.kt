/*
 *  Created by Jim Eckerlein on 7/28/18 10:25 AM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/28/18 10:21 AM
 */

package io.jim.tesserapp.util

import android.os.Handler
import android.os.Looper
import kotlin.coroutines.experimental.AbstractCoroutineContextElement
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.ContinuationInterceptor

/**
 * Android coroutine context, executing everything on the UI thread.
 */
object UiCoroutineContext :
        AbstractCoroutineContextElement(ContinuationInterceptor),
        ContinuationInterceptor {
    
    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> =
            UiContinuation(continuation)
    
    /**
     * Guarantees that continuation is always executed on the UI thread.
     */
    private class UiContinuation<T>(val cont: Continuation<T>) : Continuation<T> by cont {
        override fun resume(value: T) {
            if (Looper.myLooper() == Looper.getMainLooper()) cont.resume(value)
            else Handler(Looper.getMainLooper()).post { cont.resume(value) }
        }
        
        override fun resumeWithException(exception: Throwable) {
            if (Looper.myLooper() == Looper.getMainLooper()) cont.resumeWithException(exception)
            else Handler(Looper.getMainLooper()).post { cont.resumeWithException(exception) }
        }
    }
    
}
