/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
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
 * </p>
 */

package io.shardingjdbc.core.parsing.integrate.engine;

import com.google.common.base.Preconditions;
import io.shardingjdbc.core.metadata.ColumnMetaData;
import io.shardingjdbc.core.metadata.ShardingMetaData;
import io.shardingjdbc.core.metadata.TableMetaData;
import io.shardingjdbc.core.rule.ShardingRule;
import io.shardingjdbc.core.yaml.sharding.YamlShardingConfiguration;
import lombok.AccessLevel;
import lombok.Getter;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public abstract class AbstractBaseIntegrateSQLParsingTest {
    
    @Getter(AccessLevel.PROTECTED)
    private static ShardingRule shardingRule;
    
    @Getter(AccessLevel.PROTECTED)
    private static ShardingMetaData shardingMetaData;
    
    @BeforeClass
    public static void setUp() throws IOException {
        shardingRule = buildShardingRule();
        shardingMetaData = buildShardingMetaData();
    }
    
    private static ShardingRule buildShardingRule() throws IOException {
        URL url = AbstractBaseIntegrateSQLParsingTest.class.getClassLoader().getResource("yaml/parser-rule.yaml");
        Preconditions.checkNotNull(url, "Cannot found parser rule yaml configuration.");
        YamlShardingConfiguration yamlShardingConfig = YamlShardingConfiguration.unmarshal(new File(url.getFile()));
        return yamlShardingConfig.getShardingRule(yamlShardingConfig.getDataSources().keySet());
    }
    
    private static ShardingMetaData buildShardingMetaData() {
        Map<String, TableMetaData> tableMetaDataMap = new HashMap<>();
        tableMetaDataMap.put("t_order", getTableMetaData(new ArrayList<String>(){{add("user_id");add("order_id");}}));
        tableMetaDataMap.put("t_order_item", getTableMetaData(new ArrayList<String>(){{add("user_id");add("order_id");add("item_id");add("c_date");}}));
        tableMetaDataMap.put("t_place", getTableMetaData(new ArrayList<String>(){{add("user_new_id");add("guid");}}));
        ShardingMetaData shardingMetaData = Mockito.mock(ShardingMetaData.class);
        when(shardingMetaData.getTableMetaDataMap()).thenReturn(tableMetaDataMap);
        return shardingMetaData;
    }
    
    private static TableMetaData getTableMetaData(final List<String> columnNames) {
        List<ColumnMetaData> columnMetaDataList = new ArrayList<>();
        for (String columnName : columnNames) {
            columnMetaDataList.add(new ColumnMetaData(columnName, "int(11)", ""));
        }
        return new TableMetaData(columnMetaDataList);
    }
}
