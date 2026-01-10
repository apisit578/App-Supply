package th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote

import androidx.compose.runtime.snapshots.SnapshotStateList
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.mapper.DTOModelMapper
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteSubmitAdjustModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteSubmitUIModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils
import java.util.Date

class SdToteSubmitAdjustUseCaseTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    @RelaxedMockK
    lateinit var dtoModelMapper: DTOModelMapper

    private lateinit var useCase: SdToteSubmitAdjustUseCase

    override fun setUp() {
        super.setUp()
        useCase =
            SdToteSubmitAdjustUseCase(
                sageApiRepository = repository,
                resourceUtils = resourceUtils,
                dtoModelMapper = dtoModelMapper,
            )
    }

    private fun buildSubmitModel(qtyForBefore: Int = 10): SdToteSubmitUIModel {
        val d1 = Date(1735689600000L) // 2025-01-01
        val d2 = Date(1735776000000L) // 2025-01-02

        // ใช้ spyk กับ abstract class โดยสร้าง anonymous subclass แล้วส่งค่าเข้าคอนสตรัคเตอร์
        val exp1 = spyk(object : ISdToteExpiryDateModel(d1, 2, 2, "L111") {})
        val exp2 = spyk(object : ISdToteExpiryDateModel(d2, 3, 3, "L222") {})

        val expList =
            SnapshotStateList<ISdToteExpiryDateModel>().apply {
                add(exp1)
                add(exp2)
            }

        val model =
            SdToteSubmitUIModel(
                sdNo = "SD-0001",
                toteCode = "TOTE-01",
                receivedDate = Date(),
                noOfExp = 2,
                barcode = "1234567890",
                skuCode = "SKU123456",
                discountType = "DISC",
                actualQty = 5,
                expList = expList,
                quantityAdjustDate = null,
            )
        model.qty = qtyForBefore
        return model
    }

    @Test
    fun test_submitAdjust_success_expect_result_success() =
        runTest {
            // given
            val submit = buildSubmitModel()
            val mockModel = mockk<SdToteSubmitAdjustModel>()
            coEvery {
                repository.shipmentToteDocumentSubmitAdjust(any())
            } returns flowOf(Result.Success(data = mockModel))

            // when
            val flow = useCase.execute(submit)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = mockModel), result)
            }
        }

    @Test
    fun test_submitAdjust_failure_expect_result_error() =
        runTest {
            // given
            val submit = buildSubmitModel()
            coEvery {
                repository.shipmentToteDocumentSubmitAdjust(any())
            } returns
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

            // when
            val flow = useCase.execute(submit)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_submitAdjust_errorCode_401_expect_session_timeout_message() =
        runTest {
            // given
            val submit = buildSubmitModel()

            coEvery {
                repository.shipmentToteDocumentSubmitAdjust(any())
            } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                type = ErrorType.ERROR,
                                errorCode = "401",
                                message = "Session expired",
                            ),
                    ),
                )
            coEvery { resourceUtils.getString(R.string.text_because) } returns "เนื่องจาก %s"
            coEvery { resourceUtils.getString(R.string.text_session_timeout) } returns "เซสชันหมดอายุ"
            coEvery { resourceUtils.getString(R.string.text_please_sign_in_again) } returns "กรุณาเข้าสู่ระบบอีกครั้ง"

            // when
            val flow = useCase.execute(submit)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val cause = (result as Result.Error).cause
                assertEquals(ErrorType.WARNING, cause.type)
                cause.message?.let { assertTrue(it.contains("เซสชันหมดอายุ")) }
                cause.message?.let { assertTrue(it.contains("กรุณาเข้าสู่ระบบอีกครั้ง")) }
            }
        }

    @Test
    fun test_submitAdjust_errorCode_INVALID_BODY_expect_result_error() =
        runTest {
            // given
            val submit = buildSubmitModel()

            coEvery {
                repository.shipmentToteDocumentSubmitAdjust(any())
            } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                type = ErrorType.ERROR,
                                errorCode = ErrorCodes.INVALID_BODY,
                                message = "",
                            ),
                    ),
                )
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "ไม่สามารถดำเนินการต่อได้"
            every { resourceUtils.getString(R.string.text_actual_qty_mismatch) } returns "จำนวนไม่ถูกต้อง"

            // when
            val flow = useCase.execute(submit)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val cause = (result as Result.Error).cause
                assertEquals(ErrorType.WARNING, cause.type)
                cause.title?.let { assertTrue(it.contains("ไม่สามารถดำเนินการต่อได้")) }
                cause.message?.let { assertTrue(it.contains("จำนวนไม่ถูกต้อง")) }
            }
        }
}
