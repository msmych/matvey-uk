package uk.matvey.begit

import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.PostgreSQLContainer
import javax.sql.DataSource

open class TestContainerSetup {

    companion object {

        private val postgres = PostgreSQLContainer("postgres")
        private lateinit var dataSource: DataSource

        fun dataSource(): DataSource = dataSource

        @BeforeAll
        @JvmStatic
        fun globalSetup() {
            postgres.start()
            dataSource = HikariDataSource().apply {
                jdbcUrl = postgres.jdbcUrl
                username = postgres.username
                password = postgres.password
            }
            val flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas("begit")
                .defaultSchema("begit")
                .createSchemas(true)
                .cleanDisabled(true)
                .load()
            flyway.migrate()
        }
    }
}