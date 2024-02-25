package house.bakers.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BudgetMonthsResponse(
    val data: BudgetMonthsData
)

@JsonClass(generateAdapter = true)
data class BudgetMonthsData (
    val months: List<BudgetMonth>,
    @Json(name = "server_knowledge") val serverKnowledge: Int
)

@JsonClass(generateAdapter = true)
data class BudgetMonth (
    val month: String,
    val note: String?,
    val income: Int,
    val budgeted: Int,
    val activity: Int,
    @Json(name = "to_be_budgeted") val toBeBudgeted: Int,
    @Json(name = "age_of_money") val ageOfMoney: Int?,
    val deleted: Boolean
)
