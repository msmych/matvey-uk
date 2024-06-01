package uk.matvey.shkaf

import uk.matvey.shkaf.QueryParams.setParams

class Repo(
    private val dataAccess: DataAccess,
) {
    
    fun insert(tableName: String, vararg params: Pair<String, QueryParam>) {
        val columnNames = params.joinToString { it.first }
        val questions = generateSequence { "?" }.take(params.size).joinToString()
        val query = "INSERT INTO $tableName ($columnNames) VALUES ($questions)"
        dataAccess.withStatement(query) { statement ->
            setParams(statement, params.map { it.second })
            statement.executeUpdate()
        }
    }
    
    fun <T> select(query: String, vararg params: QueryParam, read: (RecordReader) -> T): T {
        return dataAccess.withStatement(query) { statement ->
            setParams(statement, params.toList())
            statement.executeQuery().use { rs ->
                rs.next()
                read(RecordReader(rs))
            }
        }
    }
}
