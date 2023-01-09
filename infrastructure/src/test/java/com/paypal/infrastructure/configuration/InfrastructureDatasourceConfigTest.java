package com.paypal.infrastructure.configuration;

import com.paypal.infrastructure.InfrastructureConnectorApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InfrastructureDatasourceConfigTest {

	@InjectMocks
	private InfrastructureDatasourceConfig testObj;

	@Mock
	private DataSource dataSourceMock;

	@Mock
	private EntityManagerFactory entityManagerFactoryMock;

	@Mock
	private EntityManagerFactoryBuilder.Builder builderMock;

	@Mock
	private DataSourceBuilder<DataSource> dataSourceBuilderMock;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private DataSourceProperties applicationDataSourcePropertiesMock;

	@Mock
	private EntityManagerFactoryBuilder entityManagerFactoryBuilderMock;

	@Mock
	private LocalContainerEntityManagerFactoryBean applicationTransactionManagerMock;

	@Captor
	private ArgumentCaptor<Class> packagesArgumentCaptor;

	@Test
	void applicationDataSourceDataSourceProperties_shouldCreateANewDataSourcePropertiesInstance() {
		final DataSourceProperties result = testObj.applicationDataSourceDataSourceProperties();

		assertThat(result).isNotNull().isInstanceOf(DataSourceProperties.class);
	}

	@Test
	void applicationDataSource_shouldCreateANewDataSourceInstance() {
		doReturn(dataSourceBuilderMock).when(applicationDataSourcePropertiesMock).initializeDataSourceBuilder();
		doReturn(dataSourceMock).when(dataSourceBuilderMock).build();

		final DataSource result = testObj.applicationDataSource(applicationDataSourcePropertiesMock);

		assertThat(result).isSameAs(dataSourceMock);
	}

	@Test
	void applicationEntityManagerFactory_scanPackageJobExecutionInformationEntity() {
		when(entityManagerFactoryBuilderMock.dataSource(dataSourceMock)).thenReturn(builderMock);
		when(builderMock.packages(any(Class.class))).thenReturn(builderMock);

		testObj.applicationEntityManagerFactory(entityManagerFactoryBuilderMock, dataSourceMock);

		verify(builderMock).packages(packagesArgumentCaptor.capture());
		assertThat(packagesArgumentCaptor.getAllValues())
				.containsExactlyInAnyOrder(InfrastructureConnectorApplication.class);
	}

	@Test
	void applicationTransactionManager_shouldPlatformTransactionManager() {
		when(applicationTransactionManagerMock.getObject()).thenReturn(entityManagerFactoryMock);
		final PlatformTransactionManager result = testObj
				.applicationTransactionManager(applicationTransactionManagerMock);

		assertThat(result).isNotNull().isInstanceOf(PlatformTransactionManager.class);
	}

}
