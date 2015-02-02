package cgwap.data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cgwap.entities.Answer;
import cgwap.util.database.ConnectionPool;
import cgwap.util.exception_handler.ApplicationException;

/**
 * Accesses the table containing the answers. It supports the persistence
 * functions CRUD(create, read, update,
 * delete).
 * 
 */

public class AnswersDatabaseAccess {

    /**
     * The table's name.
     */
    protected static final String TABLE = "answers";

    /**
     * The column name for the identifier.
     */
    protected static final String COL_QUESTION_ID = "question_id";

    /**
     * The column name for the answer's url.
     */
    protected static final String COL_URL = "url";

    /**
     * Stores a new Instance in Database.
     * 
     * @param entity
     *            - the Instance to save
     * @return the saved instance
     * @throws ApplicationException
     */
    public static Answer store(Answer entity) throws ApplicationException {

        Answer result = null;

        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();

            // perform store
            result = AnswersDatabaseAccess.store(entity, connection);

        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    protected static Answer store(Answer entity, Connection connection) throws ApplicationException {

        // build query
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(AnswersDatabaseAccess.TABLE);
        query.append("(");
        query.append(AnswersDatabaseAccess.COL_QUESTION_ID);
        query.append(",");
        query.append(AnswersDatabaseAccess.COL_URL);
        query.append(") VALUES (?,?) RETURNING *;");

        PreparedStatement statement = null;
        try {
            try {
                // set parameters and execute query
                statement = connection.prepareStatement(query.toString());
                statement.setInt(1, entity.getQuestionId());

                String cleanURL = cleanUrl(entity.getAnswerUrl());
                statement.setString(2, cleanURL);

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
     * Helper method to clean Europeana Urls of unnecessary parameters.
     * 
     * @param answerUrl
     *            the url to clean
     * @return the clean url
     */
    private static String cleanUrl(String answerUrl) {
        String cleanURL = "";
        Pattern MY_PATTERN = Pattern.compile("(europeana\\.eu.*?(\\.html)){1,1}.*");
        Matcher m = MY_PATTERN.matcher(answerUrl);
        m.find();
        cleanURL = m.group(1);
        return cleanURL;
    }

    /**
     * Removes an Instance from storage.
     * 
     * @param entity
     *            - the Instance to remove
     * @return true, if a Instance was deleted; otherwise false
     * @throws ApplicationException
     */
    public static boolean delete(Answer entity) throws ApplicationException {
        boolean result = false;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = AnswersDatabaseAccess.delete(entity, connection);

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
    protected static boolean delete(Answer entity, Connection connection) throws ApplicationException {

        StringBuilder sqlQuery = new StringBuilder("DELETE FROM ");
        sqlQuery.append(TABLE);
        sqlQuery.append(" WHERE " + COL_QUESTION_ID);
        sqlQuery.append(" = ?");
        sqlQuery.append(" AND " + COL_URL);
        sqlQuery.append(" = ?;");

        PreparedStatement statement = null;
        try {
            try {
                statement = connection.prepareStatement(sqlQuery.toString());
                statement.setInt(1, entity.getQuestionId());
                statement.setString(2, entity.getAnswerUrl());

                int resultRows = statement.executeUpdate();

                return resultRows > 0;
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw new ApplicationException("Failed on " + sqlQuery + " with param \"" + entity.getAnswerUrl() + " "
                    + entity.getQuestionId() + "\"", e);
        }
    }

    //
    // /**
    // * Updates a changed instance in Database.
    // *
    // * @param entity
    // * the instance to save
    // * @return true if the instance could be saved, otherwise false
    // * @throws ApplicationException
    // */
    // public static Answer update(Answer entity) throws ApplicationException {
    // Answer result = new Answer();
    //
    // Connection connection = null;
    // try {
    // connection = ConnectionPool.getConnection();
    // result = QuestionDatabaseAccess.update(entity, connection);
    // } finally {
    // ConnectionPool.releaseConnection(connection);
    // }
    //
    // return result;
    // }
    //
    // /**
    // * Performs a 'UPDATE' statement on the database table to update an
    // existing
    // * instance.
    // *
    // * @param entity
    // * the instance to save
    // * @param connection
    // * the database connection to perform the statement on
    // * @return true if the instance could be saved, otherwise false
    // * @throws ApplicationException
    // */
    // protected static Answer update(Answer entity, Connection connection)
    // throws ApplicationException {
    // // LOGGER.fine("Perform 'UPDATE' on table " +
    // // QuestionDatabaseAccess.TABLE);
    //
    // // build query
    // StringBuilder query = new StringBuilder("UPDATE ");
    // query.append(AnswerUrlsDatabaseAccess.TABLE);
    // query.append(" SET ");
    // query.append("(");
    // query.append(AnswerUrlsDatabaseAccess.COL_ID);
    // query.append(", ");
    // query.append(AnswerUrlsDatabaseAccess.COL_URL);
    // query.append(") = (?, ?) ");
    // query.append("WHERE ");
    // query.append(AnswerUrlsDatabaseAccess.COL_ID);
    // query.append(" = ?  RETURNING *;");
    //
    // PreparedStatement statement = null;
    // try {
    // try {
    // // set parameters and execute query
    // statement = connection.prepareStatement(query.toString());
    // statement.setInt(1, entity.getQuestionId());
    // statement.setInt(2, entity.getRating());
    // statement.setInt(4, entity.getSkipped());
    // statement.setInt(5, entity.getReported());
    // statement.setInt(6, entity.getUserId());
    // statement.setBoolean(7, entity.isIs_follow_up_question());
    // statement.setInt(8, entity.getPrevious_question_id());
    // statement.setInt(9, entity.getId());
    //
    // return convertToInstance(statement.executeQuery());
    // } finally {
    // if (statement != null) {
    // statement.close();
    // }
    // }
    // } catch (SQLException e) {
    // throw new ApplicationException("Failed to update", e);
    // }
    //
    // }

    /**
     * Fetches answer according to question id.
     * 
     * @param questionId
     * 
     * @return the desired answer
     * @throws ApplicationException
     */
    public static List<Answer> getAnswerUrlByQuestionId(int questionId) throws ApplicationException {
        List<Answer> result = new LinkedList<Answer>();

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = AnswersDatabaseAccess.getAnswerUrlByQuestionId(questionId, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * /**
     * Fetches answer according to question id.
     * 
     * @param questionId
     * @param connection
     *            the database connection to perform the statement on
     * @return the desired answer
     * @throws ApplicationException
     */
    protected static List<Answer> getAnswerUrlByQuestionId(int questionId, Connection connection)
            throws ApplicationException {

        List<Answer> entities = new LinkedList<Answer>();

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

    // *************************************************
    // COUNT
    // *************************************************
    /**
     * Count the number of answers for one question in storage.
     * 
     * @return number of Instances stored in storage
     * @throws ApplicationException
     */
    public static int countPerQuestion(int id) throws ApplicationException {
        int result = 0;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = AnswersDatabaseAccess.countPerQuestion(id, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'COUNT' statement on the database table to count the number of
     * answers for one question in
     * storage.
     * 
     * @param connection
     *            - the database connection to perform the statement on
     * @return number of Instances stored in storage
     * @throws ApplicationException
     */
    protected static int countPerQuestion(int id, Connection connection) throws ApplicationException {
        // LOGGER.fine("Perform 'COUNT' on table " + DB_TABLE);

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
     *            result of a get/fetch on the database table
     * @return the Instance saved in an entity
     * @throws ApplicationException
     */
    protected static Answer convertToInstance(ResultSet resultSet) throws ApplicationException {
        Answer entity = null;

        try {
            if (resultSet.next()) {
                entity = new Answer();
                entity.setQuestionId(resultSet.getInt(COL_QUESTION_ID));
                entity.setAnswerUrl(resultSet.getString(COL_URL));
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
    protected static List<Answer> convertToInstances(ResultSet resultSet) throws ApplicationException {
        List<Answer> entities = new LinkedList<Answer>();

        Answer entity = AnswersDatabaseAccess.convertToInstance(resultSet);

        // while there are tags left and none have been listed already
        while (entity != null) {
            entities.add(entity);
            entity = AnswersDatabaseAccess.convertToInstance(resultSet);

        }

        return entities;

    }

}
