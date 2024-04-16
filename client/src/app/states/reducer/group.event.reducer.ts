import { createReducer, on } from '@ngrx/store';
import { GroupEvent } from '../../models';
import * as GroupEventActions from '../action/group.event.action'

export interface GroupEventState {
  groupEvents: GroupEvent[];
}

export const initialState: GroupEventState = {
  groupEvents: []
};

export const groupEventReducer = createReducer(
  initialState,
  on(GroupEventActions.setGroupEvents, (state, { groupEvents }) => ({
    ...state,
    groupEvents
  })),
  on(GroupEventActions.clearGroupEvents, state => ({
    ...state,
    groupEvents: []
  }))
);