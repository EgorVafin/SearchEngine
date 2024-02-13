package searchengine.dto.statistics;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

public class StatisticsData {
    private TotalStatistics total;
    private List<DetailedStatisticsItem> detailed;

    public TotalStatistics getTotal() {
        return total;
    }

    public StatisticsData setTotal(TotalStatistics total) {
        this.total = total;
        return this;
    }

    public List<DetailedStatisticsItem> getDetailed() {
        return detailed;
    }

    public StatisticsData setDetailed(List<DetailedStatisticsItem> detailed) {
        this.detailed = detailed;
        return this;
    }
}
