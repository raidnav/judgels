package judgels.jerahmeel.submission.programming;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import javax.inject.Named;
import javax.inject.Singleton;
import judgels.fs.FileSystem;
import judgels.fs.FileSystems;
import judgels.fs.aws.AwsConfiguration;
import judgels.jerahmeel.JerahmeelBaseDataDir;
import judgels.sandalphon.submission.programming.GradingResponsePoller;
import judgels.sandalphon.submission.programming.GradingResponseProcessor;
import judgels.sandalphon.submission.programming.SubmissionClient;
import judgels.sandalphon.submission.programming.SubmissionRegradeProcessor;
import judgels.sandalphon.submission.programming.SubmissionRegrader;
import judgels.sandalphon.submission.programming.SubmissionSourceBuilder;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;

@Module
public class SubmissionModule {
    private final SubmissionConfiguration config;

    public SubmissionModule(SubmissionConfiguration config) {
        this.config = config;
    }

    @Provides
    @Singleton
    @SubmissionFs
    FileSystem submissionFs(Optional<AwsConfiguration> awsConfig, @JerahmeelBaseDataDir Path baseDataDir) {
        return FileSystems.get(config.getFs(), awsConfig, baseDataDir.resolve("submissions"));
    }

    @Provides
    @Singleton
    SubmissionSourceBuilder submissionSourceBuilder(@SubmissionFs FileSystem submissionFs) {
        return new SubmissionSourceBuilder(submissionFs);
    }

    @Provides
    @Singleton
    SubmissionClient submissionClient(
            SubmissionStore submissionStore,
            @Named("sealtiel") BasicAuthHeader sealtielClientAuthHeader,
            MessageService messageService,
            @Named("gabrielClientJid") String gabrielClientJid,
            ObjectMapper mapper) {

        return new SubmissionClient(
                submissionStore,
                sealtielClientAuthHeader,
                messageService,
                gabrielClientJid,
                mapper);
    }

    @Provides
    @Singleton
    SubmissionRegrader submissionRegrader(
            LifecycleEnvironment lifecycleEnvironment,
            SubmissionStore submissionStore,
            SubmissionRegradeProcessor processor) {

        ExecutorService executorService =
                lifecycleEnvironment.executorService("submission-regrade-processor-%d")
                        .maxThreads(5)
                        .minThreads(5)
                        .build();

        return new SubmissionRegrader(submissionStore, executorService, processor);
    }

    @Provides
    @Singleton
    static GradingResponsePoller gradingResponsePoller(
            LifecycleEnvironment lifecycleEnvironment,
            @Named("sealtiel") BasicAuthHeader sealtielClientAuthHeader,
            MessageService messageService,
            GradingResponseProcessor processor) {

        ExecutorService executorService =
                lifecycleEnvironment.executorService("grading-response-processor-%d")
                        .maxThreads(10)
                        .minThreads(10)
                        .build();

        return new GradingResponsePoller(sealtielClientAuthHeader, messageService, executorService, processor);
    }

    @Provides
    @Singleton
    static GradingResponseProcessor gradingResponseProcessor(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ObjectMapper mapper,
            SubmissionStore submissionStore,
            @Named("sealtiel") BasicAuthHeader sealtielClientAuthHeader,
            MessageService messageService) {

        return unitOfWorkAwareProxyFactory.create(
                GradingResponseProcessor.class,
                new Class<?>[] {
                        ObjectMapper.class,
                        SubmissionStore.class,
                        BasicAuthHeader.class,
                        MessageService.class},
                new Object[] {
                        mapper,
                        submissionStore,
                        sealtielClientAuthHeader,
                        messageService});
    }
}
