package com.cmj.wanandroid.lib.base.kt

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 查找泛型中的 ViewModel
 */
fun <VM : ViewModel> findViewModelClass(clazz: Class<*>, index: Int = -1): Class<VM> {
    val viewModelClass = findViewModelClassOrNull<VM>(clazz, index)
    if (viewModelClass != null) {
        return viewModelClass
    }
    throw IllegalStateException("Not found Generic Type of ViewModel in $clazz")
}

@Suppress("UNCHECKED_CAST")
fun <VM : ViewModel> findViewModelClassOrNull(clazz: Class<*>, index: Int = -1): Class<VM>? {
    var types: Array<Type>? = (clazz.genericSuperclass as? ParameterizedType)?.actualTypeArguments
    while (types.isNullOrEmpty()) {
        types = (clazz.superclass.genericSuperclass as? ParameterizedType)?.actualTypeArguments
        types ?: return null
    }
    if (index < 0) {
        for (type in types) {
            val typeClazz = (type as? Class<*>) ?: continue
            if (typeClazz.isAssignableFrom(ViewModel::class.java)) {
                return EmptyViewModel::class.java as Class<VM>
            } else if (ViewModel::class.java.isAssignableFrom(typeClazz)) {
                return type as Class<VM>
            }
        }
        return null
    }
    while (types!!.size <= index) {
        types = (clazz.superclass.genericSuperclass as? ParameterizedType)?.actualTypeArguments
        types ?: return null
    }
    val typeClazz = (types[index] as? Class<*>) ?: return null
    if (typeClazz.isAssignableFrom(ViewModel::class.java)) {
        return EmptyViewModel::class.java as Class<VM>
    } else if (ViewModel::class.java.isAssignableFrom(typeClazz)) {
        return types[index] as Class<VM>
    }
    return null
}

/**
 * An implementation of [Lazy] used by [androidx.fragment.app.Fragment.genericViewModels] and
 * [androidx.activity.ComponentActivity.genericViewModels].
 *
 * [storeProducer] is a lambda that will be called during initialization, [VM] will be created
 * in the scope of returned [ViewModelStore].
 *
 * [factoryProducer] is a lambda that will be called during initialization,
 * returned [ViewModelProvider.Factory] will be used for creation of [VM]
 */
class ViewModelLazy<VM : ViewModel>(
    private val viewModelClass: Class<VM>,
    private val storeProducer: () -> ViewModelStore,
    private val factoryProducer: () -> ViewModelProvider.Factory,
    private val onInitialized: ((viewModel: VM) -> Boolean)? = null
) : Lazy<VM> {
    private var cached: VM? = null

    private var initialized = false

    override val value: VM
        get() {
            val viewModel = cached
            return if (viewModel == null) {
                val factory = factoryProducer()
                val store = storeProducer()
                ViewModelProvider(store, factory).get(viewModelClass).also {
                    initialized = onInitialized?.invoke(it) ?: false
                    cached = it
                }
            } else {
                if (!initialized) {
                    initialized = onInitialized?.invoke(viewModel) ?: false
                }
                viewModel
            }
        }

    override fun isInitialized() = cached != null
}

/**
 * 通过查找 [Activity] 上的泛型来生成具体的 VM 对象
 * Returns a [Lazy] delegate to access the ComponentActivity's ViewModel, if [factoryProducer]
 * is specified then [ViewModelProvider.Factory] returned by it will be used
 * to create [ViewModel] first time.
 *
 * ```
 * class BaseActivity<VM : ViewModel> : AppCompatActivity() {
 *     val viewModel by genericViewModels<VM>()
 * }
 * ```
 *
 * This property can be accessed only after the Activity is attached to the Application,
 * and access prior to that will result in IllegalArgumentException.
 */
@MainThread
fun <VM : ViewModel> ComponentActivity.genericViewModels(
    factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: {
        defaultViewModelProviderFactory
    }
    return ViewModelLazy(findViewModelClass(this.javaClass), { viewModelStore }, factoryPromise)
}

/**
 * 通过查找 [Fragment] 上的泛型来生成具体的 VM 对象
 * Returns a property delegate to access [ViewModel] by **default** scoped to this [Fragment]:
 * ```
 * class BaseFragment<VM : ViewModel> : Fragment() {
 *     val viewModel by genericViewModels<VM>()
 * }
 * ```
 *
 * Custom [ViewModelProvider.Factory] can be defined via [factoryProducer] parameter,
 * factory returned by it will be used to create [ViewModel]:
 * ```
 * class BaseFragment<VM : ViewModel> : Fragment() {
 *     val viewModel by genericViewModels<VM> { myFactory }
 * }
 * ```
 *
 * Default scope may be overridden with parameter [ownerProducer]:
 * ```
 * class BaseFragment<VM : ViewModel> : Fragment() {
 *     val viewModel by genericViewModels<VM> ({requireParentFragment()})
 * }
 * ```
 *
 * This property can be accessed only after this Fragment is attached i.e., after
 * [Fragment.onAttach()], and access prior to that will result in IllegalArgumentException.
 */
@MainThread
fun <VM : ViewModel> Fragment.genericViewModels(
    ownerProducer: () -> ViewModelStoreOwner = { this },
    factoryProducer: (() -> ViewModelProvider.Factory)? = null,
    onInitialized: ((viewModel: VM) -> Boolean)? = null,
    index: Int = -1
) = createViewModelLazy(
    findViewModelClass(this.javaClass, index),
    { ownerProducer().viewModelStore },
    factoryProducer,
    onInitialized
)

/**
 * 通过查找 [Fragment] 上的泛型来生成具体的 VM 对象,
 *
 * Returns a property delegate to access parent activity's [ViewModel],
 * if [factoryProducer] is specified then [ViewModelProvider.Factory]
 * returned by it will be used to create [ViewModel] first time. Otherwise, the activity's
 * [androidx.activity.ComponentActivity.getDefaultViewModelProviderFactory](default factory)
 * will be used.
 *
 * ```
 * class BaseFragment<VM : ViewModel> : Fragment() {
 *     val viewModel by genericActivityViewModels<VM>()
 * }
 * ```
 *
 * This property can be accessed only after this Fragment is attached i.e., after
 * [Fragment.onAttach()], and access prior to that will result in IllegalArgumentException.
 */
@MainThread
fun <VM : ViewModel> Fragment.genericActivityViewModels(
    factoryProducer: (() -> ViewModelProvider.Factory)? = null,
    onInitialized: ((viewModel: VM) -> Boolean)? = null,
    index: Int = -1
) = createViewModelLazy(
    findViewModelClass(this.javaClass, index),
    { requireActivity().viewModelStore },
    factoryProducer ?: { requireActivity().defaultViewModelProviderFactory },
    onInitialized
)

/**
 * Helper method for creation of [ViewModelLazy], that resolves `null` passed as [factoryProducer]
 * to default factory.
 */
@MainThread
fun <VM : ViewModel> Fragment.createViewModelLazy(
    viewModelClass: Class<VM>,
    storeProducer: () -> ViewModelStore,
    factoryProducer: (() -> ViewModelProvider.Factory)? = null,
    onInitialized: ((viewModel: VM) -> Boolean)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: { defaultViewModelProviderFactory }
    return ViewModelLazy(viewModelClass, storeProducer, factoryPromise, onInitialized)
}

private class EmptyViewModel : ViewModel()
