package cgwap.data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import cgwap.entities.User;
import cgwap.enums.UserLevel;
import cgwap.util.database.ConnectionPool;
import cgwap.util.exception_handler.ApplicationException;

/**
 * Accesses the table containing the user's. It supports the persistence
 * functions CRUD(create, read, update,
 * delete).
 * 
 * 
 */
public class UserDatabaseAccess {

    /**
     * The database table name.
     */
    protected static final String TABLE = "users";

    /**
     * The database table column name for the identifier.
     */
    protected static final String COL_USER_ID = "user_id";

    /**
     * The database table column name for the nickname.
     */
    protected static final String COL_NICKNAME = "nickname";

    /**
     * The database table column name for the email.
     */
    protected static final String COL_EMAIL = "email";

    /**
     * The database table column name for the password.
     */
    protected static final String COL_PASSWORD_HASH = "password_hash";

    /**
     * The database table column name for the isAdmin flag.
     */
    protected static final String COL_IS_ADMIN = "is_admin";

    /**
     * The database table column name for the user's xp.
     */
    protected static final String COL_XP = "xp";

    /**
     * The database table column name for the user's level.
     */
    protected static final String COL_LEVEL = "level";
    
    /**
     * The database table column name for the user's status.
     */
    protected static final String COL_IS_ACTIVE = "is_active";
   
    /**
     * The database table column name for the user's status.
     */
    protected static final String COL_REGISTERED = "registered";
    

    // static class
    private UserDatabaseAccess() {
    }

    // *************************************************
    // FETCH
    // *************************************************
    /**
     * Returns a List of all Instances in storage.
     * 
     * @return a List of Instances
     * @throws ApplicationException
     */
    public static List<User> fetch() throws ApplicationException {
        return UserDatabaseAccess.fetch(0, 0);
    }

