package pers.teacchen.activityrotatedemo

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

open class BaseConfigExtraActivity: AppCompatActivity() {
    /**
     *  初始化化为null，设计上需要使用configLifecycleOwner时才实例化赋值，
     *  在横竖屏切换时若实例已经存在，则使其声明周期走到DESTROYED中并将幕后属性重置null，
     */
    private var _configLifecycleOwner: CommonLifecycleOwner? = null
    protected val configLifecycleOwner: LifecycleOwner
        get() = _configLifecycleOwner ?: CommonLifecycleOwner().also {
            debugLog("new CommonViewLifecycleOwner@${it.identifyStr}")
            _configLifecycleOwner = it
            /* 初始化时将新的configLifecycleOwner与当前页面的LifeCycle状态同步起来 */
            it.setCurrentState(lifecycle.currentState)
            it.lifecycle.addObserver(LifecycleEventObserver { owner, event ->
                debugLog("$event from ${owner.identifyStr}")
            })
        }

    init {
        lifecycle.addObserver(LifecycleEventObserver { owner, event ->
            debugLog("$event from ${owner.identifyStr}")
            /* 将当前的_configLifecycleOwner（若已存在）与Activity的生命周期同步 */
            _configLifecycleOwner?.handleLifecycleEvent(event)
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        /* 如果上一个View的LifeCycleOwner存在，则将其置入onDestroy的生命周期回调里并重新置空 */
        _configLifecycleOwner?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        _configLifecycleOwner = null
    }
}