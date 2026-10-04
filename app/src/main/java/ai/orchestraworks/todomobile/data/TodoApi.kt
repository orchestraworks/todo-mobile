package ai.orchestraworks.todomobile.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * The todo-app REST API. Every route and JSON shape the app depends on lives in this file.
 * Routes are relative to the base URL, which includes the server's `/api` prefix.
 */
interface TodoApi {

    @GET("todos")
    suspend fun listTodos(
        @Query("q") query: String?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): TodoPage

    @GET("todos/{id}")
    suspend fun getTodo(@Path("id") id: String): Todo

    @POST("todos")
    suspend fun createTodo(@Body body: NewTodo): Todo

    @PATCH("todos/{id}")
    suspend fun updateTodo(@Path("id") id: String, @Body body: TodoChanges): Todo

    @POST("todos/{id}/done")
    suspend fun markDone(@Path("id") id: String): Todo

    @DELETE("todos/{id}")
    suspend fun deleteTodo(@Path("id") id: String)

    @GET("tags")
    suspend fun listTags(): List<TagCount>

    companion object {
        const val PAGE_SIZE = 20

        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
        }

        fun create(baseUrl: String, client: OkHttpClient): TodoApi =
            Retrofit.Builder()
                .baseUrl(baseUrl.trimEnd('/') + "/")
                .client(client)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(TodoApi::class.java)
    }
}

@Serializable
data class Todo(
    val id: String,
    val title: String,
    val done: Boolean = false,
    val createdAt: String? = null,
    val tags: List<String> = emptyList(),
)

@Serializable
data class TodoPage(
    val items: List<Todo>,
    val page: Int = 1,
    val pageSize: Int = TodoApi.PAGE_SIZE,
    val total: Int = items.size,
)

@Serializable
data class NewTodo(
    val title: String,
    val tags: List<String> = emptyList(),
)

@Serializable
data class TodoChanges(
    val title: String? = null,
    val done: Boolean? = null,
    val tags: List<String>? = null,
)

@Serializable
data class TagCount(
    val name: String,
    val count: Int,
)
