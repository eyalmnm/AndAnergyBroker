package com.usenergysolutions.energybroker.model

//import com.usenergysolutions.energybroker.application.AppThrowable

class DataWrapper<T> {
    var data: T? = null
    var throwable: Throwable? = null
    // var throwable: AppThrowable? = null  // TODO USE IT when server transmit error codes
}