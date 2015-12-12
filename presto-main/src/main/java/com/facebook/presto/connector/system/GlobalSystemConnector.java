/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.connector.system;

import com.facebook.presto.spi.ColumnHandle;
import com.facebook.presto.spi.ColumnMetadata;
import com.facebook.presto.spi.ConnectorHandleResolver;
import com.facebook.presto.spi.ConnectorSession;
import com.facebook.presto.spi.ConnectorTableHandle;
import com.facebook.presto.spi.ConnectorTableMetadata;
import com.facebook.presto.spi.SchemaTableName;
import com.facebook.presto.spi.SchemaTablePrefix;
import com.facebook.presto.spi.SystemTable;
import com.facebook.presto.spi.TransactionalConnectorPageSourceProvider;
import com.facebook.presto.spi.TransactionalConnectorSplitManager;
import com.facebook.presto.spi.transaction.ConnectorTransactionHandle;
import com.facebook.presto.spi.transaction.IsolationLevel;
import com.facebook.presto.spi.transaction.TransactionalConnectorMetadata;
import com.facebook.presto.transaction.InternalTransactionalConnector;
import com.facebook.presto.transaction.TransactionId;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class GlobalSystemConnector
        implements InternalTransactionalConnector
{
    public static final String NAME = "system";

    private final String connectorId;
    private final Set<SystemTable> systemTables;

    public GlobalSystemConnector(String connectorId, Set<SystemTable> systemTables)
    {
        this.connectorId = requireNonNull(connectorId, "connectorId is null");
        this.systemTables = ImmutableSet.copyOf(requireNonNull(systemTables, "systemTables is null"));
    }

    @Override
    public ConnectorTransactionHandle beginTransaction(TransactionId transactionId, IsolationLevel isolationLevel, boolean readOnly)
    {
        return new GlobalSystemTransactionHandle(connectorId, transactionId);
    }

    @Override
    public ConnectorHandleResolver getHandleResolver()
    {
        return new GlobalSystemHandleResolver(connectorId);
    }

    @Override
    public TransactionalConnectorMetadata getMetadata(ConnectorTransactionHandle transactionHandle)
    {
        return new TransactionalConnectorMetadata()
        {
            @Override
            public List<String> listSchemaNames(ConnectorSession session)
            {
                return ImmutableList.of();
            }

            @Override
            public ConnectorTableHandle getTableHandle(ConnectorSession session, SchemaTableName tableName)
            {
                return null;
            }

            @Override
            public ConnectorTableMetadata getTableMetadata(ConnectorSession session, ConnectorTableHandle table)
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<SchemaTableName> listTables(ConnectorSession session, String schemaNameOrNull)
            {
                return ImmutableList.of();
            }

            @Override
            public Map<String, ColumnHandle> getColumnHandles(ConnectorSession session, ConnectorTableHandle tableHandle)
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public ColumnMetadata getColumnMetadata(ConnectorSession session, ConnectorTableHandle tableHandle, ColumnHandle columnHandle)
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public Map<SchemaTableName, List<ColumnMetadata>> listTableColumns(ConnectorSession session, SchemaTablePrefix prefix)
            {
                return ImmutableMap.of();
            }
        };
    }

    @Override
    public TransactionalConnectorSplitManager getSplitManager()
    {
        return new TransactionalConnectorSplitManager() {};
    }

    @Override
    public TransactionalConnectorPageSourceProvider getPageSourceProvider()
    {
        return (transactionHandle, session, split, columns) -> {
            throw new UnsupportedOperationException();
        };
    }

    @Override
    public Set<SystemTable> getSystemTables()
    {
        return systemTables;
    }
}