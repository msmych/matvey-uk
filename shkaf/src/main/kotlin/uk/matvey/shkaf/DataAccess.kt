package uk.matvey.shkaf

import java.sql.Connection
import java.sql.PreparedStatement
import javax.sql.DataSource

class DataAccess(
    private val dataSource: DataSource,
) {
    
    fun update(query: String) {
        withStatement(query, PreparedStatement::executeUpdate)
    }
    
    fun <T> withStatement(query: String, block: (PreparedStatement) -> T): T {
        return withConnection { connection -> connection.prepareStatement(query).use(block) }
    }
    
    fun <T> withConnection(block: (Connection) -> T): T {
        return dataSource.connection.use(block)
    }
}
