package cgwap.data_access;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import cgwap.entities.User;
import cgwap.enums.UserLevel;
import cgwap.util.Config;
import cgwap.util.database.ConnectionPool;
import cgwap.util.exception_handler.ApplicationException;

/**
 * Creates the database tables,if they don't exist already.
 * 
 */
public class CreateScheme {
    /**
     * Table chosen to indicate existence of database scheme.
     */
    private static final String CHECK_TABLE = UserDatabaseAccess.TABLE;

    private static final Logger LOGGER = Logger.getLogger(CreateScheme.class.getName());

    /*
     * Queries
     */

    // check for scheme existence
    private static final String CHECK_EXISTENCE = "SELECT EXISTS("
            + "SELECT 1"
            + " FROM information_schema.tables"
            + " WHERE table_name = '" + CHECK_TABLE
            + "')";

    // create Tables
    private static final String CREATE_USERS = "CREATE TABLE IF NOT EXISTS " + UserDatabaseAccess.TABLE + " ("
            + UserDatabaseAccess.COL_USER_ID + " SERIAL          PRIMARY KEY,"
            + UserDatabaseAccess.COL_EMAIL + " VARCHAR(255)    NOT NULL UNIQUE,"
            + UserDatabaseAccess.COL_PASSWORD_HASH + " VARCHAR(255)    DEFAULT NULL,"
            + UserDatabaseAccess.COL_NICKNAME + " VARCHAR(30)     UNIQUE,"
            + UserDatabaseAccess.COL_XP + " INT             DEFAULT NULL,"
            + UserDatabaseAccess.COL_LEVEL + " VARCHAR(30)     DEFAULT NULL,"
            + UserDatabaseAccess.COL_IS_ADMIN + " BOOLEAN     DEFAULT FALSE,"
            + UserDatabaseAccess.COL_REGISTERED + " TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ,"
            + UserDatabaseAccess.COL_IS_ACTIVE + " BOOLEAN     DEFAULT TRUE"
            + ");";

    private static final String CREATE_QUESTIONS = "CREATE TABLE IF NOT EXISTS " + QuestionDatabaseAccess.TABLE + " ("
            + QuestionDatabaseAccess.COL_QUESTION_ID + " SERIAL          PRIMARY KEY,"
            + QuestionDatabaseAccess.COL_QUESTION_TEXT + " VARCHAR(255)    NOT NULL,"
            + QuestionDatabaseAccess.COL_QUESTION_TIMESTAMP + " TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,"
            + QuestionDatabaseAccess.COL_QUESTION_MODIFIED + " TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,"
            + QuestionDatabaseAccess.COL_DIFFICULTY_RATING + " FLOAT           DEFAULT 3,"
            + QuestionDatabaseAccess.COL_SKIPPED + " INTEGER         DEFAULT NULL,"
            + QuestionDatabaseAccess.COL_REPORTED + " INTEGER         DEFAULT NULL,"
            + QuestionDatabaseAccess.COL_USER_ID + " INTEGER         NOT NULL REFERENCES users,"
            + QuestionDatabaseAccess.COL_HAS_FOLLOW_UP + " BOOLEAN         DEFAULT FALSE,"
            + QuestionDatabaseAccess.COL_PREVIOUS_ID + " INTEGER         DEFAULT NULL,"
            + QuestionDatabaseAccess.COL_RATING_COUNTER + " INTEGER         DEFAULT 0,"
            + QuestionDatabaseAccess.COL_IS_ACTIVE + " BOOLEAN         DEFAULT TRUE"
            + ");";

    private static final String CREATE_ROUNDS = "CREATE TABLE IF NOT EXISTS " + RoundsDatabaseAccess.TABLE + " ("
            + RoundsDatabaseAccess.COL_ID + " SERIAL          PRIMARY KEY,"
            + RoundsDatabaseAccess.COL_USER_ID + " INTEGER         REFERENCES users,  "
            + RoundsDatabaseAccess.COL_QUESTION_ID + " INTEGER         REFERENCES questions,"
            + RoundsDatabaseAccess.COL_START_TIME + " TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,"
            + RoundsDatabaseAccess.COL_END_TIME + " TIMESTAMP,"
            + RoundsDatabaseAccess.COL_DURATION + " INTEGER          DEFAULT 0,"
            + RoundsDatabaseAccess.COL_SCORE + " INTEGER         DEFAULT NULL,"
            + RoundsDatabaseAccess.COL_PASS + " VARCHAR(10)      DEFAULT 'fail',"
            + RoundsDatabaseAccess.COL_LIVES_LEFT + " INTEGER          DEFAULT 3,"
            + RoundsDatabaseAccess.COL_USER_COMMENT + " VARCHAR(255)    "
            + ");";

