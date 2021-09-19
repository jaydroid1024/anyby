package com.jay.vbhelper.simple.inflate_test

import android.content.Context
import android.content.res.XmlResourceParser
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.jay.vbhelper.delegate.vb
import com.jay.vbhelper.simple.R
import com.jay.vbhelper.simple.databinding.ActivityInflateTestBinding

/**
 * @author jaydroid
 * @version 1.0
 * @date 2021/9/3
 */
class InflateTestActivity : AppCompatActivity() {

    private val binding: ActivityInflateTestBinding by vb()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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
        //调用 LayoutInflater.inflate 的四个方法重载
        //如果传入的 root 为 null ，此时会将 Xml 布局生成的根 View 对象直接返回
        val view1_1 = layoutInflater3.inflate(R.layout.layout_view, null)
        //这种方式加载的布局不需要再次addView(),否则：Caused by: java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.
        //如果传入的 root 不为 null 且 attachToRoot 为 true，此时会将 Xml 布局生成的根 View 通过 addView 方法携带布局参数添加到 root 中
        //如果 root 参数不为空 和 view2_1 一样
        val view1_2 = layoutInflater3.inflate(R.layout.layout_view, binding.clContainer)
        val view2_1 = layoutInflater3.inflate(R.layout.layout_view, binding.clContainer, true)
        //如果传入的 root 不为 null 且 attachToRoot 为 false，此时会给 Xml 布局生成的根 View 设置布局参数
        val view2_2 = layoutInflater3.inflate(R.layout.layout_view, binding.clContainer, false)
        val parser: XmlResourceParser = resources.getLayout(R.layout.layout_view)
        //这两个重载方法不常用
//        val view3 = layoutInflater3.inflate(parser, binding.clContainer)
        val view4 = layoutInflater3.inflate(parser, binding.clContainer, false)
        binding.clContainer.addView(view1_1)


    }


    /*


        public View inflate(XmlPullParser parser, @Nullable ViewGroup root)
        public View inflate(@LayoutRes int resource, @Nullable ViewGroup root)
        public View inflate(@LayoutRes int resource, @Nullable ViewGroup root, boolean attachToRoot)
        public View inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot)







       public LayoutInflater cloneInContext(Context newContext) {
        return new PhoneLayoutInflater(this, newContext);
    }



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


       public static LayoutInflater from(Context context) {
        LayoutInflater LayoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (LayoutInflater == null) {
            throw new AssertionError("LayoutInflater not found.");
        }
        return LayoutInflater;
    }




     public LayoutInflater getLayoutInflater() {
        return getWindow().getLayoutInflater();
    }

      public abstract LayoutInflater getLayoutInflater();

     */


}