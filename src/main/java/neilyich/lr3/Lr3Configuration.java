package neilyich.lr3;

import com.fasterxml.jackson.annotation.JsonProperty;
import neilyich.BrownRobinsonConfiguration;
import neilyich.FormattingConfiguration;

public record Lr3Configuration(
        H h,
        @JsonProperty("analyticSolutionConfig")
        AnalyticSolutionConfiguration analyticSolution,
        @JsonProperty("numericSolutionConfig")
        NumericSolutionConfiguration numericSolution
) {
    public record AnalyticSolutionConfiguration(
            FormattingConfiguration formatting
    ) {}

    public record NumericSolutionConfiguration(
            @JsonProperty("brownRobinsonConfig")
            BrownRobinsonConfiguration brownRobinson,
            FormattingConfiguration formatting,
            int  maxN,
            double maxE,
            int lastResultsCount,
            int stepsLimit
    ) {}
}
