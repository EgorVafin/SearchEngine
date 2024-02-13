package searchengine.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class SearchResponse {
    private boolean result;
    private long count;
    private List<SearchData> searchDataList;
}
