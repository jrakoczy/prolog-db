package pl.edu.agh.jrakoczy.rules.model.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.edu.agh.jrakoczy.rules.model.dto.ForallDTO;
import pl.edu.agh.jrakoczy.rules.model.dto.RuleDTO;

public class RuleParser {

	private static final String RULE_DELIMITER = ":-";
	private static final String COND_DELIMITER = "then";
	private static final String PREDICATE_DELIMITER = ",";
	private static final String FORALL_COMMAND = "forall";
	private static final String OPEN_BLOCK = "(";
	private static final String CLOSE_BLOCK = ")";
	private static final String CLOSE_RULE = ".";
	private static final String NULL_STR = "null";
	private static final String EMPTY_REGEX = "^\\s*$";
	private static final String DISPENSABLES_REGEX = ",*\\s*";
	private static final String PREDICATE_REGEX = DISPENSABLES_REGEX
			+ "(\\+)?\\s*([^(]*\\([^)]*\\))";
	private static final String FORALL_REGEX = DISPENSABLES_REGEX + "("
			+ FORALL_COMMAND + "\\(" + PREDICATE_REGEX + ",(.*\\))\\))";

	/**
	 * 
	 * @param rule
	 * @return
	 */
	public static String composeRule(RuleDTO rule) {
		String name = rule.getName();
		String condPredicatesString = buildPredicates(rule.getCondPredicates());
		String funcPredicatesString = buildPredicates(rule.getFuncPredicates());
		String forallPredicatesString = buildForallPredicates(rule
				.getForallPredicates());

		return name
				+ RULE_DELIMITER
				+ condPredicatesString
				+ PREDICATE_DELIMITER
				+ COND_DELIMITER
				+ (funcPredicatesString.isEmpty() ? "" : PREDICATE_DELIMITER
						+ funcPredicatesString)
				+ (forallPredicatesString.isEmpty() ? "" : PREDICATE_DELIMITER
						+ forallPredicatesString) + CLOSE_RULE;

	}

	/**
	 * 
	 * @param forallPredicate
	 * @return
	 */
	public static String composeForall(ForallDTO forallPredicate) {
		String condPredicateString = forallPredicate.getCondPredicate();
		String funcPredicatesString = buildPredicates(forallPredicate
				.getFuncPredicates());

		return FORALL_COMMAND + OPEN_BLOCK + condPredicateString + CLOSE_BLOCK
				+ PREDICATE_DELIMITER + OPEN_BLOCK + funcPredicatesString
				+ CLOSE_BLOCK + CLOSE_BLOCK;
	}

	/**
	 * 
	 * @param forallPredicates
	 * @return
	 */
	private static String buildForallPredicates(List<ForallDTO> forallPredicates) {

		List<String> predicatesList = new ArrayList<String>();

		for (ForallDTO forallPredicate : forallPredicates) {
			String forallPredicateString = composeForall(forallPredicate);
			predicatesList.add(forallPredicateString);
		}

		return buildPredicates(predicatesList);
	}

	/**
	 * 
	 * @param prediactes
	 * @return
	 */
	private static String buildPredicates(List<String> prediactes) {
		if (prediactes.isEmpty())
			return "";

		StringBuilder sb = new StringBuilder();
		for (String predicate : prediactes) {
			sb.append(PREDICATE_DELIMITER);
			sb.append(predicate);
		}
		return sb.toString().substring(1);
	}

	/**
	 * 
	 * @param code
	 * @return
	 * @throws ParsingException
	 * @throws IOException
	 */
	public static RuleDTO parseRule(String code) throws ParsingException,
			IOException {
		RuleDTO rule = new RuleDTO();

		String body = parseName(rule, code);
		parseBody(rule, body);

		return rule;
	}

	/**
	 * 
	 * 
	 * @param code
	 * @return
	 * @throws ParsingException
	 * @throws IOException
	 */
	public static ForallDTO parseForall(String code) throws ParsingException,
			IOException {
		ForallDTO forallPredicate = new ForallDTO();
		Pattern pattern = Pattern.compile(FORALL_REGEX);
		Matcher matcher = pattern.matcher(code);

		if (!matcher.matches())
			throw new ParsingException();

		String condPredicate = matcher.group(2);
		String stripped = stripParentheses(matcher.group(3));

		List<String> funcPredicates = parsePredicates(stripped,
				PREDICATE_REGEX, 2);

		forallPredicate.setCondPredicate(condPredicate);
		forallPredicate.setFuncPredicates(funcPredicates);

		return forallPredicate;
	}

	/**
	 * 
	 * @param rule
	 * @param code
	 * @return
	 * @throws ParsingException
	 */
	private static String parseName(RuleDTO rule, String code)
			throws ParsingException {
		String ruleElements[] = code.split(RULE_DELIMITER);

		validateSplitResult(ruleElements);
		String name = ruleElements[0].trim();
		rule.setName(name);
		return ruleElements[1];
	}

	/**
	 * 
	 * @param rule
	 * @param body
	 * @throws ParsingException
	 * @throws IOException
	 */
	private static void parseBody(RuleDTO rule, String body)
			throws ParsingException, IOException {

		List<ForallDTO> forallDtos = new ArrayList<ForallDTO>();

		List<String> forallPredicates = parsePredicates(body, FORALL_REGEX, 1);
		body = body.replaceAll(FORALL_REGEX, "");
		String predicates[] = body.split(COND_DELIMITER);
		validateSplitResult(predicates);

		List<String> condPredicates = parsePredicates(predicates[0],
				PREDICATE_REGEX, 2);
		List<String> funcPredicates = parsePredicates(predicates[1],
				PREDICATE_REGEX, 2);

		for (String predicate : forallPredicates) {
			ForallDTO forallDto = parseForall(predicate);
			forallDtos.add(forallDto);
		}

		rule.setForallPredicates(forallDtos);
		rule.setCondPredicates(condPredicates);
		rule.setFuncPredicates(funcPredicates);
	}

	/**
	 * Validates the result of split operation by checking the length of the
	 * results array. The aforementioned length has to equal 2.
	 * 
	 * @param results
	 * @throws ParsingException
	 */
	private static void validateSplitResult(String results[])
			throws ParsingException {
		if (results.length != 2)
			throw new ParsingException();
	}

	/**
	 * Parses @{code code} creating the list of prolog predicates/facts.
	 * 
	 * @param code
	 * @return
	 */
	private static List<String> parsePredicates(String code, String regex,
			int groupNum) {

		LinkedList<String> parsed = new LinkedList<String>();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(code);

		while (matcher.find()) {
			String predicate = matcher.group(groupNum).trim();
			if (predicate.matches(EMPTY_REGEX) || predicate.contains(NULL_STR))
				continue;
			parsed.add(predicate);
		}

		return parsed;
	}

	/**
	 * Strips {@code code} from the outermost parentheses.
	 * 
	 * @param code
	 * @return
	 */
	private static String stripParentheses(String code) {

		if (code.charAt(0) == '(')
			return code.substring(1, code.length() - 1);

		return code;
	}
}
