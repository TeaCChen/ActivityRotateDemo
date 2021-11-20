package pers.teacchen.activityrotatedemo

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pers.teacchen.activityrotatedemo.databinding.ActivityMainBinding

class MainActivity : BaseConfigExtraActivity() {

    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private val isPortrait: Boolean
        get() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    override fun onCreate(savedInstanceState: Bundle?) {
        debugLog("onCreate ${resources.configuration.getOrientationStr()}  $identifyStr")
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        initView(savedInstanceState)
        /* 不需因横竖屏切换而切断liveData的观察回调时，使用Activity本身作LifeCycleOwner实例传入 */
        viewModel.observeCommonStr(this) {
            debugLog("observeStr changed")
            viewBinding.commonTv.text = it
        }
        registerObserveAllOrientation()
        loopAsyncInConfig()
    }

    override fun onStart() {
        debugLog("onStart $identifyStr")
        super.onStart()
    }

    override fun onResume() {
        debugLog("onStart $identifyStr")
        super.onResume()
    }

    override fun onPause() {
        debugLog("onPause $identifyStr")
        super.onPause()
    }

    override fun onStop() {
        debugLog("onStop $identifyStr")
        super.onStop()
    }

    override fun onDestroy() {
        debugLog("onDestroy  $identifyStr")
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        debugLog("onConfigurationChanged  $identifyStr")
        super.onConfigurationChanged(newConfig)
        initView(null)
        debugLog(newConfig.getOrientationStr())
        loopAsyncInConfig()
        registerObserveAllOrientation()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (!isPortrait) {
            @SuppressLint("SourceLockedOrientationActivity")
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun initView(savedInstanceState: Bundle?) {
        debugLog("initView savedInstanceState is null: ${savedInstanceState == null}")
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.apply {
            commonTv.text = viewModel.commonStr
            portraitTv?.text = viewModel.portraitStr
            landscapeTv?.text = viewModel.landscapeStr
        }

        viewBinding.switchBtn.setOnClickListener {
            requestedOrientation = when (resources.configuration.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                Configuration.ORIENTATION_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }

    /**
     * 根据横竖屏的情况，分别在对应的屏幕方向时才注册LiveData的回调
     */
    private fun registerObserveAllOrientation() {
        runWithOrientationOwner(true) {
            viewModel.observePortraitStr(this) {
                viewBinding.portraitTv?.text = it
            }
        }
        runWithOrientationOwner(false) {
            viewModel.observeLandscapeStr(this) {
                viewBinding.landscapeTv?.text = it
            }
        }
    }

    /**
     * 简单封装configLifecycleOwner和屏幕方向的使用，
     * DSL风格减少configLifecycleOwner和屏幕方向之间的逻辑判断逻辑。
     */
    private inline fun runWithOrientationOwner(
        isPortrait: Boolean,
        block: LifecycleOwner.() -> Unit,
    ) {
        if (this.isPortrait == isPortrait) {
            configLifecycleOwner.block()
        }
    }

    private fun Configuration.getOrientationStr(): String = when (orientation) {
        Configuration.ORIENTATION_PORTRAIT -> "ORIENTATION_PORTRAIT"
        Configuration.ORIENTATION_LANDSCAPE -> "ORIENTATION_LANDSCAPE"
        Configuration.ORIENTATION_UNDEFINED -> "ORIENTATION_UNDEFINED"
        else -> "ORIENTATION_UNKNOWN"
    }

    private fun loopAsyncInConfig() {
        configLifecycleOwner.lifecycleScope.launch(block = suspendBlock)
    }

    /**
     * 将lambda表达式实例化存起来，用以定时任务。
     * 此方式可以避免Handler的postDelay风险或remove延时任务使用上的麻烦。
     * 结束条件由协程作用域本身自动取消，如lifeCycleScope/viewModelScope自身的自动取消
     */
    private val suspendBlock: suspend CoroutineScope.() -> Unit = {
        debugLog("loopAsyncInConfig coroutine runs")
        viewModel.run {
            asyncCommonStr(viewModelScope)
            if (isPortrait) {
                asyncPortraitStr(lifecycleScope)
            } else {
                asyncLandscapeStr(configLifecycleOwner.lifecycleScope)
            }
        }
        delay(3000L)
        loopAsyncInConfig()
    }
}