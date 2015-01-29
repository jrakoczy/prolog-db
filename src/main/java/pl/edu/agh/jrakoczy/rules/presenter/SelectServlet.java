package pl.edu.agh.jrakoczy.rules.presenter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.prefs.InvalidPreferencesFormatException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;

import pl.edu.agh.jrakoczy.rules.model.DatabaseAccess;
import pl.edu.agh.jrakoczy.rules.model.dao.RuleDAO;
import pl.edu.agh.jrakoczy.rules.model.dto.RuleDTO;
import pl.edu.agh.jrakoczy.rules.model.parser.RuleParser;

/**
 * Servlet implementation class TestServlet
 */
public class SelectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String NAME_PARAM = "name";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SelectServlet() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String name = request.getParameter(NAME_PARAM);

		RuleDAO ruleDAO = new RuleDAO(getServletContext());
		RuleDTO ruleDTO = null;
		try {
			ruleDTO = ruleDAO.selectRule(name);
		} catch (ClassNotFoundException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			e.printStackTrace();
		} catch (InvalidPreferencesFormatException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		response.getWriter().println(RuleParser.composeRule(ruleDTO));
	}

}
