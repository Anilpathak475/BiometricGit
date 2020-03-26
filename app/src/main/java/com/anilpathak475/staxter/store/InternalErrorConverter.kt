package com.anilpathak475.staxter.store

import com.anilpathak475.staxter.data.model.login.ErrorType

interface InternalErrorConverter {
    fun convertToGeneralErrorType(error: Throwable): ErrorType
}
