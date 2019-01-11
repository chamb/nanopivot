/*
 * (C) ActiveViam 2018
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.nanopivot.cfg;

import static com.activeviam.nanopivot.cfg.datastore.DatastoreDescriptionConfig.STORE_ORDERS;
import static com.activeviam.nanopivot.cfg.datastore.DatastoreDescriptionConfig.STORE_PRODUCTS;

import java.time.LocalDate;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import com.activeviam.nanopivot.cfg.datastore.DatastoreDescriptionConfig;
import com.qfs.gui.impl.JungSchemaPrinter;
import com.qfs.store.IDatastore;
import com.qfs.store.impl.SchemaPrinter;
import com.qfs.store.transaction.ITransactionManager;
import com.qfs.util.timing.impl.StopWatch;

/**
 * Spring configuration for data sources
 * 
 * @author ActiveViam
 *
 */
public class DataLoadingConfig {

    private static final Logger LOGGER = Logger.getLogger(DataLoadingConfig.class.getSimpleName());

    @Autowired
    protected Environment env;

    @Autowired
    protected IDatastore datastore;


	/*
	 * **************************** Data loading *********************************
	 */
    @Bean
    @DependsOn(value = "startManager")
    public Void loadData() throws Exception {
    	
    	final ITransactionManager tm = datastore.getTransactionManager();
    	final int orders = tm.getMetadata().getStoreId(STORE_ORDERS);
    	final int products = tm.getMetadata().getStoreId(STORE_PRODUCTS);
    	
    	// Load data into ActivePivot
    	final long before = System.nanoTime();
    	
    	// Transaction for TV data
    	{
	    	tm.startTransaction();
	    	
	    	tm.add(products, new Object[] {"SN-KD-55AF9", "Sony OLED 4K KD-55AF9", "High Tech", "TV"});
	    	tm.add(products, new Object[] {"LG-49SK8500", "LG SUPER UHD NanoCell 49in", "High Tech", "TV"});
	
	    	tm.add(orders, new Object[] {01, LocalDate.of(2018, 12, 10), "France", "SN-KD-55AF9", 1, 2790});
	    	tm.add(orders, new Object[] {02, LocalDate.of(2018, 12, 20), "Spain",  "SN-KD-55AF9", 2, 4890});
	    	tm.add(orders, new Object[] {03, LocalDate.of(2019, 01, 10), "France", "LG-49SK8500", 1, 1499});
	    	tm.add(orders, new Object[] {04, LocalDate.of(2019, 01, 14), "Spain",  "LG-49SK8500", 3, 4190});
	    	
	    	tm.commitTransaction();
	    }
    	
    	// Transaction for computer data
    	{
	    	tm.startTransaction();
	    	
	    	tm.add(products, new Object[] {"DELL-XPS15-9570",     "Dell Laptop XPS 15-9570", "High Tech", "Computer"});
	    	tm.add(products, new Object[] {"HP-ENVY-13-AH0002NF", "HP Laptop Envy 13.3in",   "High Tech", "Computer"});
	
	    	tm.add(orders, new Object[] {11, LocalDate.of(2018, 12, 10), "Spain",  "DELL-XPS15-9570",     1, 2210});
	    	tm.add(orders, new Object[] {12, LocalDate.of(2018, 12, 14), "France", "DELL-XPS15-9570",     2, 4090});
	    	tm.add(orders, new Object[] {13, LocalDate.of(2019, 01,  8), "France", "HP-ENVY-13-AH0002NF", 1, 949});
	    	tm.add(orders, new Object[] {14, LocalDate.of(2019, 02, 02), "Spain",  "HP-ENVY-13-AH0002NF", 1, 919});
	    	
	    	tm.commitTransaction();
    	}

    	
    	final long elapsed = System.nanoTime() - before;
    	LOGGER.info("Data load completed in " + elapsed / 1000000L + "ms");
    	
    	printStoreSizes();
    	
    	return null;
    	
    }

   
	private void printStoreSizes() {

		// add some logging
		if (Boolean.parseBoolean(env.getProperty("storeStructure.display", "true"))) {
			// display the graph
			new JungSchemaPrinter(false).print("NanoPivot datastore", datastore);

			// example of printing a store content
			SchemaPrinter.printStore(datastore, DatastoreDescriptionConfig.STORE_ORDERS);
			SchemaPrinter.printStore(datastore, DatastoreDescriptionConfig.STORE_PRODUCTS);
		}

		// Print stop watch profiling
		StopWatch.get().printTimings();
		StopWatch.get().printTimingLegend();

		// print sizes
		SchemaPrinter.printStoresSizes(datastore.getHead().getSchema());
	}

}
