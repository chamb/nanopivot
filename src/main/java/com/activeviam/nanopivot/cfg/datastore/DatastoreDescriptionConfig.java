/*
 * (C) ActiveViam 2018
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.nanopivot.cfg.datastore;

import static com.qfs.literal.ILiteralType.DOUBLE;
import static com.qfs.literal.ILiteralType.INT;
import static com.qfs.literal.ILiteralType.STRING;

import java.util.Collection;
import java.util.LinkedList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.desc.impl.ReferenceDescription;
import com.qfs.desc.impl.StoreDescriptionBuilder;
import com.qfs.server.cfg.IDatastoreDescriptionConfig;

/**
 * Spring configuration file that exposes the datastore
 * {@link IDatastoreSchemaDescription description}.
 *
 * @author ActiveViam
 *
 */
@Configuration
public class DatastoreDescriptionConfig implements IDatastoreDescriptionConfig {
	
	
	/*********************** Stores names **********************/
	public static final String STORE_ORDERS = "Orders";
	public static final String STORE_PRODUCTS = "Products";
	
	/********************* Store fields ***********************/
	public static final String ORDER_ID = "id";
	public static final String ORDER_DATE = "date";
	public static final String ORDER_COUNTRY = "country";
	public static final String ORDER_PRODUCT_ID = "productId";
	public static final String ORDER_QTY = "quantity";
	public static final String ORDER_SALES = "sales";
	
	public static final String PRODUCT_ID = "id";
	public static final String PRODUCT_NAME = "date";
	public static final String PRODUCT_CATEGORY = "category";
	public static final String PRODUCT_SUBCATEGORY = "subCategory";
	
	public static final String ORDER_TO_PRODUCT = "orderToProduct";
	

	/******************** Formatters ***************************/
	public static final String DATE_FORMAT = "localDate[yyyy-MM-dd]";

	
	@Bean
	public IStoreDescription orders() {
		return new StoreDescriptionBuilder().withStoreName(STORE_ORDERS)
				.withField(ORDER_ID, INT).asKeyField()
				.withField(ORDER_DATE, DATE_FORMAT)
				.withField(ORDER_COUNTRY, STRING)
				.withField(ORDER_PRODUCT_ID, STRING)
				.withField(ORDER_QTY, INT)
				.withField(ORDER_SALES, DOUBLE)
				.build();
	}
	
	@Bean
	public IStoreDescription products() {
		return new StoreDescriptionBuilder().withStoreName(STORE_PRODUCTS)
				.withField(PRODUCT_ID, STRING).asKeyField()
				.withField(PRODUCT_NAME, STRING)
				.withField(PRODUCT_CATEGORY, STRING)
				.withField(PRODUCT_SUBCATEGORY, STRING)
				.build();
	}
	
	@Bean
	public Collection<IReferenceDescription> references(){
		final Collection<IReferenceDescription> references = new LinkedList<>();
		references.add(ReferenceDescription.builder()
				.fromStore(STORE_ORDERS)
				.toStore(STORE_PRODUCTS)
				.withName(ORDER_TO_PRODUCT)
				.withMapping(ORDER_PRODUCT_ID, PRODUCT_ID)
				.build());
		return references;
	} 
	
	/**
	 *
	 * Provide the schema description of the datastore.
	 * <p>
	 * It is based on the descriptions of the stores in the datastore, the descriptions of the
	 * references between those stores, and the optimizations and constraints set on the schema.
	 *
	 * @return schema description
	 */
	@Override
	@Bean
	public IDatastoreSchemaDescription schemaDescription() {
		final Collection<IStoreDescription> stores = new LinkedList<>();
		stores.add(orders());
		stores.add(products());
		return new DatastoreSchemaDescription(stores, references());
	}

}
