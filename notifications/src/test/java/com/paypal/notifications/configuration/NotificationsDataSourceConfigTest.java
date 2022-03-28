package com.paypal.notifications.configuration;

import com.paypal.notifications.model.entity.NotificationEntity;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationsDataSourceConfigTest {

	@InjectMocks
	private NotificationsDataSourceConfig testObj;

	@Mock
	private EntityManagerFactoryBuilder entityManagerFactoryBuilderMock;

	@Mock
	private DataSource dataSourceMock;

	@Mock
	private EntityManagerFactoryBuilder.Builder builderMock;

	@Captor
	private ArgumentCaptor<Class> packagesArgumentCaptor;

	@Mock
	private LocalContainerEntityManagerFactoryBean notificationsTransactionManagerMock;

	@Mock
	private EntityManagerFactory entityManagerFactoryMock;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private DataSourceProperties notificationsDataSourcePropertiesMock;

	@Mock
	private DataSourceBuilder<DataSource> dataSourceBuilderMock;

	@Test
	void notificationsDataSourceDataSourceProperties_shouldCreateANewDataSourcePropertiesInstance() {
		final DataSourceProperties result = testObj.notificationsDataSourceDataSourceProperties();

		assertThat(result).isNotNull().isInstanceOf(DataSourceProperties.class);
	}

	@Test
	void notificationsDataSource_shouldCreateANewDataSourceInstance() {
		doReturn(dataSourceBuilderMock).when(notificationsDataSourcePropertiesMock).initializeDataSourceBuilder();
		doReturn(dataSourceMock).when(dataSourceBuilderMock).build();

		final DataSource result = testObj.notificationsDataSource(notificationsDataSourcePropertiesMock);

		assertThat(result).isSameAs(dataSourceMock);
	}

	@Test
	void notificationsEntityManagerFactory_scanPackageJobExecutionInformationEntity() {
		when(entityManagerFactoryBuilderMock.dataSource(dataSourceMock)).thenReturn(builderMock);
		when(builderMock.packages(any(Class.class))).thenReturn(builderMock);

		testObj.notificationsEntityManagerFactory(entityManagerFactoryBuilderMock, dataSourceMock);

		verify(builderMock).packages(packagesArgumentCaptor.capture());
		final List<Class> allValues = packagesArgumentCaptor.getAllValues();
		assertThat(allValues).hasSize(1).containsExactlyInAnyOrder(NotificationEntity.class);
	}

	@Test
	void notificationsTransactionManager_shouldPlatformTransactionManager() {
		when(notificationsTransactionManagerMock.getObject()).thenReturn(entityManagerFactoryMock);
		final PlatformTransactionManager result = testObj
				.notificationsTransactionManager(notificationsTransactionManagerMock);

		assertThat(result).isNotNull().isInstanceOf(PlatformTransactionManager.class);
	}

}
