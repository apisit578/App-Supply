package th.co.cjexpress.store.sage.data.source.remote

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_BRANCH_IP
import th.co.cjexpress.store.sage.data.model.branchMaster.BranchIpResponse
import th.co.cjexpress.store.sage.data.network.RestfulClient
import th.co.cjexpress.store.sage.data.network.RestfulResponse
import th.co.cjexpress.store.sage.data.network.getRestfulClient

class BranchIpApiTest {
    private lateinit var branchIpApi: BranchIpApi

    @Before
    fun setUp() {
        mockkStatic("th.co.cjexpress.store.sage.data.network.RestfulUtilsKt")
        branchIpApi = BranchIpApi()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_getBranchIp() =
        runTest {
            // given
            val restfulClientMock = mockk<RestfulClient<BranchIpResponse>>(relaxed = true)
            val restfulResponseMock = mockk<RestfulResponse<BranchIpResponse>>(relaxed = true)
            every { restfulClientMock.requestGet() } returns restfulResponseMock
            every {
                getRestfulClient<BranchIpResponse>(
                    serviceName = SERVICE_BRANCH_IP,
                    header = any(),
                )
            } returns restfulClientMock
            // when
            val result = branchIpApi.getBranchIp("192.168.0.1")
            // then
            assertEquals(result, restfulResponseMock)
        }
}
