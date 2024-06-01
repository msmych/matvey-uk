package uk.matvey.shkaf

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.shkaf.QueryParam.Companion.jsonb
import uk.matvey.shkaf.QueryParam.Companion.text
import uk.matvey.shkaf.QueryParam.Companion.textArray
import uk.matvey.shkaf.QueryParam.Companion.timestamp
import uk.matvey.shkaf.QueryParam.Companion.uuid
import java.time.Instant
import java.util.UUID.randomUUID

class RepoTest : FunctionalTestSetup() {
    
    @Test
    fun `should insert records`() {
        // given
        val dataAccess = DataAccess(dataSource())
        val repo = Repo(dataAccess)
        dataAccess.update(
            """
                CREATE TABLE test (
                    id UUID NULL,
                    name TEXT NULL,
                    created_at TIMESTAMP NULL,
                    details JSONB NULL,
                    tags TEXT[] NULL
                )
                """.trimIndent()
        )
        
        val id = randomUUID()
        val name = "name"
        val createdAt = Instant.now()
        val details = """{"key": "value"}"""
        val tags = listOf("tag1", "tag2")
        
        repo.insert(
            "test",
            "id" to uuid(id),
            "name" to text(name),
            "created_at" to timestamp(createdAt),
            "details" to jsonb(details),
            "tags" to textArray(tags),
        )
        
        // when
        val result = repo.select("SELECT * FROM test WHERE id = ?", uuid(id)) { reader ->
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
        val result2 = repo.select("SELECT * FROM test WHERE name = ?", text("name2")) { reader ->
            reader.nullableUuid("id")
        }
        
        // then
        assertThat(result2).isNull()
        
        // given
        val id3 = randomUUID()
        repo.insert(
            "test",
            "id" to uuid(id3),
            "name" to text(null),
            "created_at" to timestamp(createdAt),
            "details" to jsonb(details),
            "tags" to textArray(tags),
        )
        
        // when
        val result3 = repo.select("SELECT * FROM test WHERE id = ?", uuid(id3)) { reader ->
            reader.nullableString("name")
        }
        
        // then
        assertThat(result3).isNull()
        
        // given
        val id4 = randomUUID()
        repo.insert(
            "test",
            "id" to uuid(id4),
            "name" to text(name),
            "created_at" to timestamp(null),
            "details" to jsonb(details),
            "tags" to textArray(tags),
        )
        
        // when
        val result4 = repo.select("SELECT * FROM test WHERE id = ?", uuid(id4)) { reader ->
            reader.nullableInstant("created_at")
        }
        
        // then
        assertThat(result4).isNull()
        
        // given
        val id5 = randomUUID()
        repo.insert(
            "test",
            "id" to uuid(id5),
            "name" to text(name),
            "created_at" to timestamp(createdAt),
            "details" to jsonb(null),
            "tags" to textArray(tags),
        )
        
        // when
        val result5 = repo.select("SELECT * FROM test WHERE id = ?", uuid(id5)) { reader ->
            reader.nullableString("details")
        }
        
        // then
        assertThat(result5).isNull()
        
        // given
        val id6 = randomUUID()
        repo.insert(
            "test",
            "id" to uuid(id6),
            "name" to text(name),
            "created_at" to timestamp(createdAt),
            "details" to jsonb(details),
            "tags" to textArray(null),
        )
        
        // when
        val result6 = repo.select("SELECT * FROM test WHERE id = ?", uuid(id6)) { reader ->
            reader.nullableStringList("tags")
        }
        
        // then
        assertThat(result6).isNull()
    }
}