    private static final String CREATE_QUERIES = "CREATE TABLE IF NOT EXISTS " + SearchQueryDatabaseAccess.TABLE + " ("
            + SearchQueryDatabaseAccess.COL_ROUND_ID + " INTEGER         REFERENCES rounds ON DELETE CASCADE,"
            + SearchQueryDatabaseAccess.COL_QUERY + " VARCHAR(255)    NOT NULL,"
            + SearchQueryDatabaseAccess.COL_QUERY_TIMESTAMP + " TIMESTAMP       DEFAULT CURRENT_TIMESTAMP, "
            + SearchQueryDatabaseAccess.COL_FILTER_USED + " BOOLEAN         DEFAULT false,"
            + SearchQueryDatabaseAccess.COL_FILTER_PROVIDER + " VARCHAR(30), "
            + SearchQueryDatabaseAccess.COL_FILTER_LANGUAGE + " VARCHAR(30),"
            + SearchQueryDatabaseAccess.COL_FILTER_TYPE + " VARCHAR(30) "
            + ");";

    private static final String CREATE_ANSWERS = "CREATE TABLE IF NOT EXISTS " + AnswersDatabaseAccess.TABLE + " ("
            + AnswersDatabaseAccess.COL_QUESTION_ID + " INTEGER        REFERENCES questions  ON DELETE CASCADE  ,"
            + AnswersDatabaseAccess.COL_URL + " VARCHAR(255)    NOT NULL,"
            + "UNIQUE                   (question_id, url)"
            + ");";

    private static final String CREATE_TAGS = "CREATE TABLE IF NOT EXISTS " + TagsDatabaseAccess.TABLE + " ("
            + TagsDatabaseAccess.COL_QUESTION_ID + " INTEGER       REFERENCES questions  ON DELETE CASCADE,"
            + TagsDatabaseAccess.COL_TAG + " VARCHAR(255)  DEFAULT NULL"
            + ");";

    private static final String CREATE_FUNCTION_UPDATE_QUESTION_MODIFIED =
            "DROP FUNCTION IF EXISTS update_question_modified_column() CASCADE; "
                    + "CREATE FUNCTION update_question_modified_column() "
                    + "RETURNS TRIGGER AS ' "
                    + "BEGIN "
                    + "    NEW.question_modified = NOW(); "
                    + "  RETURN NEW; "
                    + "END; "
                    + "' LANGUAGE plpgsql;";

    // Create modification trigger.
    private static final String CREATE_TRIGGER_QUESTION_MODIFIED =
            "DROP TRIGGER IF EXISTS update_question_modified_column ON " + QuestionDatabaseAccess.TABLE + ";"
                    + " CREATE TRIGGER update_question_modified_column"
                    + " BEFORE UPDATE"
                    + " ON " + QuestionDatabaseAccess.TABLE
                    + " FOR EACH ROW EXECUTE PROCEDURE"
                    + " update_question_modified_column();";

    /**
     * Creates database scheme.
     * 
     * @throws ApplicationException
     *             if the creation of a part of the scheme failed.
     */
    public static void createSchemes() throws ApplicationException {
        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();

            boolean exists = CreateScheme.checkScheme(connection, CHECK_EXISTENCE);
            if (!exists) {
                CreateScheme.executeStatement(connection, CREATE_USERS);
                CreateScheme.executeStatement(connection, CREATE_QUESTIONS);
                CreateScheme.executeStatement(connection, CREATE_TAGS);
                CreateScheme.executeStatement(connection, CREATE_ANSWERS);
                CreateScheme.executeStatement(connection, CREATE_ROUNDS);
                CreateScheme.executeStatement(connection, CREATE_QUERIES);

                CreateScheme.executeStatement(connection, CREATE_FUNCTION_UPDATE_QUESTION_MODIFIED);
                CreateScheme.executeStatement(connection, CREATE_TRIGGER_QUESTION_MODIFIED);

                // create initial admin
                User adminUser = new User(1, Config.ADMIN_EMAIL_ADDRESS, Config.ADMIN_NICKNAME, true,
                        Config.ADMIN_NICKNAME, UserLevel.Questling, 0);
                if (UserDatabaseAccess.store(adminUser) != null) {
                    LOGGER.info("Successfully created initial user with email " + Config.ADMIN_EMAIL_ADDRESS);
                } else {
                    LOGGER.warning("Error on creating initial user");
                }
            }
        } finally {
            ConnectionPool.releaseConnection(connection);
        }
    }

    private static boolean checkScheme(Connection connection, String sqlQuery) throws ApplicationException {
        Statement statement = null;

        try {
            try {
                // explicitly no use of prepared statements as all given queries
                // must not contain user-generated content
                statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sqlQuery);
                resultSet.next();
                return resultSet.getBoolean(1);
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw new ApplicationException("Failed to create Scheme " + sqlQuery, e);
        }
    }

    private static void executeStatement(Connection connection, String sqlQuery) throws ApplicationException {
        Statement statement = null;

        try {
            try {
                statement = connection.createStatement();
                statement.execute(sqlQuery);
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw new ApplicationException("Failed to create Scheme " + sqlQuery, e);
        }
    }
}