package pers.teacchen.activityrotatedemo

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * 该类的实现简单参照了Fragment源码中的FragmentViewLifecycleOwner设计和功能使用
 */
class CommonLifecycleOwner : LifecycleOwner {

    private var _lifecycleRegistry: LifecycleRegistry? = null
    private val lifecycleRegistry: LifecycleRegistry
        get() = _lifecycleRegistry ?: LifecycleRegistry(this).also {
            _lifecycleRegistry = it
        }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        lifecycleRegistry.handleLifecycleEvent(event)
    }

    fun setCurrentState(state: Lifecycle.State) {
        lifecycleRegistry.currentState = state
    }
}