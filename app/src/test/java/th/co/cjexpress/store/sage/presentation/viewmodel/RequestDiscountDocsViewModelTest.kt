package th.co.cjexpress.store.sage.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.constants.RequestDiscountType
import th.co.cjexpress.store.sage.domain.livedata.requestDiscountRtc.RequestDiscountLiveData
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.masterMapping.BdSearchTypeItemModel
import th.co.cjexpress.store.sage.domain.model.masterMapping.BdSearchTypeMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentItemMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentNoDetailMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentNoDetailModel
import th.co.cjexpress.store.sage.domain.usecase.masterMapping.GetBdSearchTypeUseCase
import th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc.GetBdDocumentNoDetailUseCase
import th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc.GetBdDocumentUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentItemUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentNoDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdSearchTypeUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.viewmodel.requestDiscountRtc.RequestDiscountDocsViewModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers

@ExperimentalCoroutinesApi
class RequestDiscountDocsViewModelTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var requestDiscountLiveData: RequestDiscountLiveData

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    @RelaxedMockK
    lateinit var getBdListUseCase: GetBdDocumentUseCase

    @RelaxedMockK
    lateinit var getBdDetailUseCase: GetBdDocumentNoDetailUseCase

    @RelaxedMockK
    lateinit var getMasterMappingUseCase: GetBdSearchTypeUseCase

    @RelaxedMockK
    lateinit var uiModelMapper: UiModelMapper

    private lateinit var viewModel: RequestDiscountDocsViewModel

    @Before
    override fun setUp() {
        super.setUp()
        viewModel =
            RequestDiscountDocsViewModel(
                requestDiscountLiveData,
                resourceUtils,
                getBdListUseCase,
                getBdDetailUseCase,
                getMasterMappingUseCase,
                uiModelMapper,
                dispatcherProvider = TestDispatchers(),
            )
    }

    @Test
    fun `getBdList success should update state with data`() =
        runTest {
            // Given
            val domainList =
                listOf(
                    BdDocumentItemMappingModel("2025-01-01", RequestDiscountType.NORMAL, "DOC001"),
                )
            coEvery { getBdListUseCase.execute() } returns
                flowOf(
                    Result.Success(
                        BdDocumentMappingModel(
                            "SUCCESS",
                            "SUCCESS",
                            timestamp = "11111",
                            message = "11111",
                            data = domainList,
                        ),
                    ),
                )
            val sampleUI = mockk<BdDocumentUIModel>(relaxed = true)
            every { uiModelMapper.bdDocumentUIMapper.toUIModel(any()) } returns sampleUI

            // When
            viewModel.getBdList()

            // Then
            viewModel.getBdListState.test {
                assertEquals(
                    RequestDiscountDocsViewModel.UIState.GetBdDocumentState(sampleUI),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
            viewModel.baseScreenState.test {
                assertEquals(BaseScreenState.Idle, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getBdList error should emit error state`() =
        runTest {
            // Given
            val errorMsg = "Service failed"
            coEvery { resourceUtils.getString(R.string.text_unable_to_continue) } returns errorMsg
            coEvery { getBdListUseCase.execute() } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "UNKNOWN_ERROR",
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                    ),
                )
            // When
            viewModel.getBdList()

            // Then
            viewModel.getBdListState.test {
                assertEquals(
                    RequestDiscountDocsViewModel.UIState.GetBdDocumentState(),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
            viewModel.baseScreenState.test {
                assertTrue(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getBdDetail success should update state with detail data`() =
        runTest {
            // Given
            val mapping =
                BdDocumentNoDetailMappingModel(
                    "SUCCESS",
                    "SUCCESS",
                    timestamp = "11111",
                    message = "11111",
                    data =
                        BdDocumentNoDetailModel(
                            documentNumber = "DOC001",
                            createdDate = "2025-01-01",
                            discountType = RequestDiscountType.NORMAL,
                            products = mutableStateListOf(),
                        ),
                )
            val sampleUI =
                BdDocumentNoDetailUIModel(
                    documentNumber = "DOC001",
                    createdDate = "2025-01-01",
                    discountType = RequestDiscountType.NORMAL,
                    products = mutableStateListOf(),
                )

            coEvery { getBdDetailUseCase.execute("DOC001") } returns flowOf(Result.Success(mapping))
            every { uiModelMapper.bdDocumentNoDetailUIMapper.toUIModel(any()) } returns sampleUI

            // When
            viewModel.getBdDetail("DOC001")

            // Then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(
                        RequestDiscountDocsViewModel.UIState.GetBdDocumentNoDetailBdState(sampleUI),
                    ),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getBdDetail error should emit error state`() =
        runTest {
            // Given
            coEvery { getBdDetailUseCase.execute("DOC001") } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "UNKNOWN_ERROR",
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                    ),
                )
            // When
            viewModel.getBdDetail("DOC001")

            // Then
            viewModel.getBdDetailState.test {
                assertEquals(
                    RequestDiscountDocsViewModel.UIState.GetBdDocumentNoDetailBdState(),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
            viewModel.baseScreenState.test {
                assertTrue(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getCampaignDiscountType success should update mapping state`() =
        runTest {
            // Given
            val model = listOf<BdSearchTypeItemModel>()
            val domain =
                BdSearchTypeMappingModel(
                    "SUCCESS",
                    "SUCCESS",
                    timestamp = "11111",
                    message = "11111",
                    types = model,
                )
            coEvery { getMasterMappingUseCase.execute(any()) } returns
                flowOf(
                    Result.Success(
                        BdSearchTypeMappingModel(
                            "SUCCESS",
                            "SUCCESS",
                            timestamp = "11111",
                            message = "11111",
                            types = model,
                        ),
                    ),
                )
            val sampleUIList = listOf(BdSearchTypeUIModel("code", "name", "name2"))
            every { uiModelMapper.bdSearchTypeUIMapper.toUIModel(domain.types) } returns sampleUIList

            // When
            viewModel.getCampaignDiscountType()

            // Then
            viewModel.getMasterMappingState.test {
                assertEquals(
                    RequestDiscountDocsViewModel.UIState.GetBdSearchTypeUIState(sampleUIList),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
            viewModel.baseScreenState.test {
                assertEquals(BaseScreenState.Idle, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getCampaignDiscountType error should emit error state`() =
        runTest {
            // Given
            coEvery { getMasterMappingUseCase.execute(any()) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "UNKNOWN_ERROR",
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                    ),
                )
            // When
            viewModel.getCampaignDiscountType()

            // Then
            viewModel.getMasterMappingState.test {
                assertEquals(
                    RequestDiscountDocsViewModel.UIState.GetBdSearchTypeUIState(),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
            viewModel.baseScreenState.test {
                assertTrue(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `resetGetMasterMappingState should clear mapping state`() {
        viewModel.resetGetMasterMappingState()
        assertEquals(
            RequestDiscountDocsViewModel.UIState.GetBdSearchTypeUIState(),
            viewModel.getMasterMappingState.value,
        )
    }

    @Test
    fun `setDocsDataLD should delegate to liveData`() {
        val ui = mockk<BdDocumentNoDetailUIModel>()
        viewModel.setDocsDataLD(ui)
        verify { requestDiscountLiveData.setDocsData(ui) }
    }

    @Test
    fun `setDiscountTypeLD should delegate to liveData`() {
        val t = RequestDiscountType.NORMAL
        viewModel.setDiscountTypeLD(t)
        verify { requestDiscountLiveData.setDiscountType(t) }
    }

    @Test
    fun `resetDocsDataLD should delegate to liveData`() {
        viewModel.resetDocsDataLD()
        verify { requestDiscountLiveData.resetDocsData() }
    }

    @Test
    fun `logEventScreenView should logInfo`() =
        runTest {
            // when
            viewModel.logEventScreenView()

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SCREEN_VIEW,
                    message = any(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventOnBackPressed should logInfo`() =
        runTest {
            // when
            viewModel.logEventOnBackPressed()

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.ON_BACK_PRESSED,
                    message = any(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventOpenRequestDiscountDocsDialog should logInfo`() =
        runTest {
            // given
            every { resourceUtils.getString(R.string.text_create_doc) } returns "สร้างเอกสาร"

            // when
            viewModel.logEventOpenRequestDiscountDocsDialog()

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.OPEN_CREATE_REQUEST_DISCOUNT_DOCS_DIALOG,
                    message = "สร้างเอกสาร",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventSubmitDiscountType should logInfo`() =
        runTest {
            // when
            viewModel.logEventSubmitDiscountType("NORMAL")

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SUBMIT_DISCOUNT_TYPE,
                    message = "NORMAL",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventSelectRequestDiscountDoc should logInfo`() =
        runTest {
            // given
            val mockModel = mockk<BdDocumentItemUIModel>()

            // when
            viewModel.logEventSelectRequestDiscountDoc(mockModel)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SELECT_REQUEST_DISCOUNT_DOC,
                    message = mockModel.toString(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }
}
