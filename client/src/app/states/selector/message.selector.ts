import { createSelector, createFeatureSelector } from '@ngrx/store';
import { MessageState } from '../../models';

export const selectMessageState = createFeatureSelector<MessageState>('message');

export const selectMessage = createSelector(
  selectMessageState,
  (state: MessageState) => state.message
);


