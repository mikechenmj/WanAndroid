package com.cmj.wanandroid.lib.base.kt

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigator.Destination
import androidx.navigation.fragment.NavHostFragment

fun FragmentActivity.findNavigationById(id: Int): NavHostFragment? {
    return supportFragmentManager.findFragmentById(id) as? NavHostFragment
}

/**
 * 使用 attach 和 detach 处理 Fragment
**/

@Navigator.Name("fragment")
class TabNavigator(
    private val context: Context,
    private val manager: FragmentManager,
    private val containerId: Int
) : FragmentNavigator(context, manager, containerId) {

    companion object {
        const val TAG = "TabNavigator"

    }

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        if (manager.isStateSaved) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already saved its state")
            return null
        }
        var className = destination.className
        if (className[0] == '.') {
            className = context.packageName + className
        }

        val ft = manager.beginTransaction()

        var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }

        val tag = destination.id.toString()

        val currentFragment = manager.primaryNavigationFragment
        currentFragment?.let {
            ft.detach(currentFragment)
        }

        manager.findFragmentByTag(tag)?.also {
            ft.attach(it)
            ft.setPrimaryNavigationFragment(it)
        } ?: kotlin.run {
            val fragment = manager.fragmentFactory.instantiate(context.classLoader, className).apply {
                arguments = args
            }
            ft.add(containerId, fragment, tag)
            ft.setPrimaryNavigationFragment(fragment)
        }

        ft.setReorderingAllowed(true)
        ft.commitAllowingStateLoss()
        ft.commitNow()
        return destination
    }

    override fun popBackStack(): Boolean {
        return true
    }

}

@Suppress("unused")
class TabNavHostFragment : NavHostFragment() {
    override fun createFragmentNavigator(): Navigator<out Destination> {
        return TabNavigator(requireContext(), childFragmentManager, id)
    }
}