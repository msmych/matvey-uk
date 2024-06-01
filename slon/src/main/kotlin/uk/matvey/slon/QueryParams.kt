package uk.matvey.slon

import org.postgresql.util.PGobject
import uk.matvey.slon.QueryParam.Type.JSONB
import uk.matvey.slon.QueryParam.Type.TEXT
import uk.matvey.slon.QueryParam.Type.TEXT_ARRAY
import uk.matvey.slon.QueryParam.Type.TIMESTAMP
import uk.matvey.slon.QueryParam.Type.UUID
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.sql.Types.NULL

object QueryParams {
    
    @Suppress("NAME_SHADOWING")
    fun setParams(statement: PreparedStatement, params: List<QueryParam>) {
        params.forEachIndexed { index, param ->
            val index = index + 1
            if (param.value == null) {
                statement.setNull(index, NULL)
            } else {
                when (param.type) {
                    TEXT -> statement.setString(index, param.value as String)
                    UUID -> statement.setObject(index, param.value)
                    TIMESTAMP -> statement.setTimestamp(index, param.value as Timestamp)
                    JSONB -> {
                        val pgObj = PGobject()
                        pgObj.type = "jsonb"
                        pgObj.value = param.value as String
                        statement.setObject(index, pgObj)
                    }
                    
                    TEXT_ARRAY -> {
                        val array =
                            statement.connection.createArrayOf("text", (param.value as Collection<*>).toTypedArray())
                        statement.setArray(index, array)
                    }
                }
            }
        }
    }
}
