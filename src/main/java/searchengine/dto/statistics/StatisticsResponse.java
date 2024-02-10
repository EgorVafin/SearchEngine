package searchengine.dto.statistics;

import lombok.*;
import lombok.experimental.Accessors;

//@Accessors(fluent = true, chain = true)
@AllArgsConstructor
@Getter
@Setter
public class StatisticsResponse {
    private boolean result;
    private StatisticsData statistics;
}
