package house.bakers.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BudgetTransactionsResponse(
    val data: BudgetTransactionsData
)

@JsonClass(generateAdapter = true)
data class BudgetTransactionsData (
    val transactions: List<Transaction>,
    @Json(name = "server_knowledge") val serverKnowledge: Int
)

@JsonClass(generateAdapter = true)
data class Transaction (
    val id: String,
    val date: String,
    val amount: Int,
    val memo: String?,
    val cleared: String,
    val approved: Boolean,
    @Json(name = "flag_color") val flagColor: String?,
    @Json(name = "flag_name") val flagName: String?,
    @Json(name = "account_id") val accountId: String,
    @Json(name = "account_name") val accountName: String,
    @Json(name = "payee_id") val payeeId: String,
    @Json(name = "payee_name") val payeeName: String,
    @Json(name = "category_id") val categoryId: String?,
    @Json(name = "category_name") val categoryName: String,
    @Json(name = "transfer_account_id") val transferAccountId: String?,
    @Json(name = "transfer_transaction_id") val transferTransactionId: String?,
    @Json(name = "matched_transaction_id") val matchedTransactionId: String?,
    @Json(name = "import_id") val importId: String?,
    @Json(name = "import_payee_name") val importPayeeName: String?,
    @Json(name = "import_payee_name_original") val importPayeeNameOriginal: String?,
    @Json(name = "debt_transaction_type") val debtTransactionType: String?,
    val deleted: Boolean,
    val subtransactions: List<Subtransaction?>
)

@JsonClass(generateAdapter = true)
data class Subtransaction (
    val id: String,
    @Json(name = "transaction_id") val transactionId: String?,
    val amount: Int,
    val memo: String?,
    @Json(name = "payee_id") val payeeId: String?,
    @Json(name = "payee_name") val payeeName: String?,
    @Json(name = "category_id") val categoryId: String?,
    @Json(name = "category_name") val categoryName: String,
    @Json(name = "transfer_account_id") val transferAccountId: String?,
    @Json(name = "transfer_transaction_id") val transferTransactionId: String?,
    val deleted: Boolean,
)
