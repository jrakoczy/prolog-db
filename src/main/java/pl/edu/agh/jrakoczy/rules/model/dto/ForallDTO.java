package pl.edu.agh.jrakoczy.rules.model.dto;

import java.util.List;

public class ForallDTO {
	private String condPredicate;
	private List<String> funcPredicates;
	
	
	public String getCondPredicate() {
		return condPredicate;
	}

	public void setCondPredicate(String condPredicate) {
		this.condPredicate = condPredicate;
	}

	public List<String> getFuncPredicates() {
		return funcPredicates;
	}

	public void setFuncPredicates(List<String> funcPredicates) {
		this.funcPredicates = funcPredicates;
	}
}
