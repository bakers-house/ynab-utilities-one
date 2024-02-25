package house.bakers

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import house.bakers.models.BudgetResponse
import house.bakers.models.BudgetMonthsResponse
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
    val budgetAdapter: JsonAdapter<BudgetResponse> = moshi.adapter(BudgetResponse::class.java)
    val budgetMonthsAdapter: JsonAdapter<BudgetMonthsResponse> = moshi.adapter(BudgetMonthsResponse::class.java)

    val client = OkHttpClient()
    val budgetsRequest = Request.Builder()
        .url("https://api.ynab.com/v1/budgets")
        .addHeader("Authorization", "Bearer $token")
        .build()

    var budgetId: String

    client.newCall(budgetsRequest).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")

        val budgetInfo = budgetAdapter.fromJson(response.body!!.string())

        if (budgetInfo!!.data.budgets.size > 1) {
            println("Please select which budget we should work with:")
            var budgets = 0
            budgetInfo.data.budgets.forEach {
                println("[$budgets] ${it.name}")
                budgets++
            }
            val selectedBudget = readln().toInt()
            budgetId = budgetInfo.data.budgets[selectedBudget].id
        } else {
            budgetId = budgetInfo.data.budgets[0].id
        }
    }

    var budgetMonth: String

    val budgetMonthsRequest = Request.Builder()
        .url("https://api.ynab.com/v1/budgets/$budgetId/months")
        .addHeader("Authorization", "Bearer $token")
        .build()

    client.newCall(budgetMonthsRequest).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")

        val budgetMonthsInfo = budgetMonthsAdapter.fromJson(response.body!!.string())
        print(budgetMonthsInfo!!.data.months.reversed())

        if (budgetMonthsInfo.data.months.size > 5) {
            println("Please select which month we should work with:")
            var months = 0
            budgetMonthsInfo.data.months.forEach {
                println("[$months] ${it.month}")
                months++
            }
            val selectedMonth = readln().toInt()
            budgetMonth = budgetMonthsInfo.data.months[selectedMonth].month
        } else {
            budgetMonth = budgetMonthsInfo.data.months[0].month
        }

        // If more than 5 months, show most recent 5, then an option to show more, or an option to input month.
    }

    val transactionsRequest = Request.Builder()
        .url("https://api.ynab.com/v1/budgets/$budgetId/transactions?since_date=$budgetMonth")
        .addHeader("Authorization", "Bearer $token")
        .build()

    client.newCall(transactionsRequest).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")

        print(response.body!!.string())

        // flag_color + flag_name
    }
}
