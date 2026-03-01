export class RoleDTO {

  constructor(data:Partial<RoleDTO>) {
    Object.assign(this, data);
  }

  id?: number|null;
  name?: string|null;
  description?: string|null;
  privilege?: number[]|null;

}
