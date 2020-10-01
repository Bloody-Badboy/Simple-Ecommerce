package dev.arpan.ecommerce.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.arpan.ecommerce.data.DefaultProductsRepository
import dev.arpan.ecommerce.data.ProductsRepository
import dev.arpan.ecommerce.data.source.local.DefaultProductsLocalDataSource
import dev.arpan.ecommerce.data.source.local.ProductsLocalDataSource
import dev.arpan.ecommerce.data.source.remote.DefaultProductsRemoteDataSource
import dev.arpan.ecommerce.data.source.remote.ProductsRemoteDataSource
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
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
