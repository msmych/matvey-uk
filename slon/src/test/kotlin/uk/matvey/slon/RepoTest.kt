package uk.matvey.slon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import uk.matvey.slon.QueryParam.Companion.jsonb
import uk.matvey.slon.QueryParam.Companion.text
import uk.matvey.slon.QueryParam.Companion.textArray
import uk.matvey.slon.QueryParam.Companion.timestamp
import uk.matvey.slon.QueryParam.Companion.uuid
import java.time.Instant
import java.util.UUID.randomUUID

class RepoTest : FunctionalTestSetup() {
    
    private lateinit var repo: Repo
    
    private val name = "name"
    private val createdAt = Instant.now()
    private val details = """{"key": "value"}"""
    private val tags = listOf("tag1", "tag2")
    
    @BeforeEach
    fun setup() {
        val dataAccess = DataAccess(dataSource())
        repo = Repo(dataAccess)
        dataAccess.execute(
            """
                CREATE TABLE IF NOT EXISTS test (
                    id UUID NULL,
                    name TEXT NULL,
                    created_at TIMESTAMP NULL,
                    details JSONB NULL,
                    tags TEXT[] NULL
                )
                """.trimIndent()
        )
    }
    
    @Test
    fun `should insert records`() {
        // given
        val id = randomUUID()
        repo.insert(
            "test",
            "id" to uuid(id),
            "name" to text(name),
            "created_at" to timestamp(createdAt),
            "details" to jsonb(details),
            "tags" to textArray(tags),
        )
        
        // when
        val result = repo.selectSingle("SELECT * FROM test WHERE id = ?", listOf(uuid(id))) { reader ->
            mapOf(
                "id" to reader.uuid("id"),
                "name" to reader.string("name"),
                "createdAt" to reader.instant("created_at"),
                "details" to reader.string("details"),
                "tags" to reader.stringList("tags")
            )
        }
        
        // then
        assertThat(result["id"]).isEqualTo(id)
        assertThat(result["name"]).isEqualTo(name)
        assertThat(result["createdAt"]).isEqualTo(createdAt)
        assertThat(result["details"]).isEqualTo(details)
        assertThat(result["tags"]).isEqualTo(tags)
    }
    
    @Test
    fun `should insert NULL to UUID column`() {
        // given
        repo.insert(
            "test",
            "id" to uuid(null),
            "name" to text("name2"),
            "created_at" to timestamp(createdAt),
            "details" to jsonb(details),
            "tags" to textArray(tags),
        )
        
        // when
        val result2 = repo.selectSingle("SELECT * FROM test WHERE name = ?", listOf(text("name2"))) { reader ->
            reader.nullableUuid("id")
        }
        
        // then
        assertThat(result2).isNull()
    }
    
    @Test
    fun `should insert NULL to TEXT column`() {
        // given
        val id = randomUUID()
        repo.insert(
            "test",
            "id" to uuid(id),
            "name" to text(null),
            "created_at" to timestamp(createdAt),
            "details" to jsonb(details),
            "tags" to textArray(tags),
        )
        
        // when
        val result3 = repo.selectSingle("SELECT * FROM test WHERE id = ?", listOf(uuid(id))) { reader ->
            reader.nullableString("name")
        }
        
        // then
        assertThat(result3).isNull()
    }
    
    @Test
    fun `should insert NULL to TIMESTAMP column`() {
        // given
        val id = randomUUID()
        repo.insert(
            "test",
            "id" to uuid(id),
            "name" to text(name),
            "created_at" to timestamp(null),
            "details" to jsonb(details),
            "tags" to textArray(tags),
        )
        
        // when
        val result4 = repo.selectSingle("SELECT * FROM test WHERE id = ?", listOf(uuid(id))) { reader ->
            reader.nullableInstant("created_at")
        }
        
        // then
        assertThat(result4).isNull()
    }
    
    @Test
    fun `should insert NULL to JSONB column`() {
        // given
        val id = randomUUID()
        repo.insert(
            "test",
            "id" to uuid(id),
            "name" to text(name),
            "created_at" to timestamp(createdAt),
            "details" to jsonb(null),
            "tags" to textArray(tags),
        )
        
        // when
        val result5 = repo.selectSingle("SELECT * FROM test WHERE id = ?", listOf(uuid(id))) { reader ->
            reader.nullableString("details")
        }
        
        // then
        assertThat(result5).isNull()
    }
    
    @Test
    fun `should insert NULL to ARRAY column`() {
        // given
        val id = randomUUID()
        repo.insert(
            "test",
            "id" to uuid(id),
            "name" to text(name),
            "created_at" to timestamp(createdAt),
            "details" to jsonb(details),
            "tags" to textArray(null),
        )
        
        // when
        val result6 = repo.selectSingle("SELECT * FROM test WHERE id = ?", listOf(uuid(id))) { reader ->
            reader.nullableStringList("tags")
        }
        
        // then
        assertThat(result6).isNull()
    }
    
    @Test
    fun `should update records`() {
        // given
        val id = randomUUID()
        repo.insert(
            "test",
            "id" to uuid(id),
            "name" to text(name),
            "created_at" to timestamp(createdAt),
            "details" to jsonb(details),
            "tags" to textArray(tags),
        )
        
        // when
        val newDetails = """{"key": "value2"}"""
        val newTags = listOf("tag3", "tag4")
        repo.update(
            "test",
            listOf(
                "details" to jsonb(newDetails),
                "tags" to textArray(newTags),
            ),
            "id = ?",
            listOf(uuid(id))
        )
        
        // then
        val result = repo.selectSingle("SELECT * FROM test WHERE id = ?", listOf(uuid(id))) { reader ->
            mapOf(
                "details" to reader.string("details"),
                "tags" to reader.stringList("tags")
            )
        }
        
        assertThat(result["details"]).isEqualTo(newDetails)
        assertThat(result["tags"]).isEqualTo(newTags)
    }
    
    @Test
    fun `should delete records`() {
        // given
        val id = randomUUID()
        repo.insert(
            "test",
            "id" to uuid(id),
            "name" to text(name),
            "created_at" to timestamp(createdAt),
            "details" to jsonb(details),
            "tags" to textArray(tags),
        )
        
        // when
        repo.delete("test", "id = ?", listOf(uuid(id)))
        
        // then
        val result = repo.select("SELECT * FROM test WHERE id = ?", listOf(uuid(id))) { reader ->
            reader.uuid("id")
        }
        
        assertThat(result).isEmpty()
    }
}
