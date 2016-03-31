SET DEFINE OFF;


create table buildings (building_id varchar(5), building_name varchar(30)  primary key,vertices integer, build_location sdo_geometry);

create table fire_buildings(fire_build_name varchar2(30), foreign key(fire_build_name) references buildings(building_name));

create table fire_hydrants(hydrant_id varchar2(5) primary key, hydrant_location sdo_geometry);


INSERT INTO user_sdo_geom_metadata VALUES ('buildings','build_location', SDO_DIM_ARRAY(
    SDO_DIM_ELEMENT('X', 0, 820, 0.005),
    SDO_DIM_ELEMENT('Y', 0, 580, 0.005)),NULL);

INSERT INTO user_sdo_geom_metadata VALUES ('fire_hydrants','hydrant_location', SDO_DIM_ARRAY(
    SDO_DIM_ELEMENT('X', 0, 820, 0.005),
    SDO_DIM_ELEMENT('Y', 0, 580, 0.005)),NULL);


CREATE INDEX building_idx
   ON buildings(build_location)
   INDEXTYPE IS MDSYS.SPATIAL_INDEX;


CREATE INDEX hydrant_idx
   ON fire_hydrants(hydrant_location)
   INDEXTYPE IS MDSYS.SPATIAL_INDEX;




