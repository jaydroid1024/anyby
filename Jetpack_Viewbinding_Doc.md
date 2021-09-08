

# Jetpack | ViewBinding 详解

通过 **ViewBinding(视图绑定)** 功能，我们可以更轻松地编写与布局文件交互的代码。在模块中启用视图绑定之后，AGP 会为该模块中的每个 XML 布局文件生成一个绑定类。该绑定类的实例中会直接引用那些在布局中声明了资源 id 的控件。这样一来就减少了很多像 `findViewById` 这种操作，同时也为控件的安全性保驾护航。



**文章核心点：**

- VB 集成与一般使用方式，包括：Activity 、Fragment、Adapter、include、merge、ViewStub
- KT 属性代理与泛型实化类型参数 `reified` 的介绍
- 通过 KT 属性代理简化 VB 创建流程，并封装了一个库 VBHelper
- LayoutInflater 原理与参数解析
- XXXBinding 类的绑定过程
- XXXBinding 类的生成过程



**[VBHelper](https://github.com/jaydroid1024/VBHelper)**：是我写这篇文章提取的一个库，通过属性代理简化了VB的使用，有想了解的可以提提意见

1. 在 Activity 中创建 ViewBinding 绑定类

```kotlin
//通过自定义属性代理 + 反射绑定类的 inflate 方法
private val binding: ActivityMainBinding by vb()
//通过自定义属性代理 + 传递 inflate 方法引用
private val binding: ActivityMainBinding by vb(ActivityMainBinding::inflate)
```

2. 在 Fragment 中创建 ViewBinding 绑定类

```kotlin
//通过自定义属性代理 + 反射绑定类的 inflate 方法
private val binding: FragmentMainBinding by vb()
//通过自定义属性代理 + 传递 inflate 方法引用
private val binding: FragmentMainBinding by vb(FragmentMainBinding::inflate)
```

3. 在 View 中创建 ViewBinding 绑定类

```kotlin
//通过自定义属性代理 + 反射绑定类的 inflate 三参数方法
private val binding: MyViewBinding by vb()
//通过自定义属性代理 + 传递 inflate 三参数方法引用
private val binding: MyViewBinding by vb(MyViewBinding::inflate)
```

4. 在 Adapter 中创建包含了绑定类的 BindingViewHolder

```kotlin
//通过自定义属性代理 + 反射绑定类的 inflate 三参数方法
val holder: BindingViewHolder<LayoutItemTextBinding> by vh(parent)
//通过自定义属性代理 + 传递绑定类的 inflate 三参数方法引用
val holder: BindingViewHolder<LayoutItemTextBinding> by vh(parent, LayoutItemTextBinding::inflate)
```



## 1.VB 概述

- 视图绑定在 Android Studio 3.6 Canary 11 及更高版本中可用。

- 开启自动生成绑定类：模块 `build.gradle` 文件中的 `android` 闭包下，两种方式
  - `viewBinding {enabled = true} ` 默认值为false, Android Studio 3.6 Canary 11 及更高版本中可用。
  -  ` buildFeatures {viewBinding = true}` 默认值为false,  Android Studio 4.0 及更高版本中可用

- 忽略自动生成绑定类：请将 `tools:viewBindingIgnore="true"` 属性添加到相应布局文件的根视图中

- 生成绑定类的名称：将 XML 文件的名称转换为驼峰式大小写，并在末尾添加“Binding”一词。
  LayoutInflater.Factory
  - result_profile.xml ====>ResultProfileBinding
  - 每个绑定类还包含一个 `getRoot()` 方法，用于为相应布局文件的根视图提供直接引用。

- 与使用 findViewById 相比

  - **Null 安全**：绑定类的创建是通过解析布局文件在编译时生成，布局文件添加了id的控件才会生成对应的引用，因此不会发生绑定类中存在而布局中没有对应控件的情况，如果布局引用了错误的类型也会在编译时暴露错误。
  - **类型安全**：布局中声明的控件是确定类型的。这意味着不存在发生类转换异常的风险。

- 与使用 DataBinding 对比

  - 视图绑定和[数据绑定](https://developer.android.com/topic/libraries/data-binding?hl=zh-cn)均会生成可用于直接引用视图的绑定类。但是，视图绑定旨在处理更简单的用例。

  - **更快的编译速度**：视图绑定不需要处理注解信息，因此编译时间更短。
  - **易于使用**：视图绑定不需要在 XML 布局文件中标记，只要在模块中启用视图绑定后，它会自动应用于该模块的所有布局。

  - 如果项目中使用了数据绑定最好在项目中**同时使用视图绑定和数据绑定**。这样可以在需要高级功能的布局中使用数据绑定，而在不需要高级功能的布局中使用视图绑定。如果只是取代 `findViewById()` 调用，请考虑改用视图绑定。



## 2. VB 一般使用

### 2.1 Activity 

```kotlin
private lateinit var binding: ActivityMainBinding

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    setSupportActionBar(binding.toolbar)
}
```

### 2.2 Fragment

```kotlin
private var _binding: FragmentFirstBinding? = null
// This property is only valid between onCreateView and onDestroyView.
private val binding get() = _binding!!

override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    firstViewModel = ViewModelProvider(this).get(FirstViewModel::class.java)
    _binding = FragmentFirstBinding.inflate(inflater, container, false)
    binding.rvList.layoutManager = LinearLayoutManager(requireContext())
    return binding.root
}
```

### 2.3 Adapter

```kotlin
override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextHolder {
    val itemBinding = LayoutItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    //绑定类交给Holder
    return TextHolder(itemBinding)
}

override fun onBindViewHolder(holder: TextHolder, position: Int) {
    val item: String = list[position]
    //数据交给Holder
    holder.bind(item)
}

class TextHolder(val itemBinding: LayoutItemTextBinding) : RecyclerView.ViewHolder(itemBinding.root) {
    fun bind(name: String) {
        itemBinding.tvName.text = name
    }
}
```

### 2.4 include

```kotlin
binding.includeLayout.tvInfoInclude.text = "tvInfoInclude:$item"
// todo  include 方式有时候无法识别到真实的绑定类类型只能识别它是个View类型但是编译不会报错, 这种情况清理缓存可能会好 ，或者也可以强制类型转换或者自己bind
val tvInfoInclude: LayoutInfoBinding = binding.includeLayout as LayoutInfoBinding
val tvInfoInclude = LayoutInfoBinding.bind(binding.root)
tvInfoInclude.tvInfoInclude.text = "tvInfoInclude:$item"
```

### 2.5 merge

```kotlin
//include+merge 只能手动调用绑定类的bind方法
val layoutInfoMergeBinding = LayoutInfoMergeBinding.bind(binding.root)
val tvInfoMerge = layoutInfoMergeBinding.tvInfoMerge
tvInfoMerge.text = "tvInfoMerge:$item"
```

### 2.6 ViewStub

```kotlin
//ViewStub 只能手动调用绑定类的bind方法
binding.layoutViewStub.setOnInflateListener { _, inflateId ->
    val layoutInfoViewStubBinding = LayoutInfoViewStubBinding.bind(inflateId)
    val tvInfoViewStub = layoutInfoViewStubBinding.tvInfoViewStub
    tvInfoViewStub.text = "tvInfoViewStub:$item"
}
binding.layoutViewStub.inflate()
```

详细的测试代码参考：[**Github | VBHelper**](https://github.com/jaydroid1024/VBHelper)

## 3. VB 与 Kotlin by

采用 Kotlin 属性代理简化 VB 使用的三方库

- **[ViewBindingPropertyDelegate](https://github.com/androidbroadcast/ViewBindingPropertyDelegate)**
- **[ViewBindingKTX](https://github.com/DylanCaiCoding/ViewBindingKTX)**
- **[VBHelper](https://github.com/jaydroid1024/VBHelper)**：这个是我写这篇文章提取的一个库，借鉴了上面两个的实现，精简了一些代码

### 3.1 KT 属性代理：`by` `lazy`

- by关键字实际上就是一个属性代理运算符重载的符号，任何一个具备属性代理规则的类，都可以使用by关键字对属性进行代理。

- `by`关键字后面带有一个代理对象，这个代理类不一定要实现特定的接口，但是需要包含下面这两个方法的签名（val 只需要 getValue ），它就能作为一个代理属性来使用。

- ```kotlin
  //这个是扩展的实现方式，lazy就是采用的这种
  operator fun MyDelegate.getValue(thisRef: Any?, property: KProperty<*>): String = this.value
  
  class MyDelegate {
      var value: String = "YYY"
      //todo 代理类里面必须提供 getValue 方法，或者扩展这个方法也可
      operator fun getValue(thisRef: Any, property: KProperty<*>): String {
          return value
      }
      operator fun setValue(thisRef: Any, property: KProperty<*>, s: String) {
          value = s
      }
  }
  ```
  
- lazy  是Kotlin 内部对对属性代理的一个最佳实践，lazy 返回一个实现了 Lazy 接口的代理类，默认是 `SynchronizedLazyImpl`，

- Lazy<T> 有个扩展方法，符合属性代理的规则

- ```kotlin
  public inline operator fun <T> Lazy<T>.getValue(thisRef: Any?, property: KProperty<*>): T = value
  ```



### 3.2 KT 内联函数 `inline` 与泛型实化类型参数 `reified` 

[官方文档](https://www.kotlincn.net/docs/reference/inline-functions.html#%E5%85%B7%E4%BD%93%E5%8C%96%E7%9A%84%E7%B1%BB%E5%9E%8B%E5%8F%82%E6%95%B0)

[reified-type-parameters](https://github.com/JetBrains/kotlin/blob/master/spec-docs/reified-type-parameters.md)

Kotlin和Java同样存在泛型类型擦除的问题，但是 Kotlin 通过 inline 内联函数使得泛型类的类型实参在运行时能够保留，这样的操作 Kotlin 中把它称为实化，对应需要使用 reified 关键字。

- 满足实化类型参数函数的必要条件

  - 必须是 inline 内联函数，使用 inline 关键字修饰
  - 泛型类定义泛型形参时必须使用 reified 关键字修饰

- 带实化类型参数的函数基本定义

  ```kotlin
  //类型形参T是泛型函数isInstanceOf的实化类型参数
  inline fun <reified T> isInstanceOf(value: Any): Boolean = value is T 
  ```



### 3.3 通过 lazy 属性代理 + inflate方法引用

```kotlin
//通过 lazy 属性代理 + inflate方法引用
fun <VB : ViewBinding> ComponentActivity.binding1(inflate: (LayoutInflater) -> VB) =
    lazy {
        inflate(layoutInflater).also {
            setContentView(it.root)
        }
    }
```

### 3.4 通过 lazy 属性代理 + 反射

```kotlin
//通过 lazy 属性代理 + 反射
//reified 实化类型参数，作用是将泛型替换为真实的类型用于反射等
inline fun <reified VB : ViewBinding> ComponentActivity.binding3() =
    lazy {
        //经过内联后VB是可以确切知道具体类型的，所以这里可以反射获取具体的 ViewBinding
        val viewBinding: VB = VB::class.java.getMethod("inflate", LayoutInflater::class.java)
            .invoke(null, layoutInflater) as VB
        viewBinding.also {
            setContentView(it.root)
        }
    }
```

### 3.5 通过自定义属性代理 + inflate方法引用

```kotlin
//通过自定义属性代理 + inflate方法引用
fun <VB : ViewBinding> ComponentActivity.binding2(inflate: (LayoutInflater) -> VB) =
    ReadOnlyProperty<ComponentActivity, VB> { thisRef, property ->
        inflate(layoutInflater).also {
            setContentView(it.root)
        }
    }
```

### 3.6 通过自定义属性代理+ 反射

```kotlin
//通过自定义属性代理+ 反射
//reified 实化类型参数，作用是将泛型替换为真实的类型用于反射等
inline fun <reified VB : ViewBinding> ComponentActivity.binding4() =
    ReadOnlyProperty<ComponentActivity, VB> { thisRef, property ->
        //经过内联后VB是可以确切知道具体类型的，所以这里可以反射获取具体的 ViewBinding
        val viewBinding: VB = VB::class.java.getMethod("inflate", LayoutInflater::class.java)
            .invoke(null, layoutInflater) as VB
        viewBinding.also {
            setContentView(it.root)
        }
    }
```

四种方式的使用

```kotlin
//通过 lazy 属性代理 + inflate方法引用
private val binding1 by binding1(ActivityMainBinding::inflate)
//通过自定义属性代理 + inflate方法引用
private val binding2 by binding2(ActivityMainBinding::inflate)
//通过 lazy 属性代理 + 反射
private val binding3: ActivityMainBinding by binding3()
//通过自定义属性代理+ 反射
private val binding4: ActivityMainBinding by binding4()
```

其它 Fragment、View、Adapter 等绑定类的生成方式可以根据上面的方式灵活调整，也可参考：[**Github | VBHelper**](https://github.com/jaydroid1024/VBHelper)

**注意的地方：**

- 反射的方式我这里都是通过绑定类的 inflate 方法，也可以反射 bind 方法，就是入参不同可以根据具体情况灵活调整。
- merge 标签作为根视图生成的绑定类的inflate 方法只有一个两参数的 其它情况都是一参和三参同时生成，反射时需要兼容一下，VBHelper 没有兼容这一点有需要的可以处理一下，具体做法就是 try-cache 分别处理。

```java
@NonNull
public static LayoutInfoMergeBinding inflate(@NonNull LayoutInflater inflater,
    @NonNull ViewGroup parent) {
  if (parent == null) {
    throw new NullPointerException("parent");
  }
  inflater.inflate(R.layout.layout_info_merge, parent);
  return bind(parent);
}
```



## 4. VB 原理解析

### 4.1 LayoutInflater 原理与参数解析

[参考：反思|Android LayoutInflater机制的设计与实现](https://juejin.cn/post/6844903919286485000)

#### 获取 LayoutInflater 三种方式

```kotlin
//获取 LayoutInflater
//1、通过 LayoutInflater 的静态方法 from 获取，内部调用的是第二种
val layoutInflater1: LayoutInflater = LayoutInflater.from(this)
//2、通过系统服务 getSystemService 方法获取
val layoutInflater2: LayoutInflater =
    getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//3、如果是在 Activity 或 Fragment 可直接获取到实例
val layoutInflater3: LayoutInflater = layoutInflater //相当于调用 getLayoutInflater()

//三种方式在 Activity 范围内是单例
Log.d("Jay", "layoutInflater1:${layoutInflater1.hashCode()}")
Log.d("Jay", "layoutInflater2:${layoutInflater2.hashCode()}")
Log.d("Jay", "layoutInflater3:${layoutInflater3.hashCode()}")
//2021-09-06 23:41:52.925 6353-6353/com.jay.vbhelper D/Jay: layoutInflater1:31503528
//2021-09-06 23:41:52.925 6353-6353/com.jay.vbhelper D/Jay: layoutInflater2:31503528
//2021-09-06 23:41:52.925 6353-6353/com.jay.vbhelper D/Jay: layoutInflater3:31503528
```

无论哪种方式获取最终都会走到 ContextThemeWrapper 类中 getSystemService

#### PhoneLayoutInflater 创建流程

获取 LayoutInflater 三种方式最终会调到 ContextThemeWrapper#getSystemService

```kotlin
//class ContextThemeWrapper extends ContextWrapper
@Override
public Object getSystemService(String name) {
    if (LAYOUT_INFLATER_SERVICE.equals(name)) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
        }
        return mInflater;
    }
    return getBaseContext().getSystemService(name);
}
```

cloneInContext 是 LayoutInflater 接口的方法，LayoutInflater 唯一实现类是 PhoneLayoutInflater

```kotlin
//class PhoneLayoutInflater extends LayoutInflater
public LayoutInflater cloneInContext(Context newContext) {
    return new PhoneLayoutInflater(this, newContext);
}
```

#### 布局填充流程

方法签名

```kotlin
1.public View inflate(XmlPullParser parser, @Nullable ViewGroup root)
2.public View inflate(@LayoutRes int resource, @Nullable ViewGroup root)
3.public View inflate(@LayoutRes int resource, @Nullable ViewGroup root, boolean attachToRoot)
4.public View inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot)
```

四个 inflate 的重载方法最终都会调用到第四个，下面是四个方法的使用

```kotlin
//调用 LayoutInflater.inflate 的四个方法重载
//如果传入的 root 为 null ，此时会将 Xml 布局生成的根 View 对象直接返回
val view1_1 = layoutInflater3.inflate(R.layout.layout_view, null)
//这种方式加载的布局不需要再次addView(),否则：Caused by: java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.
//如果传入的 root 不为 null 且 attachToRoot 为 true，此时会将 Xml 布局生成的根 View 通过 addView 方法携带布局参数添加到 root 中
//如果 root 参数不为空 和 view2_1 一样
val view1_2 = layoutInflater3.inflate(R.layout.layout_view, binding.clContainer)
//第一个参数代表所要加载的布局，第二个参数是ViewGroup，这个参数需要与第3个参数配合使用，attachToRoot如果为true就把布局添加到ViewGroup中；若为false则只采用ViewGroup的LayoutParams作为测量的依据却不直接添加到ViewGroup中。
val view2_1 = layoutInflater3.inflate(R.layout.layout_view, binding.clContainer, true)
//如果传入的 root 不为 null 且 attachToRoot 为 false，此时会给 Xml 布局生成的根 View 设置布局参数
val view2_2 = layoutInflater3.inflate(R.layout.layout_view, binding.clContainer, false)
val parser: XmlResourceParser = resources.getLayout(R.layout.layout_view)
//这两个重载方法不常用
val view3 = layoutInflater3.inflate(parser, binding.clContainer)
val view4 = layoutInflater3.inflate(parser, binding.clContainer, false)
binding.clContainer.addView(view1_1)
```

无论是 Activity 中 setContentView 加载内容还是 DecorView 加载屏幕根视图都是通过 LayoutInflater 加载。

inflate 方法，详细的加载过程会单独整理一篇文章

```java
public View inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot) {
    synchronized (mConstructorArgs) {
        Trace.traceBegin(Trace.TRACE_TAG_VIEW, "inflate");
        final Context inflaterContext = mContext;
        final AttributeSet attrs = Xml.asAttributeSet(parser);
        Context lastContext = (Context) mConstructorArgs[0];
        mConstructorArgs[0] = inflaterContext;
        View result = root;
        try {
            advanceToRootNode(parser);
            final String name = parser.getName();
            if (TAG_MERGE.equals(name)) {
                if (root == null || !attachToRoot) {
                    throw new InflateException("<merge /> can be used only with a valid "+ "ViewGroup root and attachToRoot=true");
                }
								//merge 根视图单独处理
                rInflate(parser, root, inflaterContext, attrs, false);
            } else {
                //Temp 是在 xml 中找到的根视图
                final View temp = createViewFromTag(root, name, inflaterContext, attrs);

                ViewGroup.LayoutParams params = null;

                if (root != null) {
                  
                    // 创建与根匹配的布局参数（如果提供）
                    params = root.generateLayoutParams(attrs);
                    if (!attachToRoot) {
                        // 如果我们不附加，请为 temp 设置根布局的布局参数
                        temp.setLayoutParams(params);
                    }
                }
                // 将所有处于临时状态的孩子都根据其上下文进行布局填充。
                rInflateChildren(parser, temp, attrs, true);
                // 将所有视图添加到 root
                if (root != null && attachToRoot) {
                    root.addView(temp, params);
                }
                // 返回传入的 root 还是在 xml 中找到的顶视图。
                if (root == null || !attachToRoot) {
                    result = temp;
                }
            }
        } 
        return result;
    }
}
```



**LayoutInflater 参数说明**

`layoutResID：`代表所要加载的布局资源id，

`root：`是ViewGroup类型，这个参数需要与第3个参数配合使用，

`attachToRoot：`如果为`true`就把布局添加到 `root` 中；若为`false`则只采用`ViewGroup`的`LayoutParams`作为测量的依据却不直接添加到`ViewGroup`中。

`parser：`包含布局层次结构描述的 XML dom 节点。

**LayoutInflater.Factory 接口的扩展功能**

`LayoutInflater`设计了一个`LayoutInflater.Factory`接口，该接口设计得非常巧妙：在`xml`解析过程中，开发者可以通过配置该接口对`View`的创建过程进行拦截：**通过new的方式创建控件以避免大量地使用反射**,`Factory`接口的意义是在`xml`解析过程中，开发者可以通过配置该接口对`View`的创建过程进行拦截

#### LayoutInflater 总结

获取 LayoutInflater实例最终都会走到 ContextThemeWrapper 类中 getSystemService 构建一个局部单例的 **PhoneLayoutInflater** 实例。

LayoutInflater 布局填充有四个重载方法，最终都会调用到同一个方法，再根据传递的参数做不同的加载处理



### 4.2 ActivityMainBinding 类的绑定过程

#### inflate 过程

View 类中通过调用apt 自动生成的绑定类的inflate方法或者 bind 方法获取绑定类

```kotlin
//CustomView
val layoutInflater: LayoutInflater = LayoutInflater.from(context)
val binding = LayoutViewBinding.inflate(layoutInflater, this, true)
//SecondFragment
override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedIS: Bundle?): View {
    _binding = FragmentSecondBinding.inflate(inflater, container, false)
    return binding.root
}
//include+merge 只能手动调用绑定类的bind方法
val layoutInfoMergeBinding = LayoutInfoMergeBinding.bind(binding.root)
```

绑定类的 inflate 方法，通过传入的 LayoutInflater 将 layout 填充为 View

```kotlin
//class FragmentSecondBinding implements ViewBinding
@NonNull
public static FragmentSecondBinding inflate(@NonNull LayoutInflater inflater,
    @Nullable ViewGroup parent, boolean attachToParent) {
  View root = inflater.inflate(R.layout.fragment_second, parent, false);
  //这里的 attachToParent 参数为 true 时不知为何不传入 LayoutInflater 来 addView 而是自己单独做了判断
  if (attachToParent) {
    parent.addView(root);
  }
  return bind(root);
}
```

#### bind 过程

从 inflate 过程填充的视图中(或者是从外部传入的 View)实例化所有控件并构建绑定类

```kotlin
  @NonNull
  public static FragmentSecondBinding bind(@NonNull View rootView) {
    //此方法的主体是以您不会编写的方式生成的。这样做是为了优化已编译的字节码的大小和性能。
    int id;
    missingId: {
      //根布局中的普通控件
      id = R.id.button_second;
      Button buttonSecond = ViewBindings.findChildViewById(rootView, id);
      if (buttonSecond == null) {
        break missingId;
      }
      //根布局中的 include 标签
      id = R.id.include_layout;
      View includeLayout = ViewBindings.findChildViewById(rootView, id);
      if (includeLayout == null) {
        break missingId;
      }
      LayoutInfoBinding binding_includeLayout = LayoutInfoBinding.bind(includeLayout);
			//ViewStub标签
      id = R.id.layout_view_stub;
      ViewStub layoutViewStub = ViewBindings.findChildViewById(rootView, id);
      if (layoutViewStub == null) {
        break missingId;
      }
			//自定义 View
      id = R.id.name;
      CustomView name = ViewBindings.findChildViewById(rootView, id);
      if (name == null) {
        break missingId;
      }

 			//include+merge 没有生成对应的类型，只能手动调用绑定类的bind方法

      //构建绑定类，并将所有控件赋值给类属性
      return new FragmentSecondBinding((ConstraintLayout) rootView, buttonSecond, flSecond,
          binding_includeLayout, layoutViewStub, llInfo, name, textviewSecond);
    }
    // 如果有任何一个控件在 findChildViewById 过程中没有被找到就会抛NPE异常
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
```

遍历根视图匹配布局文件中的id并通过findViewById方法返回View实例

```kotlin
//ViewBindings
/**
 Like `findViewById` but skips the view itself.
 */
@Nullable
public static <T extends View> T findChildViewById(View rootView, @IdRes int id) {
    if (!(rootView instanceof ViewGroup)) {
        return null;
    }
    final ViewGroup rootViewGroup = (ViewGroup) rootView;
    final int childCount = rootViewGroup.getChildCount();
    for (int i = 0; i < childCount; i++) {
        final T view = rootViewGroup.getChildAt(i).findViewById(id);
        if (view != null) {
            return view;
        }
    }
    return null;
}
```



#### 绑定过程总结

DataBinding 借助 AGP 会为所有布局文件自动生成绑定类

绑定类的 inflate 方法通过传入的布局填充器 LayoutInflater 以及自动收集的根布局 id 加载出根布局 rootView 然后传给 bind 方法实例化控件

绑定类的 bind 方法通过传入的根布局以及自动收集的控件 id 实例化所有控件 并构建绑定类

### 4.3 ActivityMainBinding 类的生成过程

[参考：ViewBinding 的本质](https://juejin.cn/post/6844904106268557326)

[DataBinding Compiler Common](https://mvnrepository.com/artifact/androidx.databinding/databinding-compiler-common)

依赖源码方便查看

```groovy
//todo 依赖 databinding-compiler 方便查看 ViewBinding 类的生成过程
// https://mvnrepository.com/artifact/androidx.databinding/databinding-compiler-common
implementation group: 'androidx.databinding', name: 'databinding-compiler-common', version: '7.0.1'
// https://mvnrepository.com/artifact/com.android.tools.build/gradle
implementation group: 'com.android.tools.build', name: 'gradle', version: '7.0.1'
```

ViewBinding 是属于 dataBinding 库里面的一个小功能,对于解析布局文件生成绑定类的逻辑是通用的，

#### 阶段一：解析xml布局文件

**LayoutXmlProcessor**：处理布局 XML，剥离绑定属性和元素，并将信息写入带注解的类文件以供注释处理器使用

**processResources**：假装这个方法就是布局文件改动后调用的入口方法（应该是由AGP 触发，暂时未找到）

```java

android.databinding.tool.LayoutXmlProcessor
  
public boolean processResources(ResourceInput input, boolean isViewBindingEnabled, boolean isDataBindingEnabled)
        throws ParserConfigurationException, SAXException, XPathExpressionException,
        IOException {
    ProcessFileCallback callback = new ProcessFileCallback() {
    //省略回调代码
    }
    //布局文件的改动输入源支持增量构建
    if (input.isIncremental()) {
        processIncrementalInputFiles(input, callback);
    } else {
        processAllInputFiles(input, callback);
    }
    return true;
}
```

processIncrementalInputFiles 处理增量输入（Added、Removed、Changed）

processAllInputFiles 处理全部输入

```java
//遍历文件
for (File firstLevel : input.getRootInputFolder().listFiles())
//处理 layout_xx 目录下面的 xxx.xml 文件
if (LAYOUT_FOLDER_FILTER.accept(firstLevel, firstLevel.getName())) {
    callback.processLayoutFolder(firstLevel);
    //noinspection ConstantConditions
    for (File xmlFile : firstLevel.listFiles(XML_FILE_FILTER)) {
        callback.processLayoutFile(xmlFile);
    }
}
```

ProcessFileCallback 扫描文件后的回调

```java
public void processLayoutFile(File file)
        throws ParserConfigurationException, SAXException, XPathExpressionException,
        IOException {
          //处理单个文件，
    processSingleFile(RelativizableFile.fromAbsoluteFile(file, null),
            convertToOutFile(file), isViewBindingEnabled, isDataBindingEnabled);
}
```

processSingleFile 

```java
public boolean processSingleFile(@NonNull RelativizableFile input, @NonNull File output,
        boolean isViewBindingEnabled, boolean isDataBindingEnabled)
        throws ParserConfigurationException, SAXException, XPathExpressionException,
        IOException {
          //解析xml文件 封账布局文件扫描类
    final ResourceBundle.LayoutFileBundle bindingLayout = LayoutFileParser
            .parseXml(input, output, mResourceBundle.getAppPackage(), mOriginalFileLookup,
                    isViewBindingEnabled, isDataBindingEnabled);
    if (bindingLayout == null
            || (bindingLayout.isBindingData() && bindingLayout.isEmpty())) {
        return false;
    }
          //添加到map缓存起来
    mResourceBundle.addLayoutBundle(bindingLayout, true);
    return true;
}
```

**LayoutFileParser**：获取 XML 文件列表并创建可以持久化或转换为 LayoutBinder 的ResourceBundle列表

android.databinding.tool.store public final class LayoutFileParser

parseXml:路径、编码、校验等

parseOriginalXml :将布局文件解析为描述类

```java
private static ResourceBundle.LayoutFileBundle parseOriginalXml(
        @NonNull final RelativizableFile originalFile, @NonNull final String pkg,
        @NonNull final String encoding, boolean isViewBindingEnabled,
        boolean isDataBindingEnabled)
        throws IOException {}

//layout 标签判断databinding
XMLParser.ElementContext root = expr.element();
boolean isBindingData = "layout".equals(root.elmName.getText());
//dataBinding
if (isBindingData) {
    if (!isDataBindingEnabled) {
        L.e(ErrorMessages.FOUND_LAYOUT_BUT_NOT_ENABLED);
        return null;
    }
    data = getDataNode(root);
    rootView = getViewNode(original, root);
} else if (isViewBindingEnabled) {
  //viewBindingIgnore 根布局添加这个属性为true可以跳过生成绑定类的过程
    if ("true".equalsIgnoreCase(attributeMap(root).get("tools:viewBindingIgnore"))) {
        L.d("Ignoring %s for view binding", originalFile);
        return null;
    }
    data = null;
    rootView = root;
} else {
    return null;
}

//dataBinding <include> 元素不支持作为 <merge> 元素的直接子元素
boolean isMerge = "merge".equals(rootView.elmName.getText());
if (isBindingData && isMerge && !filter(rootView, "include").isEmpty()) {
//public static final String INCLUDE_INSIDE_MERGE = "<include> elements are not supported as direct children of <merge> elements";
    L.e(ErrorMessages.INCLUDE_INSIDE_MERGE);
    return null;
}

String rootViewType = getViewName(rootView);
String rootViewId = attributeMap(rootView).get("android:id");
//构建布局描述的封装类
ResourceBundle.LayoutFileBundle bundle =
    new ResourceBundle.LayoutFileBundle(
        originalFile, xmlNoExtension, original.getParentFile().getName(), pkg,
        isMerge, isBindingData, rootViewType, rootViewId);
final String newTag = original.getParentFile().getName() + '/' + xmlNoExtension;
//data 数据只有 databinding 才会有的元素，viewBinding 是不会去解析的
parseData(original, data, bundle);
//解析表达式，这里面会循环遍历元素，解析 view 的 id、tag、include、fragment 等等 xml 相关的元素，并且还有 databinding 相关的 @={ 的表达式，最后将结果缓存起来
parseExpressions(newTag, rootView, isMerge, bundle);
```

#### 阶段二：输出描述文件

**LayoutXmlProcessor**

**writeLayoutInfoFiles**：这个方法的执行点可以在AGP里面找到，task 为：**com.android.build.gradle.tasks.MergeResources**

**MergeResources**

```java
@Override
public void doTaskAction(@NonNull InputChanges changedInputs) {
    ...
    SingleFileProcessor dataBindingLayoutProcessor = maybeCreateLayoutProcessor();
    if (dataBindingLayoutProcessor != null) {
        dataBindingLayoutProcessor.end();
    }
    ...
}
```

```java
//maybeCreateLayoutProcessor
return new SingleFileProcessor() {

    private LayoutXmlProcessor getProcessor() {
        return processor;
    }

    @Override
    public boolean processSingleFile(
            @NonNull File inputFile,
            @NonNull File outputFile,
            @Nullable Boolean inputFileIsFromDependency)
            throws Exception {
        return getProcessor()
               .processSingleFile(
                        normalizedInputFile,
                        outputFile,
                        getViewBindingEnabled().get(),
                        getDataBindingEnabled().get());
    }
    @Override
    public void end() throws JAXBException {
        getProcessor().writeLayoutInfoFiles(getDataBindingLayoutInfoOutFolder().get().getAsFile());
    }
};

//输出路径可以从这里查看
artifacts.setInitialProvider(taskProvider, MergeResources::getDataBindingLayoutInfoOutFolder)
        .withName("out")
        .on( mergeType == MERGE? DATA_BINDING_LAYOUT_INFO_TYPE_MERGE.INSTANCE
                        : DATA_BINDING_LAYOUT_INFO_TYPE_PACKAGE.INSTANCE);
```

**writeLayoutInfoFiles**

```java
public void writeLayoutInfoFiles(File xmlOutDir, JavaFileWriter writer) throws JAXBException {
    //遍历之前收集到的所有 LayoutFileBundle，写入 xmlOutDir 路径
    for (ResourceBundle.LayoutFileBundle layout : mResourceBundle
            .getAllLayoutFileBundlesInSource()) {
        writeXmlFile(writer, xmlOutDir, layout);
    }
}
```

writeXmlFile

```kotlin
private void writeXmlFile(JavaFileWriter writer, File xmlOutDir,
        ResourceBundle.LayoutFileBundle layout)
        throws JAXBException {
    String filename = generateExportFileName(layout);//  fileName + '-' + dirName + ".xml";
          //遍历之前收集到的所有 LayoutFileBundle，写入 xmlOutDir 路径
    writer.writeToFile(new File(xmlOutDir, filename), layout.toXML());
}
```

描述文件的生成路径为：app/build/intermediates/data_binding_layout_info_type_merge/debug/out

```xml
//fragment_second-layout.xml

<?xml version="1.0" encoding="utf-8" standalone="yes"?>
<Layout directory="layout" filePath="app/src/main/res/layout/fragment_second.xml"
    isBindingData="false" isMerge="false" layout="fragment_second"
    modulePackage="com.jay.vbhelper" rootNodeType="androidx.constraintlayout.widget.ConstraintLayout">
    <Targets>
        <Target tag="layout/fragment_second_0"
            view="androidx.constraintlayout.widget.ConstraintLayout">
            <Expressions />
            <location endLine="78" endOffset="51" startLine="1" startOffset="0" />
        </Target>
        <Target id="@+id/ll_info" tag="binding_1"
            view="androidx.appcompat.widget.LinearLayoutCompat">
            <Expressions />
            <location endLine="51" endOffset="50" startLine="9" startOffset="4" />
        </Target>
        <Target id="@+id/include_layout" include="layout_info" tag="binding_1">
            <Expressions />
            <location endLine="31" endOffset="42" startLine="29" startOffset="8" />
        </Target>
        <Target include="layout_info_merge" tag="binding_1">
            <Expressions />
            <location endLine="35" endOffset="53" startLine="35" startOffset="8" />
        </Target>
 
    </Targets>
</Layout>
```

#### 阶段三：输出绑定类

AGP Task DataBindingGenBaseClassesTask 触发

com.android.build.gradle.internal.tasks.databinding.DataBindingGenBaseClassesTask

DataBindingGenBaseClassesTask

```kotlin
@TaskAction
fun writeBaseClasses(inputs: IncrementalTaskInputs) {
    // TODO extend NewIncrementalTask when moved to new API so that we can remove the manual call to recordTaskAction
    recordTaskAction(analyticsService.get()) {
        // TODO figure out why worker execution makes the task flake.
        // Some files cannot be accessed even though they show up when directory listing is
        // invoked.
        // b/69652332
        val args = buildInputArgs(inputs)
        CodeGenerator(
            args,
            sourceOutFolder.get().asFile,
            Logger.getLogger(DataBindingGenBaseClassesTask::class.java),
            encodeErrors,
            collectResources()).run()//触发生成流程
    }
}

//绑定类生成器
class CodeGenerator @Inject constructor(
    val args: LayoutInfoInput.Args,
    private val sourceOutFolder: File,
    private val logger: Logger,
    private val encodeErrors: Boolean,
    private val symbolTables: List<SymbolTable>? = null
) : Runnable, Serializable {
    override fun run() {
        try {
            initLogger()
            BaseDataBinder(LayoutInfoInput(args), if (symbolTables != null) this::getRPackage else null)
          //生成逻辑
                .generateAll(DataBindingBuilder.GradleFileWriter(sourceOutFolder.absolutePath))
        } finally {
            clearLogger()
        }
    }
    ...
}

//sourceOutFolder路径信息
creationConfig.artifacts.setInitialProvider(
    taskProvider,
    DataBindingGenBaseClassesTask::sourceOutFolder
).withName("out").on(InternalArtifactType.DATA_BINDING_BASE_CLASS_SOURCE_OUT)
```



BaseDataBinder

```java
@Suppress("unused")// used by tools
class BaseDataBinder(val input : LayoutInfoInput, val getRPackage: ((String, String) -> (String))?) {
    private val resourceBundle : ResourceBundle = ResourceBundle(
            input.packageName, input.args.useAndroidX)
      //
    init {
        input.filesToConsider .forEach {
                    it.inputStream().use {
                     // 又将上面收集的 layout，将 xml 转成 LayoutFileBundle
                        val bundle = LayoutFileBundle.fromXML(it)
                        resourceBundle.addLayoutBundle(bundle, true)
                    }
                }
        resourceBundle.addDependencyLayouts(input.existingBindingClasses)
        resourceBundle.validateAndRegisterErrors()
    }
  
  
  
    @Suppress("unused")// used by android gradle plugin
    fun generateAll(writer : JavaFileWriter) {
    		// 拿到所有的 LayoutFileBundle，并根据文件名进行分组排序
        val layoutBindings = resourceBundle.allLayoutFileBundlesInSource
            .groupBy(LayoutFileBundle::getFileName).toSortedMap()

        layoutBindings.forEach { layoutName, variations ->
            // 将 LayoutFileBundle 信息包装成 BaseLayoutModel
            val layoutModel = BaseLayoutModel(variations, getRPackage)
            val javaFile: JavaFile
            val classInfo: GenClassInfoLog.GenClass
            if (variations.first().isBindingData) {
                val binderWriter = BaseLayoutBinderWriter(layoutModel, libTypes)
                javaFile = binderWriter.write()
                classInfo = binderWriter.generateClassInfo()
            } else {
              //不是DataBinding，按照 ViewBinding 处理
              //toViewBinder 是 BaseLayoutModel 的拓展函数，他会将 LayoutFileBundle 包装成 ViewBinder 类返回 
                val viewBinder = layoutModel.toViewBinder()
              //toJavaFile 是 ViewBinder 的拓展函数，通过Javapoet生成Java文件
                javaFile = viewBinder.toJavaFile(useLegacyAnnotations = !useAndroidX)
                classInfo = viewBinder.generatedClassInfo()
            }
            writer.writeToFile(javaFile)
            myLog.classInfoLog.addMapping(layoutName, classInfo)
            variations.forEach {
                it.bindingTargetBundles.forEach { bundle ->
                    if (bundle.isBinder) {
                        myLog.addDependency(layoutName, bundle.includedLayout)
                    }
                }
            }
        }
        input.saveLog(myLog)
        // data binding will eat some errors to be able to report them later on. This is a good
        // time to report them after the processing is done.
        Scope.assertNoError()
    }
}
```

通过Javapoet 生成绑定类

```kotlin

fun ViewBinder.toJavaFile(useLegacyAnnotations: Boolean = false) =
    JavaFileGenerator(this, useLegacyAnnotations).create()

fun create() = javaFile(binder.generatedTypeName.packageName(), typeSpec()) {
    addFileComment("Generated by view binder compiler. Do not edit!")
}

private fun typeSpec() = classSpec(binder.generatedTypeName) {
  
    addModifiers(PUBLIC, FINAL)
    addSuperinterface(ClassName.get(viewBindingPackage, "ViewBinding"))

    // TODO elide the separate root field if the root tag has an ID (and isn't a binder)
    addField(rootViewField())
    addFields(bindingFields())

    addMethod(constructor())
    addMethod(rootViewGetter())
		//如果跟标签是 merge  是生成的两参数的infate 参数
    if (binder.rootNode is RootNode.Merge) {
        addMethod(mergeInflate())
    } else {
      //其它情况都是同时生成一参数和三参数的inflate方法
        addMethod(oneParamInflate())
        addMethod(threeParamInflate())
    }

    addMethod(bind())
}
```

#### 生成过程总结

实时更新生成：布局文件改动(新加/更新/删除)后AS或AGP或立即更新绑定类，这个过程还没找到对应的源码

编译更新生成：AGP 不同的任务触发

- 解析xml布局文件：LayoutXmlProcessor#processResources 方法应该是改动布局文件的输入口，暂时没找到对应的Task，收集过程支持增量更新。处理 layout_xx 目录下面的 xxx.xml 文件，解析xml文件的过程区分 DataBinding 和 ViewBinding ，最后的产物是 ResourceBundle.LayoutFileBundle 以及 HashMap<String, List<LayoutFileBundle>> mLayoutBundles
- 输出描述文件：有AGP中的 **MergeResources**Task 触发 , 遍历之前收集到的所有 LayoutFileBundle，写入 xmlOutDir 路径, 这个xml文件中描述了布局的 文件路径、包名、布局名、控件id、控件行号等信息
- 输出绑定类：AGP **DataBindingGenBaseClassesTask**触发，将上个过程生成的布局描述xml文件再解析成 LayoutFileBundle 类信息，然后再次包装这些信息，最后通过Javapoet 生成绑定类



#### TODO 

- 布局文件更新后触发扫描和处理布局文件的操作也就是调用 processResources 方法的地方
  - 猜测AGP 和 AS 都有参与

- 为什么点击 ActivityMainBinding 会跳转到对应的布局文件
  - 这个猜测应该和编译相关，生成词法分析器和解析器代码

- 为什么添加了新的布局文件还没有编译就获取到绑定类，但是在data_binding_base_class_source_out路径下没有这个绑定类只有编译才会看到

  - 应该也有AS的份

  - [AS 中关于 DataBind 的一个库：Generate lexer and parser code](https://android.googlesource.com/platform/tools/adt/idea/+/refs/heads/mirror-goog-studio-master-dev/android-lang-databinding/)

    

