package com.example.demo

import org.locationtech.jts.geom.Geometry
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("geometric")
class GeometricEntity(
    @Id
    val id: Long,
    val geometry: Geometry
)

