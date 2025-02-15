/*
 * Copyright (c) 2020-present SMC Treviso s.r.l. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import clsx from "clsx";
import { createUseStyles } from "react-jss";
import { useRouter } from "next/router";
import { format } from "date-fns";
import useSWR from "swr";
import ClayNavigationBar from "@clayui/navigation-bar";
import ClayIcon from "@clayui/icon";
import { firstOrString, ThemeType } from "@openk9/search-ui-components";
import { Layout } from "../../../../../components/Layout";
import { getDataSourceInfo } from "@openk9/http-api";

const useStyles = createUseStyles((theme: ThemeType) => ({
  root: {
    margin: [theme.spacingUnit * 2, "auto"],
    backgroundColor: "white",
    boxShadow: theme.baseBoxShadow,
    width: "100%",
    maxWidth: 1000,
    borderRadius: theme.borderRadius,
    overflow: "auto",
    padding: theme.spacingUnit * 2,
  },
  dataList: {
    "& div": {
      marginBottom: "0.2rem",
    },
    marginBottom: "1rem",
  },
  json: {
    marginTop: "0.2rem",
    backgroundColor: theme.digitalLakeMainL2,
    color: "white",
    padding: theme.spacingUnit * 2,
    borderRadius: theme.borderRadius,
  },
  navMenu: {
    backgroundColor: "transparent",
  },
  navActionButton: {
    marginLeft: theme.spacingUnit,
  },
}));

function Controls() {
  const classes = useStyles();
  return (
    <ClayNavigationBar triggerLabel="Configuration" className={classes.navMenu}>
      <ClayNavigationBar.Item active>
        <a className="nav-link">Configuration</a>
      </ClayNavigationBar.Item>
      <ClayNavigationBar.Item>
        <a className="nav-link">Data Browser</a>
      </ClayNavigationBar.Item>
      <ClayNavigationBar.Item>
        <a className="nav-link">Enrich</a>
      </ClayNavigationBar.Item>
      <ClayNavigationBar.Item>
        <a className="nav-link">Schedule</a>
      </ClayNavigationBar.Item>
      <ClayNavigationBar.Item>
        <a className="nav-link">ACL</a>
      </ClayNavigationBar.Item>
      <ClayNavigationBar.Item>
        <a className={clsx("btn btn-primary", classes.navActionButton)}>
          <ClayIcon symbol="reload" />
        </a>
      </ClayNavigationBar.Item>
    </ClayNavigationBar>
  );
}

function Inner({
  tenantId,
  datasourceId,
}: {
  tenantId: number;
  datasourceId: number;
}) {
  const classes = useStyles();

  const { data: datasource } = useSWR(
    `/api/v2/datasource/${datasourceId}`,
    () => getDataSourceInfo(datasourceId),
  );

  if (!datasource) {
    return <span className="loading-animation" />;
  }

  return (
    <>
      <h2>
        {datasource.datasourceId}: {datasource.name}
      </h2>
      <div className={classes.dataList}>
        <div>
          <strong>Status:</strong>{" "}
          {datasource.active ? (
            <span className="label label-success">
              <span className="label-item label-item-expand">ENABLED</span>
            </span>
          ) : (
            <span className="label label-warning">
              <span className="label-item label-item-expand">DISABLED</span>
            </span>
          )}
        </div>
        <div>
          <strong>Description:</strong> {datasource.description}
        </div>
        <div>
          <strong>Driver Service Name:</strong> {datasource.driverServiceName}
        </div>
        <div>
          <strong>Tenant Id:</strong> {datasource.tenantId}
        </div>
        <div>
          <strong>Last Ingestion Date:</strong>{" "}
          {format(datasource.lastIngestionDate, "dd/MM/yyyy, HH:mm")}
        </div>
        <div>
          <strong>Scheduling:</strong> {datasource.scheduling}
        </div>
      </div>

      <h5>JSON Configuration</h5>
      <pre className={classes.json}>
        {JSON.stringify(JSON.parse(datasource.jsonConfig), null, 4)}
      </pre>
    </>
  );
}

function DSSettings() {
  const classes = useStyles();

  const { query } = useRouter();
  const tenantId = query.tenantId && firstOrString(query.tenantId);
  const datasourceId = query.datasourceId && firstOrString(query.datasourceId);

  return (
    <Layout
      breadcrumbsPath={[
        { label: "Tenants", path: "/tenants" },
        { label: tenantId },
        { label: "DataSources", path: `/tenants/${tenantId}/dataSources` },
        { label: datasourceId },
        { label: "Settings", path: `/tenants/${tenantId}/dataSources` },
      ]}
      breadcrumbsControls={<Controls />}
    >
      <div className={classes.root}>
        <Inner
          tenantId={parseInt(tenantId)}
          datasourceId={parseInt(datasourceId)}
        />
      </div>
    </Layout>
  );
}

export default DSSettings;
