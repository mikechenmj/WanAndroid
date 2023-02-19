/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cmj.wanandroid.lib.base.page

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cmj.wanandroid.lib.network.bean.PageModule

class NormalPagingSource<T: Any>(
    private val start: Int = 0,
    private val sourceFun: suspend (Int, Int) -> PageModule<T>
) : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: start
        return try {
            val module = sourceFun.invoke(page, params.loadSize)
            LoadResult.Page(
                data = module.datas,
                prevKey = if (page == start) null else page - 1,
                nextKey = if (module.over) null else page + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
