package uk.matvey.postal

import org.postgresql.util.PGobject
import java.sql.ResultSet
import java.time.Instant
import java.util.*

class ResultExtractor(private val resultSet: ResultSet) {

    fun stringOrNull(name: String): String? {
        return this.resultSet.getString(name)
            .takeUnless { this.resultSet.wasNull() }
    }

    fun string(name: String) = requireNotNull(stringOrNull(name))

    fun longOrNull(name: String): Long? {
        return this.resultSet.getLong(name)
            .takeUnless { this.resultSet.wasNull() }
    }

    fun long(name: String) = requireNotNull(longOrNull(name))

    fun uuidOrNull(name: String): UUID? {
        return this.resultSet.getObject(name)
            .takeUnless { this.resultSet.wasNull() }
            ?.let { it as UUID }
    }

    fun uuid(name: String) = requireNotNull(uuidOrNull(name))

    fun instantOrNull(name: String): Instant? {
        return this.resultSet.getTimestamp(name)
            .takeUnless { this.resultSet.wasNull() }
            ?.toInstant()
    }

    fun instant(name: String) = requireNotNull(instantOrNull(name))

    fun jsonbOrNull(name: String): String? {
        return this.resultSet.getObject(name)
            .takeUnless { this.resultSet.wasNull() }
            ?.let { it as PGobject }?.value
    }

    fun jsonb(name: String) = requireNotNull(jsonbOrNull(name))
}