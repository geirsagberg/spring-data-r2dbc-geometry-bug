CREATE ALIAS IF NOT EXISTS H2GIS_SPATIAL FOR "org.h2gis.functions.factory.H2GISFunctions.load";
CALL H2GIS_SPATIAL();

INSERT INTO geometric ("id", "geometry") VALUES (1, ST_GeomFromText('POINT(0 0)', 4326));
