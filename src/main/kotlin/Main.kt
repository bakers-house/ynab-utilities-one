package house.bakers

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import house.bakers.models.BudgetResponse
import house.bakers.models.BudgetMonthsResponse
import house.bakers.models.BudgetTransactionsResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath
import kotlin.math.abs

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
    val budgetTransactionsAdapter: JsonAdapter<BudgetTransactionsResponse> = moshi.adapter(BudgetTransactionsResponse::class.java)

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

    var budgetMonth = ""

    // TODO: Allow multiple months to be selected, based on their year.
    val budgetMonthsRequest = Request.Builder()
        .url("https://api.ynab.com/v1/budgets/$budgetId/months")
        .addHeader("Authorization", "Bearer $token")
        .build()

    client.newCall(budgetMonthsRequest).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")

        val budgetMonthsInfo = budgetMonthsAdapter.fromJson(response.body!!.string())

        println("Please select which month we should work with:")
        var months = 0
        budgetMonthsInfo!!.data.months.forEach {
            println("[$months] ${it.month}")
            months++
        }

        val selection = readln().toInt()
        budgetMonth = budgetMonthsInfo.data.months[selection].month
    }

    // TODO: Move this request into a method, so it can be called with multiple months.
    val transactionsRequest = Request.Builder()
        .url("https://api.ynab.com/v1/budgets/$budgetId/transactions?since_date=$budgetMonth")
        .addHeader("Authorization", "Bearer $token")
        .build()

    client.newCall(transactionsRequest).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")

        val transactionsInfo = budgetTransactionsAdapter.fromJson(response.body!!.string())

        var totalCategoryZeroWaste = 0.0F
        var totalCategoryOneWaste = 0.0F
        var totalCategoryTwoWaste = 0.0F
        var totalCategoryThreeWaste = 0.0F
        var totalCategoryFourWaste = 0.0F
        var totalCategoryFiveWaste = 0.0F

        transactionsInfo!!.data.transactions.forEach {
            when (it.flagColor) {
                "red" -> totalCategoryZeroWaste += abs(it.amount)
                "orange" -> totalCategoryOneWaste += abs(it.amount)
                "yellow" -> totalCategoryTwoWaste += abs(it.amount)
                "green" -> totalCategoryThreeWaste += abs(it.amount)
                "blue" -> totalCategoryFourWaste += abs(it.amount)
                "purple" -> totalCategoryFiveWaste += abs(it.amount)
            }
        }

        val totalWaste: Float = totalCategoryZeroWaste + totalCategoryOneWaste + totalCategoryTwoWaste + totalCategoryThreeWaste + totalCategoryFourWaste + totalCategoryFiveWaste

        println("Total waste this month: ${totalWaste / 1000}")
        println("Category zero waste this month: ${totalCategoryZeroWaste / 1000}")
        println("Category one waste this month: ${totalCategoryOneWaste / 1000}")
        println("Category two waste this month: ${totalCategoryTwoWaste / 1000}")
        println("Category three waste this month: ${totalCategoryThreeWaste / 1000}")
        println("Category four waste this month: ${totalCategoryFourWaste / 1000}")
        println("Category five waste this month: ${totalCategoryFiveWaste / 1000}")
    }
}

// TODO: Dump results in a table, with date, payee name, category name, amount and memo. Split table per month.
// TODO: Calculate totals for the entire year.
// TODO: Compare results with last year, per month/category.
