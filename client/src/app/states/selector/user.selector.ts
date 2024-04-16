import { createSelector, createFeatureSelector } from '@ngrx/store';
import { User } from '../../models';

// Select the entire user state slice
export const selectUser = createFeatureSelector<User>('user');

// Select specific user properties
export const selectUserMobile = createSelector(
  selectUser,
  (state: User) => state.mobile
);

export const selectUserName = createSelector(
  selectUser,
  (state: User) => state.name
);

export const selectUserEmail = createSelector(
  selectUser,
  (state: User) => state.email
);
