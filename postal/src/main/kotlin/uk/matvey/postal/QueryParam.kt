package uk.matvey.postal

import org.postgresql.util.PGobject
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.sql.Types
import java.time.Instant
import java.util.*

interface QueryParam {

    fun setValue(st: PreparedStatement, index: Int)

    class TextParam(private val value: String?) : QueryParam {

        override fun setValue(st: PreparedStatement, index: Int) {
            this.value?.let {
                st.setString(index, it)
            } ?: st.setNull(index, Types.VARCHAR)
        }
    }

    class BigintParam(private val value: Long?) : QueryParam {

        override fun setValue(st: PreparedStatement, index: Int) {
            this.value?.let {
                st.setLong(index, it)
            } ?: st.setNull(index, Types.BIGINT)
        }
    }

    class UuidParam(private val value: UUID?) : QueryParam {

        override fun setValue(st: PreparedStatement, index: Int) {
            st.setObject(index, this.value)
        }
    }

    class TimestampParam(private val value: Instant?) : QueryParam {

        override fun setValue(st: PreparedStatement, index: Int) {
            this.value?.let {
                st.setTimestamp(index, Timestamp.from(it))
            } ?: st.setNull(index, Types.TIMESTAMP)
        }
    }

    class JsonbParam(private val value: String?) : QueryParam {

        override fun setValue(st: PreparedStatement, index: Int) {
            val pgObj = PGobject()
            pgObj.type = "jsonb"
            pgObj.value = this.value
            st.setObject(index, pgObj)
        }
    }
}