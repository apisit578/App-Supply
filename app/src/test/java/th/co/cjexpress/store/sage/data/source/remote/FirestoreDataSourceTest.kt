package th.co.cjexpress.store.sage.data.source.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.reflect.TypeToken
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.data.model.HandheldLogModel
import th.co.cjexpress.store.sage.utils.JsonUtils

class FirestoreDataSourceTest {
    @RelaxedMockK
    lateinit var db: FirebaseFirestore

    @RelaxedMockK
    lateinit var jsonUtils: JsonUtils

    private lateinit var firestoreDataSource: FirestoreDataSource

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        firestoreDataSource = FirestoreDataSource(db, jsonUtils)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_insertHandheldLog() {
        // given
        val logModelMock = mockk<HandheldLogModel>(relaxed = true)
        every { jsonUtils.toJson(logModelMock) } returns "test_json"
        every {
            jsonUtils.fromJson<HashMap<String, Any?>>(
                "test_json",
                object : TypeToken<HashMap<String, Any?>>() {}.type,
            )
        } returns hashMapOf()
        // when
        firestoreDataSource.insertHandheldLog(logModelMock)
        // then
        verify { db.collection("hh_log").add(any()) }
    }
}
