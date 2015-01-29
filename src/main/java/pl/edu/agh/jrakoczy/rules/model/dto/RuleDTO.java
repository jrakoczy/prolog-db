package pl.edu.agh.jrakoczy.rules.model.dto;

import java.util.List;

public class RuleDTO {
	private String name;
	private List<String> funcPredicates;
	private List<String> condPredicates;
	private List<ForallDTO> forallPredicates;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getFuncPredicates() {
		return funcPredicates;
	}

	public void setFuncPredicates(List<String> funcPredicates) {
		this.funcPredicates = funcPredicates;
	}

	public List<String> getCondPredicates() {
		return condPredicates;
	}

	public void setCondPredicates(List<String> condPredicates) {
		this.condPredicates = condPredicates;
	}

	public List<ForallDTO> getForallPredicates() {
		return forallPredicates;
	}

	public void setForallPredicates(List<ForallDTO> forallPredicates) {
		this.forallPredicates = forallPredicates;
	}

}
