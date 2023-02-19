package com.cmj.wanandroid.lib.base.kt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

val HUMP_PATTERN: Pattern = Pattern.compile("[A-Z]")

fun <VB : ViewBinding> getLayoutResource(context: Context, clazz: Class<*>): Int {
    val viewBindingClass = findViewBindingClassOrNull<VB>(clazz)
            ?: throw IllegalStateException("Not found Generic Type of ViewBinding in $clazz")
    return getLayoutResource(context, viewBindingClass.simpleName)
}

fun <VB : ViewBinding> inflate(fragment: Fragment, inflater: LayoutInflater, container: ViewGroup?): View {
    return inflater.inflate(getLayoutResource<VB>(inflater.context, fragment.javaClass), container, false)
}

fun getLayoutResource(context: Context, viewBindingClassName: String): Int {
    val matcher: Matcher = HUMP_PATTERN.matcher(viewBindingClassName)
    val sb = StringBuffer()
    while (matcher.find()) {
        val match = matcher.group(0)
        if (match != null) {
            matcher.appendReplacement(sb, "_" + match.toLowerCase(Locale.ROOT))
        }
    }
    matcher.appendTail(sb)
    val index = sb.lastIndexOf("_")
    val resourceName = sb.substring(1).substring(0, index - 1)
    return context.resources.getIdentifier(resourceName, "layout", context.packageName)
}

fun <VB : ViewBinding> findViewBindingClass(clazz: Class<*>): Class<VB> {
    val viewBindingClass = findViewBindingClassOrNull<VB>(clazz)
    if (viewBindingClass != null) {
        return viewBindingClass
    }
    throw IllegalStateException("Not found Generic Type of ViewBinding in $clazz")
}

/**
 * 在泛型中查找 ViewBinding 的类型
 */
@Suppress("UNCHECKED_CAST")
fun <VB : ViewBinding> findViewBindingClassOrNull(clazz: Class<*>): Class<VB>? {
    var currentClazz = clazz
    var types: Array<Type>? = (currentClazz.genericSuperclass as? ParameterizedType)?.actualTypeArguments
    do {
        if (!types.isNullOrEmpty()) {
            for (type in types) {
                val typeClazz = (type as? Class<*>) ?: continue
                if (ViewBinding::class.java.isAssignableFrom(typeClazz)) {
                    return type as Class<VB>
                }
            }
        }
        currentClazz = currentClazz.superclass
        types = (currentClazz.genericSuperclass as? ParameterizedType)?.actualTypeArguments
    } while (types != null)

    return null
}
