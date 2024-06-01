package uk.matvey.shkaf

import uk.matvey.shkaf.QueryParam.Type.JSONB
import uk.matvey.shkaf.QueryParam.Type.TEXT
import uk.matvey.shkaf.QueryParam.Type.TEXT_ARRAY
import uk.matvey.shkaf.QueryParam.Type.TIMESTAMP
import uk.matvey.shkaf.QueryParam.Type.UUID
import java.sql.Timestamp
import java.time.Instant

class QueryParam(
    val type: Type,
    val value: Any?,
) {
    
    enum class Type {
        TEXT,
        UUID,
        TIMESTAMP,
        JSONB,
        TEXT_ARRAY,
    }
    
    companion object {
        
        fun text(value: String?) = QueryParam(TEXT, value)
        
        fun uuid(value: java.util.UUID?) = QueryParam(UUID, value)
        
        fun timestamp(value: Instant?) = QueryParam(TIMESTAMP, value?.let(Timestamp::from))
        
        fun jsonb(value: String?) = QueryParam(JSONB, value)
        
        fun textArray(value: Collection<String>?) = QueryParam(TEXT_ARRAY, value)
    }
}
