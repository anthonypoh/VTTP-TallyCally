import { ActionReducerMap } from '@ngrx/store';
import { userReducer } from './user.reducer';
import { messageReducer } from './message.reducer';
import { MessageState, User } from '../../models';
import { GroupEventState, groupEventReducer } from './group.event.reducer';

export interface AppState {
  user: User
  message: MessageState
  groupEvents: GroupEventState
}

export const reducers: ActionReducerMap<AppState> = {
  user: userReducer,
  message: messageReducer,
  groupEvents: groupEventReducer
};
