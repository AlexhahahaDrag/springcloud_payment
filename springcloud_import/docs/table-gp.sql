-- dataworks_sql.sql_info definition

-- Drop table

-- DROP TABLE dataworks_sql.sql_info;

CREATE TABLE dataworks_sql.sql_info (
                                        id int8 NOT NULL,
                                        table_name_cn varchar NULL,
                                        table_name varchar NULL,
                                        ods_sql varchar NULL,
                                        dwd_zipper_table_name varchar NULL,
                                        dwd_sql_zipper varchar NULL,
                                        dwd_add_table_name varchar NULL,
                                        dwd_sql_add varchar NULL,
                                        ods_to_dwd_init_sql varchar NULL,
                                        ods_to_dwd_sql varchar NULL,
                                        ods_sql_mysql varchar NULL,
                                        dwd_sql_zipper_mysql varchar NULL,
                                        dwd_sql_add_mysql varchar NULL,
                                        sys_code varchar NULL,
                                        belong_to varchar NULL,
                                        ods_test_sql varchar NULL,
                                        sql_zipper_greenplum_name varchar NULL,
                                        sql_zipper_greenplum varchar NULL
);