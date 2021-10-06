package com.jay.vbhelper.simple.delegate_api

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * java 动态代理测试类
 * 利用反射机制在运行时创建代理类。
 * @author jaydroid
 * @version 1.0
 * @date 2021/10/6
 */
interface HelloInterface {
    fun sayHello()
}

class Hello : HelloInterface {
    override fun sayHello() {
        println("Hello HelloInterface!")
    }
}

interface ByeInterface {
    fun sayBye()
}

class Bye : ByeInterface {
    override fun sayBye() {
        println("Bye ByeInterface!")
    }
}

//由于代理只能为一个类服务，如果需要代理的类很多，那么就需要编写大量的代理类，比较繁琐
class HelloProxy : HelloInterface {
    private val helloInterface: HelloInterface = Hello()
    override fun sayHello() {
        println("Before invoke sayHello")
        helloInterface.sayHello()
        println("After invoke sayHello")
    }
}


class ProxyHandler(private val o: Any) : InvocationHandler {

    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any? {
        System.out.println("Before invoke " + method.name)
        val result = method.invoke(o, *args.orEmpty())
        System.out.println("After invoke " + method.name)
        return result
    }
}

fun testProxy() {
//    System.getProperties().setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true")
    val bye: ByeInterface = Bye()
    val proxyBye = Proxy.newProxyInstance(
        Bye::class.java.classLoader,
        arrayOf(ByeInterface::class.java),
        ProxyHandler(bye)
    ) as ByeInterface
    proxyBye.sayBye()

    val hello: HelloInterface = Hello()
    val proxyHello: HelloInterface = Proxy.newProxyInstance(
        Hello::class.java.classLoader,
        arrayOf(HelloInterface::class.java),
        ProxyHandler(hello)
    ) as HelloInterface
    proxyHello.sayHello()
}


interface IPlay {

    fun playFilm()

    fun isPlay(): Boolean

}


class Actor : IPlay {
    private var name: String

    constructor(name: String) {
        this.name = name
    }

    override fun playFilm() {
        println("The actor $name is playing film")
    }

    override fun isPlay(): Boolean {
        return true
    }
}

fun dynamicProxy1() {
    val actor = Actor("hha")
    val proxy = Proxy.newProxyInstance(
        Actor::class.java.classLoader,
        arrayOf(IPlay::class.java),
        object : InvocationHandler {
            override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
                val result = method?.invoke(actor, *args.orEmpty())
                return result
            }
        }) as IPlay

    proxy.playFilm()
    val play = proxy.isPlay()
    println("isplay:$play")
}

fun dynamicProxy2() {

    val noOpHandler = InvocationHandler { _, _, _ ->
        // no op
    }

    val actor = Actor("haha")
    val proxy = Proxy.newProxyInstance(
        Actor::class.java.classLoader,
        arrayOf(IPlay::class.java),
        noOpHandler
    ) as IPlay

    proxy.playFilm()

}

inline fun <reified T : Any> noOpDelegate(): T {
    val javaClass = T::class.java
    val noOpHandler = InvocationHandler { _, _, _ ->
        // no op
    }
    return Proxy.newProxyInstance(
        javaClass.classLoader, arrayOf(javaClass), noOpHandler
    ) as T
}

class DynamicProxyHandler : InvocationHandler {

    private var traget: Any

    constructor(target: Any) {
        this.traget = target
    }

    val noOpHandler = InvocationHandler { _, _, _ ->
        // no op
    }

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        val proxy =
            Proxy.newProxyInstance(javaClass.classLoader, arrayOf(javaClass), noOpHandler)
        println("执行之前....")
//由于传来的参数是不确定的，这里用*args.orEmpty()传参
        val result = method?.invoke(proxy, *args.orEmpty())
        println("执行之后....")
        return result
    }
}

fun main() {
    println("静态代理")
//    val helloProxy = HelloProxy()
//    helloProxy.sayHello()

    println("动态代理")
//    dynamicProxy1()
//    dynamicProxy2()

//    testProxy()

    val p: IPlay = object : IPlay by noOpDelegate() {
//        override fun isPlay(): Boolean {
//            println("isPlay")
//            return true
//        }

        override fun playFilm() {
            println("playFilm")
        }
    }

    p.isPlay()
    p.playFilm()

}