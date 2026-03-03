import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useNavigate, useSearchParams } from 'react-router';
import { handleServerError, getListParams } from 'app/common/utils';
import { UsersDTO } from 'app/users/users-model';
import { PagedModel, Pagination } from 'app/common/list-helper/pagination';
import axios from 'axios';
import SearchFilter from 'app/common/list-helper/search-filter';
import Sorting from 'app/common/list-helper/sorting';
import useDocumentTitle from 'app/common/use-document-title';


export default function UsersList() {
  const { t } = useTranslation();
  useDocumentTitle(t('users.list.headline'));

  const [userses, setUserses] = useState<PagedModel<UsersDTO>|undefined>(undefined);
  const navigate = useNavigate();
  const [searchParams, ] = useSearchParams();
  const listParams = getListParams();
  const sortOptions = {
    'id,ASC': t('users.list.sort.id,ASC'), 
    'name,ASC': t('users.list.sort.name,ASC'), 
    'email,ASC': t('users.list.sort.email,ASC')
  };

  const getAllUserses = async () => {
    try {
      const response = await axios.get('/api/user?' + listParams);
      setUserses(response.data);
    } catch (error: any) {
      handleServerError(error, navigate);
    }
  };

  const confirmDelete = async (id: number) => {
    if (!confirm(t('delete.confirm'))) {
      return;
    }
    try {
      await axios.delete('/api/user/' + id);
      navigate('/Users', {
            state: {
              msgInfo: t('users.delete.success')
            }
          });
      getAllUserses();
    } catch (error: any) {
      handleServerError(error, navigate);
    }
  };

  useEffect(() => {
    getAllUserses();
  }, [searchParams]);

  return (<>
    <div className="d-flex flex-wrap mb-4">
      <h1 className="flex-grow-1">{t('users.list.headline')}</h1>
      <div>
        <Link to="/Users/add" className="btn btn-primary ms-2">{t('users.list.createNew')}</Link>
      </div>
    </div>
    {((userses && userses.page.totalElements !== 0) || searchParams.get('filter')) && (
    <div className="row">
      <SearchFilter placeholder={t('users.list.filter')} />
      <Sorting sortOptions={sortOptions} rowClass="offset-lg-4" />
    </div>
    )}
    {!userses || userses.page.totalElements === 0 ? (
    <div>{t('users.list.empty')}</div>
    ) : (<>
    <div className="table-responsive">
      <table className="table table-striped table-hover align-middle">
        <thead>
          <tr>
            <th scope="col">{t('users.id.label')}</th>
            <th scope="col">{t('users.name.label')}</th>
            <th scope="col">{t('users.email.label')}</th>
            <th scope="col">{t('users.username.label')}</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {userses.content.map((users) => (
          <tr key={users.id}>
            <td>{users.id}</td>
            <td>{users.name}</td>
            <td>{users.email}</td>
            <td>{users.username}</td>
            <td>
              <div className="float-end text-nowrap">
                <Link to={'/Users/edit/' + users.id} className="btn btn-sm btn-secondary">{t('users.list.edit')}</Link>
                <span> </span>
                <button type="button" onClick={() => confirmDelete(users.id!)} className="btn btn-sm btn-secondary">{t('users.list.delete')}</button>
              </div>
            </td>
          </tr>
          ))}
        </tbody>
      </table>
    </div>
    <Pagination page={userses.page} />
    </>)}
  </>);
}
