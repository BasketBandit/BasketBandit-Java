package basketbandit.core.tfl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "lineStatuses"
})

@JsonIgnoreProperties(ignoreUnknown = true)
public class LineManager {

    @JsonProperty("name")
    private String name;

    @JsonProperty("lineStatuses")
    private List<LineStatus> lineStatuses = new ArrayList<>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("lineStatuses")
    public String getLineStatusString() {
        StringBuilder statuses = new StringBuilder();

        for(LineStatus line: lineStatuses) {
            statuses.append(line.getStatusSeverityDescription()).append("\n");
        }

        int index = statuses.lastIndexOf("\n");
        statuses.replace(index, index + 1, "");

        return statuses.toString();
    }

}
