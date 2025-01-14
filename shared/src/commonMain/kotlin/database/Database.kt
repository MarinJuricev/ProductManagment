package database

import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.Filter
import dev.gitlive.firebase.firestore.FilterBuilder
import kotlinx.coroutines.flow.Flow

interface Database {

    suspend fun save(
        id: String,
        path: String,
        data: Any,
    )

    suspend fun update(
        id: String,
        path: String,
        fields: Map<String, Any>,
    )

    suspend fun deleteDocument(
        id: String,
        path: String,
    )

    suspend fun getDocument(
        id: String,
        path: String,
    ): DocumentSnapshot

    suspend fun getCollection(
        path: String,
    ): List<DocumentSnapshot>

    fun observeCollection(
        path: String,
        query: (FilterBuilder.() -> Filter)? = null,
        orderByField: String? = null,
        orderDirection: Direction = Direction.ASCENDING,
    ): Flow<List<DocumentSnapshot>>

    fun observeDocument(
        id: String,
        path: String,
    ): Flow<DocumentSnapshot>

    suspend fun queryCollection(
        path: String,
        query: FilterBuilder.() -> Filter,
    ): List<DocumentSnapshot>
}
