项目简介：
一款基于 WanAndroid 社区提供的 Api 实现的个人开源项目。
项目简介：
项目遵循 Android 推荐的 Jetpack MVVM 架构思想，使用 Kotlin 进行编程，通过协程 + Flow 的方式进行异步操作以及各层之间的数据传递，使用了Navgation、Paging 和 Room 等官方推荐的 jetpack 组件。
使用 Retrofit + Okhttp + Moshi 作为网络框架，通过扩展函数对 Retrofit 的 Call 进行外包装，解决接口灵活性以及调用安全性等问题。
通过组件化的方式，将项目拆分为 lib_base 、lib_network、data_content、data_user、feature_home、feature_mine 等模块，减少不同功能间的相互依赖，使其更加内聚更容易维护。
