package house.bakers

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import house.bakers.models.BudgetResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath

@Throws(IOException::class)
fun getToken(path: Path): String {
    var token = ""
    FileSystem.SYSTEM.read(path) {
        while (true) {
            token = readUtf8Line() ?: break
        }
    }
    return token
}

fun main() {
    val token: String = getToken("src/main/resources/ynab_api_token.txt".toPath())

    val moshi: Moshi = Moshi.Builder().build()
    val adapter: JsonAdapter<BudgetResponse> = moshi.adapter(BudgetResponse::class.java)

    val client = OkHttpClient()
    val budgetsRequest = Request.Builder()
        .url("https://api.ynab.com/v1/budgets")
        .addHeader("Authorization", "Bearer $token")
        .build()

    client.newCall(budgetsRequest).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")

        val budgetInfo = adapter.fromJson(response.body!!.string())

        print(budgetInfo!!.data.budgets[1].id)
    }
}
