package uk.matvey.slon

import uk.matvey.slon.QueryParams.setParams

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
    
    fun update(
        tableName: String,
        params: List<Pair<String, QueryParam>>,
        condition: String? = null,
        conditionParams: List<QueryParam> = listOf(),
    ) {
        val setting = params.joinToString { "${it.first} = ?" }
        val query = "UPDATE $tableName SET $setting" + (condition?.let { " $it" } ?: "")
        dataAccess.withStatement(query) { statement ->
            setParams(statement, params.map { it.second } + conditionParams)
            statement.executeUpdate()
        }
    }
    
    fun delete(
        tableName: String,
        condition: String? = null,
        conditionParams: List<QueryParam> = listOf()
    ) {
        val query = "DELETE FROM $tableName" + (condition?.let { " $it" } ?: "")
        dataAccess.withStatement(query) { statement ->
            setParams(statement, conditionParams)
            statement.executeUpdate()
        }
    }
    
    fun <T> select(query: String, params: List<QueryParam>, read: (RecordReader) -> T): List<T> {
        return dataAccess.withStatement(query) { statement ->
            setParams(statement, params)
            statement.executeQuery().use { rs ->
                val list = mutableListOf<T>()
                while (rs.next()) {
                    list += read(RecordReader(rs))
                }
                list
            }
        }
    }
    
    fun <T> selectSingle(query: String, params: List<QueryParam>, read: (RecordReader) -> T): T {
        return select(query, params, read).single()
    }
}
