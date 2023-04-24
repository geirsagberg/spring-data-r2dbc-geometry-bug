package com.example.demo

import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query

@SpringBootTest
class DemoApplicationTests(
    @Autowired
    private val template: R2dbcEntityTemplate
) {

    @Test
    fun `can select geometry column`() = runBlocking {
        template.databaseClient.sql("""
CREATE TABLE IF NOT EXISTS geometric
(
    id       LONG PRIMARY KEY,
    geometry GEOMETRY
);

CREATE ALIAS IF NOT EXISTS H2GIS_SPATIAL FOR "org.h2gis.functions.factory.H2GISFunctions.load";
CALL H2GIS_SPATIAL();

INSERT INTO geometric (id, geometry) VALUES (1, ST_GeomFromText('POINT(0 0)', 4326));
"""
        ).fetch().rowsUpdated().awaitSingleOrNull()

        val result = template.selectOne(Query.query(Criteria.where("id").`is`(1)), GeometricEntity::class.java).awaitSingleOrNull()

        println(result)
    }
}
