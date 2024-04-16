export interface User {
  name: string,
  email: string,
  password: string | undefined,
  mobile: number,
  fcmToken: string,
  msg: string | undefined
}
export interface MessageState {
  message: string;
}
export interface Event {
  _id: string
  title: string,
  description: string,
  location: string
  start: Date,
  end: Date,
  timestamp: Date
}
export interface Group {
  _id: string,
  groupName: string,
  members: Array<string>
  events: Event[]
}

export interface GroupEvent {
  email: string,
  groupName: string,
  events: Event[],
  group_id: string,
}

export interface FcmObject {
  title: string,
  body: string
}

export interface Holiday {
  date: string;
  localName: string;
  name: string;
  countryCode: string;
  fixed: boolean;
  global: boolean;
  counties: null | string[];
  launchYear: number | null;
  types: string[];
}


