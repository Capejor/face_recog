package com.faceRecog.manage.sharding;

 

import com.faceRecog.manage.mapper.UserMapper;
import com.faceRecog.manage.strategy.ModuloDatabaseShardingAlgorithm;
import com.faceRecog.manage.strategy.ModuloTableShardingAlgorithm;
import com.zaxxer.hikari.HikariDataSource;

import io.shardingsphere.core.api.ShardingDataSourceFactory;
import io.shardingsphere.core.api.config.ShardingRuleConfiguration;
import io.shardingsphere.core.api.config.TableRuleConfiguration;
import io.shardingsphere.core.api.config.strategy.StandardShardingStrategyConfiguration;

import org.apache.ibatis.session.SqlSessionFactory;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

import java.sql.SQLException;
import java.util.*;

/**
 *  数据源及分表配置
 * Created by Kane on 2018/1/17.
 */
@Configuration
//@MapperScan(basePackages = "com.faceRecog.manage.mapper", sqlSessionTemplateRef  = "shardSqlSessionTemplate")
public class DataSourceConfig {

		@Value("${spring.datasource.name}")
	    private String databaseName;// 数据库名
	
	 	@ConfigurationProperties(prefix = "spring.datasource")
	    @Bean(name = "dataSource1")
	    public DataSource dataSource1() {
	        return   new HikariDataSource();
	    }


	    @Primary
	    @Bean(name = "shardingDataSource")
	    public DataSource getDataSource(@Qualifier("dataSource1") DataSource dataSource1) throws SQLException {
	        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
	        
	        shardingRuleConfig.getTableRuleConfigs().add(getOrderTableRuleConfiguration());
	        shardingRuleConfig.getTableRuleConfigs().add(getOrigSignTableRuleConfiguration());
	        shardingRuleConfig.getBindingTableGroups().add("fr_attend_detail,fr_original_sign_record");
	        /*shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("create_time", new ModuloDatabaseShardingAlgorithm()));
	        shardingRuleConfig.setDefaultTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("create_time", new ModuloTableShardingAlgorithm()));*/
	        Map<String, DataSource> dataSourceMap = new HashMap<>();
	        dataSourceMap.put(databaseName, dataSource1);
	        Properties properties = new Properties();
//	        properties.setProperty("sql.show", Boolean.TRUE.toString());
	        
	        return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, new HashMap<String, Object>(),properties);
	    }

	    private static TableRuleConfiguration getOrderTableRuleConfiguration() {
	        TableRuleConfiguration result = new TableRuleConfiguration();
	        result.setLogicTable("attend_detail");
	        result.setActualDataNodes("face_recog.fr_attend_detail_${2019..2020}");
	        result.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("create_time",  new ModuloDatabaseShardingAlgorithm()));
	        result.setTableShardingStrategyConfig( new StandardShardingStrategyConfiguration("create_time",  new ModuloTableShardingAlgorithm()));
	        return result;
	    }

	    private static TableRuleConfiguration getOrigSignTableRuleConfiguration() {
	        TableRuleConfiguration result = new TableRuleConfiguration();
	        result.setLogicTable("original_sign_record");
	        result.setActualDataNodes("face_recog.fr_original_sign_record_${2019..2020}");
	        result.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("create_time",  new ModuloDatabaseShardingAlgorithm()));
	        result.setTableShardingStrategyConfig( new StandardShardingStrategyConfiguration("create_time",  new ModuloTableShardingAlgorithm()));
	        return result;
	    }
	    
	    
	   /* @Bean(name = "shardSqlSessionFactory")
	    public SqlSessionFactory shardSqlSessionFactory(@Qualifier("shardingDataSource") DataSource dataSource) throws Exception {
	        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
	        bean.setDataSource(dataSource);
	        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
	        //bean.setConfigLocation(new DefaultResourceLoader().getResource("classpath:mybatis/mybatis-config.xml"));
	        return bean.getObject();
	    }

	    @Bean(name = "shardTransactionManager")
	    public DataSourceTransactionManager shardTransactionManager(@Qualifier("shardingDataSource") DataSource dataSource) {
	        return new DataSourceTransactionManager(dataSource);
	    }

	    @Bean(name = "shardSqlSessionTemplate")
	    public SqlSessionTemplate shardSqlSessionTemplate(@Qualifier("shardSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
	        return new SqlSessionTemplate(sqlSessionFactory);
	    }*/
}