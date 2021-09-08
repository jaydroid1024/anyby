# VBHelper
ViewBinding 超详细原理解析，利用 Kotlin 属性代理实现 VBHelper 方便 VB 在多场景下的生成

ViewBinding 原理解析点 [这里](/Jetpack_Viewbinding_Doc.md)，主要介绍了以下几点
- VB 集成与一般使用方式，包括：Activity 、Fragment、Adapter、include、merge、ViewStub
- KT 属性代理与泛型实化类型参数 `reified` 的介绍
- 通过 KT 属性代理创建 VB
- LayoutInflater 原理与参数解析
- XXXBinding 类的绑定过程
- XXXBinding 类的生成过程



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



## VBHelper 使用

### 1. 在 Activity 中创建 ViewBinding 绑定类

**反射和无反射两种使用方式如下：**

1. 通过自定义属性代理 + 反射绑定类的 inflate 方法

```kotlin
private val binding: ActivityMainBinding by vb()
```

2. 通过自定义属性代理 + 传递 inflate 方法引用

```kotlin
private val binding: ActivityMainBinding by vb(ActivityMainBinding::inflate)
```

### 2. 在 Fragment 中创建 ViewBinding 绑定类

**反射和无反射两种使用方式如下：**

1. 通过自定义属性代理 + 反射绑定类的 inflate 方法

```kotlin
private val binding: FragmentMainBinding by vb()
```

2. 通过自定义属性代理 + 传递 inflate 方法引用

```kotlin
private val binding: FragmentMainBinding by vb(FragmentMainBinding::inflate)
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



