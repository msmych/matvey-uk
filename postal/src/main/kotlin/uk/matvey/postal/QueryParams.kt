package uk.matvey.postal

import java.sql.PreparedStatement

class QueryParams {
    
    private val params = LinkedHashMap<String, QueryParam>()
    
    fun add(name: String, param: QueryParam): QueryParams {
        this.params[name] = param
        return this
    }
    
    fun paramNames(): List<String> {
        return this.params.keys.toList()
    }
    
    fun setValues(st: PreparedStatement, startIndex: Int): Int {
        var index = startIndex
        this.params.values.forEach { param ->
            param.setValue(st, ++index)
        }
        return index
    }
    
    fun setValues(st: PreparedStatement): Int {
        return setValues(st, 0)
    }
}
