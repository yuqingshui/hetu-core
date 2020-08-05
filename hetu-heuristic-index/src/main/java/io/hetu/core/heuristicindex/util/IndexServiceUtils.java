/*
 * Copyright (C) 2018-2020. Huawei Technologies Co., Ltd. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.hetu.core.heuristicindex.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Util class for creating external index.
 */
public class IndexServiceUtils
{
    /**
     * Error message for an invalid table name
     */
    private static final String INVALID_TABLE_NAME_ERR_MSG =
            "fully qualified table name is invalid, expected 'catalog.schema.table'";

    /**
     * there are minimum three parts in "catalog.schema.table"1
     */
    private static final int MINIMUM_FULLY_QUALIFIED_TABLE_FORMAT_PARTS = 3;

    private static final int DATABASE_NAME_OFFSET = 2;

    private static final int TABLE_NAME_OFFSET = 1;

    private IndexServiceUtils()
    {
    }

    /**
     * format a string into path format, add file separator if it's missing
     *
     * @param path path need to be formatted
     * @return formatted path
     */
    public static String formatPathAsFolder(String path)
    {
        if (!path.endsWith(File.separator)) {
            return path + File.separator;
        }
        return path;
    }

    /**
     * check if a file with specific file path exist
     *
     * @param file file need to be checked
     */
    public static void isFileExisting(File file) throws IOException
    {
        checkArgument(file.exists(), file.getCanonicalPath() + " not found");
    }

    /**
     * load properties from a file object
     *
     * @param propertyFile property file
     * @return Property object which holds all properties
     * @throws IOException when property file does NOT exist
     */
    public static Properties loadProperties(File propertyFile) throws IOException
    {
        try (InputStream is = new FileInputStream(propertyFile)) {
            Properties properties = new Properties();
            properties.load(is);
            return properties;
        }
    }

    /**
     * get files path with a specific suffix from a path array
     *
     * @param paths  paths array
     * @param suffix specific suffix
     * @return first path with specific suffix from that array or null if nothing found
     */
    public static String getPath(String[] paths, String suffix)
    {
        for (String path : paths) {
            if (path.endsWith(suffix)) {
                return path;
            }
        }
        return null;
    }

    /**
     * split the fully qualified table name into three components
     * [catalog, schema, table]
     *
     * @param fullyQualifiedTableName table name in the form "catalog.schema.table"
     * @return a string array of size 3 containing the valid catalogName, databaseName, and tableName in sequence
     */
    public static String[] getTableParts(String fullyQualifiedTableName)
    {
        String[] parts = fullyQualifiedTableName.split("\\.");

        checkArgument(parts.length >= MINIMUM_FULLY_QUALIFIED_TABLE_FORMAT_PARTS,
                INVALID_TABLE_NAME_ERR_MSG);

        String databaseName = parts[parts.length - DATABASE_NAME_OFFSET].trim();
        checkArgument(!databaseName.isEmpty(), INVALID_TABLE_NAME_ERR_MSG);

        String catalogName = fullyQualifiedTableName
                .substring(0, fullyQualifiedTableName.indexOf(databaseName) - 1).trim();
        checkArgument(!catalogName.isEmpty(), INVALID_TABLE_NAME_ERR_MSG);

        String tableName = parts[parts.length - TABLE_NAME_OFFSET];
        checkArgument(!catalogName.isEmpty(), INVALID_TABLE_NAME_ERR_MSG);

        return new String[]{catalogName, databaseName, tableName};
    }

    /**
     * Returns the subset of properties that start with the prefix, prefix is removed
     *
     * @param properties <code>Properties</code> object to extract properties with
     * @param prefix A String prefix of some property key
     * @return A new <code>Properties</code> object with all property key starting with the <code>prefix</code>
     * given and is present in <code>properties</code>,
     * and the value of each key is exactly the same as it is in <code>properties</code>
     */
    public static Properties getPropertiesSubset(Properties properties, String prefix)
    {
        Properties subsetProps = new Properties();
        properties.stringPropertyNames().forEach(k -> {
            if (k.startsWith(prefix)) {
                subsetProps.put(k.substring(k.indexOf(prefix) + prefix.length()), properties.getProperty(k));
            }
        });

        return subsetProps;
    }
}
