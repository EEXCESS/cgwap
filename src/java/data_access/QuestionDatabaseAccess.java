package cgwap.data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

import cgwap.entities.Question;
import cgwap.util.database.ConnectionPool;
import cgwap.util.exception_handler.ApplicationException;

/**
 * This database access class handles communication with the Questions Table.
 * The persistence functions CRUD(create, read, update, delete) are implemented.
 * 
 */
public class QuestionDatabaseAccess {

    /**
     * The table's name.
     */
    protected static final String TABLE = "questions";

    /**
     * The column name for the identifier.
     */
    public static final String COL_QUESTION_ID = "question_id";

    /**
     * The column name for the question's text.
     */
    protected static final String COL_QUESTION_TEXT = "question_text";

    /**
     * The column name for the question's timestamp i.e. the point in time where
     * the question has been entered.
     */
    protected static final String COL_QUESTION_TIMESTAMP = "question_timestamp";

    /**
     * The column name for the changed question's timestamp i.e. the point in
     * time where
     * the question has been changed.
     */
    protected static final String COL_QUESTION_MODIFIED = "question_modified";

    /**
     * The column name for the question's rating.
     */
    protected static final String COL_DIFFICULTY_RATING = "difficulty_rating";

    /**
     * The column name for the question's rating counter.
     */
    protected static final String COL_RATING_COUNTER = "rating_counter";

    /**
     * The column name for the times the question has been skipped.
     */
    protected static final String COL_SKIPPED = "skipped";

    /**
     * The column name for the times the question has been reported.
     */
    protected static final String COL_REPORTED = "reported";

    /**
     * The column name for the user who asked this question.
     */
    protected static final String COL_USER_ID = "user_id";

    /**
     * The column name for the possibility of the question being a follow up
     * question.
     */
    protected static final String COL_HAS_FOLLOW_UP = "has_follow_up";

    /**
     * The column name for the status of the question ie. if it has been
     * deleted..
     */
    protected static final String COL_IS_ACTIVE = "is_active";

    /**
     * The column name for the id of the question preceding this question.
     */
    protected static final String COL_PREVIOUS_ID = "previous_id";

