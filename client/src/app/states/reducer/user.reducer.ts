// user.reducer.ts
import { createReducer, on } from '@ngrx/store';
// import { setUser, clearUser } from './user.actions';
import { setUser, clearUser } from '../action/user.action';
import { User } from '../../models';

export const initialState: User = {
  name: '',
  email: '',
  password: undefined,
  mobile: 0,
  fcmToken: '',
  msg: undefined
};

const _userReducer = createReducer(
  initialState,
  on(setUser, (state, { user }) => ({ ...state, ...user })),
  on(clearUser, () => initialState)
);

export function userReducer(state: User | undefined, action: any) {
  return _userReducer(state, action);
}
