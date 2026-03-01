export class UrlsDTO {

  constructor(data:Partial<UrlsDTO>) {
    Object.assign(this, data);
  }

  id?: number|null;
  endpoint?: string|null;
  method?: string|null;
  privilege?: number|null;

}
