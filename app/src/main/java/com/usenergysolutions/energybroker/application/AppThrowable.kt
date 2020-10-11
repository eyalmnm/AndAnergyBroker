package com.usenergysolutions.energybroker.application

class AppThrowable(open val msg: String?, open val code: Int?, open val caus: Throwable? = null) :
    Throwable(msg, caus) {

    constructor(msg: String?, code: Int) : this(msg, code, null)

    constructor(throwable: Throwable?) : this(throwable?.toString(), null, throwable)

    constructor() : this(null, null, null)

}