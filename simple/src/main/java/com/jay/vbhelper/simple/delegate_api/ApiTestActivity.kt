package com.jay.vbhelper.simple.delegate_api

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.jay.anyby.api.api
import com.jay.vbhelper.delegate.vb
import com.jay.vbhelper.simple.databinding.ActivityApiTestBinding

class ApiTestActivity : AppCompatActivity(), View.OnTouchListener {

    private val binding: ActivityApiTestBinding by vb()

    private var gestureDetector: GestureDetector? = null


    //接口委托
    private val lifecycleCallbacksWithDelegate =
        object : Application.ActivityLifecycleCallbacks by api() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Log.d("Jay", "onActivityCreated, $activity")
            }

            override fun onActivityDestroyed(activity: Activity) {
                Log.d("Jay", "onActivityDestroyed, $activity")
            }
        }

    //接口委托
    private val gestureListenerWithDelegate = object : GestureDetector.OnGestureListener by api() {

        override fun onDown(e: MotionEvent?): Boolean {
            Log.d("Jay", "onDown")
            return true
        }

        //todo 带返回值的方法不能通过动态代理正常返回
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return true
        }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //GestureDetector
//        mGestureDetector = GestureDetector(this, gestureListener)
        gestureDetector = GestureDetector(this, gestureListenerWithDelegate)
        binding.tvTouch.setOnTouchListener(this)
        binding.tvTouch.isFocusable = true
        binding.tvTouch.isClickable = true
        binding.tvTouch.isLongClickable = true

        //ActivityLifecycleCallbacks
//        application.registerActivityLifecycleCallbacks(lifecycleCallbacks)
        application.registerActivityLifecycleCallbacks(lifecycleCallbacksWithDelegate)
    }


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector?.onTouchEvent(event) ?: false
    }

    //非接口委托
    private val lifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        /**
         * Called when the Activity calls [super.onCreate()][Activity.onCreate].
         */
        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {

        }

        /**
         * Called when the Activity calls [super.onStart()][Activity.onStart].
         */
        override fun onActivityStarted(activity: Activity?) {

        }

        /**
         * Called when the Activity calls [super.onResume()][Activity.onResume].
         */
        override fun onActivityResumed(activity: Activity?) {

        }

        /**
         * Called when the Activity calls [super.onPause()][Activity.onPause].
         */
        override fun onActivityPaused(activity: Activity?) {

        }

        /**
         * Called when the Activity calls [super.onStop()][Activity.onStop].
         */
        override fun onActivityStopped(activity: Activity?) {

        }

        /**
         * Called when the Activity calls
         * [super.onSaveInstanceState()][Activity.onSaveInstanceState].
         */
        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

        }

        /**
         * Called when the Activity calls [super.onDestroy()][Activity.onDestroy].
         */
        override fun onActivityDestroyed(activity: Activity?) {

        }
    }

    //非接口委托
    private val gestureListener: GestureDetector.OnGestureListener =
        object : GestureDetector.OnGestureListener {

            override fun onDown(e: MotionEvent?): Boolean {
                Log.d("Jay", "onDown")
                return true
            }


            override fun onShowPress(e: MotionEvent?) {

            }


            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                return true
            }


            override fun onLongPress(e: MotionEvent?) {

            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                return true
            }

        }

}