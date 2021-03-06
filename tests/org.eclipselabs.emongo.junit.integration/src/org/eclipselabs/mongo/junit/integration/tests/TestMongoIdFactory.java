/*******************************************************************************
 * Copyright (c) 2012 Bryan Hunt.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bryan Hunt - initial API and implementation
 *******************************************************************************/

package org.eclipselabs.mongo.junit.integration.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.eclipselabs.emongo.MongoIdFactory;
import org.eclipselabs.emongo.junit.util.MongoDatabase;
import org.eclipselabs.eunit.junit.utils.ServiceLocator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * @author bhunt
 * 
 */
public class TestMongoIdFactory
{
	@Rule
	public MongoDatabase database = new MongoDatabase();

	@Rule
	public ServiceLocator<MongoIdFactory> mongoIdFactoryLocator = new ServiceLocator<MongoIdFactory>(MongoIdFactory.class);

	private MongoIdFactory mongoId;

	@Before
	public void setUp()
	{
		mongoId = mongoIdFactoryLocator.getService();
	}

	@Test
	public void testGetNextId() throws IOException
	{
		assertThat(mongoId.getNextId(), is("1"));
		DBCollection collection = database.getMongoDB().getCollection("junit_id");
		DBObject result = collection.findOne(new BasicDBObject("_id", "0"));
		assertThat((Long) result.get("_lastId"), is(1L));
	}
}
