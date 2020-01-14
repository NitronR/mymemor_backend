package com.mymemor.mymemor.response;

import com.mymemor.mymemor.model.SearchResult;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class SearchResponse extends StringResponse {
    @Getter
    @Setter
    private List<SearchResult> searchResults;
}
