package org.paradisehell.one

interface APIResult<T, F : APIResult.Failure> {

    interface Failure

    val isSuccess: Boolean

    val isFailure: Boolean
        get() {
            return isSuccess.not()
        }

    val data: T?

    val failure: F?
}

inline fun <T, F, R : APIResult<T, F>> R.onSuccess(successBlock: (data: T) -> Unit): R {
    data?.takeIf { isSuccess }?.let(successBlock)
    return this
}

inline fun <T, F, R : APIResult<T, F>> R.onFailure(failureBlock: (failure: F) -> Unit): R {
    failure?.takeIf { isFailure }?.let(failureBlock)
    return this
}

inline fun <IT, IF, IR : APIResult<IT, IF>, OT, OF, OR : APIResult<OT, OF>> IR.onFailureThen(
    failureBlock: (failure: IF) -> OR
): OR? {
    return failure?.takeIf { isFailure }?.let(failureBlock)
}