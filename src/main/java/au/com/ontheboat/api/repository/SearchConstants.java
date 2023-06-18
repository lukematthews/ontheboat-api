package au.com.ontheboat.api.repository;

public class SearchConstants {
    public static final String LIKE_CLAUSE = "LOWER(CONCAT('%',:searchTerm, '%'))";
}
