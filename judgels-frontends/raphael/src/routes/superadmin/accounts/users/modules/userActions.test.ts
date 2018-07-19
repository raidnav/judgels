import { userActions } from './userActions';
import { AppState } from '../../../../../modules/store';
import { user } from '../../../../../fixtures/state';
import { sessionState, token } from '../../../../../fixtures/state';
import { Page, OrderDir } from '../../../../../modules/api/pagination';
import { User } from '../../../../../modules/api/jophiel/user';

describe('userActions', () => {
  let dispatch: jest.Mock<any>;
  let userAPI: jest.Mocked<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  beforeEach(() => {
    dispatch = jest.fn();

    userAPI = {
      getUsers: jest.fn(),
    };
  });

  describe('getUsers()', () => {
    const currentPage = 1;
    const orderBy = 'username';
    const orderDir = OrderDir.ASC;
    const { getUsers } = userActions;
    const doGetUsers = async () => getUsers(currentPage, orderBy, orderDir)(dispatch, getState, { userAPI });

    beforeEach(async () => {
      const users: Page<User> = {
        totalData: 1,
        data: [user],
      };
      userAPI.getUsers.mockReturnValue(users);

      await doGetUsers();
    });

    it('calls API to get users', () => {
      expect(userAPI.getUsers).toHaveBeenCalledWith(token, currentPage, orderBy, orderDir);
    });
  });
});
