package org.paradisehell.one

import java.lang.reflect.Type

/**
 * A [ThrowableResolver] try to resolve [Throwable] when a suspend method throw [Throwable]
 * in the run time.
 *
 * @param T a type the [ThrowableResolver] return after resolving.
 */
interface ThrowableResolver<T> {

    /**
     * Resolve [Throwable].
     *
     * @param throwable the [Throwable] to resolve
     *
     * @return resolved result
     */
    fun resolve(throwable: Throwable): T

    /**
     * A Factory to create [ThrowableResolver].
     */
    interface Factory<T> {
        /**
         * Create a [ThrowableResolver].
         *
         * @param type the suspend method's return type
         *
         * @return an instance of [ThrowableResolver]
         */
        fun create(type: Type): ThrowableResolver<T>?
    }
}