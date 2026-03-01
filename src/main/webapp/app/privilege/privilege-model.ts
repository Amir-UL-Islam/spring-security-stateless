export class PrivilegeDTO {

  constructor(data:Partial<PrivilegeDTO>) {
    Object.assign(this, data);
  }

  id?: number|null;
  name?: string|null;

}