    /**
     * Returns a List of a limited number of Instances in storage, after the
     * offset was jumped.
     * 
     * @param limit
     *            - maximal number of Instances to be returned
     * @param offset
     *            - number of Instances to be skipped
     * @return a List of InstancesInstances
     * @throws ApplicationException
     */
    public static List<User> fetch(int limit, int offset) throws ApplicationException {
        List<User> result = null;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = UserDatabaseAccess.fetch(limit, offset, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'FETCH' statement on the database table to return a List of
     * a
     * limited number of
     * Instances in storage, after the offset was jumped.
     * 
     * @param limit
     *            - maximal number of Instances to be returned
     * @param offset
     *            - number of Instances to be skipped
     * @param connection
     *            - the database connection to perform the statement on
     * @return a List of Instances
     * @throws ApplicationException
     */
    protected static List<User> fetch(int limit, int offset, Connection
            connection) throws ApplicationException {
        List<User> result = null;

        String sqlString = "";
        sqlString += "SELECT * FROM " + UserDatabaseAccess.TABLE;
        sqlString += " OFFSET ?";
        if (limit != 0) {
            sqlString += " LIMIT ?";
        }

        PreparedStatement statement = null;
        try {
            try {
                statement = connection.prepareStatement(sqlString);

                // set variables
                statement.setInt(1, offset);
                if (limit != 0) {
                    statement.setInt(2, limit);
                }

                // get result
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
    // GET
    // *************************************************
    /**
     * Return a existing Instance from storage, based on its id.
     * 
     * @param entity
     *            - an entity with the ID of the Instance to load
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    public static User getById(User entity) throws ApplicationException {
        User result = null;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = UserDatabaseAccess.getById(entity, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'SELECT' statement on the database table to return a existing
     * Instance from
     * storage, based on its id.
     * 
     * @param connection
     *            - the database connection to perform the statement on
     * @param entity
     *            - an entity with the ID of the Instance to load
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    protected static User getById(User entity, Connection connection) throws ApplicationException {
        User result = null;

        String sqlString = "";
        sqlString += "SELECT * FROM " + UserDatabaseAccess.TABLE;
        sqlString += " WHERE " + UserDatabaseAccess.COL_USER_ID + " = ?";

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
     * Return existing Instances from storage, based on the user's login data.
     * 
     * @param entity
     *            - an entity with the user's login data of the Instances to load
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    public static User getByLogin(User entity) throws ApplicationException {
        User result = null;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = UserDatabaseAccess.getByLogin(entity, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'SELECT' statement on the database table to return existing
     * Instances from
     * storage, based on the user's login data.
     * 
     * @param connection
     *            - the database connection to perform the statement on
     * @param entity
     *            - an entity with the user's login data of the Instances to load
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    protected static User getByLogin(User entity, Connection connection) throws ApplicationException {
        User result = null;

        String sqlString = "";
        sqlString += "SELECT * FROM " + UserDatabaseAccess.TABLE;
        sqlString += " WHERE " + UserDatabaseAccess.COL_EMAIL + " = ?";
        sqlString += " AND " + UserDatabaseAccess.COL_PASSWORD_HASH + " = ?";

        PreparedStatement statement = null;
        try {
            try {
                statement = connection.prepareStatement(sqlString);
                statement.setString(1, entity.getEmail());
                statement.setString(2, entity.getPassword());

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
     * Return existing Instances from storage, based on the email.
     * 
     * @param entity
     *            an entity with the email of the Instances to load
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    public static User getByEmail(User entity) throws ApplicationException {
        User result = null;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = UserDatabaseAccess.getByEmail(entity, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'SELECT' statement on the database table to return existing
     * Instances from
     * storage, based on the email.
     * 
     * @param connection
     *            the database connection to perform the statement on
     * @param entity
     *            an entity with the email of the Instances to load
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    protected static User getByEmail(User entity, Connection connection) throws ApplicationException {
        User result = null;

        String sqlString = "";
        sqlString += "SELECT * FROM " + UserDatabaseAccess.TABLE;
        sqlString += " WHERE " + UserDatabaseAccess.COL_EMAIL + " = ?";

        PreparedStatement statement = null;
        try {
            try {
                statement = connection.prepareStatement(sqlString);
                statement.setString(1, entity.getEmail());
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
     * Return existing Instances from storage, based on the nickname.
     * 
     * @param entity
     *            an entity with the nickname of the Instances to load
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    public static User getByNickname(User entity) throws ApplicationException {
        User result = null;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = UserDatabaseAccess.getByNickname(entity, connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'SELECT' statement on the database table to return existing
     * Instances from
     * storage, based on the nickname.
     * 
     * @param connection
     *            the database connection to perform the statement on
     * @param entity
     *            an entity with the nickname of the Instances to load
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    protected static User getByNickname(User entity, Connection connection) throws ApplicationException {
        User result = null;

        String sqlString = "";
        sqlString += "SELECT * FROM " + UserDatabaseAccess.TABLE;
        sqlString += " WHERE " + UserDatabaseAccess.COL_NICKNAME + " = ?";

        PreparedStatement statement = null;
        try {
            try {
                statement = connection.prepareStatement(sqlString);
                statement.setString(1, entity.getNickname());
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
     * Return a existing Instance from storage, based on its xp.
     * 
     * @return the found Instances, otherwise null
     * @throws ApplicationException
     */
    public static List<User> getBestPlayers() throws ApplicationException {
        List<User> result = null;

        Connection connection = null;

        try {
            connection = ConnectionPool.getConnection();
            result = UserDatabaseAccess.getBestPlayers(connection);
        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'SELECT' statement on the database table to return a existing
     * Instance from
     * storage, based on its xp.
     * 
     * @param connection
     *            the database connection to perform the statement on
     * @return the found Instances, otherwise null
     * @throws ApplicationException
     */
    protected static List<User> getBestPlayers(Connection connection)
            throws ApplicationException {
        // LOGGER.fine("SELECT on table " + RoundDatabaseAccess.DB_TABLE);

        List<User> result = new LinkedList<User>();

        StringBuilder query = new StringBuilder("");
        query.append("SELECT * FROM " + TABLE);
        query.append(" ORDER BY " + COL_XP + " DESC");
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

    /**
     * Checks if a given email address does already exist.
     * 
     * @param entity
     *            an entity with the email of the Instances to load
     * @return whether the instance could be found
     * @throws ApplicationException
     */
    public static boolean doesEmailExist(User entity) throws ApplicationException {
        return UserDatabaseAccess.getByEmail(entity) != null;
    }

    /**
     * Checks if a given nickname does already exist.
     * 
     * @param entity
     *            an entity with the nickname of the Instances to load
     * @return the found Instance, otherwise null
     * @throws ApplicationException
     */
    public static boolean doesNicknameExist(User entity) throws ApplicationException {
        return UserDatabaseAccess.getByNickname(entity) != null;
    }

    // *************************************************
    // COUNT
    // *************************************************
    // /**
    // * Count the number of all Instances in storage.
    // *
    // * @return number of Instances stored in storage
    // * @throws ApplicationException
    // */
    // public static int count() throws ApplicationException {
    // int result = 0;
    //
    // Connection connection = null;
    // try {
    // connection = ConnectionPool.getConnection();
    // result = UserDatabaseAccess.count(connection);
    // } finally {
    // ConnectionPool.releaseConnection(connection);
    // }
    //
    // return result;
    // }

    // /**
    // * Performs a 'COUNT' statement on the database table to count the number
    // of all Instances in
    // * storage.
    // *
    // * @param connection - the database connection to perform the statement on
    // * @return number of Instances stored in storage
    // * @throws ApplicationException
    // */
    // protected static int count(Connection connection) throws
    // ApplicationException {
    // LOGGER.fine("Perform 'COUNT' on table " + UserDatabaseAccess.DB_TABLE);
    // int result = 0;
    //
    // String sqlString = "SELECT COUNT(*) FROM " + UserDatabaseAccess.DB_TABLE;
    //
    // PreparedStatement statement = null;
    // try {
    // try {
    // statement = connection.prepareStatement(sqlString);
    // ResultSet resultSet = statement.executeQuery();
    // if (resultSet.next()) {
    // result = resultSet.getInt(1);
    // }
    // } finally {
    // if (statement != null) {
    // statement.close();
    // }
    // }
    // } catch (SQLException e) {
    // throw new ApplicationException("Failed to count", e);
    // }
    //
    // return result;
    // }

    // *************************************************
    // STORE, UPDATE, DESTROY
    // *************************************************
    /**
     * Stores a new Instance in Database.
     * 
     * @param entity
     *            - the Instance to save
     * @return the Instance, if successfully saved
     * @throws ApplicationException
     */
    public static User store(User entity) throws ApplicationException {
        User result = null;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();

            connection.setAutoCommit(false);

            // check (within the same transaction!) whether the email address is
            // already in use
            if (UserDatabaseAccess.getByEmail(entity, connection) != null) {
                return null;
            }

            // perform store
            result = UserDatabaseAccess.store(entity, connection);

            connection.commit();

        } catch (SQLException | ApplicationException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException excep) {
                }
            }
        } finally {
            try {
                connection.setAutoCommit(true);
                ConnectionPool.releaseConnection(connection);
            } catch (SQLException e) {
                // LOGGER.log(Level.SEVERE, "Enable Auto Commit failed", e);
            }
        }

        return result;
    }

    /**
     * Performs a 'INSERT' statement on the database table to store a new
     * Instance.
     * 
     * @param entity
     *            the Instance to save
     * @param connection
     *            the database connection to perform the statement on
     * @return the generated entity, null if email already in use
     * @throws ApplicationException
     */
    protected static User store(User entity, Connection connection) throws ApplicationException {

        // build query
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(UserDatabaseAccess.TABLE);
        query.append("(");
        query.append(UserDatabaseAccess.COL_EMAIL);
        query.append(", ");
        query.append(UserDatabaseAccess.COL_PASSWORD_HASH);
        query.append(", ");
        query.append(UserDatabaseAccess.COL_IS_ADMIN);
        query.append(", ");
        query.append(UserDatabaseAccess.COL_NICKNAME);
        query.append(", ");
        query.append(UserDatabaseAccess.COL_XP);
        query.append(", ");
        query.append(UserDatabaseAccess.COL_LEVEL);
        query.append(") VALUES (?, ?, ?, ?, ?, ?) RETURNING *;");

        PreparedStatement statement = null;
        try {
            try {
                // set parameters and execute query
                statement = connection.prepareStatement(query.toString());
                statement.setString(1, entity.getEmail());
                statement.setString(2, entity.getPassword());
                statement.setBoolean(3, entity.isAdmin());
                statement.setString(4, entity.getNickname());
                statement.setInt(5, entity.getXp());
                statement.setString(6, entity.getLevel());

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
     * Updates a changed Instance in Database.
     * 
     * @param entity
     *            - the Instance to save
     * @return true if the Instance could be saved, otherwise false
     * @throws ApplicationException
     */
    public static boolean update(User entity) throws ApplicationException {
        boolean result = false;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = UserDatabaseAccess.update(entity, connection);

        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'UPDATE' statement on the database table to update an existing
     * Instance.
     * 
     * @param entity
     *            - the Instance to save
     * @param connection
     *            - the database connection to perform the statement on
     * @return true if the Instance could be saved, otherwise false
     * @throws ApplicationException
     */
    protected static boolean update(User entity, Connection connection) throws ApplicationException {
        boolean result = false;

        // build query
        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(UserDatabaseAccess.TABLE);
        query.append(" SET ");
        query.append("(");
        query.append(UserDatabaseAccess.COL_EMAIL);
        query.append(", ");
        query.append(UserDatabaseAccess.COL_PASSWORD_HASH);
        query.append(", ");
        query.append(UserDatabaseAccess.COL_IS_ADMIN);
        query.append(", ");
        query.append(UserDatabaseAccess.COL_XP);
        query.append(", ");
        query.append(UserDatabaseAccess.COL_LEVEL);
        query.append(", ");
        query.append(UserDatabaseAccess.COL_NICKNAME);
        query.append(") = (?, ?, ?, ?, ?, ?) ");
        query.append("WHERE ");
        query.append(UserDatabaseAccess.COL_USER_ID);
        query.append(" = ? ;");

        PreparedStatement statement = null;
        try {
            try {
                // set parameters and execute query
                statement = connection.prepareStatement(query.toString());
                statement.setString(1, entity.getEmail());
                statement.setString(2, entity.getPassword());
                statement.setBoolean(3, entity.isAdmin());
                statement.setInt(4, entity.getXp());
                statement.setString(5, entity.getLevel());
                statement.setString(6, entity.getNickname());

                statement.setInt(7, entity.getId());

                int resultRows = statement.executeUpdate();
                result = resultRows > 0;
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw new ApplicationException("Failed to update", e);
        }

        return result;
    }

    /**
     * Deactivates an Instance in storage.
     * 
     * @param entity
     *            - the Instance to deactivate
     * @return true, if a Instance was deactivated; otherwise false
     * @throws ApplicationException
     */
    public static boolean remove(User entity) throws ApplicationException {
        boolean result = false;

        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            result = UserDatabaseAccess.remove(entity, connection);

        } finally {
            ConnectionPool.releaseConnection(connection);
        }

        return result;
    }

    /**
     * Performs a 'UPDATE' statement on the database table to deactivate an
     * existing Instance.
     * 
     * @param entity
     *            the Instance to remove
     * @param connection
     *            the database connection to perform the statement on
     * @return true, if a Instance was deactivated; otherwise false
     * @throws ApplicationException
     */
    protected static boolean remove(User entity, Connection connection) throws ApplicationException {

        StringBuilder sqlQuery = new StringBuilder("UPDATE ");
        sqlQuery.append(TABLE);
        sqlQuery.append(" WHERE ");
        sqlQuery.append(COL_USER_ID);
        sqlQuery.append(" = ? SET" + COL_IS_ACTIVE);
        sqlQuery.append(" = false;");
        
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

    // *************************************************
    // HELPER METHODES
    // *************************************************

    /**
     * Converts a ResultSet form the database table into an entity of the
     * Instance.
     * 
     * @param resultSet
     *            - result of a get/fetch on the database table
     * @return the Instance saved in an entity
     * @throws ApplicationException
     */
    protected static User convertToInstance(ResultSet resultSet) throws ApplicationException {
        User entity = null;

        try {
            if (resultSet.next()) {
                entity = new User();
                entity.setId(resultSet.getInt(COL_USER_ID));
                entity.setEmail(resultSet.getString(COL_EMAIL));
                entity.setHashedPassword(resultSet.getString(COL_PASSWORD_HASH));
                entity.setAdmin(resultSet.getBoolean(COL_IS_ADMIN));
                entity.setNickname(resultSet.getString(COL_NICKNAME));
                entity.setXp(resultSet.getInt(COL_XP));
                entity.setLevel(UserLevel.valueOfString(resultSet.getString(COL_LEVEL)));
                entity.setRegistered(resultSet.getTimestamp(COL_REGISTERED));
                entity.setActive(resultSet.getBoolean(COL_IS_ACTIVE));
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
    protected static List<User> convertToInstances(ResultSet resultSet) throws ApplicationException {
        List<User> entities = new LinkedList<User>();

        User entity = UserDatabaseAccess.convertToInstance(resultSet);

        while (entity != null) {
            entities.add(entity);
            entity = UserDatabaseAccess.convertToInstance(resultSet);
        }

        return entities;
    }

}
