/*******************************************************************************
 * Copyright (c) 2011 Bryan Hunt.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bryan Hunt - initial API and implementation
 *******************************************************************************/

package org.eclipselabs.emongo.junit.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.emf.common.util.URI;
import org.eclipselabs.emongo.DatabaseLocator;
import org.eclipselabs.emongo.junit.util.bundle.Activator;
import org.junit.rules.ExternalResource;
import org.osgi.util.tracker.ServiceTracker;

import com.mongodb.DB;

/**
 * This class is intended to be used as a JUnit @Rule. It will verify that
 * the IMongoDB service exists, and it will delete all collections in the specified
 * database after each test runs.
 * 
 * Example usage:
 * 
 * @Rule
 *       public MongoDatabase database = new MongoDatabase();
 * 
 * @author bhunt
 * 
 */
public class MongoDatabase extends ExternalResource
{
	/**
	 * Connects to the "junit" database on localhost:27017
	 */
	public MongoDatabase()
	{
		this("junit");
	}

	public MongoDatabase(DatabaseLocator dbLocator)
	{
		this("junit", dbLocator);
	}

	/**
	 * Connects to the specified database on localhost:27017
	 * 
	 * @param database the name of the database to use for unit testing
	 */
	public MongoDatabase(String database)
	{
		this("localhost", database);
	}

	public MongoDatabase(String database, DatabaseLocator dbLocator)
	{
		this("localhost", database, dbLocator);
	}

	/**
	 * Connects to the specified database on the specified host using the default port 27017
	 * 
	 * @param hostname the host running MongoDB
	 * @param database the name of the database to use for unit testing
	 */
	public MongoDatabase(String hostname, String database)
	{
		this(hostname, 27017, database);
	}

	public MongoDatabase(String hostname, String database, DatabaseLocator dbLocator)
	{
		this(hostname, 27017, database, dbLocator);
	}

	/**
	 * Connects to the specified database on the specified host and port
	 * 
	 * @param hostname the host running MongoDB
	 * @param port the port MongoDB is listening on
	 * @param database the name of the database to use for unit testing
	 */
	public MongoDatabase(String hostname, int port, String database)
	{
		this(hostname, port, database, null);
	}

	public MongoDatabase(String hostname, int port, String database, DatabaseLocator dbLocator)
	{
		baseURI = URI.createURI("mongodb://" + hostname + (port == 27017 ? "" : ":" + port) + "/" + database);
		databaseLocator = dbLocator;
	}

	/**
	 * Creates a URI for accessing a collection using the database connection as a base URI
	 * and appending the collection as the next segment.
	 * 
	 * @param collection the collection name
	 * @return a URI referencing a MongoDB collection
	 */
	public URI createCollectionURI(String collection)
	{
		return baseURI.appendSegments(new String[] { collection, "" });
	}

	/**
	 * Creates a URI for accessing an object using the database connection as a base URI
	 * and appending the collection and object id as the next segments.
	 * 
	 * @param collection
	 * @param id
	 * @return
	 */
	public URI createObjectURI(String collection, String id)
	{
		return baseURI.appendSegments(new String[] { collection, id });
	}

	/**
	 * Provides access to the MongoDB driver for the database this instance is connected to.
	 * 
	 * @return the low-level MongoDB driver interface
	 */
	public DB getMongoDB()
	{
		return db;
	}

	/**
	 * Provides access to the MongoLocatorService OSGi service
	 * 
	 * @return the MongoDB OSGi service
	 */
	public DatabaseLocator getMongoDBService()
	{
		return databaseLocator;
	}

	@Override
	protected void before() throws Throwable
	{
		super.before();

		if (databaseLocator == null)
		{
			ServiceTracker<DatabaseLocator, DatabaseLocator> mongoServiceTracker = new ServiceTracker<DatabaseLocator, DatabaseLocator>(Activator.getBundleContext(), DatabaseLocator.class, null);
			mongoServiceTracker.open();
			databaseLocator = mongoServiceTracker.waitForService(1000);
		}

		assertThat("Failed to get the IDatabaseLocator service", databaseLocator, is(notNullValue()));
		db = databaseLocator.getDatabase(baseURI.toString());
		assertThat("No database configured for: " + baseURI.toString(), db, is(notNullValue()));
	}

	@Override
	protected void after()
	{
		if (databaseLocator != null)
		{
			try
			{

				for (String collectionName : db.getCollectionNames())
				{
					if (!"system.indexes".equals(collectionName))
						db.getCollection(collectionName).drop();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		super.after();
	}

	private DatabaseLocator databaseLocator;
	private DB db;
	private URI baseURI;
}
