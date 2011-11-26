package com.intersystems.globals.hospmon.persistence;

import static org.mockito.Mockito.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.intersys.globals.Connection;
import com.intersys.globals.NodeReference;

public class TestContext {
	
	/** The mock connection set in the tests */
	private Connection connection;
	
	@Before
	public void setup() {
		connection = mock(Connection.class);
		Context context = Context.getContext();
		context.init(connection);
	}

	@Test
	public void testAverage() {
		NodeReference node = mock(NodeReference.class);
		
		when(node.exists("test")).thenReturn(true);
		when(node.getInt("test")).thenReturn(10);
		
		int avg = Context.updateAverage(node, 20, 5, "test");
		
		Assert.assertEquals(11, avg);
		verify(node).set(11, "test");
	}
	
	@Test
	public void testIncrement() {
		NodeReference node = mock(NodeReference.class);
		
		when(node.exists("test")).thenReturn(true);
		when(node.getInt("test")).thenReturn(10);
		
		int total = Context.incrementTotal(node, "test");
		
		Assert.assertEquals(11, total);
		verify(node).set(11, "test");
	}
	
	@Test
	public void testUpdateTotal() {
		NodeReference node = mock(NodeReference.class);
		
		when(node.exists("test")).thenReturn(true);
		when(node.getInt("test")).thenReturn(90);
		
		int total = Context.updateTotal(node, 10, "test");
		
		Assert.assertEquals(100, total);
		verify(node).set(100, "test");
	}
}
