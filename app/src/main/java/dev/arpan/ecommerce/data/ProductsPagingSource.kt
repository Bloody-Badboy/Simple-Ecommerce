/*
 * Copyright 2020 Arpan Sarkar
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

package dev.arpan.ecommerce.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.arpan.ecommerce.data.model.ProductItem

class ProductsPagingSource : PagingSource<Int, ProductItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductItem> {
        TODO("Not yet implemented")
    }

    override fun getRefreshKey(state: PagingState<Int, ProductItem>): Int? {
        TODO("Not yet implemented")
    }
}
