package searchengine.dto.statistics;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

//@Accessors(fluent = true, chain = true)
@AllArgsConstructor
@Getter
@Setter
public class StatisticsData {
    private TotalStatistics total;
    private List<DetailedStatisticsItem> detailed;
}
