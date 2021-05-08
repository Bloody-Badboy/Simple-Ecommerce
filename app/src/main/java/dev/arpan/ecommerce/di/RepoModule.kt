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

package dev.arpan.ecommerce.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.arpan.ecommerce.data.DefaultProductsRepository
import dev.arpan.ecommerce.data.ProductsRepository
import dev.arpan.ecommerce.data.source.local.DefaultProductsLocalDataSource
import dev.arpan.ecommerce.data.source.local.ProductsLocalDataSource
import dev.arpan.ecommerce.data.source.remote.DefaultProductsRemoteDataSource
import dev.arpan.ecommerce.data.source.remote.ProductsRemoteDataSource
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepoModule {

    @Singleton
    @Provides
    fun provideProductsLocalDataSource(): ProductsLocalDataSource {
        return DefaultProductsLocalDataSource()
    }

    @Singleton
    @Provides
    fun provideProductsRemoteDataSource(): ProductsRemoteDataSource {
        return DefaultProductsRemoteDataSource()
    }

    @Singleton
    @Provides
    fun provideProductsRepository(
        localDataSource: ProductsLocalDataSource,
        remoteDataSource: ProductsRemoteDataSource
    ): ProductsRepository {
        return DefaultProductsRepository(localDataSource, remoteDataSource)
    }
}
