package pers.teacchen.activityrotatedemo

import androidx.lifecycle.*
import kotlinx.coroutines.*


class MainViewModel : ViewModel() {

    /**
     * 个人习惯，将LiveData完全封装在ViewModel内部，
     * 外部仅开放LiveData的数据读取部分（不开放LiveData类型），
     * 对于LiveData的观察部分，通过函数形式暴露。
     * 较为麻烦，但利于维护LiveData的各项特性，
     * 如LiveData通过幕后属性形式简化调用，
     * 且写操作限制在ViewModel内部，仅向UI层公开观察方法和数据读取，
     * 所以能引起观察回调的必然在ViewModel内部中便于维护。
     */

    private var commonUpdateTimes = 0
    private val commonStrLiveData = MutableLiveData<String>()
    var commonStr: String
        get() = commonStrLiveData.value ?: "default"
        private set(value) {
            debugLog("commonStrLiveData setValue: $value in $identifyStr")
            ++ commonUpdateTimes
            val updateStr = "$value updateTimes: $commonUpdateTimes"
            commonStrLiveData.value = updateStr
        }

    private var portraitUpdateTimes = 0
    private val portraitStrLiveData = MutableLiveData<String>()
    var portraitStr: String
        get() = portraitStrLiveData.value ?: "default"
        private set(value) {
            ++ portraitUpdateTimes
            val updateStr = "$value updateTimes: $portraitUpdateTimes"
            debugLog("portraitStrLiveData setValue: $updateStr in $identifyStr")
            portraitStrLiveData.value = updateStr
        }

    private var landscapeUpdateTimes = 0
    private val landscapeStrLiveData = MutableLiveData<String>()
    var landscapeStr: String
        get() = landscapeStrLiveData.value ?: "default"
        private set(value) {
            ++ landscapeUpdateTimes
            val updateStr = "$value updateTimes: $landscapeUpdateTimes"
            debugLog("landscapeStrLiveData setValue: $updateStr")
            landscapeStrLiveData.value = updateStr
        }

    fun observeCommonStr(owner: LifecycleOwner, observer: Observer<String>) =
        commonStrLiveData.observe(owner, observer)

    fun observePortraitStr(owner: LifecycleOwner, observer: Observer<String>) =
        portraitStrLiveData.observe(owner, observer)

    fun observeLandscapeStr(owner: LifecycleOwner, observer: Observer<String>) =
        landscapeStrLiveData.observe(owner, observer)

    /**
     * 用协程进行异步操作，仅异步操作场景示例代码，下同
     */
    fun asyncCommonStr(scope: CoroutineScope) {
        scope.launch {
            delay(5000L)
            withContext(Dispatchers.Main.immediate) {
                commonStr = "Common String"
            }
        }
    }

    fun asyncPortraitStr(scope: CoroutineScope) {
        scope.launch {
            delay(5000L)
            withContext(Dispatchers.Main.immediate) {
                portraitStr = "Portrait String"
            }
        }
    }

    fun asyncLandscapeStr(scope: CoroutineScope) {
        scope.launch {
            delay(5000L)
            withContext(Dispatchers.Main.immediate) {
                landscapeStr = "Landscape String"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        debugLog("onCleared in $identifyStr")
    }
}