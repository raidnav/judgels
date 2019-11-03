package judgels.jerahmeel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.fs.aws.AwsConfiguration;
import judgels.jerahmeel.submission.programming.SubmissionConfiguration;
import judgels.jophiel.api.JophielClientConfiguration;
import judgels.sandalphon.api.SandalphonClientConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableJerahmeelConfiguration.class)
public interface JerahmeelConfiguration {
    String getBaseDataDir();

    @JsonProperty("jophiel")
    JophielClientConfiguration getJophielConfig();

    @JsonProperty("sandalphon")
    SandalphonClientConfiguration getSandalphonConfig();

    @JsonProperty("aws")
    Optional<AwsConfiguration> getAwsConfig();

    @JsonProperty("submission")
    SubmissionConfiguration getSubmissionConfig();

    class Builder extends ImmutableJerahmeelConfiguration.Builder {}
}
