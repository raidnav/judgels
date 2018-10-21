package judgels.uriel.api.contest.clarification;

import static java.util.Optional.empty;
import static judgels.uriel.api.AbstractServiceIntegrationTests.URIEL_JDBC_SUFFIX;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_JID;
import static judgels.uriel.api.mocks.MockJophiel.mockJophiel;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.time.Instant;
import java.util.List;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.AbstractServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestService;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.contestant.ContestContestantService;
import judgels.uriel.api.contest.module.ContestModuleService;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.persistence.AdminRoleModel;
import judgels.uriel.persistence.ContestClarificationModel;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModuleModel;
import judgels.uriel.role.AdminRoleStore;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@WithHibernateSession(
        urlSuffix = URIEL_JDBC_SUFFIX,
        models = {
                AdminRoleModel.class,
                ContestModel.class,
                ContestModuleModel.class,
                ContestContestantModel.class,
                ContestClarificationModel.class})
class ContestClarificationServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private static WireMockServer mockJophiel;
    private ContestService contestService = createService(ContestService.class);
    private ContestModuleService moduleService = createService(ContestModuleService.class);
    private ContestContestantService contestantService = createService(ContestContestantService.class);
    private ContestClarificationService clarificationService = createService(ContestClarificationService.class);

    @BeforeAll
    static void setUpMocks() {
        mockJophiel = mockJophiel();
        mockJophiel.start();
    }

    @BeforeAll
    static void setUpSession(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = createComponent(sessionFactory);

        AdminRoleStore adminRoleStore = component.adminRoleStore();
        adminRoleStore.addAdmin(ADMIN_JID);
    }

    @AfterAll
    static void tearDownMocks() {
        mockJophiel.shutdown();
    }

    @Test
    void end_to_end_flow() {
        Contest contest = contestService.createContest(
                ADMIN_HEADER,
                new ContestCreateData.Builder().slug("contest").build());
        contestService.updateContest(ADMIN_HEADER, contest.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.now())
                .build());

        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), ContestModuleType.REGISTRATION);
        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), ContestModuleType.CLARIFICATION);

        contestantService.registerMyselfAsContestant(USER_A_HEADER, contest.getJid());
        contestantService.registerMyselfAsContestant(USER_B_HEADER, contest.getJid());

        clarificationService.createClarification(USER_A_HEADER, contest.getJid(), new ContestClarificationData.Builder()
                .topicJid(contest.getJid())
                .title("Snack")
                .question("Is snack provided?")
                .build());

        clarificationService.createClarification(USER_B_HEADER, contest.getJid(), new ContestClarificationData.Builder()
                .topicJid(contest.getJid())
                .title("Printing")
                .question("Can we print?")
                .build());

        List<ContestClarification> clarifications = clarificationService
                .getClarifications(ADMIN_HEADER, contest.getJid(), empty(), empty()).getData().getData();

        ContestClarification clarification1 = clarifications.get(0);
        ContestClarification clarification2 = clarifications.get(1);

        assertThat(clarification2.getUserJid()).isEqualTo(USER_A_JID);
        assertThat(clarification2.getTopicJid()).isEqualTo(contest.getJid());
        assertThat(clarification2.getTitle()).isEqualTo("Snack");
        assertThat(clarification2.getQuestion()).isEqualTo("Is snack provided?");
        assertThat(clarification2.getStatus()).isEqualTo(ContestClarificationStatus.ASKED);
        assertThat(clarification2.getAnswer()).isEmpty();
        assertThat(clarification2.getAnswererJid()).isEmpty();

        assertThat(clarification1.getUserJid()).isEqualTo(USER_B_JID);

        ContestClarificationAnswerData answer = new ContestClarificationAnswerData.Builder()
                .answer("Yes!")
                .build();
        clarificationService.answerClarification(ADMIN_HEADER, contest.getJid(), clarification1.getJid(), answer);

        clarifications = clarificationService
                .getClarifications(ADMIN_HEADER, contest.getJid(), empty(), empty()).getData().getData();
        clarification1 = clarifications.get(0);

        assertThat(clarification1.getAnswer()).contains("Yes!");
        assertThat(clarification1.getAnswererJid()).contains(ADMIN_JID);
    }
}
