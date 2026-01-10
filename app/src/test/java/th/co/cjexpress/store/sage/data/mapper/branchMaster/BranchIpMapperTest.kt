package th.co.cjexpress.store.sage.data.mapper.branchMaster

import io.mockk.mockk
import io.mockk.unmockkAll
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.isA
import org.junit.After
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.data.model.branchMaster.BranchIpResponse
import th.co.cjexpress.store.sage.domain.model.BranchIpModel

class BranchIpMapperTest {
    private lateinit var branchIpMapper: BranchIpMapper

    @Before
    fun setUp() {
        branchIpMapper = BranchIpMapper()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_branch_master_branchIpMapper() {
        // given
        val respMock = mockk<BranchIpResponse>(relaxed = true)
        // when
        val result = branchIpMapper.map(respMock)
        // then
        MatcherAssert.assertThat(result, isA(BranchIpModel::class.java))
    }
}
