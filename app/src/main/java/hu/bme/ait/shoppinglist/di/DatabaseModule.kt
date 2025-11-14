package hu.bme.ait.shoppinglist.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hu.bme.ait.shoppinglist.data.AppDatabase
import hu.bme.ait.shoppinglist.data.CartDAO


@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun provideCartDao(appDatabase: AppDatabase) : CartDAO {
        return appDatabase.cartDAO()
    }

    @Provides
    fun provideCartAppDatabase(
        @ApplicationContext appContext: Context
    ) : AppDatabase {
        return AppDatabase.getDatabase(appContext)
    }

}