    // *************************************************
    // FETCH
    // *************************************************
    /**
     * Returns a List of all Instances in storage.
     * 
     * @return a List of Instances
     * @throws ApplicationException
     */
    public static List<Question> fetch() throws ApplicationException {
        List<Question> result = null;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = QuestionDatabaseAccess.fetch(connection);
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
    protected static List<Question> fetch(Connection connection)
            throws ApplicationException {
        // LOGGER.fine("Perform 'SELECT' on table " +
        // QuestionDatabaseAccess.TABLE);

        List<Question> result = new LinkedList<>();

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
     * Returns a List of all Instances in storage save the ones entered by the
     * current user.
     * 
     * @param the
     *            user id
     * @return a List of Instances
     * @throws ApplicationException
     */
    public static List<Question> fetch(int id) throws ApplicationException {
        List<Question> result = null;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = QuestionDatabaseAccess.fetch(connection, id);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'FETCH' statement on the database table to return a List of
     * all questions save the ones entered or already answered by the current
     * user.
     * 
     * @param the
     *            user id
     * @param connection
     *            the database connection to perform the statement on
     * @return a List of Instances
     * @throws ApplicationException
     */
    protected static List<Question> fetch(Connection connection, int id)
            throws ApplicationException {
        // LOGGER.fine("Perform 'SELECT' on table " +
        // QuestionDatabaseAccess.TABLE);

        List<Question> result = new LinkedList<>();

        StringBuilder sqlQuery = new StringBuilder("");
        sqlQuery.append("SELECT * FROM " + TABLE);
        sqlQuery.append(" WHERE " + COL_USER_ID);
        sqlQuery.append(" <> ? AND " + COL_QUESTION_ID);
        sqlQuery.append(" NOT IN (SELECT " + COL_QUESTION_ID);
        sqlQuery.append(" FROM " + RoundsDatabaseAccess.TABLE);
        sqlQuery.append(" WHERE " + COL_USER_ID);
        sqlQuery.append(" = ?)");
        // and not deleted, reported or followUps
        sqlQuery.append(" AND " + COL_QUESTION_ID);
        sqlQuery.append(" NOT IN (SELECT " + COL_QUESTION_ID);
        sqlQuery.append(" FROM " + QuestionDatabaseAccess.TABLE);
        sqlQuery.append(" WHERE " + QuestionDatabaseAccess.COL_REPORTED);
        sqlQuery.append(" >= 3  OR " + QuestionDatabaseAccess.COL_PREVIOUS_ID);
        sqlQuery.append(" != 0);");

        PreparedStatement statement = null;
        try {
            try {
                statement = connection.prepareStatement(sqlQuery.toString());
                statement.setInt(1, id);
                statement.setInt(2, id);
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
    public static Question store(Question entity) throws ApplicationException {

        Question result = null;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = QuestionDatabaseAccess.store(entity, connection);
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
    protected static Question store(Question entity, Connection connection) throws ApplicationException {
        // build query
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(QuestionDatabaseAccess.TABLE);
        query.append("(");
        query.append(QuestionDatabaseAccess.COL_QUESTION_TEXT);
        query.append(", ");
        query.append(QuestionDatabaseAccess.COL_DIFFICULTY_RATING);
        query.append(", ");
        query.append(QuestionDatabaseAccess.COL_SKIPPED);
        query.append(", ");
        query.append(QuestionDatabaseAccess.COL_REPORTED);
        query.append(", ");
        query.append(QuestionDatabaseAccess.COL_USER_ID);
        query.append(", ");
        query.append(QuestionDatabaseAccess.COL_HAS_FOLLOW_UP);
        query.append(", ");
        query.append(QuestionDatabaseAccess.COL_PREVIOUS_ID);
        query.append(", ");
        query.append(QuestionDatabaseAccess.COL_RATING_COUNTER);
        query.append(") VALUES (?, ?, ?, ?, ?, ?, ?, ?)  RETURNING *;");

        PreparedStatement statement = null;
        try {
            try {
                // set parameters and execute query
                statement = connection.prepareStatement(query.toString());
                statement.setString(1, entity.getQuestionText());
                statement.setFloat(2, entity.getDifficultyRating());
                statement.setInt(3, entity.getSkipped());
                statement.setInt(4, entity.getReported());
                statement.setInt(5, entity.getUserId());
                statement.setBoolean(6, entity.isHas_follow_up_question());
                statement.setInt(7, entity.getPrevious_question_id());
                statement.setInt(8, entity.getRatingCounter());

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
     * Updates a changed instance in Database.
     * 
     * @param entity
     *            the instance to save
     * @return true if the instance could be saved, otherwise false
     * @throws ApplicationException
     */
    public static Question update(Question entity) throws ApplicationException {
        Question result = new Question();

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = QuestionDatabaseAccess.update(entity, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'UPDATE' statement on the database table to update an existing
     * instance.
     * 
     * @param entity
     *            the instance to save
     * @param connection
     *            the database connection to perform the statement on
     * @return true if the instance could be saved, otherwise false
     * @throws ApplicationException
     */
    protected static Question update(Question entity, Connection connection) throws ApplicationException {

        // build query
        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(QuestionDatabaseAccess.TABLE);
        query.append(" SET ");
        query.append("(");
        query.append(QuestionDatabaseAccess.COL_QUESTION_TEXT);
        query.append(", ");
        query.append(QuestionDatabaseAccess.COL_QUESTION_TIMESTAMP);
        query.append(", ");
        query.append(QuestionDatabaseAccess.COL_DIFFICULTY_RATING);
        query.append(", ");
        query.append(QuestionDatabaseAccess.COL_SKIPPED);
        query.append(", ");
        query.append(QuestionDatabaseAccess.COL_REPORTED);
        query.append(", ");
        query.append(QuestionDatabaseAccess.COL_USER_ID);
        query.append(", ");
        query.append(QuestionDatabaseAccess.COL_HAS_FOLLOW_UP);
        query.append(", ");
        query.append(QuestionDatabaseAccess.COL_PREVIOUS_ID);
        query.append(", ");
        query.append(QuestionDatabaseAccess.COL_RATING_COUNTER);
        query.append(") = (?, ?, ?, ?, ?, ?, ?, ?, ?) ");
        query.append("WHERE ");
        query.append(QuestionDatabaseAccess.COL_QUESTION_ID);
        query.append(" = ?  RETURNING *;");

        PreparedStatement statement = null;
        try {
            try {
                // set parameters and execute query
                statement = connection.prepareStatement(query.toString());
                statement.setString(1, entity.getQuestionText());
                if (entity.getTimestamp() != null) {
                    statement.setTimestamp(2, new Timestamp(entity.getTimestamp().getTime()));
                } else {
                    statement.setNull(2, Types.TIMESTAMP);
                }
                statement.setFloat(3, entity.getDifficultyRating());
                statement.setInt(4, entity.getSkipped());
                statement.setInt(5, entity.getReported());
                statement.setInt(6, entity.getUserId());
                statement.setBoolean(7, entity.isHas_follow_up_question());
                statement.setInt(8, entity.getPrevious_question_id());
                statement.setInt(9, entity.getRatingCounter());
                statement.setInt(10, entity.getId());

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
     * Removes an Instance from storage.
     * 
     * @param entity
     *            - the Instance to remove
     * @return true, if a Instance was deleted; otherwise false
     * @throws ApplicationException
     */
    public static boolean delete(Question entity) throws ApplicationException {
        boolean result = false;

        // entities which depend on entity must also be deleted TODO: if process
        // of entering a follow up question was canceled a nullpointer is
        // happening here
        if (entity.isHas_follow_up_question()) {
            Question followUp = QuestionDatabaseAccess.getFollowUpQuestion(entity);

            QuestionDatabaseAccess.delete(followUp);

        }

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = QuestionDatabaseAccess.delete(entity, connection);

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
    protected static boolean delete(Question entity, Connection connection) throws ApplicationException {

        StringBuilder sqlQuery = new StringBuilder("UPDATE ");
        sqlQuery.append(TABLE);
        sqlQuery.append(" SET " + COL_IS_ACTIVE);
        sqlQuery.append(" = false");
        sqlQuery.append(" WHERE " + COL_QUESTION_ID);
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
            throw new ApplicationException("Failed on " + sqlQuery + " with param \""
                    + entity.getId() + "\"", e);
        }
    }

    // *************************************************
    // GET
    // *************************************************
    /**
     * Return a existing Instance from storage, based on its id.
     * 
     * @param entity
     *            an entity with the ID of the Instance to load
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    public static Question getQuestionById(Question entity) throws ApplicationException {
        Question result = null;

        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            result = QuestionDatabaseAccess.getQuestionById(entity, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'SELECT' statement on the database table to return a existing
     * Instance from storage, based on its id.
     * 
     * @param connection
     *            the database connection to perform the statement on
     * @param entity
     *            an entity with the ID of the Instance to load
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    protected static Question getQuestionById(Question entity, Connection connection) throws ApplicationException {

        Question result = null;

        String sqlString = "";
        sqlString += "SELECT * FROM " + QuestionDatabaseAccess.TABLE;
        sqlString += " WHERE " + QuestionDatabaseAccess.COL_QUESTION_ID + " = ?";

        PreparedStatement statement = null;

        try {
            try {
                statement = connection.prepareStatement(sqlString);
                statement.setInt(1, entity.getId());
                ResultSet resultSet = statement.executeQuery();
                result = convertToInstance(resultSet);
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
     * Return a existing Instance from storage, based on its predeccessor.
     * 
     * @param entity
     *            an entity with the ID of the Instance to load
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    public static Question getFollowUpQuestion(Question entity) throws ApplicationException {
        Question result = null;

        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            result = QuestionDatabaseAccess.getFollowUpFollowUpQuestion(entity, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Return a existing Instance from storage, based on its predecessor.
     * 
     * @param entity
     *            an entity with the ID of the Instance to load
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    public static Question getFollowUpFollowUpQuestion(Question entity, Connection connection)
            throws ApplicationException {

        Question result = null;

        String sqlString = "";
        sqlString += "SELECT * FROM " + QuestionDatabaseAccess.TABLE;
        sqlString += " WHERE " + QuestionDatabaseAccess.COL_PREVIOUS_ID + " = ?";

        PreparedStatement statement = null;

        try {
            try {
                statement = connection.prepareStatement(sqlString);
                statement.setInt(1, entity.getId());
                ResultSet resultSet = statement.executeQuery();
                result = convertToInstance(resultSet);
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
     * Return a existing Instance from storage, based on its user id.
     * 
     * @param currentUserId
     *            the user id to find the questions for
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    public static List<Question> getQuestionsByUserId(int currentUserId) throws ApplicationException {
        List<Question> result = null;

        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            result = QuestionDatabaseAccess.getQuestionsByUserId(currentUserId, connection);
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
    protected static List<Question> getQuestionsByUserId(int currentUserId, Connection connection)
            throws ApplicationException {

        List<Question> result = null;

        StringBuilder query = new StringBuilder("");
        query.append("SELECT * FROM " + TABLE);
        query.append(" WHERE " + COL_USER_ID);
        query.append("=" + currentUserId);
        query.append(" AND " + COL_IS_ACTIVE);
        query.append(" = true;");

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
     * Return a existing Instance from storage, based on its text.
     * 
     * @param currentUserId
     *            the user id to find the questions for
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    public static int countQuestionByText(Question entity) throws ApplicationException {
     int result = 0;

        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            result = QuestionDatabaseAccess.countQuestionByText(entity, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'SELECT' statement on the database table to return a existing
     * Instance from
     * storage, based on its text.
     * 
     * @param currentUserId
     *            the user id to find the questions for
     * @param connection
     *            the database connection to perform the statement on
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    protected static int countQuestionByText(Question entity, Connection connection)
            throws ApplicationException {

        int result = 0;

        StringBuilder query = new StringBuilder("");
        query.append("SELECT COUNT (*)");
        query.append(" FROM " + TABLE);
        query.append(" WHERE (" + COL_QUESTION_TEXT);
        query.append(" = ?");
        query.append(" AND " + COL_REPORTED);
        query.append(" <= 3);");

        PreparedStatement statement = null;
        try {
            try {
                // set parameters and execute query
                statement = connection.prepareStatement(query.toString());
                statement.setString(1, entity.getQuestionText());
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
            throw new ApplicationException("Failed to fetch", e);
        }

        return result;
    }

    /**
     * Checks if a given question belongs to the user.
     * 
     * @param entity
     *            an entity with the id of the Instances to load
     * @return whether the instance could be found
     * @throws ApplicationException
     */
    public static boolean doesUserOwn(int userId, int questionId) throws ApplicationException {
        return QuestionDatabaseAccess.getQuestionsByUserId(userId).contains(new Question(questionId));
    }

    /**
     * Fetches the most recent question asked by the active User.
     * 
     * @param currentUserId
     *            the active user's id
     * @return the desired question
     * @throws ApplicationException
     */
    public static Question getAskedQuestion(int currentUserId) throws ApplicationException {
        Question result = null;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = QuestionDatabaseAccess.getAskedQuestion(currentUserId, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;

    }

    /**
     * Fetches the most recent question asked by the active User.
     * 
     * @param currentUserId
     *            the active user's id
     * @param connection
     *            the database connection to perform the statement on
     * @return the desired question
     * @throws ApplicationException
     */
    protected static Question getAskedQuestion(int currentUserId, Connection connection) throws ApplicationException {
        Question result = new Question();

        StringBuilder query = new StringBuilder("");
        query.append("SELECT * FROM " + TABLE);
        query.append(" WHERE " + COL_USER_ID);
        query.append("=" + currentUserId);
        query.append(" ORDER BY " + COL_QUESTION_TIMESTAMP);
        query.append(" DESC LIMIT 1; ");

        PreparedStatement statement = null;
        try {
            try {
                // set parameters and execute query
                statement = connection.prepareStatement(query.toString());

                ResultSet resultSet = statement.executeQuery();
                result = convertToInstance(resultSet);

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
     * Fetches the most recent question modified by the active User.
     * 
     * @param currentUserId
     *            the active user's id
     * @return the desired question
     * @throws ApplicationException
     */
    public static Question getModifiedQuestion(int currentUserId) throws ApplicationException {
        Question result = null;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = QuestionDatabaseAccess.getAskedQuestion(currentUserId, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;

    }

    /**
     * Fetches the most recent question modified by the active User.
     * 
     * @param currentUserId
     *            the active user's id
     * @param connection
     *            the database connection to perform the statement on
     * @return the desired question
     * @throws ApplicationException
     */
    protected static Question getModifiedQuestion(int currentUserId, Connection connection) throws ApplicationException {
        Question result = new Question();

        StringBuilder query = new StringBuilder("");
        query.append("SELECT * FROM " + TABLE);
        query.append(" WHERE " + COL_USER_ID);
        query.append("=" + currentUserId);
        query.append(" ORDER BY " + COL_QUESTION_MODIFIED);
        query.append(" DESC LIMIT 1; ");

        PreparedStatement statement = null;
        try {
            try {
                // set parameters and execute query
                statement = connection.prepareStatement(query.toString());

                ResultSet resultSet = statement.executeQuery();
                result = convertToInstance(resultSet);

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
     * Converts a ResultSet form the database table into an entity of the
     * Instance.
     * 
     * @param resultSet
     *            result of a get/fetch on the database table
     * @return the Instance saved in an entity
     * @throws ApplicationException
     */
    protected static Question convertToInstance(ResultSet resultSet) throws ApplicationException {
        Question entity = null;

        try {
            if (resultSet.next()) {
                entity = new Question();
                entity.setId(resultSet.getInt(COL_QUESTION_ID));
                entity.setQuestionText(resultSet.getString(COL_QUESTION_TEXT));
                entity.setTimestamp(resultSet.getTimestamp(COL_QUESTION_TIMESTAMP));
                entity.setSkipped(resultSet.getInt(COL_SKIPPED));
                entity.setDifficultyRating(resultSet.getFloat(COL_DIFFICULTY_RATING));
                entity.setReported(resultSet.getInt(COL_REPORTED));
                entity.setUserId(resultSet.getInt(COL_USER_ID));
                entity.setHas_follow_up_question(resultSet.getBoolean(COL_HAS_FOLLOW_UP));
                entity.setPrevious_question_id(resultSet.getInt(COL_PREVIOUS_ID));
                entity.setRatingCounter(resultSet.getInt(COL_RATING_COUNTER));
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
    protected static List<Question> convertToInstances(ResultSet resultSet) throws ApplicationException {
        List<Question> entities = new LinkedList<Question>();

        Question entity = QuestionDatabaseAccess.convertToInstance(resultSet);

        while (entity != null) {
            entities.add(entity);
            entity = QuestionDatabaseAccess.convertToInstance(resultSet);
        }

        return entities;
    }

}