package th.co.cjexpress.store.sage.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.data.mapper.branchMaster.BranchIpMapper
import th.co.cjexpress.store.sage.data.model.branchMaster.BranchIpResponse
import th.co.cjexpress.store.sage.data.network.RestfulResponse
import th.co.cjexpress.store.sage.data.source.remote.BranchIpApi
import th.co.cjexpress.store.sage.domain.model.BranchIpModel
import th.co.cjexpress.store.sage.domain.model.Result

class BranchIpRepositoryImplTest {
    private lateinit var branchIpApi: BranchIpApi
    private lateinit var modelMapper: BranchIpMapper
    private lateinit var repository: BranchIpRepositoryImpl

    @Before
    fun setUp() {
        branchIpApi = mockk()
        modelMapper = mockk()
        repository = BranchIpRepositoryImpl(branchIpApi, modelMapper)
    }

    @Test
    fun `checkBranchIP returns Success when API response is successful and data is mapped correctly`() =
        runTest {
            // given
            val ipAddress = "192.168.0.1"
            val mockRespModel = mockk<BranchIpResponse>()
            val mockBranchIpModel = mockk<BranchIpModel>()
            val mockRestfulResponse: RestfulResponse<BranchIpResponse> = mockk()
            every { mockRestfulResponse.bodyToObject(BranchIpResponse::class.java) } returns mockRespModel
            every { mockRestfulResponse.isSuccessful } returns true
            coEvery { branchIpApi.getBranchIp(ipAddress) } returns mockRestfulResponse
            every { modelMapper.map(mockRespModel) } returns mockBranchIpModel

            // when
            val result = repository.checkBranchIP(ipAddress)

            // then
            assertTrue(result is Result.Success)
            assertEquals(mockBranchIpModel, (result as Result.Success).data)
            coVerify { branchIpApi.getBranchIp(ipAddress) }
            verify { modelMapper.map(mockRespModel) }
        }

    @Test
    fun `checkBranchIP returns Error when API response is unsuccessful with resp body`() =
        runTest {
            // given
            val ipAddress = "192.168.0.1"
            val mockRespModel = mockk<BranchIpResponse>()
            val mockBranchIpModel = mockk<BranchIpModel>()
            val mockRestfulResponse: RestfulResponse<BranchIpResponse> = mockk()
            every { mockRestfulResponse.bodyToObject(BranchIpResponse::class.java) } returns mockRespModel
            every { mockRestfulResponse.isSuccessful } returns false
            coEvery { branchIpApi.getBranchIp(ipAddress) } returns mockRestfulResponse
            every { modelMapper.map(mockRespModel) } returns mockBranchIpModel

            // when
            val result = repository.checkBranchIP(ipAddress)

            // then
            assertTrue(result is Result.Error)
            coVerify { branchIpApi.getBranchIp(ipAddress) }
        }

    @Test
    fun `checkBranchIP returns Error when API response is null`() =
        runTest {
            // given
            val ipAddress = "192.168.0.1"
            coEvery { branchIpApi.getBranchIp(ipAddress) } returns null

            // when
            val result = repository.checkBranchIP(ipAddress)

            // then
            assertTrue(result is Result.Error)
            assertEquals("ไม่ได้รับข้อมูลตอบกลับจากเซอร์วิส", (result as Result.Error).cause?.message)
            coVerify { branchIpApi.getBranchIp(ipAddress) }
        }

    @Test
    fun `checkBranchIP returns Error when API response exception`() =
        runTest {
            // given
            val ipAddress = "192.168.0.1"
            coEvery { branchIpApi.getBranchIp(ipAddress) } throws Exception("service not found!")

            // when
            val result = repository.checkBranchIP(ipAddress)

            // then
            assertTrue(result is Result.Error)
            assertEquals("เกิดข้อผิดพลาดในขั้นตอนการตรวจสอบสาขาของเครื่อง service not found!", (result as Result.Error).cause?.message)
            coVerify { branchIpApi.getBranchIp(ipAddress) }
        }

    @Test
    fun `checkBranchIP returns Error when API response success but parse resp body error`() =
        runTest {
            // given
            val ipAddress = "192.168.0.1"
            val mockRestfulResponse: RestfulResponse<BranchIpResponse> = mockk()
            every { mockRestfulResponse.bodyToObject(BranchIpResponse::class.java) } returns null
            coEvery { branchIpApi.getBranchIp(ipAddress) } returns mockRestfulResponse

            // when
            val result = repository.checkBranchIP(ipAddress)

            // then
            assertTrue(result is Result.Error)
            assertEquals(
                "เกิดข้อผิดพลาดในขั้นตอนการตรวจสอบสาขาของเครื่อง ข้อมูลการตรวจสอบสาขาของเครื่องไม่ถูกต้อง",
                (result as Result.Error).cause?.message,
            )
            coVerify { branchIpApi.getBranchIp(ipAddress) }
        }

    @Test
    fun `checkBranchIP returns Error when API response success but io exception`() =
        runTest {
            // given
            val ipAddress = "192.168.0.1"
            val mockRestfulResponse: RestfulResponse<BranchIpResponse> = mockk()
            every { mockRestfulResponse.bodyToObject(BranchIpResponse::class.java) } throws IOException("IO Error")
            coEvery { branchIpApi.getBranchIp(ipAddress) } returns mockRestfulResponse

            // when
            val result = repository.checkBranchIP(ipAddress)

            // then
            assertTrue(result is Result.Error)
            assertEquals("ไม่สามารถตรวจสอบสาขาของเครื่องได้ในเวลาที่กำหนด (IO Error)", (result as Result.Error).cause?.message)
            coVerify { branchIpApi.getBranchIp(ipAddress) }
        }
}
