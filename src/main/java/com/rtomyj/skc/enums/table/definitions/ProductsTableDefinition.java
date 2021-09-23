package com.rtomyj.skc.enums.table.definitions;

public enum ProductsTableDefinition
{

    PRODUCT_ID("product_id")
    , PRODUCT_LOCALE("product_locale")
    , PRODUCT_NAME("product_name")
    , PRODUCT_RELEASE_DATE("product_release_date")
    , PRODUCT_TYPE("product_type")
    , PRODUCT_SUB_TYPE("product_sub_type");


    ProductsTableDefinition(final String columnName) { this.columnName = columnName; }


    private final String columnName;


    @Override
    public String toString() {
        return columnName;
    }

}