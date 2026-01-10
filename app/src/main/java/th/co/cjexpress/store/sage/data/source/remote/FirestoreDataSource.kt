package th.co.cjexpress.store.sage.data.source.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.reflect.TypeToken
import th.co.cjexpress.store.sage.data.model.HandheldLogModel
import th.co.cjexpress.store.sage.utils.JsonUtils
import java.util.Calendar

class FirestoreDataSource(
    private val firestoreDB: FirebaseFirestore,
    private val jsonUtils: JsonUtils,
) {
    private val collectionName = "hh_log"

    fun insertHandheldLog(logData: HandheldLogModel) {
        val mapData =
            jsonUtils.fromJson<HashMap<String, Any?>>(
                jsonUtils.toJson(logData),
                object : TypeToken<HashMap<String, Any?>>() {}.type,
            )

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 30)
        val expiryAt = calendar.time

        mapData["expiryAt"] = expiryAt
        mapData["serverTimestamp"] = FieldValue.serverTimestamp()
        firestoreDB.collection(collectionName).add(mapData)
    }
}
