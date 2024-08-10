package uk.matvey.begit

import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeAll
import uk.matvey.slon.PostgresTestContainer
import javax.sql.DataSource

open class TestContainerSetup {

    companion object {

        private val postgres = PostgresTestContainer()

        fun dataSource(): DataSource = postgres.dataSource()

        @BeforeAll
        @JvmStatic
        fun globalSetup() {
            postgres.start()
            val flyway = Flyway.configure()
                .dataSource(dataSource())
                .schemas("begit")
                .defaultSchema("begit")
                .createSchemas(true)
                .cleanDisabled(true)
                .load()
            flyway.migrate()
        }
    }
}