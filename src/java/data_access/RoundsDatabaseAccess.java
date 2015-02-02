package cgwap.data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cgwap.entities.Round;
import cgwap.util.database.ConnectionPool;
import cgwap.util.exception_handler.ApplicationException;

/**
 * Accesses the table containing the rounds. It supports the persistence
 * functions CRUD(create, read, update,
 * delete).
 * 
 * 
 */

public class RoundsDatabaseAccess {

    /**
     * The table's name.
     */
    protected static final String TABLE = "rounds";

    /**
     * The column name for the identifier.
     */
    protected static final String COL_ID = "id";

    /**
     * The column name for the player's id.
     */
    protected static final String COL_USER_ID = "user_id";

    /**
     * The column name for the question's id.
     */
    public static final String COL_QUESTION_ID = "question_id";

    /**
     * The column name for the result's timestamp i.e. the point in time where
     * the round has been started.
     */
    protected static final String COL_START_TIME = "start_time";

    /**
     * The column name for the result's end timestamp i.e. the point in time
     * where
     * the round has been finished.
     */
    protected static final String COL_END_TIME = "end_time";

    /**
     * The column name for the result's score.
     */
    protected static final String COL_SCORE = "score";

    /**
     * The column name for boolean indicating whether the round was successful
     * or not.
     */
    protected static final String COL_PASS = "pass";

    /**
     * The column name for an optional comment made by the player.
     */
    protected static final String COL_USER_COMMENT = "user_comment";

    /**
     * The column name for the remaining lives.
     */
    protected static final String COL_LIVES_LEFT = "lives_left";

    /**
     * The column name for the rounds duration.
     */
    protected static final String COL_DURATION = "duration";

