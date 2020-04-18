package svm.jenna;

import java.util.Objects;
import java.util.Set;

public class TermCacheKey {
    private Set<String> skip;
    private String key;

    public TermCacheKey(String key) {
        this.key = key;
    }

    public TermCacheKey( String key,Set<String> skip) {
        this.skip = skip;
        this.key = key;
    }

    public Set<String> getSkip() {
        return skip;
    }

    public void setSkip(Set<String> skip) {
        this.skip = skip;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TermCacheKey that = (TermCacheKey) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
