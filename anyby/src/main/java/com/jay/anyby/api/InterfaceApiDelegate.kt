/**
 * 委托所有接口的实现类，通过动态代理实现
 * 使用方式如下：
 * private val lifecycleCallbacksWithDelegate = object : Application.ActivityLifecycleCallbacks by api() {}
 * 注意：目前只支持接口中无返回值的代理，返回值的方法还需要自己实现
 * @author jaydroid
 * @version 1.0
 * @date 2021/10/6
 */

package com.jay.anyby.api

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

inline fun <reified T : Any> api(): T {
    val javaClass = T::class.java
    val noOpHandler = InvocationHandler { _, _, _ ->
        // no op
    }
    return Proxy.newProxyInstance(javaClass.classLoader, arrayOf(javaClass), noOpHandler) as T
}

