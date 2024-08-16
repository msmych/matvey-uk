package uk.matvey.corsa

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import uk.matvey.slon.PostgresTestContainer

open class TestContainersSetup {

    companion object {

        private val postgres = PostgresTestContainer()

        fun dataSource() = postgres.dataSource()

        @BeforeAll
        @JvmStatic
        fun globalSetup() {
            postgres.start()
            migrate(dataSource(), clean = true)
        }

        @AfterAll
        @JvmStatic
        fun globalTeardown() {
            postgres.stop()
        }
    }
}
