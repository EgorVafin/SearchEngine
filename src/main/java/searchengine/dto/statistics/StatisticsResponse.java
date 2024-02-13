package searchengine.dto.statistics;

import lombok.*;
import lombok.experimental.Accessors;

public class StatisticsResponse {
    private boolean result;
    private StatisticsData statistics;

    public boolean isResult() {
        return result;
    }

    public StatisticsResponse setResult(boolean result) {
        this.result = result;
        return this;
    }

    public StatisticsData getStatistics() {
        return statistics;
    }

    public StatisticsResponse setStatistics(StatisticsData statistics) {
        this.statistics = statistics;
        return this;
    }
}
