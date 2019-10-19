import * as React from 'react';

import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { ContentCardLink } from '../../../../components/ContentCardLink/ContentCardLink';
import { Course } from '../../../../modules/api/jerahmeel/course';

import './CourseCard.css';

export interface CourseCardProps {
  course: Course;
}

export class CourseCard extends React.PureComponent<CourseCardProps> {
  render() {
    const { course } = this.props;

    return (
      <ContentCardLink to={`/training/course/${course.id}`} className="course-card">
        <h4>{`${course.id}. ${course.name}`}</h4>
        <hr />
        <HtmlText>{course.description}</HtmlText>
      </ContentCardLink>
    );
  }
}