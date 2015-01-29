package pl.edu.agh.jrakoczy.rules.model;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.prefs.InvalidPreferencesFormatException;

import javax.servlet.ServletContext;

import pl.edu.agh.jrakoczy.rules.credentials.DBCredentials;

/**
 * A class used to access a database. Uses JDBC to establish a connection and
 * execute a query.
 * 
 * @author kuba
 */
public class DatabaseAccess {

	/**
	 * A key of user name stored in an external file.
	 */
	private static final String unameKey = "username";

	/**
	 * A key of password name stored in an external file.
	 */
	private static final String passwordKey = "password";

	/**
	 * A path to an external file containing database credentials.
	 */
	private static final String credentialsPath = "/classified/dbcredentials.xml";

	private static final String driverName = "org.postgresql.Driver";
	private static final String urlPrefix = "jdbc:postgresql://";
	private static final String hostname = "localhost";
	private static final String port = "5432";
	private static final String dbname = "rules";

	private ServletContext context;

	/**
	 * Creates a new {@code DatabaseAccess} instance using a given
	 * {@code ServletContext}.
	 * 
	 * @param context
	 *            a {@code SerlvetContext} of a servlet that invoked constructor
	 */
	public DatabaseAccess(ServletContext context) {
		this.context = context;
	}

	/**
	 * Establishes a connection to a database. Retrieves credentials and
	 * composes a proper URL. Then creates a connection using prepared values.
	 * 
	 * 
	 * @return a new {@code Connection} to a database
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws InvalidPreferencesFormatException
	 * @throws SQLException
	 */
	public Connection connect() throws ClassNotFoundException, IOException,
			InvalidPreferencesFormatException, SQLException {

		// Retrieve credentials
		InputStream inStream = context.getResourceAsStream(credentialsPath);
		
		DBCredentials dbCredentials = new DBCredentials(inStream);
		String username = dbCredentials.get(unameKey);
		String password = dbCredentials.get(passwordKey);

		// Compose url
		String url = composeDBUrl();

		// Establish connection
		Class.forName(driverName);
		Connection connection = DriverManager.getConnection(url, username,
				password);

		connection.setAutoCommit(true);
		return connection;
	}

	/**
	 * Composes JDBC URL using consts defined in the class.
	 * 
	 * @return a JDBC URL
	 */
	private String composeDBUrl() {
		return urlPrefix + hostname + ":" + port + "/" + dbname;
	}
}
