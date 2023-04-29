package com.sailingwebtools.marina.repository;

public class SearchConstants {
    public static final String LIKE_CLAUSE = "LOWER(CONCAT('%',:searchTerm, '%'))";
}
