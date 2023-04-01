package neilyich.lr2;

import com.fasterxml.jackson.annotation.JsonProperty;
import neilyich.BrownRobinsonConfiguration;
import neilyich.FormattingConfiguration;
import org.ejml.simple.SimpleMatrix;

public record Lr2Configuration(
    SimpleMatrix c,
    @JsonProperty("inverseMatrixConfig")
    InverseMatrixConfiguration inverseMatrix,
    @JsonProperty("brownRobinsonConfig")
    BrownRobinsonConfiguration brownRobinson
) {
    public record InverseMatrixConfiguration(
            FormattingConfiguration formatting
    ) {}
}
