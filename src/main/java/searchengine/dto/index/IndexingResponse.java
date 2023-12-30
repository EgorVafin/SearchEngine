package searchengine.dto.index;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IndexingResponse {
    private boolean result;
    private String error;

    public static IndexingResponse success(){
        return new IndexingResponse(true, null);
    }

    public static IndexingResponse error(String error){
        return new IndexingResponse(false, error);
    }
}
