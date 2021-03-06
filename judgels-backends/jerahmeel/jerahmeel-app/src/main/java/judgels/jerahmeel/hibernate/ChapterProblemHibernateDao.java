package judgels.jerahmeel.hibernate;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.ChapterProblemModel;
import judgels.jerahmeel.persistence.ChapterProblemModel_;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.api.problem.ProblemType;

public class ChapterProblemHibernateDao extends HibernateDao<ChapterProblemModel> implements ChapterProblemDao {
    @Inject
    public ChapterProblemHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<ChapterProblemModel> selectByProblemJid(String problemJid) {
        return selectByFilter(new FilterOptions.Builder<ChapterProblemModel>()
                .putColumnsEq(ChapterProblemModel_.problemJid, problemJid)
                .putColumnsEq(ChapterProblemModel_.status, "VISIBLE")
                .build());
    }

    @Override
    public List<ChapterProblemModel> selectAllByProblemJids(Set<String> problemJids) {
        return selectAll(new FilterOptions.Builder<ChapterProblemModel>()
                .putColumnsIn(ChapterProblemModel_.problemJid, problemJids)
                .putColumnsEq(ChapterProblemModel_.status, "VISIBLE")
                .build());
    }

    @Override
    public Optional<ChapterProblemModel> selectByChapterJidAndProblemAlias(String chapterJid, String problemAlias) {
        return selectByFilter(new FilterOptions.Builder<ChapterProblemModel>()
                .putColumnsEq(ChapterProblemModel_.chapterJid, chapterJid)
                .putColumnsEq(ChapterProblemModel_.alias, problemAlias)
                .putColumnsEq(ChapterProblemModel_.status, "VISIBLE")
                .build());
    }

    @Override
    public List<ChapterProblemModel> selectAllByChapterJid(String chapterJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ChapterProblemModel>()
                .putColumnsEq(ChapterProblemModel_.chapterJid, chapterJid)
                .putColumnsEq(ChapterProblemModel_.status, "VISIBLE")
                .build(), options);
    }

    @Override
    public List<ChapterProblemModel> selectAllBundleByChapterJid(String chapterJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ChapterProblemModel>()
                .putColumnsEq(ChapterProblemModel_.chapterJid, chapterJid)
                .putColumnsEq(ChapterProblemModel_.status, "VISIBLE")
                .putColumnsEq(ChapterProblemModel_.type, ProblemType.BUNDLE.name())
                .build(), options);
    }

    @Override
    public List<ChapterProblemModel> selectAllProgrammingByChapterJid(String chapterJid, SelectionOptions options) {
        return selectAll(new FilterOptions.Builder<ChapterProblemModel>()
                .putColumnsEq(ChapterProblemModel_.chapterJid, chapterJid)
                .putColumnsEq(ChapterProblemModel_.status, "VISIBLE")
                .putColumnsEq(ChapterProblemModel_.type, ProblemType.PROGRAMMING.name())
                .build(), options);
    }
}
