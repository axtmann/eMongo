package org.eclipselabs.emongo.components.junit

import java.util.HashMap
import java.util.Map
import org.eclipselabs.emongo.components.DatabaseAuthenticationProviderComponent
import org.eclipselabs.emongo.components.DatabaseAuthenticationProvider

/*
 * The **DatabaseAuthenticationProvider** provides MongoDB credentials
 * as a configured OSGi service.  There are three service properties
 * that must be read by the service: uri, user, and password.
 */
describe DatabaseAuthenticationProviderComponent
{
	Map<String, Object> properties = new HashMap<String, Object>()

	before
	{
		properties.put(DatabaseAuthenticationProvider::PROP_URI, "mongodb://localhost/db")
		properties.put(DatabaseAuthenticationProvider::PROP_USER, "user")
		properties.put(DatabaseAuthenticationProvider::PROP_PASSWORD, "password")
	}
	
	fact "configuration parameters are available through the API"
	{
		subject.configure(properties)
		subject.URI should be "mongodb://localhost/db"
		subject.user should be "user"
		subject.password should be "password"
	}
	
	fact "configure throws exception when URI is missing the database name"
	{
		properties.put(DatabaseAuthenticationProvider::PROP_URI, "mongodb://localhost/")
		subject.configure(properties) throws IllegalStateException
	}

	fact "configure throws exception when URI is missing the database segment"
	{
		properties.put(DatabaseAuthenticationProvider::PROP_URI, "mongodb://localhost")
		subject.configure(properties) throws IllegalStateException
	}

	fact "configure throws exception when URI is missing"
	{
		properties.put(DatabaseAuthenticationProvider::PROP_URI, null)
		subject.configure(properties) throws IllegalStateException
	}

	fact "configure throws exception when user is missing"
	{
		properties.put(DatabaseAuthenticationProvider::PROP_USER, null)
		subject.configure(properties) throws IllegalStateException
	}

	fact "configure throws exception when password is missing"
	{
		properties.put(DatabaseAuthenticationProvider::PROP_PASSWORD, null)
		subject.configure(properties) throws IllegalStateException
	}
}