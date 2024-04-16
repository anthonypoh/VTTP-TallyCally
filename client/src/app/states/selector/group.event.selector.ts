import { createFeatureSelector, createSelector } from '@ngrx/store';
import { GroupEventState } from '../reducer/group.event.reducer';

export const selectGroupEvent = createFeatureSelector<GroupEventState>('groupEvents');

export const selectGroupEvents = createSelector(
  selectGroupEvent,
  (state: GroupEventState) => state.groupEvents
);
