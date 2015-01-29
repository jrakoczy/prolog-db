package pl.edu.agh.jrakoczy.rules.model.dao;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.InvalidPreferencesFormatException;

import javax.servlet.ServletContext;

import pl.edu.agh.jrakoczy.rules.model.dto.ForallDTO;
import pl.edu.agh.jrakoczy.rules.model.dto.RuleDTO;

public class RuleDAO extends AbstractDAO {

	private static final String NAME_COL = "name";
	private static final String FUNC_CODE_COL = "fcode";
	private static final String COND_CODE_COL = "ccode";

	private static final String SELECT_RULE = "SELECT r.name, cp.code AS "
			+ COND_CODE_COL + ", fp.code AS " + FUNC_CODE_COL
			+ " FROM rules AS r"
			+ " LEFT JOIN cond_rule_xref AS crx ON r.id = crx.rule_id"
			+ " LEFT JOIN cond_predicates AS cp ON crx.cond_id = cp.id"
			+ " LEFT JOIN func_rules_xref AS frx ON r.id = frx.rule_id"
			+ " LEFT JOIN func_predicates AS fp ON frx.func_id = fp.id"
			+ " WHERE r.name = ?;";

	private static final String RULES_QUERY = "INSERT INTO rules (id, name) VALUES (DEFAULT, ?) RETURNING id;";
	private static final String FUNC_QUERY = "INSERT INTO func_predicates (id, name, code) VALUES (DEFAULT, ?, ?) RETURNING id;";
	private static final String COND_QUERY = "INSERT INTO cond_predicates (id, name, code, negation) VALUES (DEFAULT, ?, ?, TRUE) RETURNING id;";
	private static final String FUNC_RULE_QUERY = "INSERT INTO func_rules_xref (id, rule_id, func_id) VALUES (DEFAULT, ?, ?) RETURNING id;";
	private static final String COND_RULE_QUERY = "INSERT INTO cond_rule_xref (id, rule_id, cond_id) VALUES (DEFAULT, ?, ?) RETURNING id;";
	private static final String FORALL_RULE_QUERY = "INSERT INTO loop_rules_xref (id, rule_id, loop_id) VALUES (DEFAULT, ? ?) RETURNING id";
	private static final String FUNC_FORALL_QUERY = "INSERT INTO func_loop_xref (id, func_id, loop_id) VALUES (DEFAULT, ? ?) RETURNING id";
	private static final String COND_FORALL_QUERY = "INSERT INTO cond_loop_xref (id, cond_id, loop_id) VALUES (DEFAULT, ? ?) RETURNING id";

	public RuleDAO(ServletContext context) {
		super(context);
	}

	/**
	 * 
	 * @param name
	 * @param description
	 * @param picture
	 * @param surveyId
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @throws InvalidPreferencesFormatException
	 */
	public void insertRecord(RuleDTO ruleDTO) throws ClassNotFoundException,
			SQLException, IOException, InvalidPreferencesFormatException {

		int ruleId = insertRule(ruleDTO.getName());

		for (String funcPredicate : ruleDTO.getFuncPredicates()) {
			Integer funcId = insertFunc(funcPredicate);
			//insertFuncRule(ruleId, funcId);
		}

		for (String condPredicate : ruleDTO.getCondPredicates()) {
			Integer condId = insertCond(condPredicate);
			insertCondRule(ruleId, condId);
		}

		ForallDAO forallDAO = new ForallDAO(context);
		Integer forallId = forallDAO.insertForall();
		insertForallRule(ruleId, forallId);

	}

	/**
	 * 
	 * @param ruleName
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @throws InvalidPreferencesFormatException
	 */
	public int insertRule(String ruleName) throws ClassNotFoundException,
			SQLException, IOException, InvalidPreferencesFormatException {

		String rulesQuery = RULES_QUERY;

		StatementLambda stLambda = (statement) -> {
			try {
				statement.setString(1, ruleName);
				return statement.executeQuery();
			} catch (Exception e) {
				throw new SQLException();
			}
		};

		return alter(rulesQuery, stLambda);
	}

	/**
	 * 
	 * @param funcPredicate
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @throws InvalidPreferencesFormatException
	 */
	public int insertFunc(String funcPredicate) throws ClassNotFoundException,
			SQLException, IOException, InvalidPreferencesFormatException {

		String funcQuery = FUNC_QUERY;
		StatementLambda stLambda = (statement) -> {
			try {
				statement.setString(1, "func");
				statement.setString(2, funcPredicate);
				return statement.executeQuery();
			} catch (Exception e) {
				throw new SQLException();
			}
		};

		return alter(funcQuery, stLambda);
	}

	/**
	 * 
	 * @param ruleDTO
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @throws InvalidPreferencesFormatException
	 */
	public int insertCond(String condPredicate) throws ClassNotFoundException,
			SQLException, IOException, InvalidPreferencesFormatException {

		String condQuery = COND_QUERY;
		StatementLambda stLambda = (statement) -> {
			try {
				statement.setString(1, "cond");
				statement.setString(2, condPredicate);
				return statement.executeQuery();
			} catch (Exception e) {
				throw new SQLException();
			}
		};

		return alter(condQuery, stLambda);
	}

