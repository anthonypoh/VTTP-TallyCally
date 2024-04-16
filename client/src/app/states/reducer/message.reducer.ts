import { createReducer, on } from '@ngrx/store';
import { setMessage } from '../action/message.action';

export interface State {
  message: string;
}

export const initialState: State = {
  message: ''
};

export const messageReducer = createReducer(
  initialState,
  on(setMessage, (state, { message }) => ({ ...state, message }))
);
