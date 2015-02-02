package cgwap.data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import cgwap.entities.QuestionTag;
import cgwap.util.database.ConnectionPool;
import cgwap.util.exception_handler.ApplicationException;

/**
 * Accesses the table containing the tags. It supports the persistence
 * functions CRUD(create, read, update,
 * delete).
 */

public class TagsDatabaseAccess {

    /**
     * The table's name.
     */
    protected static final String TABLE = "tags";

    /**
     * The column name for the identifier.
     */
    protected static final String COL_QUESTION_ID = "question_id";

    /**
     * The column name for the answer's url.
     */
    protected static final String COL_TAG = "tag";

    /**
     * Stores a new Instance in Database.
     * 
     * @param entity
     *            - the Instance to save
     * @return the saved instance
     * @throws ApplicationException
     */
    public static QuestionTag store(QuestionTag entity) throws ApplicationException {

        QuestionTag result = null;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = TagsDatabaseAccess.store(entity, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    protected static QuestionTag store(QuestionTag entity, Connection connection) throws ApplicationException {

        // build query
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(TagsDatabaseAccess.TABLE);
        query.append("(");
        query.append(TagsDatabaseAccess.COL_QUESTION_ID);
        query.append(",");
        query.append(TagsDatabaseAccess.COL_TAG);
        query.append(") VALUES (?,?) RETURNING *;");

        PreparedStatement statement = null;
        try {
            try {
                // set parameters and execute query
                statement = connection.prepareStatement(query.toString());
                statement.setInt(1, entity.getQuestionId());
                statement.setString(2, entity.getTag().toLowerCase());

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
     * Fetches an Instance from the Database.
     * 
     * @return
     *         the Instance
     * @throws ApplicationException
     */
    public static List<QuestionTag> fetch(boolean distinctTags) throws ApplicationException {
        List<QuestionTag> result = null;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = TagsDatabaseAccess.fetch(connection, distinctTags);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Fetches an Instance from the Database.
     * 
     * @return
     *         the Instance
     * @throws ApplicationException
     */
    private static List<QuestionTag> fetch(Connection connection, boolean distinctTags) throws ApplicationException {
        List<QuestionTag> result = null;

        if (!distinctTags) {
            // build query
            StringBuilder query = new StringBuilder("SELECT * FROM ");
            query.append(TagsDatabaseAccess.TABLE);
            query.append(";");
        }
        else {
            // build query

            StringBuilder query = new StringBuilder("SELECT DISTINCT ON (");
            query.append(TagsDatabaseAccess.COL_TAG);
            query.append(") ");
            query.append(TagsDatabaseAccess.COL_TAG);
            query.append(", ");
            query.append(TagsDatabaseAccess.COL_QUESTION_ID);
            query.append(" FROM ");
            query.append(TagsDatabaseAccess.TABLE);
            query.append(";");

            PreparedStatement statement = null;
            try {
                try {
                    // set parameters and execute query
                    statement = connection.prepareStatement(query.toString());

                    result = convertToInstances(statement.executeQuery());
                } finally {
                    if (statement != null) {
                        statement.close();
                    }
                }
            } catch (SQLException e) {
                throw new ApplicationException("Failed to select", e);
            }
        }

        return result;
    }

    /**
     * Fetches question ids with a specific tag.
     * 
     * @param currentUserId
     *            the active user's id
     * @return the desired question
     * @throws ApplicationException
     */
    public static List<QuestionTag> getQuestionIdsByTag(String tagToChooseQuestionBy, int currentUserId)
            throws ApplicationException {
        List<QuestionTag> result = new LinkedList<QuestionTag>();

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = TagsDatabaseAccess.getQuestionIdsByTag(tagToChooseQuestionBy, currentUserId, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Fetches question ids with a specific tag (but only those that have not
     * been deleted, reported too often or are followUp questions).
     * 
     * @param currentUserId
     *            the active user's id
     * @param connection
     *            the database connection to perform the statement on
     * @return the desired question
     * @throws ApplicationException
     */
    protected static List<QuestionTag> getQuestionIdsByTag(String tagToChooseQuestionBy, int currentUserId,
            Connection connection)
            throws ApplicationException {

        List<QuestionTag> entities = new LinkedList<QuestionTag>();

        StringBuilder query = new StringBuilder("");
        query.append("SELECT *");
        query.append(" FROM " + TABLE);
        query.append(" WHERE " + COL_TAG);
        // and not yet played by active user
        query.append(" = ?  AND " + COL_QUESTION_ID);
        query.append(" NOT IN (SELECT " + COL_QUESTION_ID);
        query.append(" FROM " + RoundsDatabaseAccess.TABLE);
        query.append(" WHERE " + UserDatabaseAccess.COL_USER_ID);
        query.append(" = ?)");
        // and not deleted, reported or followUps
        query.append(" AND " + COL_QUESTION_ID);
        query.append(" NOT IN (SELECT " + COL_QUESTION_ID);
        query.append(" FROM " + QuestionDatabaseAccess.TABLE);
        query.append(" WHERE " + QuestionDatabaseAccess.COL_REPORTED);
        query.append(" >= 3  OR " + QuestionDatabaseAccess.COL_PREVIOUS_ID);
        query.append(" != 0);");

        PreparedStatement statement = null;
        try {
            try {
                // set parameters and execute query
                statement = connection.prepareStatement(query.toString());
                statement.setString(1, tagToChooseQuestionBy);
                statement.setInt(2, currentUserId);
                ResultSet resultSet = statement.executeQuery();
                entities = convertToInstances(resultSet);

            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw new ApplicationException("Failed to fetch", e);
        }

        return entities;
    }

    /**
     * Fetches tags by question id.
     * 
     * @param questionId
     *            the question id for which to fetch tags
     * @return the desired tags
     * @throws ApplicationException
     */
    public static List<QuestionTag> getTagsByQuestionId(int questionId) throws ApplicationException {
        List<QuestionTag> result = new LinkedList<QuestionTag>();

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = TagsDatabaseAccess.getTagsByQuestionId(questionId, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Fetches tags by question id.
     * 
     * @param questionId
     *            the question id for which to fetch tags
     * @param connection
     *            the database connection to perform the statement on
     * @return the desired tags
     * @throws ApplicationException
     */
    protected static List<QuestionTag> getTagsByQuestionId(int questionId, Connection connection)
            throws ApplicationException {

        List<QuestionTag> entities = new LinkedList<QuestionTag>();

        StringBuilder query = new StringBuilder("");
        query.append("SELECT *");
        query.append(" FROM " + TABLE);
        query.append(" WHERE " + COL_QUESTION_ID);
        query.append(" = ?;");

        PreparedStatement statement = null;
        try {
            try {
                // set parameters and execute query
                statement = connection.prepareStatement(query.toString());
                statement.setInt(1, questionId);
                ResultSet resultSet = statement.executeQuery();
                entities = convertToInstances(resultSet);

            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw new ApplicationException("Failed to fetch", e);
        }

        return entities;
    }

    /**
     * Removes an Instance from storage.
     * 
     * @param entity
     *            - the Instance to remove
     * @return true, if a Instance was deleted; otherwise false
     * @throws ApplicationException
     */
    public static boolean delete(QuestionTag entity) throws ApplicationException {
        boolean result = false;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = TagsDatabaseAccess.delete(entity, connection);

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
    protected static boolean delete(QuestionTag entity, Connection connection) throws ApplicationException {

        StringBuilder sqlQuery = new StringBuilder("DELETE FROM ");
        sqlQuery.append(TABLE);
        sqlQuery.append(" WHERE " + COL_QUESTION_ID);
        sqlQuery.append(" = ?");
        sqlQuery.append(" AND " + COL_TAG);
        sqlQuery.append(" = ?;");

        PreparedStatement statement = null;
        try {
            try {
                statement = connection.prepareStatement(sqlQuery.toString());
                statement.setInt(1, entity.getQuestionId());
                statement.setString(2, entity.getTag());

                int resultRows = statement.executeUpdate();

                return resultRows > 0;
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw new ApplicationException("Failed on " + sqlQuery + " with param \"" + entity.getTag() + " "
                    + entity.getQuestionId() + "\"", e);
        }
    }

    // *************************************************
    // COUNT
    // *************************************************
    /**
     * Count the number of tags for one question in storage.
     * 
     * @return number of Instances stored in storage
     * @throws ApplicationException
     */
    public static int countPerQuestion(int id) throws ApplicationException {
        int result = 0;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = TagsDatabaseAccess.countPerQuestion(id, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'COUNT' statement on the database table to count the number of
     * tags for one question in storage.
     * 
     * @param connection
     *            - the database connection to perform the statement on
     * @return number of Instances stored in storage
     * @throws ApplicationException
     */
    protected static int countPerQuestion(int id, Connection connection) throws ApplicationException {

        int result = -1;

        StringBuilder query = new StringBuilder("");
        query.append("SELECT COUNT (*)");
        query.append(" FROM " + TABLE);
        query.append(" WHERE " + COL_QUESTION_ID);
        query.append(" = ?;");

        PreparedStatement statement = null;
        try {
            try {
                statement = connection.prepareStatement(query.toString());
                statement.setInt(1, id);
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
            throw new ApplicationException("Failed to select", e);
        }

        return result;
    }

    /**
     * Converts a ResultSet form the database table into an entity of the
     * Instance.
     * 
     * @param resultSet
     *            - result of a get/fetch on the database table
     * @return the Instance saved in an entity
     * @throws ApplicationException
     */
    protected static QuestionTag convertToInstance(ResultSet resultSet) throws ApplicationException {
        QuestionTag entity = null;

        try {
            if (resultSet.next()) {
                entity = new QuestionTag();
                entity.setQuestionId(resultSet.getInt(COL_QUESTION_ID));
                entity.setTag(resultSet.getString(COL_TAG));

            }
        } catch (SQLException e) {
            throw new ApplicationException("Failed to convert resultSet to entity.");
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
    protected static List<QuestionTag> convertToInstances(ResultSet resultSet) throws ApplicationException {
        List<QuestionTag> entities = new LinkedList<QuestionTag>();

        QuestionTag entity = TagsDatabaseAccess.convertToInstance(resultSet);

        // while there are tags left and none have been listed already
        while (entity != null) {
            entities.add(entity);
            entity = TagsDatabaseAccess.convertToInstance(resultSet);

        }

        return entities;
    }

}
