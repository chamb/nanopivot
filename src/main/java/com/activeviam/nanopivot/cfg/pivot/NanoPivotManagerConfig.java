/*
 * (C) ActiveViam 2018
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.nanopivot.cfg.pivot;

import static com.activeviam.nanopivot.cfg.datastore.DatastoreDescriptionConfig.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.activeviam.builders.StartBuilding;
import com.activeviam.copper.builders.BuildingContext;
import com.activeviam.copper.builders.dataset.Datasets.Dataset;
import com.activeviam.copper.columns.Columns;
import com.activeviam.desc.build.ICanBuildCubeDescription;
import com.activeviam.desc.build.ICubeDescriptionBuilder.INamedCubeDescriptionBuilder;
import com.activeviam.desc.build.dimensions.ICanStartBuildingDimensions;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.server.cfg.IActivePivotManagerDescriptionConfig;
import com.quartetfs.biz.pivot.cube.hierarchy.ILevelInfo.LevelType;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import com.quartetfs.biz.pivot.definitions.IActivePivotManagerDescription;
import com.quartetfs.biz.pivot.definitions.ISelectionDescription;

/**
 * @author ActiveViam
 *
 */
@Configuration
public class NanoPivotManagerConfig implements IActivePivotManagerDescriptionConfig {
	
	/* ********** */
	/* Formatters */
	/* ********** */
	public static final String DOUBLE_FORMATTER_ONE_DECIMAL = "DOUBLE[##.#]";


	/** The datastore schema {@link IDatastoreSchemaDescription description}. */
	@Autowired
	protected IDatastoreSchemaDescription datastoreDescription;

	@Override
	@Bean
	public IActivePivotManagerDescription managerDescription() {
		
		return StartBuilding.managerDescription("NanoPivot")
				.withCatalog("NanoPivot Catalog")
				.containingAllCubes()
				.withSchema("NanoPivot Schema")
				.withSelection(createNanoPivotSchemaSelectionDescription(this.datastoreDescription))
				.withCube(createCubeDescription())
				.build();
	}
	
	/**
	 * Creates the {@link ISelectionDescription} for NanoPivot Schema.
	 * 
	 * @param datastoreDescription : The datastore description
	 * @return The created selection description
	 */
	public static ISelectionDescription createNanoPivotSchemaSelectionDescription(
			final IDatastoreSchemaDescription datastoreDescription) {
		return StartBuilding.selection(datastoreDescription)
				.fromBaseStore(STORE_ORDERS)
				.withAllReachableFields()
				.build();
	}
	
	/**
	 * Creates the cube description.
	 * @return The created cube description
	 */
	public static IActivePivotInstanceDescription createCubeDescription() {
		return configureCubeBuilder(StartBuilding.cube("NanoPivot")).build();
	}

	/**
	 * Configures the given builder in order to created the cube description.
	 *
	 * @param builder The builder to configure
	 * @return The configured builder
	 */
	public static ICanBuildCubeDescription<IActivePivotInstanceDescription> configureCubeBuilder(
			final INamedCubeDescriptionBuilder builder) {
		
		return builder
				.withDimensions(NanoPivotManagerConfig::dimensions)
				
				//Suggestion : PostProcessor definitions can be added here
				
				.withDescriptionPostProcessor(
						StartBuilding.copperCalculations()
							.withDefinition(NanoPivotManagerConfig::coPPerCalculations)
							.build()
					)
				;
	}


	/**
	 * Adds the dimensions descriptions to the input
	 * builder.
	 *
	 * @param builder The cube builder
	 * @return The builder for chained calls
	 */
	public static ICanBuildCubeDescription<IActivePivotInstanceDescription> dimensions (ICanStartBuildingDimensions builder) {
		
		return builder
				.withDimension("Product")
				.withHierarchyOfSameName()
				.withLevel(PRODUCT_NAME)
					.withHierarchy("Category")
						.withLevels(PRODUCT_CATEGORY, PRODUCT_SUBCATEGORY, PRODUCT_NAME)
						
				.withDimension("Geography")
					.withSingleLevelHierarchy(ORDER_COUNTRY)
						
				.withDimension("OrderDate")
					.withHierarchyOfSameName()
						.withLevel(ORDER_DATE)
							.withType(LevelType.TIME)
							.withFormatter("DATE[yyyy-MM-dd]");
	}

	/* ******************* */
	/* Measures definition */
	/* ******************* */
	
	/**
	 * The CoPPer calculations to add to the cube
	 * @param context The context with which to build the calculations.
	 */
	public static void coPPerCalculations(BuildingContext context) {
		NanoPivotManagerConfig.someAggregatedMeasures(context).publish();
	}

	
	/**
	 * Define some calculations using the COPPER API.
	 *
	 * @param context The CoPPer build context.
	 *
	 * @return The Dataset of the aggregated measures.
	 */		
	protected static Dataset someAggregatedMeasures(final BuildingContext context) {
		
		return context.createDatasetFromFacts()
			.agg(
				Columns.count().as("Order Count"),
				Columns.sum("sales").as("Sum of Sales"),
				Columns.avg("sales").as("Average Sales")
			);
		
	}


}
