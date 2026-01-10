package th.co.cjexpress.store.sage.data.network

interface RestfulCallback<T> {
    fun onResponse(response: RestfulResponse<T>)
}
