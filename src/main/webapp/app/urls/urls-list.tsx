import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useNavigate, useSearchParams } from 'react-router';
import { handleServerError, getListParams } from 'app/common/utils';
import { UrlsDTO } from 'app/urls/urls-model';
import { PagedModel, Pagination } from 'app/common/list-helper/pagination';
import axios from 'axios';
import SearchFilter from 'app/common/list-helper/search-filter';
import Sorting from 'app/common/list-helper/sorting';
import useDocumentTitle from 'app/common/use-document-title';


export default function UrlsList() {
  const { t } = useTranslation();
  useDocumentTitle(t('urls.list.headline'));

  const [urlses, setUrlses] = useState<PagedModel<UrlsDTO>|undefined>(undefined);
  const navigate = useNavigate();
  const [searchParams, ] = useSearchParams();
  const listParams = getListParams();
  const sortOptions = {
    'id,ASC': t('urls.list.sort.id,ASC'), 
    'endpoint,ASC': t('urls.list.sort.endpoint,ASC'), 
    'method,ASC': t('urls.list.sort.method,ASC')
  };

  const getAllUrlses = async () => {
    try {
      const response = await axios.get('/api/urls?' + listParams);
      setUrlses(response.data);
    } catch (error: any) {
      handleServerError(error, navigate);
    }
  };

  const confirmDelete = async (id: number) => {
    if (!confirm(t('delete.confirm'))) {
      return;
    }
    try {
      await axios.delete('/api/urls/' + id);
      navigate('/urls', {
            state: {
              msgInfo: t('urls.delete.success')
            }
          });
      getAllUrlses();
    } catch (error: any) {
      handleServerError(error, navigate);
    }
  };

  useEffect(() => {
    getAllUrlses();
  }, [searchParams]);

  return (<>
    <div className="d-flex flex-wrap mb-4">
      <h1 className="flex-grow-1">{t('urls.list.headline')}</h1>
      <div>
        <Link to="/urls/add" className="btn btn-primary ms-2">{t('urls.list.createNew')}</Link>
      </div>
    </div>
    {((urlses && urlses.page.totalElements !== 0) || searchParams.get('filter')) && (
    <div className="row">
      <SearchFilter placeholder={t('urls.list.filter')} />
      <Sorting sortOptions={sortOptions} rowClass="offset-lg-4" />
    </div>
    )}
    {!urlses || urlses.page.totalElements === 0 ? (
    <div>{t('urls.list.empty')}</div>
    ) : (<>
    <div className="table-responsive">
      <table className="table table-striped table-hover align-middle">
        <thead>
          <tr>
            <th scope="col">{t('urls.id.label')}</th>
            <th scope="col">{t('urls.endpoint.label')}</th>
            <th scope="col">{t('urls.method.label')}</th>
            <th scope="col">{t('urls.privilege.label')}</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {urlses.content.map((urls) => (
          <tr key={urls.id}>
            <td>{urls.id}</td>
            <td>{urls.endpoint}</td>
            <td>{urls.method}</td>
            <td>{urls.privilege}</td>
            <td>
              <div className="float-end text-nowrap">
                <Link to={'/urls/edit/' + urls.id} className="btn btn-sm btn-secondary">{t('urls.list.edit')}</Link>
                <span> </span>
                <button type="button" onClick={() => confirmDelete(urls.id!)} className="btn btn-sm btn-secondary">{t('urls.list.delete')}</button>
              </div>
            </td>
          </tr>
          ))}
        </tbody>
      </table>
    </div>
    <Pagination page={urlses.page} />
    </>)}
  </>);
}