	/**
	 * 
	 * @param ruleDTO
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @throws InvalidPreferencesFormatException
	 */
	public int insertCondRule(int ruleId, int condId)
			throws ClassNotFoundException, SQLException, IOException,
			InvalidPreferencesFormatException {

		String condRuleQuery = COND_RULE_QUERY;
		StatementLambda stLambda = (statement) -> {
			try {
				statement.setInt(1, ruleId);
				statement.setInt(1, condId);
				return statement.executeQuery();
			} catch (Exception e) {
				throw new SQLException();
			}
		};

		return alter(condRuleQuery, stLambda);
	}

	/**
	 * 
	 * @param ruleDTO
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @throws InvalidPreferencesFormatException
	 */
	public int insertFuncRule(int ruleId, int funcId)
			throws ClassNotFoundException, SQLException, IOException,
			InvalidPreferencesFormatException {

		String funcRuleQuery = FUNC_RULE_QUERY;
		StatementLambda stLambda = (statement) -> {
			try {
				statement.setInt(1, ruleId);
				statement.setInt(2, funcId);
				return statement.executeQuery();
			} catch (Exception e) {
				throw new SQLException();
			}
		};

		return alter(funcRuleQuery, stLambda);
	}

	/**
	 * 
	 * @param ruleDTO
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @throws InvalidPreferencesFormatException
	 */
	public int insertForallRule(int ruleId, int forallId)
			throws ClassNotFoundException, SQLException, IOException,
			InvalidPreferencesFormatException {

		String forallRuleQuery = FORALL_RULE_QUERY;
		StatementLambda stLambda = (statement) -> {
			try {
				statement.setInt(1, ruleId);
				statement.setInt(2, forallId);
				return statement.executeQuery();
			} catch (Exception e) {
				throw new SQLException();
			}
		};

		return alter(forallRuleQuery, stLambda);
	}

	/**
	 * 
	 * @param ruleDTO
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @throws InvalidPreferencesFormatException
	 */
	public int insertCondForall(int condId, int forallId)
			throws ClassNotFoundException, SQLException, IOException,
			InvalidPreferencesFormatException {

		String condForallQuery = COND_FORALL_QUERY;
		StatementLambda stLambda = (statement) -> {
			try {
				statement.setInt(1, condId);
				statement.setInt(2, forallId);
				return statement.executeQuery();
			} catch (Exception e) {
				throw new SQLException();
			}
		};

		return alter(condForallQuery, stLambda);
	}

	/**
	 * 
	 * @param ruleDTO
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @throws InvalidPreferencesFormatException
	 */
	public int insertFuncForall(int funcId, int forallId)
			throws ClassNotFoundException, SQLException, IOException,
			InvalidPreferencesFormatException {

		String funcForallQuery = FUNC_FORALL_QUERY;
		StatementLambda stLambda = (statement) -> {
			try {
				statement.setInt(1, funcId);
				statement.setInt(2, forallId);
				return statement.executeQuery();
			} catch (Exception e) {
				throw new SQLException();
			}
		};

		return alter(funcForallQuery, stLambda);
	}

	/**
	 * 
	 * @param surveyId
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @throws InvalidPreferencesFormatException
	 */
	public RuleDTO selectRule(String ruleName) throws ClassNotFoundException,
			SQLException, IOException, InvalidPreferencesFormatException {

		String query = SELECT_RULE;
		StatementLambda stLambda = (statement) -> {
			try {
				statement.setString(1, ruleName);
				return statement.executeQuery();
			} catch (Exception e) {
				throw new SQLException();
			}
		};

		ResultSet rs = select(query, stLambda);
		return createRuleDTO(rs, ruleName);
	}

	/**
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 * @throws InvalidPreferencesFormatException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private RuleDTO createRuleDTO(ResultSet rs, String ruleName)
			throws SQLException, ClassNotFoundException, IOException,
			InvalidPreferencesFormatException {
		String name = "";
		List<String> funcPredicates = new ArrayList<String>();
		List<String> condPredicates = new ArrayList<String>();
		List<ForallDTO> forallPredicates = new ArrayList<ForallDTO>();

		while (rs.next()) {
			name = rs.getString(NAME_COL);
			funcPredicates.add(rs.getString(FUNC_CODE_COL));
			condPredicates.add(rs.getString(COND_CODE_COL));
		}

		RuleDTO ruleDTO = new RuleDTO();
		ruleDTO.setName(name);
		ruleDTO.setCondPredicates(condPredicates);
		ruleDTO.setFuncPredicates(funcPredicates);
		ForallDAO forallDAO = new ForallDAO(context);
		forallPredicates = forallDAO.selectForall(ruleName);
		ruleDTO.setForallPredicates(forallPredicates);

		return ruleDTO;
	}
}
