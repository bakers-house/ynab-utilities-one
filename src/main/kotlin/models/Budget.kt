package house.bakers.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BudgetResponse (
    val data: BudgetData
)

@JsonClass(generateAdapter = true)
data class BudgetData (
    val budgets: List<Budget>,
    @Json(name = "default_budget") val defaultBudget: String?
)

@JsonClass(generateAdapter = true)
data class Budget (
    val id: String,
    val name: String,
    @Json(name = "last_modified_on") val lastModifiedOn: String,
    @Json(name = "first_month") val firstMonth: String,
    @Json(name = "last_month") val lastMonth: String,
    @Json(name = "date_format") val dateFormat: BudgetDateFormat,
    @Json(name = "currency_format") val currencyFormat: BudgetCurrencyFormat,
)

@JsonClass(generateAdapter = true)
data class BudgetDateFormat (
    val format: String
)

@JsonClass(generateAdapter = true)
data class BudgetCurrencyFormat (
    @Json(name = "currency_symbol") val currencySymbol: String,
    @Json(name = "decimal_digits") val decimalDigits: Int,
    @Json(name = "decimal_separator") val decimalSeparator: Char,
    @Json(name = "display_symbol") val displaySymbol: Boolean,
    @Json(name = "example_format") val exampleFormat: String,
    @Json(name = "group_separator") val groupSeparator: Char,
    @Json(name = "iso_code") val isoCode: String,
    @Json(name = "symbol_first") val symbolFirst: Boolean,
)