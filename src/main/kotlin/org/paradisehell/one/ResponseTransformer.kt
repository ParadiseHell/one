package org.paradisehell.one

import java.io.InputStream
import java.lang.reflect.Type

/**
 * A [ResponseTransformer] is to transform a response to another response.
 *
 * As we know, different backend may return different data structs, like {code, data, message}
 * or {status, data, message} and so on. With [ResponseTransformer], we can transform different
 * data structs to one data struct.
 */
interface ResponseTransformer {
    /**
     * Transform response to another response.
     *
     * @param original the original response [InputStream]
     *
     * @return Another response [InputStream] (better same data struct in one
     * App)
     */
    fun transform(original: InputStream): InputStream

    /**
     * A [Factory] is to create [ResponseTransformer].
     */
    interface Factory {
        /**
         * Create a [ResponseTransformer].
         *
         * @param type the return type of method
         *
         * @return a instance of [ResponseTransformer]
         */
        fun create(type: Type): ResponseTransformer?
    }
}