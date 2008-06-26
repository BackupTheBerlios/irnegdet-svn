package at.ac.tuwien.ir.negdetector;

public class NegationDataErrors {

	private boolean err_nf_negpat = false;
	private boolean err_nf_negphr = false;
	private boolean err_mtoa_tregex_negpat = false;
	private boolean err_mtoa_tregex_negphr = false;
	private boolean err_mtof_negpat = false;
	private boolean err_mtof_negphr = false;
	
	public boolean isNotFoundNegPattern() {
		return err_nf_negpat;
	}
	public boolean isNotFoundNegPhrase() {
		return err_nf_negphr;
	}
	public boolean isAppliedMoreThanOneTregexNegPattern() {
		return err_mtoa_tregex_negpat;
	}
	public boolean isAppliedMoreThanOneTregexNegPhrase() {
		return err_mtoa_tregex_negphr;
	}
	public boolean isMoreThanOneFoundNegPattern() {
		return err_mtof_negpat;
	}
	public boolean isMoreThanOneFoundNegPhrase() {
		return err_mtof_negphr;
	}
	
	void setNotFoundNegPattern(boolean notFoundNegPattern) {
		this.err_nf_negpat = notFoundNegPattern;
	}
	void setNotFoundNegPhrase(boolean notFoundNegPhrase) {
		this.err_nf_negphr = notFoundNegPhrase;
	}
	void setAppliedMoreThanOneTregexNegPattern(boolean appliedMoreThanOneTregexNegPattern) {
		this.err_mtoa_tregex_negpat = appliedMoreThanOneTregexNegPattern;
	}
	void setAppliedMoreThanOneTregexNegPhrase(boolean appliedMoreThanOneTregexNegPhrase) {
		this.err_mtoa_tregex_negphr = appliedMoreThanOneTregexNegPhrase;
	}
	void setMoreThanOneFoundNegPattern(boolean moreThanOneFoundNegPattern) {
		this.err_mtof_negpat = moreThanOneFoundNegPattern;
	}
	void setMoreThanOneFoundNegPhrase(boolean moreThanOneFoundNegPhrase) {
		this.err_mtof_negphr = moreThanOneFoundNegPhrase;
	}
}
