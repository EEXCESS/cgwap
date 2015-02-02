package cgwap.entities;

import java.sql.Timestamp;
import java.util.Date;

public class SearchQuery {
    /**
     * Data Transfer Object to represent a search query.
     */

    private int roundId;
    private String query;
    private boolean filterUsed = false;
    private String filterProvider = "none";
    private String filterLanguage = "none";
    private String filterType = "none";

    private Date timestamp;

    public SearchQuery() {
    }

    public SearchQuery(int roundId) {
        this.roundId = roundId;
    }

    public SearchQuery(int resultId, String query, boolean filter_used, String filter_provider, String filter_language,
            String filter_type, Date timestamp) {
        this.roundId = resultId;
        this.query = query;
        this.filterUsed = filter_used;
        this.filterProvider = filter_provider;
        this.filterLanguage = filter_language;
        this.filterType = filter_type;
        this.timestamp = timestamp;
    }

    public int getRoundId() {
        return roundId;
    }

    public void setRoundId(int resultId) {
        this.roundId = resultId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isFilterUsed() {
        return filterUsed;
    }

    public void setFilterUsed(boolean filterUsed) {
        this.filterUsed = filterUsed;
    }

    public String getFilterProvider() {
        return filterProvider;
    }

    public void setFilterProvider(String filterProvider) {
        this.filterProvider = filterProvider;
    }

    public String getFilterLanguage() {
        return filterLanguage;
    }

    public void setFilterLanguage(String filterLanguage) {
        this.filterLanguage = filterLanguage;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + roundId;
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SearchQuery other = (SearchQuery) obj;
        if (roundId != other.roundId)
            return false;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        return true;
    }

}