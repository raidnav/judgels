package judgels.jerahmeel;

import com.palantir.websecurity.WebSecurityBundle;
import io.dropwizard.Application;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import judgels.fs.aws.AwsModule;
import judgels.jerahmeel.hibernate.JerahmeelHibernateBundle;
import judgels.jerahmeel.jophiel.JophielModule;
import judgels.jerahmeel.sandalphon.SandalphonModule;
import judgels.jerahmeel.submission.programming.SubmissionModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.service.jaxrs.JudgelsObjectMappers;
import judgels.service.jersey.JudgelsJerseyFeature;

public class JerahmeelApplication extends Application<JerahmeelApplicationConfiguration> {
    private final HibernateBundle<JerahmeelApplicationConfiguration> hibernateBundle = new JerahmeelHibernateBundle();

    public static void main(String[] args) throws Exception {
        new JerahmeelApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<JerahmeelApplicationConfiguration> bootstrap) {
        JudgelsObjectMappers.configure(bootstrap.getObjectMapper());

        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(new JerahmeelMigrationsBundle());
        bootstrap.addBundle(new WebSecurityBundle());
    }

    @Override
    public void run(JerahmeelApplicationConfiguration config, Environment env) throws Exception {
        JerahmeelConfiguration jerahmeelConfig = config.getJerahmeelConfig();
        JerahmeelComponent component = DaggerJerahmeelComponent.builder()
                .awsModule(new AwsModule(jerahmeelConfig.getAwsConfig()))
                .jophielModule(new JophielModule(jerahmeelConfig.getJophielConfig()))
                .judgelsHibernateModule(new JudgelsHibernateModule(hibernateBundle))
                .sandalphonModule(new SandalphonModule(jerahmeelConfig.getSandalphonConfig()))
                .submissionModule(new SubmissionModule(jerahmeelConfig.getSubmissionConfig()))
                .jerahmeelModule(new JerahmeelModule(jerahmeelConfig))
                .build();

        env.jersey().register(JudgelsJerseyFeature.INSTANCE);

        env.jersey().register(component.adminResource());
        env.jersey().register(component.courseResource());
        env.jersey().register(component.courseChapterResource());
        env.jersey().register(component.chapterLessonResource());
        env.jersey().register(component.chapterProblemResource());
        env.jersey().register(component.chapterSubmissionResource());
        env.jersey().register(component.pingResource());
    }
}
