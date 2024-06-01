package uk.matvey.shkaf

import java.sql.ResultSet
import java.util.UUID

class RecordReader(
    private val resultSet: ResultSet,
) {
    
    fun nullableString(name: String) = resultSet.getString(name)?.nullable()
    
    fun string(name: String) = requireNotNull(nullableString(name))
    
    fun nullableUuid(name: String) = resultSet.getObject(name)?.nullable() as UUID?
    
    fun uuid(name: String) = requireNotNull(nullableUuid(name))
    
    fun nullableInstant(name: String) = resultSet.getTimestamp(name)?.nullable()?.toInstant()
    
    fun instant(name: String) = requireNotNull(nullableInstant(name))
    
    @Suppress("UNCHECKED_CAST")
    fun nullableStringList(name: String) = (resultSet.getArray(name)?.nullable()?.array as Array<String>?)?.toList()
    
    fun stringList(name: String) = requireNotNull(nullableStringList(name))
    
    private fun <T> T?.nullable() = this?.takeIf { !resultSet.wasNull() }
}
