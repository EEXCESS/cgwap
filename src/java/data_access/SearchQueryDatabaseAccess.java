package cgwap.data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import cgwap.entities.Round;
import cgwap.entities.SearchQuery;
import cgwap.util.database.ConnectionPool;
import cgwap.util.exception_handler.ApplicationException;

/**
 * This database access class handles communication with the search query Table.
 * 
 */
public class SearchQueryDatabaseAccess {

    /**
     * The table's name.
     */
    protected static final String TABLE = "queries";

    /**
     * The column name for the associated result.
     */
    public static final String COL_ROUND_ID = "round_id";

    /**
     * The column name for the query text.
     */
    protected static final String COL_QUERY = "query";

    /**
     * The column name for the timestamp i.e. the point in time where
     * the query has been entered.
     */
    protected static final String COL_QUERY_TIMESTAMP = "query_timestamp";

    /**
     * The column name for the boolean indicating whether a filter has been
     * used.
     */
    protected static final String COL_FILTER_USED = "filter_used";

    /**
     * The column name for the language filter.
     */
    protected static final String COL_FILTER_LANGUAGE = "filter_language";

    /**
     * The column name for the provider filter.
     */
    protected static final String COL_FILTER_PROVIDER = "filter_provider";

    /**
     * The column name for the type filter.
     */
    protected static final String COL_FILTER_TYPE = "filter_type";

