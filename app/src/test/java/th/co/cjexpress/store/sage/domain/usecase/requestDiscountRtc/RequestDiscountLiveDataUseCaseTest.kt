package th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.Observer
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertSame
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import th.co.cjexpress.store.sage.constants.RequestDiscountType
import th.co.cjexpress.store.sage.domain.livedata.requestDiscountRtc.RequestDiscountLiveData
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDetailProductUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentNoDetailUIModel

class RequestDiscountLiveDataUseCaseTest {
    @get:Rule
    @JvmField
    val instantRule = InstantTaskExecutorRule()

    private lateinit var liveData: RequestDiscountLiveData
    private lateinit var useCase: RequestDiscountLiveDataUseCase

    @Before
    fun setUp() {
        ArchTaskExecutor.getInstance().setDelegate(
            object : TaskExecutor() {
                override fun executeOnDiskIO(r: Runnable) = r.run()

                override fun postToMainThread(r: Runnable) = r.run()

                override fun isMainThread(): Boolean = true
            },
        )

        liveData = RequestDiscountLiveData()
        useCase = RequestDiscountLiveDataUseCase(liveData)
    }

    @After
    fun tearDown() {
        ArchTaskExecutor.getInstance().setDelegate(null) // clear delegate
    }

    @Test
    fun `docsData exposes same LiveData`() {
        assertTrue(useCase.docsData === liveData.docsData)
    }

    @Test
    fun `setIsNewProduct updates LiveData`() {
        var observed: Boolean? = null
        val obs = Observer<Boolean> { observed = it }
        liveData.isNewItem.observeForever(obs)

        useCase.setIsNewProduct(true)
        assertEquals(true, observed)

        useCase.setIsNewProduct(false)
        assertEquals(false, observed)

        liveData.isNewItem.removeObserver(obs)
    }

    @Test
    fun `setEditingProduct updates editingProduct LiveData`() {
        val prod =
            BdDetailProductUIModel(
                barcode = "111",
                productName = "Dummy",
                expiredDate = null,
                numberOfDiscounted = 1,
                barFactor = 1,
                unitFactor = "ลัง",
            )

        var observed: BdDetailProductUIModel? = null
        val obs = Observer<BdDetailProductUIModel?> { observed = it }
        liveData.editingProduct.observeForever(obs)

        useCase.setEditingProduct(prod)
        assertSame(prod, observed)

        liveData.editingProduct.removeObserver(obs)
    }

    @Test
    fun `removeProductFromBdDetail removes product by barcode`() {
        val p1 =
            BdDetailProductUIModel(
                "111",
                productName = "A",
                expiredDate = null,
                numberOfDiscounted = 1,
                barFactor = 1,
                unitFactor = "ลัง",
            )
        val p2 =
            BdDetailProductUIModel(
                barcode = "222",
                productName = "B",
                expiredDate = null,
                numberOfDiscounted = 1,
                barFactor = 1,
                unitFactor = "ลัง",
            )
        val list =
            SnapshotStateList<BdDetailProductUIModel>().apply {
                addAll(listOf(p1, p2))
            }
        val bdDetail =
            BdDocumentNoDetailUIModel(
                documentNumber = "DOC",
                createdDate = "01/01/25",
                discountType = RequestDiscountType.FRESHLIFE,
                products = list,
            )
        liveData.setDocsData(bdDetail)

        useCase.removeProductFromBdDetail("111")
        assertEquals(
            1,
            liveData.docsData.value!!
                .products.size,
        )
        assertFalse(
            liveData.docsData.value!!
                .products
                .any { it.barcode == "111" },
        )
        assertTrue(
            liveData.docsData.value!!
                .products
                .first() === p2,
        )
    }
}
