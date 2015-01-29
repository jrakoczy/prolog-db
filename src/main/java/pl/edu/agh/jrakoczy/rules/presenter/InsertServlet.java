package pl.edu.agh.jrakoczy.rules.presenter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.prefs.InvalidPreferencesFormatException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pl.edu.agh.jrakoczy.rules.model.dao.RuleDAO;
import pl.edu.agh.jrakoczy.rules.model.dto.RuleDTO;
import pl.edu.agh.jrakoczy.rules.model.parser.ParsingException;
import pl.edu.agh.jrakoczy.rules.model.parser.RuleParser;

/**
 * Servlet implementation class InsertServlet
 */
public class InsertServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CODE_PARAM = "code";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InsertServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String code = request.getParameter(CODE_PARAM);
		RuleDTO ruleDTO = null;
		try {
			ruleDTO = RuleParser.parseRule(code);
		} catch (ParsingException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
		
		RuleDAO ruleDAO = new RuleDAO(getServletContext());
		try {
			ruleDAO.insertRecord(ruleDTO);
		} catch (ClassNotFoundException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		} catch (InvalidPreferencesFormatException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}

}
