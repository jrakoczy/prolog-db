package pl.edu.agh.jrakoczy.rules.model.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.prefs.InvalidPreferencesFormatException;

import javax.servlet.ServletContext;

import pl.edu.agh.jrakoczy.rules.model.DatabaseAccess;

abstract class AbstractDAO {

	protected ServletContext context;

	/**
	 * 
	 * @author kuba
	 *
	 */
	public interface StatementLambda {
		public ResultSet query(PreparedStatement statement) throws SQLException;
	}

	public AbstractDAO(ServletContext context) {
		this.context = context;
	}

	public ResultSet select(String query, StatementLambda stLambda)
			throws ClassNotFoundException, SQLException, IOException,
			InvalidPreferencesFormatException {
		return executeStatement(query, stLambda);
	}

	public int alter(String query, StatementLambda stLambda)
			throws ClassNotFoundException, SQLException, IOException,
			InvalidPreferencesFormatException {
		ResultSet rs = executeStatement(query, stLambda);
		if (rs != null && rs.next()) {
			return rs.getInt(1);
		}
		return -1;
	}

	/**
	 * 
	 * @param query
	 * @param stLambda
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @throws InvalidPreferencesFormatException
	 */
	private ResultSet executeStatement(String query, StatementLambda stLambda)
			throws ClassNotFoundException, SQLException, IOException,
			InvalidPreferencesFormatException {
		DatabaseAccess dbAccess = new DatabaseAccess(context);

		try (Connection connection = dbAccess.connect();) {
			PreparedStatement statement = connection.prepareStatement(query);
			return stLambda.query(statement);
		}
	}
}
