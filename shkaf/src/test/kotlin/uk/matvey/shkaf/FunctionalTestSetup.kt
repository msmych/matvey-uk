package uk.matvey.shkaf

import com.zaxxer.hikari.HikariDataSource
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.PostgreSQLContainer
import javax.sql.DataSource

open class FunctionalTestSetup {
    
    fun dataSource(): DataSource = dataSource
    
    companion object {
        
        private val postgres = PostgreSQLContainer("postgres:15-alpine")
        private lateinit var dataSource: DataSource
        
        @BeforeAll
        @JvmStatic
        fun setup() {
            postgres.start()
            dataSource = HikariDataSource().apply {
                jdbcUrl = postgres.jdbcUrl
                username = postgres.username
                password = postgres.password
            }
        }
        
        @AfterAll
        @JvmStatic
        fun teardown() {
            postgres.stop()
        }
    }
}
