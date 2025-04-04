package edu.jsu.mcis.cs310.tas_sp25.dao;

import java.io.*;
import java.util.Properties;

/**
 * <p>The {@code DAOProperties} class loads configuration values from the
 * <code>dao.properties</code> file for use in setting up database connections.</p>
 * 
 * <p>It allows retrieval of properties using a prefix (e.g., "tas.jdbc") 
 * to support multiple connection profiles. Property keys like <code>url</code>, 
 * <code>username</code>, and <code>password</code> are expected to be defined in the file.</p>
 */
public class DAOProperties {
    /** Name of the properties file containing database configuration. */
    private static final String PROPERTIES_FILE = "dao.properties";
    /** Static instance of the loaded properties. */
    private static final Properties PROPERTIES = new Properties();

    /** Prefix used to namespace property keys (e.g., "tas.jdbc"). */
    private final String prefix;

    static {

        try {

            InputStream file = DAOProperties.class.getResourceAsStream(PROPERTIES_FILE);
            PROPERTIES.load(file);

        } catch (IOException e) {
            throw new DAOException(e.getMessage());
        }

    }

    /**
     * Constructs a {@code DAOProperties} object with the given prefix.
     * 
     * @param prefix the prefix to use when looking up property values
     */
    public DAOProperties(String prefix) {

        this.prefix = prefix;

    }

    /**
     * Retrieves the value of a given property key using the object's prefix.
     * 
     * @param key the base key name (e.g., "url", "username")
     * @return the property value or {@code null} if not found or empty
     */
    public String getProperty(String key) {

        String fullKey = prefix + "." + key;
        String property = PROPERTIES.getProperty(fullKey);

        if (property == null || property.trim().length() == 0) {
            property = null;
        }

        return property;

    }

}
