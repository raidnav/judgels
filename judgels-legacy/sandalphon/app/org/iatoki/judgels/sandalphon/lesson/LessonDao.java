package org.iatoki.judgels.sandalphon.lesson;

import com.google.inject.ImplementedBy;
import judgels.persistence.JudgelsDao;

import java.util.List;

@ImplementedBy(LessonHibernateDao.class)
public interface LessonDao extends JudgelsDao<LessonModel> {

    List<String> getJidsByAuthorJid(String authorJid);

    LessonModel findBySlug(String slug);

    boolean existsBySlug(String slug);
}
