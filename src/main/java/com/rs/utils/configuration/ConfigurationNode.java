package com.rs.utils.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a configuration sub-node
 * 
 * @author Nikki
 * 
 */
public class ConfigurationNode {

	/**
	 * A map of the children of this node
	 */
	private Map<String, Object> children = new HashMap<String, Object>();

	/**
	 * Set a value
	 * 
	 * @param key
	 *            The key
	 * @param value
	 *            The value
	 */
	public void set(String key, Object value) {
		children.put(key, value);
	}

	/**
	 * Check if the map contains a key
	 * 
	 * @param key
	 *            The key to check
	 * @return true, if found
	 */
	public boolean has(String key) {
		return children.containsKey(key);
	}

	/**
	 * Get a sub-node for the specified name
	 * 
	 * @param name
	 *            The name
	 * @return The node
	 */
	public ConfigurationNode nodeFor(String name) {
		if (children.containsKey(name)) {
			Object value = children.get(name);
			if (value.getClass() != this.getClass()) {
				throw new ConfigurationException("Invalid node " + name + "!");
			}
			return (ConfigurationNode) value;
		}
		return null;
	}

	/**
	 * List the sub values
	 * 
	 * @return A list of all sub values
	 */
	public String listChildren() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (Entry<String, Object> entry : children.entrySet()) {
			builder.append(entry.getKey()).append(" => ");
			if (entry.getValue() instanceof ConfigurationNode) {
				builder.append(((ConfigurationNode) entry.getValue()).listChildren());
			} else {
				builder.append(entry.getValue());
			}
			builder.append(", ");
		}
		if (builder.length() > 2) {
			builder.deleteCharAt(builder.length() - 1);
			builder.deleteCharAt(builder.length() - 1);
		}
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Get an Object value from the map
	 * 
	 * @param key
	 *            The key
	 * @return The value
	 */
	public Object get(String key) {
		return children.get(key);
	}

	/**
	 * Get a string value from this node
	 * 
	 * @param string
	 *            The key of the value
	 * @return The string, or null
	 */
	public String getString(String string) {
		Object value = get(string);
		if (value instanceof String) {
			return (String) value;
		}
		return "null";
	}

	/**
	 * Get an integer value from this node
	 * 
	 * @param key
	 *            The key of the value
	 * @return The value, parsed as an integer
	 */
	public int getInteger(String key) {
		return Integer.parseInt(getString(key));
	}

	/**
	 * Get a boolean value from this node
	 * 
	 * @param key
	 *            The key of the value
	 * @return The value, parsed as a boolean
	 */
	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(getString(key));
	}

	/**
	 * Get a list of the children
	 * 
	 * @return The map
	 */
	public Map<String, Object> getChildren() {
		return children;
	}
}