    /**
     * Stores a new Instance in Database.
     * 
     * @param entity
     *            - the Instance to save
     * @return the saved instance
     * @throws ApplicationException
     */
    public static Round store(Round entity) throws ApplicationException {

        Round result = null;

        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();

            // perform store
            result = RoundsDatabaseAccess.store(entity, connection);

        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    protected static Round store(Round entity, Connection connection) throws ApplicationException {

        // build query
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(RoundsDatabaseAccess.TABLE);
        query.append("(");
        query.append(RoundsDatabaseAccess.COL_USER_ID);
        query.append(", ");
        query.append(RoundsDatabaseAccess.COL_QUESTION_ID);
        query.append(", ");
        query.append(RoundsDatabaseAccess.COL_END_TIME);
        query.append(", ");
        query.append(RoundsDatabaseAccess.COL_SCORE);
        query.append(", ");
        query.append(RoundsDatabaseAccess.COL_USER_COMMENT);
        query.append(", ");
        query.append(COL_LIVES_LEFT);
        query.append(") VALUES (?, ?, ?, ?, ?, ?)  RETURNING *;");

        PreparedStatement statement = null;
        try {
            try {
                // set parameters and execute query
                statement = connection.prepareStatement(query.toString());
                statement.setInt(1, entity.getUserId());
                statement.setInt(2, entity.getQuestionId());
                if (entity.getEnd() != null) {
                    statement.setTimestamp(3, new Timestamp(entity.getEnd().getTime()));
                } else {
                    statement.setNull(3, Types.TIMESTAMP);
                }
                statement.setInt(4, entity.getScore());
                statement.setString(5, entity.getUserComment());
                statement.setInt(6, entity.getLivesLeft());

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
     * Removes an Instance from storage.
     * 
     * @param entity
     *            - the Instance to remove
     * @return true, if a Instance was deleted; otherwise false
     * @throws ApplicationException
     */
    public static boolean delete(Round entity) throws ApplicationException {
        boolean result = false;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = RoundsDatabaseAccess.delete(entity, connection);

        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'DELETE' statement on the database table to destroy an
     * existing Instance.
     * 
     * @param entity
     *            the Instance to remove
     * @param connection
     *            the database connection to perform the statement on
     * @return true, if a Instance was deleted; otherwise false
     * @throws ApplicationException
     */
    protected static boolean delete(Round entity, Connection connection) throws ApplicationException {

        StringBuilder sqlQuery = new StringBuilder("DELETE FROM ");
        sqlQuery.append(TABLE);
        sqlQuery.append(" WHERE " + COL_ID);
        sqlQuery.append(" = ?;");

        PreparedStatement statement = null;
        try {
            try {
                statement = connection.prepareStatement(sqlQuery.toString());
                statement.setInt(1, entity.getId());

                int resultRows = statement.executeUpdate();

                return resultRows > 0;
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw new ApplicationException("Failed on " + sqlQuery + " with param \"" + entity.getId() + "\"", e);
        }
    }

    /**
     * Updates a changed instance in Database.
     * 
     * @param entity
     *            the instance to save
     * @return true if the instance could be saved, otherwise false
     * @throws ApplicationException
     */
    public static Round update(Round entity) throws ApplicationException {
        Round result = new Round();

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = RoundsDatabaseAccess.update(entity, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'UPDATE' statement on the database table to update an
     * existing
     * instance.
     * 
     * @param entity
     *            the instance to save
     * @param connection
     *            the database connection to perform the statement on
     * @return true if the instance could be saved, otherwise false
     * @throws ApplicationException
     */
    protected static Round update(Round entity, Connection connection)
            throws ApplicationException {
        // LOGGER.fine("Perform 'UPDATE' on table " +
        // QuestionDatabaseAccess.TABLE);

        // build query
        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(TABLE);
        query.append(" SET ");
        query.append("(");
        query.append(COL_END_TIME);
        query.append(", ");
        query.append(COL_SCORE);
        query.append(", ");
        query.append(COL_PASS);
        query.append(", ");
        query.append(COL_LIVES_LEFT);
        query.append(", ");
        query.append(COL_DURATION);
        query.append(") = (?, ?, ?, ?, ?) ");
        query.append("WHERE ");
        query.append(COL_ID);
        query.append(" = ?  RETURNING *;");

        PreparedStatement statement = null;
        try {
            try {
                // set parameters and execute query
                statement = connection.prepareStatement(query.toString());
                statement.setTimestamp(1, (Timestamp) entity.getEnd());
                statement.setInt(2, entity.getScore());
                statement.setString(3, entity.getPass());
                statement.setInt(4, entity.getLivesLeft());
                statement.setInt(5, (int) entity.getDuration());
                statement.setInt(6, entity.getId());

                return convertToInstance(statement.executeQuery());
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw new ApplicationException("Failed to update", e);
        }

    }

    /**
     * Return a existing Instance from storage, based on its user id.
     * 
     * @param currentUserId
     *            the user id to find the questions for
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    public static List<Integer> getRoundIdsByUserId(int currentUserId, boolean needToBePass)
            throws ApplicationException {
        List<Integer> result = null;

        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            result = RoundsDatabaseAccess.getRoundIdsByUserId(currentUserId, needToBePass, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'SELECT' statement on the database table to return a existing
     * Instance from
     * storage, based on its user id.
     * 
     * @param currentUserId
     *            the user id to find the questions for
     * @param connection
     *            the database connection to perform the statement on
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    protected static List<Integer> getRoundIdsByUserId(int currentUserId, boolean needToBePass, Connection connection)
            throws ApplicationException {
        // LOGGER.fine("SELECT on table " + RoundDatabaseAccess.DB_TABLE);

        List<Integer> result = new ArrayList<Integer>();

        StringBuilder query = new StringBuilder("");
        query.append("SELECT " + COL_QUESTION_ID + " FROM " + TABLE);
        query.append(" WHERE " + COL_USER_ID);
        query.append("=" + currentUserId);

        // if round has to be a successful one
        if (needToBePass) {

            query.append(" AND " + COL_PASS);
            query.append("="
                    + " 'pass'");
        }
        query.append(";");

        PreparedStatement statement = null;
        try {
            try {
                // set parameters and execute query
                statement = connection.prepareStatement(query.toString());

                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    result.add(resultSet.getInt(1));
                }

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
     * Return a existing Instance from storage, based on its user id.
     * 
     * @param currentUserId
     *            the user id to find the questions for
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    public static List<Round> getRoundsByUserId(int currentUserId, boolean needToBePass) throws ApplicationException {
        List<Round> result = null;

        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            result = RoundsDatabaseAccess.getRoundsByUserId(currentUserId, needToBePass, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'SELECT' statement on the database table to return a existing
     * Instance from
     * storage, based on its user id.
     * 
     * @param currentUserId
     *            the user id to find the questions for
     * @param connection
     *            the database connection to perform the statement on
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    protected static List<Round> getRoundsByUserId(int currentUserId, boolean needToBePass, Connection connection)
            throws ApplicationException {
        // LOGGER.fine("SELECT on table " + RoundDatabaseAccess.DB_TABLE);

        List<Round> result = new LinkedList<Round>();

        StringBuilder query = new StringBuilder("");
        query.append("SELECT * FROM " + TABLE);
        query.append(" WHERE " + COL_USER_ID);
        query.append("=" + currentUserId);

        // if round has to be a successful one
        if (needToBePass) {
            query.append(" AND " + COL_PASS);
            query.append("="
                    + " 'pass'");
        }

        query.append(";");

        PreparedStatement statement = null;
        try {
            try {
                // set parameters and execute query
                statement = connection.prepareStatement(query.toString());
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
     * Return a existing Instance from storage, based on its duration.
     * 
     * @return the found Instances, otherwise null
     * @throws ApplicationException
     */
    public static List<Round> getFastestRounds() throws ApplicationException {
        List<Round> result = null;

        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            result = RoundsDatabaseAccess.getFastestRounds(connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'SELECT' statement on the database table to return a existing
     * Instance from
     * storage, based on its duration.
     * 
     * @param connection
     *            the database connection to perform the statement on
     * @return the found Instances, otherwise null
     * @throws ApplicationException
     */
    protected static List<Round> getFastestRounds(Connection connection)
            throws ApplicationException {
        // LOGGER.fine("SELECT on table " + RoundDatabaseAccess.DB_TABLE);

        List<Round> result = new ArrayList<Round>();

        StringBuilder query = new StringBuilder("");
        query.append("SELECT * FROM " + TABLE);
        query.append(" WHERE " + COL_PASS);
        query.append("='pass'");
        query.append(" ORDER BY " + COL_DURATION);
        query.append(" LIMIT 10");
        query.append(";");

        PreparedStatement statement = null;
        try {
            try {
                // set parameters and execute query
                statement = connection.prepareStatement(query.toString());

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

    // /**
    // // * Fetches answer according to question id.
    // // *
    // // * @param questionId
    // // *
    // // * @return the desired answer
    // // * @throws ApplicationException
    // // */
    // public static List<Round> getRoundUrlsByQuestionId(int questionId)
    // throws ApplicationException {
    // List<Round> result = new LinkedList<Round>();
    //
    // Connection connection = null;
    // try {
    // connection = ConnectionPool.getConnection();
    // result = ResultsDatabaseAccess.getRoundUrlsByQuestionId(questionId,
    // connection);
    // } finally {
    // ConnectionPool.releaseConnection(connection);
    // }
    //
    // return result;
    // }
    //
    // /**
    // * /**
    // * Fetches answer according to question id.
    // *
    // * @param questionId
    // * @param connection
    // * the database connection to perform the statement on
    // * @return the desired answer
    // * @throws ApplicationException
    // */
    // protected static List<Round> getRoundUrlsByQuestionId(int questionId,
    // Connection connection)
    // throws ApplicationException {
    //
    // List<Result> entities = new LinkedList<Round>();
    //
    // StringBuilder query = new StringBuilder("");
    // query.append("SELECT *");
    // query.append(" FROM " + TABLE);
    // query.append(" WHERE " + COL_ID);
    // query.append(" = ?;");
    //
    // PreparedStatement statement = null;
    // try {
    // try {
    // // set parameters and execute query
    // statement = connection.prepareStatement(query.toString());
    // statement.setInt(1, questionId);
    // ResultSet resultSet = statement.executeQuery();
    // entities = convertToInstances(resultSet);
    //
    // } finally {
    // if (statement != null) {
    // statement.close();
    // }
    // }
    // } catch (SQLException e) {
    // throw new ApplicationException("Failed to fetch", e);
    // }
    //
    // return entities;
    // }

    /**
     * Converts a ResultSet form the database table into an entity of the
     * Instance.
     * 
     * @param resultSet
     *            result of a get/fetch on the database table
     * @return the Instance saved in an entity
     * @throws ApplicationException
     */
    protected static Round convertToInstance(ResultSet resultSet) throws ApplicationException {
        Round entity = null;
        try {
            if (resultSet.next()) {
                entity = new Round();
                entity.setId(resultSet.getInt(COL_ID));
                entity.setQuestionId(resultSet.getInt(COL_QUESTION_ID));
                entity.setUserId(resultSet.getInt(COL_USER_ID));
                entity.setStart(resultSet.getTimestamp(COL_START_TIME));
                entity.setEnd(resultSet.getTimestamp(COL_END_TIME));
                entity.setScore(resultSet.getInt(COL_SCORE));
                entity.setLivesLeft(resultSet.getInt(COL_LIVES_LEFT));
                entity.setPass(resultSet.getString(COL_PASS));
                entity.setUserComment(resultSet.getString(COL_USER_COMMENT));
                entity.setDuration(resultSet.getInt(COL_DURATION));
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
    protected static List<Round> convertToInstances(ResultSet resultSet) throws ApplicationException {
        List<Round> entities = new LinkedList<Round>();

        Round entity = RoundsDatabaseAccess.convertToInstance(resultSet);

        // while there are tags left and none have been listed already
        while (entity != null) {
            entities.add(entity);
            entity = RoundsDatabaseAccess.convertToInstance(resultSet);

        }

        return entities;

    }

}
