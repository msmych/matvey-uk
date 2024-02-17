package uk.matvey.postal

import java.sql.Connection
import java.sql.PreparedStatement
import javax.sql.DataSource

class Repo(
    private val ds: DataSource,
) {
    
    fun insert(tableName: String, params: QueryParams) {
        withConnection { conn ->
            val paramNames = params.paramNames()
            val columnNames = paramNames.joinToString(",")
            val questions = generateSequence { "?" }.take(paramNames.size).joinToString(",")
            val query = "insert into $tableName ($columnNames) values ($questions)"
            withStatement(conn, query) { st ->
                params.setValues(st)
                st.executeUpdate()
            }
        }
    }
    
    fun update(
        tableName: String,
        updateParams: QueryParams,
        condition: String,
        conditionParams: QueryParams
    ) {
        withConnection { conn ->
            val setValues = updateParams.paramNames().joinToString(",") { "$it=?" }
            val query = "update $tableName set $setValues where $condition"
            withStatement(conn, query) { st ->
                val index = updateParams.setValues(st)
                conditionParams.setValues(st, index)
                st.executeUpdate()
            }
        }
    }
    
    fun <T> select(query: String, params: QueryParams, mapper: (ResultExtractor) -> T): List<T> {
        return withConnection { conn ->
            withStatement(conn, query) { st ->
                params.setValues(st)
                val resultSet = st.executeQuery()
                val list = mutableListOf<T>()
                while (resultSet.next()) {
                    list += mapper(ResultExtractor(resultSet))
                }
                list
            }
        }
    }
    
    fun delete(tableName: String, condition: String, params: QueryParams) {
        withConnection { conn ->
            val query = "delete from $tableName where $condition"
            withStatement(conn, query) { st ->
                params.setValues(st)
                st.executeUpdate()
            }
        }
    }
    
    fun <T> withStatement(conn: Connection, query: String, block: (PreparedStatement) -> T): T {
        return conn.prepareStatement(query).use(block)
    }
    
    fun <T> withConnection(block: (Connection) -> T): T {
        return ds.connection.use(block)
    }
}
