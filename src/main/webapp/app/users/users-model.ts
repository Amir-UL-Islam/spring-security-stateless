export class UsersDTO {

  constructor(data:Partial<UsersDTO>) {
    Object.assign(this, data);
  }

  id?: number|null;
  name?: string|null;
  email?: string|null;
  username?: string|null;
  password?: string|null;
  role?: number[]|null;

}
