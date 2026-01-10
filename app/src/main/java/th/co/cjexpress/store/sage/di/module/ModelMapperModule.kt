package th.co.cjexpress.store.sage.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import th.co.cjexpress.store.sage.data.mapper.SageAdapterApiModelMapper
import th.co.cjexpress.store.sage.data.mapper.branchMaster.BranchIpMapper
import th.co.cjexpress.store.sage.domain.mapper.DTOModelMapper
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModelMapperModule {
    @Provides
    @Singleton
    fun provideBranchIpModelMapper() = BranchIpMapper()

    @Provides
    @Singleton
    fun provideUiModelMapper() = UiModelMapper()

    @Provides
    @Singleton
    fun provideSageAdapterApiModelMapper() = SageAdapterApiModelMapper()

    @Provides
    @Singleton
    fun provideDTOModelMapper() = DTOModelMapper()
}
