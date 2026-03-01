import React, { FormEvent } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate, useLocation, useSearchParams } from 'react-router';


export default function SearchFilter({ placeholder }: SearchFilterParams) {
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const navigate = useNavigate();
  const [searchParams, ] = useSearchParams();

  const handleSubmit = (event: FormEvent) => {
    const input = (event.target as HTMLFormElement)!.querySelector('input') as HTMLInputElement;
    const filterParams = new URLSearchParams({
      filter: input.value
    });
    navigate({
      pathname: pathname,
      search: filterParams.toString()
    });
    event.preventDefault();
  };

  return (
    <form onSubmit={handleSubmit} method="get" className="col-md-6 col-lg-4">
      <div className="input-group mb-3">
        <input type="text" name="filter" defaultValue={searchParams.get('filter') || ''} placeholder={placeholder} className="form-control" />
        <button type="submit" className="btn btn-secondary">{t('searchFilter.apply')}</button>
      </div>
    </form>
  );
}

interface SearchFilterParams {

  placeholder: string;

}
