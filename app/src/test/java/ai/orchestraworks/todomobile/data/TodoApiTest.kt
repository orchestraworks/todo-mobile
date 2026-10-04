package ai.orchestraworks.todomobile.data

import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class TodoApiTest {
    private val server = MockWebServer()
    private lateinit var api: TodoApi

    @Before
    fun setUp() {
        server.start()
        api = TodoApi.create(server.url("/api").toString(), OkHttpClient())
    }

    @After
    fun tearDown() {
        server.close()
    }

    @Test
    fun `list sends search and paging as query parameters`() = runTest {
        server.enqueue(json("""{"items":[{"id":"7","title":"Buy milk","done":false,"tags":["home"]}],"page":2,"pageSize":20,"total":21}"""))

        val page = api.listTodos(query = "milk", page = 2, pageSize = 20)

        val request = server.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/api/todos", request.url.encodedPath)
        assertEquals("milk", request.url.queryParameter("q"))
        assertEquals("2", request.url.queryParameter("page"))
        assertEquals("20", request.url.queryParameter("pageSize"))
        assertEquals(listOf(Todo(id = "7", title = "Buy milk", tags = listOf("home"))), page.items)
        assertEquals(21, page.total)
    }

    @Test
    fun `list reads the numeric ids the server assigns`() = runTest {
        server.enqueue(json("""{"items":[{"id":3,"title":"Call mom","done":true,"createdAt":"2026-09-30T08:00:00.000Z","tags":[]}],"page":1,"pageSize":20,"total":1}"""))

        val page = api.listTodos(query = null, page = 1, pageSize = 20)

        assertEquals(Todo(id = "3", title = "Call mom", done = true, createdAt = "2026-09-30T08:00:00.000Z"), page.items.single())
        assertNull(server.takeRequest().url.queryParameter("q"))
    }

    @Test
    fun `create posts the title and tags as json`() = runTest {
        server.enqueue(json("""{"id":"9","title":"Water plants","done":false,"tags":["home"]}""", code = 201))

        val created = api.createTodo(NewTodo(title = "Water plants", tags = listOf("home")))

        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/api/todos", request.url.encodedPath)
        assertEquals("""{"title":"Water plants","tags":["home"]}""", request.body?.utf8())
        assertEquals("9", created.id)
    }

    @Test
    fun `update sends only the fields that changed`() = runTest {
        server.enqueue(json("""{"id":"4","title":"Renamed","done":false,"tags":[]}"""))

        api.updateTodo("4", TodoChanges(title = "Renamed"))

        val request = server.takeRequest()
        assertEquals("PATCH", request.method)
        assertEquals("/api/todos/4", request.url.encodedPath)
        assertEquals("""{"title":"Renamed"}""", request.body?.utf8())
    }

    @Test
    fun `mark done posts to the done route`() = runTest {
        server.enqueue(json("""{"id":"4","title":"Renamed","done":true,"tags":[]}"""))

        val updated = api.markDone("4")

        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/api/todos/4/done", request.url.encodedPath)
        assertEquals(true, updated.done)
    }

    @Test
    fun `delete sends a delete request and accepts no content`() = runTest {
        server.enqueue(MockResponse.Builder().code(204).build())

        api.deleteTodo("4")

        val request = server.takeRequest()
        assertEquals("DELETE", request.method)
        assertEquals("/api/todos/4", request.url.encodedPath)
    }

    @Test
    fun `tags are read with their counts`() = runTest {
        server.enqueue(json("""[{"name":"groceries","count":2},{"name":"work","count":1}]"""))

        assertEquals(listOf(TagCount("groceries", 2), TagCount("work", 1)), api.listTags())
        assertEquals("/api/tags", server.takeRequest().url.encodedPath)
    }

    @Test
    fun `a base url with a trailing slash reaches the same routes`() = runTest {
        val slashed = TodoApi.create(server.url("/api/").toString(), OkHttpClient())
        server.enqueue(json("""{"items":[],"page":1,"pageSize":20,"total":0}"""))

        slashed.listTodos(query = null, page = 1, pageSize = 20)

        assertEquals("/api/todos", server.takeRequest().url.encodedPath)
    }

    private fun json(body: String, code: Int = 200): MockResponse =
        MockResponse.Builder()
            .code(code)
            .setHeader("Content-Type", "application/json")
            .body(body)
            .build()
}
