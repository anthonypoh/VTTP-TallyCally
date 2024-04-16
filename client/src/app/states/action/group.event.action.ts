import { createAction, props } from '@ngrx/store';
import { GroupEvent } from '../../models';

export const setGroupEvents = createAction('[Group Event] Set Group Events', props<{ groupEvents: GroupEvent[] }>());
export const clearGroupEvents = createAction('[Group Event] Clear Group Events');
