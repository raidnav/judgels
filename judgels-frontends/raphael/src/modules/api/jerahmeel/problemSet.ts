import { stringify } from 'query-string';

import { APP_CONFIG } from '../../../conf';
import { get } from '../http';
import { Page } from '../pagination';

export interface ProblemSet {
  id: number;
  jid: string;
  slug: string;
  name: string;
  description: string;
}

export interface ProblemSetsResponse {
  data: Page<ProblemSet>;
}

export const baseProblemSetsURL = `${APP_CONFIG.apiUrls.jerahmeel}/problemsets`;

export function baseProblemSetURL(problemSetJid: string) {
  return `${baseProblemSetsURL}/${problemSetJid}`;
}

export const problemSetAPI = {
  getProblemSets: (token: string, name?: string, page?: number): Promise<ProblemSetsResponse> => {
    const params = stringify({ name, page });
    return get(`${baseProblemSetsURL}?${params}`, token);
  },

  getProblemSetBySlug: (problemSetSlug: string): Promise<ProblemSet> => {
    return get(`${baseProblemSetsURL}/slug/${problemSetSlug}`);
  },
};