    // *************************************************
    // FETCH
    // *************************************************
    /**
     * Returns a List of all Instances in storage.
     * 
     * @return a List of Instances
     * @throws ApplicationException
     */
    public static List<SearchQuery> fetch() throws ApplicationException {
        List<SearchQuery> result = null;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = SearchQueryDatabaseAccess.fetch(connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'FETCH' statement on the database table to return a List of
     * all questions.
     * 
     * @param connection
     *            the database connection to perform the statement on
     * @return a List of Instances
     * @throws ApplicationException
     */
    protected static List<SearchQuery> fetch(Connection connection)
            throws ApplicationException {

        List<SearchQuery> result = new LinkedList<>();

        StringBuilder sqlQuery = new StringBuilder("");
        sqlQuery.append("SELECT * FROM " + TABLE);

        PreparedStatement statement = null;
        try {
            try {
                statement = connection.prepareStatement(sqlQuery.toString());

                ResultSet resultSet = statement.executeQuery();
                result = convertToInstances(resultSet);
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw new ApplicationException("Failed to fetch", e);
        }

        return result;
    }

    /**
     * Returns a List of all Instances in storage.
     * 
     * @return a List of Instances
     * @throws ApplicationException
     */
    public static List<SearchQuery> fetch(Round round) throws ApplicationException {
        List<SearchQuery> result = null;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = SearchQueryDatabaseAccess.fetch(round, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'FETCH' statement on the database table to return a List of
     * all questions.
     * 
     * @param connection
     *            the database connection to perform the statement on
     * @return a List of Instances
     * @throws ApplicationException
     */
    protected static List<SearchQuery> fetch(Round round, Connection connection)
            throws ApplicationException {

        List<SearchQuery> result = new LinkedList<>();

        StringBuilder sqlQuery = new StringBuilder("");
        sqlQuery.append("SELECT * FROM " + TABLE);
        sqlQuery.append(" WHERE " + COL_ROUND_ID);
        sqlQuery.append(" = ?;");
        PreparedStatement statement = null;
        try {
            try {
                statement = connection.prepareStatement(sqlQuery.toString());
                statement.setInt(1, round.getId());
                ResultSet resultSet = statement.executeQuery();
                result = convertToInstances(resultSet);
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw new ApplicationException("Failed to fetch", e);
        }

        return result;
    }

    // *************************************************
    // STORE, UPDATE, DESTROY
    // *************************************************
    /**
     * Stores a new Instance in Database.
     * 
     * @param entity
     *            - the Instance to save
     * @return the saved instance
     * @throws ApplicationException
     */
    public static SearchQuery store(SearchQuery entity) throws ApplicationException {

        SearchQuery result = null;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = SearchQueryDatabaseAccess.store(entity, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'CREATE' statement on the database table to store a new
     * Instance.
     * 
     * @param entity
     *            - the Instance to save
     * @param connection
     *            - the database connection to perform the statement on
     * @return true, if successfully saved
     * @throws ApplicationException
     */
    protected static SearchQuery store(SearchQuery entity, Connection connection) throws ApplicationException {

        // build query
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(SearchQueryDatabaseAccess.TABLE);
        query.append("(");
        query.append(SearchQueryDatabaseAccess.COL_ROUND_ID);
        query.append(", ");
        query.append(SearchQueryDatabaseAccess.COL_QUERY);
        query.append(", ");
        query.append(SearchQueryDatabaseAccess.COL_FILTER_USED);
        query.append(", ");
        query.append(SearchQueryDatabaseAccess.COL_FILTER_PROVIDER);
        query.append(", ");
        query.append(SearchQueryDatabaseAccess.COL_FILTER_LANGUAGE);
        query.append(", ");
        query.append(SearchQueryDatabaseAccess.COL_FILTER_TYPE);
        query.append(") VALUES (?, ?, ?, ?, ?, ?)  RETURNING *;");

        PreparedStatement statement = null;
        try {
            try {
                // set parameters and execute query
                statement = connection.prepareStatement(query.toString());
                statement.setInt(1, entity.getRoundId());
                statement.setString(2, entity.getQuery());
                statement.setBoolean(3, entity.isFilterUsed());
                statement.setString(4, entity.getFilterProvider());
                statement.setString(5, entity.getFilterLanguage());
                statement.setString(6, entity.getFilterType());

                return convertToInstance(statement.executeQuery());
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw new ApplicationException("Failed to select", e);
        }
    }

    /**
     * Returns the number of queries used in a specified round.
     * 
     * @param entity
     * @return
     */
    public static int getNumberOfQueries(Round entity) throws ApplicationException {
        int result = 0;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = SearchQueryDatabaseAccess.getNumberOfQueries(entity, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    private static int getNumberOfQueries(Round entity, Connection connection) throws ApplicationException {
        int result = 0;

        StringBuilder query = new StringBuilder("SELECT COUNT (*) FROM ");
        query.append(TABLE);
        query.append(" WHERE ");
        query.append(COL_ROUND_ID);
        query.append(" = ?;");
        PreparedStatement statement = null;
        try {
            try {
                statement = connection.prepareStatement(query.toString());
                statement.setInt(1, entity.getId());

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    result = resultSet.getInt(1);
                }

            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw new ApplicationException("Failed on " + query + " with param \"" + entity.getId() + "\"", e);
        }

        return result;
    }

    /**
     * Returns the number of filters used in a specified round.
     * 
     * @param entity
     * @return
     */
    public static int getNumberOfFilters(Round entity) throws ApplicationException {
        int result = 0;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = SearchQueryDatabaseAccess.getNumberOfFilters(entity, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Gets number of used Filters for xp calculation. It's not enough that
     * "use filters" was enabled, the filter columns also must not be empty.
     * 
     * @param entity
     * @param connection
     * @return how many filters were actually used
     * @throws ApplicationException
     */
    private static int getNumberOfFilters(Round entity, Connection connection) throws ApplicationException {
        int result = 0;

        StringBuilder query = new StringBuilder("SELECT COUNT (*) FROM ");
        query.append(TABLE);
        query.append(" WHERE ");
        query.append(COL_ROUND_ID);
        query.append(" = ? AND ");
        query.append(COL_FILTER_USED);
        query.append(" AND (");
        query.append(COL_FILTER_LANGUAGE);
        query.append(" != 'none' ");
        query.append(" OR ");
        query.append(COL_FILTER_TYPE);
        query.append(" != 'none' ");
        query.append(" OR ");
        query.append(COL_FILTER_PROVIDER);
        query.append(" != 'none');");
        PreparedStatement statement = null;
        try {
            try {
                statement = connection.prepareStatement(query.toString());
                statement.setInt(1, entity.getId());

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    result = resultSet.getInt(1);
                }

            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw new ApplicationException("Failed on " + query + " with param \"" + entity.getId() + "\"", e);
        }

        return result;
    }

  

    /**
     * Converts a ResultSet form the database table into an entity of the
     * Instance.
     * 
     * @param resultSet
     *            result of a get/fetch on the database table
     * @return the Instance saved in an entity
     * @throws ApplicationException
     */
    protected static SearchQuery convertToInstance(ResultSet resultSet) throws ApplicationException {
        SearchQuery entity = null;

        try {
            if (resultSet.next()) {
                entity = new SearchQuery();
                entity.setRoundId(resultSet.getInt(COL_ROUND_ID));
                entity.setQuery(resultSet.getString(COL_QUERY));
                entity.setTimestamp(resultSet.getTimestamp(COL_QUERY_TIMESTAMP));
                entity.setFilterUsed(resultSet.getBoolean(COL_FILTER_USED));
                entity.setFilterLanguage(resultSet.getString(COL_FILTER_LANGUAGE));
                entity.setFilterProvider(resultSet.getString(COL_FILTER_PROVIDER));
                entity.setFilterType(resultSet.getString(COL_FILTER_TYPE));
                return entity;
            }
        } catch (SQLException e) {
            throw new ApplicationException("Failed to convert resultSet to entity.", e);
        }

        return entity;
    }

    /**
     * Converts a ResultSet form the database table into multiple entities of
     * the Instances.
     * 
     * @param resultSet
     *            - result of a fetch on the database table
     * @return the Instances saved in entities
     * @throws ApplicationException
     */
    protected static List<SearchQuery> convertToInstances(ResultSet resultSet) throws ApplicationException {
        List<SearchQuery> entities = new LinkedList<SearchQuery>();

        SearchQuery entity = SearchQueryDatabaseAccess.convertToInstance(resultSet);

        while (entity != null) {
            entities.add(entity);
            entity = SearchQueryDatabaseAccess.convertToInstance(resultSet);
        }

        return entities;
    }

}