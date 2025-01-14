package database

import core.model.Environment
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.Filter
import dev.gitlive.firebase.firestore.FilterBuilder
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.orderBy
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DatabaseImpl(
    private val firestore: FirebaseFirestore,
    private val environment: Environment,
) : Database {

    override suspend fun save(
        id: String,
        path: String,
        data: Any,
    ) = firestore
        .collection("${environment.dbPrefix()}$path")
        .document(id)
        .set(data)

    override suspend fun update(
        id: String,
        path: String,
        fields: Map<String, Any>,
    ) = firestore
        .collection("${environment.dbPrefix()}$path")
        .document(id)
        .update(fields)

    override suspend fun deleteDocument(
        id: String,
        path: String,
    ) = firestore
        .collection("${environment.dbPrefix()}$path")
        .document(id)
        .delete()

    override suspend fun getDocument(
        id: String,
        path: String,
    ) = firestore
        .collection("${environment.dbPrefix()}$path")
        .document(id)
        .get()

    override suspend fun getCollection(
        path: String,
    ) =
        firestore
            .collection("${environment.dbPrefix()}$path")
            .get()
            .documents

    override fun observeCollection(
        path: String,
        query: (FilterBuilder.() -> Filter)?,
        orderByField: String?,
        orderDirection: Direction,
    ) = firestore
        .collection("${environment.dbPrefix()}$path")
        .run { query?.let { where(it) } ?: this }
        .run { orderByField?.let { orderBy(it, orderDirection) } ?: this }
        .snapshots
        .map { it.documents }

    override fun observeDocument(
        id: String,
        path: String,
    ): Flow<DocumentSnapshot> = firestore
        .collection("${environment.dbPrefix()}$path")
        .document(id)
        .snapshots

    override suspend fun queryCollection(
        path: String,
        query: FilterBuilder.() -> Filter,
    ): List<DocumentSnapshot> =
        firestore
            .collection("${environment.dbPrefix()}$path")
            .where(query)
            .get()
            .documents

    private fun Environment.dbPrefix() = when (this) {
        Environment.PRODUCTION -> ""
        Environment.DEVELOPMENT -> "Development"
    }
}
