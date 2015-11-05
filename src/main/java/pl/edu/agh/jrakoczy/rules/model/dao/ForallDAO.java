package pl.edu.agh.jrakoczy.rules.model.dao;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.InvalidPreferencesFormatException;

import javax.servlet.ServletContext;

import pl.edu.agh.jrakoczy.rules.model.dto.ForallDTO;

public class ForallDAO extends AbstractDAO {

	private static final String FUNC_CODE_COL = "fcode";
	private static final String COND_CODE_COL = "ccode";
	private static final String SELECT_FORALL = "SELECT fp.code AS "
			+ FUNC_CODE_COL + ", cp.code AS " + COND_CODE_COL
			+ " FROM rules AS r"
			+ " INNER JOIN loop_rules_xref AS lrx ON r.id = lrx.rule_id"
			+ " INNER JOIN loop_predicates AS lp ON lrx.loop_id = lp.id"
			+ " INNER JOIN func_loop_xref AS flx ON lp.id = flx.loop_id"
			+ " INNER JOIN func_predicates AS fp ON flx.func_id = fp.id"
			+ " INNER JOIN cond_loop_xref AS clx ON lp.id = clx.loop_id"
			+ " INNER JOIN cond_predicates AS cp ON clx.cond_id = cp.id"
			+ " WHERE r.name = ?;";

	public ForallDAO(ServletContext context) {
		super(context);
	}

	public List<ForallDTO> selectForall(String ruleName)
			throws ClassNotFoundException, SQLException, IOException,
			InvalidPreferencesFormatException {

		String query = SELECT_FORALL;
		StatementLambda stLambda = (statement) -> {
			try {
				statement.setString(1, ruleName);
				return statement.executeQuery();
			} catch (Exception e) {
				throw new SQLException();
			}
		};

		ResultSet rs = select(query, stLambda);
		return createForallDTOs(rs);
	}
	
	/**
	 * 
	 * @param ruleName
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @throws InvalidPreferencesFormatException
	 */
	public int insertForall() throws ClassNotFoundException,
			SQLException, IOException, InvalidPreferencesFormatException {

		String rulesQuery = "INSERT INTO loop_predicates (id, name) VALUES (DEFAULT, 'forall') RETURNING id;";
		
		StatementLambda stLambda = (statement) -> {
			try {
				return statement.executeQuery();
			} catch (Exception e) {
				throw new SQLException();
			}
		};

		return alter(rulesQuery, stLambda);
	}

	/**
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private List<ForallDTO> createForallDTOs(ResultSet rs) throws SQLException {
		Map<String, String> predicates = new HashMap<String, String>();
		List<ForallDTO> forallPredicates = new ArrayList<ForallDTO>();
		while (rs.next()) {
			String condPredicate = rs.getString(COND_CODE_COL);
			String funcPredicate = rs.getString(FUNC_CODE_COL);
			predicates.put(condPredicate, funcPredicate);
		}

		for (Map.Entry<String, String> entry : predicates.entrySet()) {
			ForallDTO forallDTO = new ForallDTO();
			forallDTO.setCondPredicate(entry.getKey());
			List<String> funcPredicates = new ArrayList<String>();
			funcPredicates.add(entry.getValue());
			forallDTO.setFuncPredicates(funcPredicates);
		}

		return forallPredicates;
	}

}
