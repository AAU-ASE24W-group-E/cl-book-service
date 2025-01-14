package at.aau.ase.cl.domain;

public class WildcardCriterion {
    String criterion;

    public WildcardCriterion(String criterion) {
        this.criterion = criterion;
    }

    public boolean isBlank() {
        return criterion == null || criterion.isBlank();
    }

    public boolean isPresent() {
        return !isBlank();
    }

    public String prepare() {
        String trimmed = criterion.strip();
        // instead of escaping %, we replace it with _ to match it as single character
        String escaped = trimmed.replace('%', '_');
        return "%" + escaped + "%";
    }
}
