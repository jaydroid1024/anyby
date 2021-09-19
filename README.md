# VBHelper
ViewBinding 超详细原理解析，利用 Kotlin 属性委托实现 VBHelper 方便 VB 在多场景下的生成

ViewBinding 原理解析点 [这里](/Jetpack_Viewbinding_Doc.md)，主要介绍了以下几点
- VB 集成与一般使用方式，包括：Activity 、Fragment、Adapter、include、merge、ViewStub
- KT 属性代理与泛型实化类型参数 `reified` 的介绍
- 通过 KT 属性代理创建 VB
- LayoutInflater 原理与参数解析
- XXXBinding 类的绑定过程
- XXXBinding 类的生成过程

借助 lazy 属性委托的优势：

* Kotlin 1.4 做的优化，当某些委托属性不会使用 KProperty。对于他们来说，在 $$delegatedProperties 中生成 KProperty 对象是多余的。Kotlin 1.4 版本将优化此类情况。如果委托的属性运算符是内联的，并且没有使用 KProperty 参数，则不会生成相应的反射对象。
* 参考博客：[What to Expect in Kotlin 1.4 and Beyond | Optimized delegated properties](https://blog.jetbrains.com/kotlin/2019/12/what-to-expect-in-kotlin-1-4-and-beyond/)
  

## VBHelper 功能与使用

### 1. 在 Activity 中创建 ViewBinding 绑定类

**反射和无反射两种使用方式如下：**

1. 借助 lazy 属性委托  + 反射 VB 的 inflate 方法

```kotlin
private val binding: ActivityMainBinding by vb()
```

2. 借助 lazy 属性委托  + 传递 inflate 方法引用

```kotlin
private val binding: ActivityMainBinding by vb(ActivityMainBinding::inflate)
```

**核心实现代码**

```kotlin
class ActivityVBLazy<T : ViewBinding>(
    private val activity: ComponentActivity,
    private val kClass: KClass<*>,
    private val inflateMethodRef: ((LayoutInflater) -> T)?
) : Lazy<T> {
    private var cachedBinding: T? = null
    override val value: T
        get() {
            var viewBinding = cachedBinding
            if (viewBinding == null) {
                viewBinding = if (inflateMethodRef != null) {
                    //借助 lazy 属性委托 + 传递 inflate 方法引用
                    inflateMethodRef.invoke(activity.layoutInflater)
                } else {
                    //借助 lazy 属性委托  + 反射绑定类的 inflate 方法
                    @Suppress("UNCHECKED_CAST")
                    kClass.java.getMethod(METHOD_INFLATE, LayoutInflater::class.java)
                        .invoke(null, activity.layoutInflater) as T
                }
                activity.setContentView(viewBinding.root)
                cachedBinding = viewBinding
            }
            return viewBinding
        }
    override fun isInitialized() = cachedBinding != null
}
```

### 2. 在 Fragment 中创建 ViewBinding 绑定类

**反射和无反射两种使用方式如下：**

1. 借助 lazy 属性委托  + 反射 VB 的 inflate 方法

```kotlin
private val binding: FragmentMainBinding by vb()
```

2. 借助 lazy 属性委托  + 传递 inflate 方法引用

```kotlin
private val binding: FragmentMainBinding by vb(FragmentMainBinding::inflate)
```

**核心实现代码**

```kotlin
class FragmentVNLazy<T>(
    private val fragment: Fragment,
    private val kClass: KClass<*>,
    private val inflateMethodRef: ((LayoutInflater) -> T)?
) : Lazy<T> {
    private var cachedBinding: T? = null
    private val clearBindingHandler by lazy(LazyThreadSafetyMode.NONE) { Handler(Looper.getMainLooper()) }

    init {
        observeFragmentDestroy(fragment) { clearBindingHandler.post { cachedBinding = null } }
    }

    override val value: T
        get() {
            var viewBinding = cachedBinding
            if (viewBinding == null) {
                checkBindingFirstInvoke(fragment)
                viewBinding = if (inflateMethodRef != null) {
                    //借助 lazy 属性委托 + 传递 inflate 方法引用
                    inflateMethodRef.invoke(fragment.layoutInflater)
                } else {
                    //借助 lazy 属性委托  + 反射绑定类的 inflate 方法
                    @Suppress("UNCHECKED_CAST")
                    kClass.java.getMethod(METHOD_INFLATE, LayoutInflater::class.java)
                        .invoke(null, fragment.layoutInflater) as T
                }
                cachedBinding = viewBinding
            }
            return viewBinding!!
        }
    
    override fun isInitialized() = cachedBinding != null

}
```

### 3. 在 View 中创建 ViewBinding 绑定类

**反射和无反射两种使用方式如下：**

1. 通过自定义属性代理 + 反射绑定类的 inflate 三参数方法

```kotlin
private val binding: MyViewBinding by vb()
```

2. 通过自定义属性代理 + 传递 inflate 三参数方法引用

```kotlin
private val binding: MyViewBinding by vb(MyViewBinding::inflate)
```

### 4. 在 Adapter 中创建包含了绑定类的 BindingViewHolder

**反射和无反射两种使用方式如下：**

1. 通过自定义属性代理 + 反射绑定类的 inflate 三参数方法

```kotlin
val holder: BindingViewHolder<LayoutItemTextBinding> by vh(parent)
```

2. 通过自定义属性代理 + 传递绑定类的 inflate 三参数方法引用

```kotlin
val holder: BindingViewHolder<LayoutItemTextBinding> by vh(parent, LayoutItemTextBinding::inflate)
```



## VBHelper 集成

[![](https://jitpack.io/v/jaydroid1024/VBHelper.svg)](https://jitpack.io/#jaydroid1024/VBHelper)

```groovy
//Step 1. Add the JitPack repository to your build file
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url 'https://jitpack.io' }
    }
}


//Step 2. Add the dependency
dependencies {
    implementation 'com.github.jaydroid1024:VBHelper:0.0.1'
}

```



更新记录
----------


| 标签 |    `New:`    | `Upgrade: ` |   `Fix:`   |
| :--: | :----------: | :---------: | :--------: |
| 描述 | 新添加的功能 | 更新的功能  | 修复的功能 |

### V1.0.2

***2021-9-17***

- `Upgrade: ` Activity 中获取VB的方式更新为：借助 Lazy 接口实现委托的方式
- `Upgrade: ` Fragment 中获取VB的方式更新为：借助 Lazy 接口实现委托的方式
- 借助 Lazy 接口实现委托的方式的优势可以避免生成多余的 KProperty 反射类

### V1.0.1

***2021-9-9***

-  `New:` 支持在 Activity 中创建 VB 绑定类
-  `New:` 支持在 Fragment 中创建  VB 绑定类
-  `New:` 支持在 View 中创建  VB 绑定类
-  `New:` 支持在 Adapter 中创建包含了绑定类的 BindingViewHolder
